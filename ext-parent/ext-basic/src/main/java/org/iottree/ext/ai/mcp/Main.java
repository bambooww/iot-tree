package org.iottree.ext.ai.mcp;

import org.iottree.ext.ai.mcp.model.ResourceDefinition;
import org.iottree.ext.ai.mcp.tools.CalculatorTool;
import org.iottree.ext.ai.mcp.tools.WeatherTool;
import org.iottree.ext.ai.mcp.transport.*;

/**
 * MCP Server entry point.
 *
 * Usage:
 *   java org.iottree.ext.mcp.Main stdio      (default) stdin/stdout
 *   java org.iottree.ext.mcp.Main post       HTTP POST
 *   java org.iottree.ext.mcp.Main sse        HTTP + SSE
 *   java org.iottree.ext.mcp.Main ws         WebSocket
 */
public class Main {
    public static void main(String[] args) throws Exception {
        String transport = args.length > 0 ? args[0].toLowerCase() : "stdio";
        int port = 8080;
        if (args.length > 1) {
            try { port = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {}
        }

        McpService service = new McpService("mcp-example", "1.0.0");
        service.registerTool(new CalculatorTool());
        service.registerTool(new WeatherTool());
        service.registerResource(new ResourceDefinition(
            "file:///help.txt", "Help", "MCP Server usage guide", "text/plain"
        ));

        System.err.println("[Main] MCP Server initialized");
        System.err.println("[Main] tools: calculator, get_weather");
        System.err.println("[Main] resources: file:///help.txt");

        switch (transport) {
            case "stdio": {
                System.err.println("[Main] transport: stdio (stdin/stdout)");
                new McpStdioServer(service).start();
                break;
            }
            case "post": {
                System.err.println("[Main] transport: HTTP POST (port " + port + ")");
                new McpHttpPostServer(service, port).start();
                Thread.currentThread().join();
                break;
            }
            case "sse": {
                System.err.println("[Main] transport: HTTP + SSE (port " + port + ")");
                new McpHttpSseServer(service, port).start();
                Thread.currentThread().join();
                break;
            }
            case "ws": {
                System.err.println("[Main] transport: WebSocket (port " + port + ")");
                new McpWebSocketServer(service, port).start();
                break;
            }
            default:
                System.err.println("unknown transport: " + transport);
                System.err.println("options: stdio, post, sse, ws");
                System.exit(1);
        }
    }
}
