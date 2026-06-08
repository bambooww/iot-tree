package org.iottree.ext.ai.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class PlainMcpServer {

//    private static final Logger logger = LoggerFactory.getLogger(PlainMcpServer.class);
//
//    public static void main(String[] args) throws Exception {
//    	 // 2. 创建 HTTP 传输提供者
//        //    指定 MCP 协议的服务端点 (endpoint), 例如 "/mcp"
//        HttpServletStreamableServerTransportProvider transportProvider =
//                new HttpServletStreamableServerTransportProvider.Builder().(new ObjectMapper(), "/mcp");
//
//    	// 1. 准备一个 MCP Server 实例
//    	
//        //    这里可以注册工具、资源等
//        McpSyncServer mcpServer = McpServer.sync(transportProvider)
//                .serverInfo("My-MCP-Server", "1.0.0")
//                .capabilities(McpSchema.ServerCapabilities.builder()
//                        .tools(true)
//                        .build())
//                .tools(new McpSchema.Tool(
//                        "greeting",            // 工具名称
//                        "A simple greeting tool", // 工具描述
//                        McpSchema.Tool.inputSchema()
//                                .addProperty("name", McpSchema.Tool.stringSchema())
//                                .build(),
//                        (exchange, args) -> {
//                            String name = args != null && args.containsKey("name") ? args.get("name").toString() : "World";
//                            String greeting = String.format("Hello, %s!", name);
//                            return new McpSchema.CallToolResult(
//                                    List.of(new McpSchema.TextContent(greeting)),
//                                    false
//                            );
//                        }
//                ))
//                .build();
//
//       
//        // 3. 将传输提供者与 MCP Server 关联
//        transportProvider.start(mcpServer);
//
//        // 4. 创建 Jetty 服务器并挂载 Servlet
//        Server jettyServer = new Server(8080);
//        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        context.setContextPath("/");
//        jettyServer.setHandler(context);
//
//        // 关键: 从 transportProvider 获取 Servlet 实例，并注册到指定路径
//        ServletHolder servletHolder = new ServletHolder(transportProvider.getServlet());
//        context.addServlet(servletHolder, "/mcp/*");
//
//        // 5. 启动 Jetty
//        jettyServer.start();
//        logger.info("MCP Server started at http://localhost:8080/mcp");
//        jettyServer.join(); // 等待服务器线程结束
//    }
}
