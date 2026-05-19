package org.iottree.ext.ai.mcp.transport;

import org.iottree.ext.ai.mcp.McpService;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class McpStdioServer {

    private final McpService server;

    public McpStdioServer(McpService server) {
        this.server = server;
    }

    public void start() throws IOException {
        System.err.println("[MCP stdio] started, waiting for stdin...");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) continue;
            try {
                String response = server.handleMessage(line.trim());
                if (response != null) {
                    System.out.println(response);
                    System.out.flush();
                }
            } catch (Exception e) {
                System.err.println("[MCP stdio] process error: " + e.getMessage());
            }
        }
    }
}
