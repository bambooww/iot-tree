package org.iottree.ext.ai.mcp.transport;

import com.sun.net.httpserver.HttpServer;
import org.iottree.ext.ai.mcp.McpService;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class McpHttpPostServer {

    private final McpService server;
    private final int port;

    public McpHttpPostServer(McpService server, int port) {
        this.server = server;
        this.port = port;
    }

    public void start() throws Exception {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/mcp", exchange -> {
            try {
                if (!"POST".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                byte[] reqBytes = exchange.getRequestBody().readAllBytes();
                String json = new String(reqBytes, StandardCharsets.UTF_8);

                System.err.println("[MCP POST] request: " + json);

                String result = server.handleMessage(json);
                byte[] resBytes = result.getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, resBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(resBytes);
                }

                System.err.println("[MCP POST] response: " + result);
            } catch (Exception e) {
                System.err.println("[MCP POST] error: " + e.getMessage());
                try {
                    byte[] err = ("{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32603,\"message\":\"" +
                            e.getMessage() + "\"}}").getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(500, err.length);
                    exchange.getResponseBody().write(err);
                } catch (Exception ignored) {}
            } finally {
                exchange.close();
            }
        });

        System.err.println("[MCP POST] server started at http://0.0.0.0:" + port + "/mcp");
        httpServer.start();
    }
}
