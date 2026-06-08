package org.iottree.ext.ai.mcp;

import java.util.List;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import io.modelcontextprotocol.spec.McpSchema.Tool;

public class MCPService
{
	public void start()
	{
		// Sync tool specification using builder
		SyncToolSpecification syncToolSpecification = SyncToolSpecification.builder()
		    .tool(Tool.builder()
		        .name("calculator")
		        .description("Basic calculator")
		        //.inputSchema(schema)
		        .build())
		    .callHandler((exchange, request) -> {
		        // Access arguments via request.arguments()
		        String operation = (String) request.arguments().get("operation");
		        int a = (int) request.arguments().get("a");
		        int b = (int) request.arguments().get("b");
		        // Tool implementation
		        return CallToolResult.builder()
		            .content(List.of(new McpSchema.TextContent("Result: ")))
		            .build();
		    })
		    .build();
		
		HttpServletStreamableServerTransportProvider transportProvider =
			    HttpServletStreamableServerTransportProvider.builder()
			        .jsonMapper(null)
			        .mcpEndpoint("/mcp")
			        .build();
		
		// Create a server with custom configuration
		McpSyncServer syncServer = McpServer.sync(transportProvider)
		    .serverInfo("my-server", "1.0.0")
		    .capabilities(ServerCapabilities.builder()
		        .resources(false, true)  // Resource support: subscribe=false, listChanged=true
		        .tools(true)             // Enable tool support with list changes
		        .prompts(true)           // Enable prompt support with list changes
		        .completions()           // Enable completions support
		        .logging()               // Enable logging support
		        .build())
		    .build();

		// Register tools, resources, and prompts
		syncServer.addTool(syncToolSpecification);
		//syncServer.addResource(syncResourceSpecification);
		//syncServer.addPrompt(syncPromptSpecification);

		// Close the server when done
		syncServer.close();
	}
}
