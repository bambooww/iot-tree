package org.iottree.ext.ai.mcp.transport;

import com.sun.net.httpserver.HttpServer;

import org.iottree.ext.ai.mcp.MCPTransport;
import org.iottree.ext.ai.mcp.McpService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class McpHttpSseServer implements MCPTransport
{
    private final McpService server;
    private final int port;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    HttpServer httpServer = null ;

    public McpHttpSseServer(McpService server, int port) {
        this.server = server;
        this.port = port;
    }

    public synchronized void start() throws Exception {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);

        httpServer.createContext("/sse", exchange -> {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            String sessionId = "sse-" + System.currentTimeMillis();
            System.err.println("[MCP SSE] new connection: " + sessionId);

            exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
            exchange.getResponseHeaders().set("Cache-Control", "no-cache");
            exchange.getResponseHeaders().set("Connection", "keep-alive");
            exchange.sendResponseHeaders(200, 0);

            OutputStream os = exchange.getResponseBody();

            String sessionEvent = "event: session_created\ndata: " + sessionId + "\n\n";
            os.write(sessionEvent.getBytes(StandardCharsets.UTF_8));
            os.flush();

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    os.write(": heartbeat\n\n".getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    Thread.sleep(15000);
                }
            } catch (InterruptedException | IOException ignored) {
            } finally {
                try { os.close(); } catch (Exception ignored) {}
                System.err.println("[MCP SSE] connection closed: " + sessionId);
            }
        });

        httpServer.createContext("/mcp", exchange -> {
            try {
                if (!"POST".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                byte[] reqBytes = exchange.getRequestBody().readAllBytes();
                String json = new String(reqBytes, StandardCharsets.UTF_8);

                System.err.println("[MCP SSE] request: " + json);

                String result = server.handleMessage(json);

                byte[] resBytes = result.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, resBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(resBytes);
                }

                System.err.println("[MCP SSE] response: " + result);
            } catch (Exception e) {
                System.err.println("[MCP SSE] error: " + e.getMessage());
                try {
                    exchange.sendResponseHeaders(500, -1);
                } catch (Exception ignored) {}
            } finally {
                exchange.close();
            }
        });

        System.err.println("[MCP SSE] server started at:");
        System.err.println("  SSE:  http://0.0.0.0:" + port + "/sse");
        System.err.println("  MCP:  http://0.0.0.0:" + port + "/mcp");
        httpServer.start();
    }
    
    public synchronized void stop()
    {
    	if(httpServer==null)
    		return ;
    	httpServer.stop(0);
    	httpServer = null ;
    }
}
