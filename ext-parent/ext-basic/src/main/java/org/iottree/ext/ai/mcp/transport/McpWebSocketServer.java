package org.iottree.ext.ai.mcp.transport;

import org.iottree.ext.ai.mcp.MCPTransport;
import org.iottree.ext.ai.mcp.McpService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class McpWebSocketServer implements MCPTransport
{

    private final McpService server;
    private final int port;
    private volatile boolean running = true;

    private static final String WS_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    public McpWebSocketServer(McpService server, int port) {
        this.server = server;
        this.port = port;
    }

    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000);
        System.err.println("[MCP WS] WebSocket server started at ws://0.0.0.0:" + port + "/mcp");

        while (running) {
            try {
                Socket client = serverSocket.accept();
                new Thread(() -> handleClient(client), "ws-client").start();
            } catch (SocketTimeoutException ignored) {
            }
        }

        serverSocket.close();
        System.err.println("[MCP WS] server stopped");
    }

    public void stop() {
        running = false;
    }

    private void handleClient(Socket client) {
        try {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();

            if (!handshake(in, out)) {
                client.close();
                return;
            }

            System.err.println("[MCP WS] client connected: " + client.getRemoteSocketAddress());

            while (running && !client.isClosed()) {
                WebSocketFrame frame = readFrame(in);
                if (frame == null) break;

                switch (frame.opcode) {
                    case 0x8:
                        writeFrame(out, 0x8, new byte[0]);
                        System.err.println("[MCP WS] client disconnected");
                        client.close();
                        return;

                    case 0x9:
                        writeFrame(out, 0xA, frame.payload);
                        break;

                    case 0x1:
                        String json = new String(frame.payload, StandardCharsets.UTF_8);
                        System.err.println("[MCP WS] received: " + json);
                        String result = server.handleMessage(json);
                        System.err.println("[MCP WS] response: " + result);
                        writeFrame(out, 0x1, result.getBytes(StandardCharsets.UTF_8));
                        break;

                    case 0xA:
                        break;

                    default:
                        System.err.println("[MCP WS] unknown opcode: " + frame.opcode);
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("[MCP WS] connection error: " + e.getMessage());
            }
        } finally {
            try { client.close(); } catch (Exception ignored) {}
        }
    }

    private boolean handshake(InputStream in, OutputStream out) throws IOException {
        String request = readHttpHeader(in);
        if (request == null || !request.contains("Upgrade: websocket")) {
            return false;
        }

        String key = extractHeader(request, "Sec-WebSocket-Key");
        if (key == null) return false;

        String accept;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update((key + WS_GUID).getBytes(StandardCharsets.US_ASCII));
            accept = Base64.getEncoder().encodeToString(md.digest());
        } catch (Exception e) {
            return false;
        }

        String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + accept + "\r\n" +
                "\r\n";
        out.write(response.getBytes(StandardCharsets.US_ASCII));
        out.flush();
        return true;
    }

    private String readHttpHeader(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int prev = -1, prev2 = -1;
        int b;
        while ((b = in.read()) != -1) {
            baos.write(b);
            if (prev2 == '\r' && prev == '\n' && b == '\r') {
                b = in.read();
                baos.write(b);
                break;
            }
            prev2 = prev;
            prev = b;
        }
        String header = baos.toString("ISO-8859-1");
        return header.contains("\r\n\r\n") ? header : null;
    }

    private String extractHeader(String request, String name) {
        for (String line : request.split("\r\n")) {
            if (line.toLowerCase().startsWith(name.toLowerCase() + ":")) {
                return line.substring(line.indexOf(':') + 1).trim();
            }
        }
        return null;
    }

    private static class WebSocketFrame {
        final int opcode;
        final byte[] payload;
        WebSocketFrame(int opcode, byte[] payload) {
            this.opcode = opcode;
            this.payload = payload;
        }
    }

    private WebSocketFrame readFrame(InputStream in) throws IOException {
        int b1 = in.read();
        if (b1 == -1) return null;

        boolean fin = (b1 & 0x80) != 0;
        int opcode = b1 & 0x0F;

        int b2 = in.read();
        if (b2 == -1) return null;

        boolean masked = (b2 & 0x80) != 0;
        int len = b2 & 0x7F;

        if (len == 126) {
            len = (in.read() << 8) | in.read();
        } else if (len == 127) {
            long longLen = 0;
            for (int i = 0; i < 8; i++) {
                longLen = (longLen << 8) | (in.read() & 0xFF);
            }
            len = (int) longLen;
        }

        byte[] maskKey = null;
        if (masked) {
            maskKey = new byte[4];
            in.read(maskKey);
        }

        byte[] payload = new byte[len];
        int offset = 0;
        while (offset < len) {
            int read = in.read(payload, offset, len - offset);
            if (read == -1) return null;
            offset += read;
        }

        if (masked && maskKey != null) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] ^= maskKey[i % 4];
            }
        }

        if (!fin && opcode == 0x1) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(payload);
            while (true) {
                WebSocketFrame next = readFrame(in);
                if (next == null) return null;
                baos.write(next.payload);
                break;
            }
            payload = baos.toByteArray();
        }

        if (!fin && opcode != 0x9 && opcode != 0xA) {
            return readFrame(in);
        }

        return new WebSocketFrame(opcode, payload);
    }

    private void writeFrame(OutputStream out, int opcode, byte[] data) throws IOException {
        out.write(0x80 | opcode);

        if (data.length < 126) {
            out.write(data.length);
        } else if (data.length < 65536) {
            out.write(126);
            out.write(data.length >> 8);
            out.write(data.length & 0xFF);
        } else {
            out.write(127);
            for (int i = 7; i >= 0; i--) {
                out.write((int) (data.length >> (i * 8)) & 0xFF);
            }
        }

        out.write(data);
        out.flush();
    }
}
