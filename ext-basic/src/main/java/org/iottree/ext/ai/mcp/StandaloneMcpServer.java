package org.iottree.ext.ai.mcp;


import com.fasterxml.jackson.databind.ObjectMapper;

import io.modelcontextprotocol.server.*;
import io.modelcontextprotocol.server.transport.*;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;

public class StandaloneMcpServer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        // 1. 创建基于标准输入输出流的最新 Stdio 传输层
    	HttpServletStreamableServerTransportProvider transportProvider =
			    HttpServletStreamableServerTransportProvider.builder()
			       // .jsonMapper(jsonMapper)
			        .mcpEndpoint("/mcp")
			        .build();
    	HttpServletStreamableServerTransportProvider pro = HttpServletStreamableServerTransportProvider.builder().build();
        //StdioServerTransport pro = new StdioServerTransport();

        // 2. 初始化最新版 McpServer
//        McpServer mcpServer = McpServer.builder(pro)
//                .serverInfo("Modern-Java-MCP-Server", "2.0.0")
//                .capabilities(McpSchema.ServerCapabilities.builder()
//                        .tools(true) // 显式声明开启 Tools 能力
//                        .build())
//                .build();

        // 3. 注册你的自定义工具
//        registerTools(mcpServer);

        // 4. 启动服务器并阻塞主线程保持运行
        try {
            System.err.println("[MCP] 正在启动最新版服务端...");
//            mcpServer.start();
            System.err.println("[MCP] 服务端已就绪，正在通过 Stdio 侦听 Client 请求...");

            // 保持进程存活
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("[MCP] 异常退出: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void registerTools(McpServer mcpServer) {
//        try {
//            // 定义符合 JSON Schema 规范的输入参数（最新版推荐使用 Map 传递 schema 结构）
//            String schemaJson = """
//                {
//                  "type": "object",
//                  "properties": {
//                    "location": {
//                      "type": "string",
//                      "description": "城市或地区名称，例如: Beijing, New York"
//                    }
//                  },
//                  "required": ["location"]
//                }
//                """;
//            Map<String, Object> inputSchema = objectMapper.readValue(schemaJson, Map.class);
//
//            // 创建 Tool 声明
//            McpSchema.Tool weatherTool = new McpSchema.Tool(
//                    "get_weather",
//                    "获取指定城市的实时天气信息",
//                    inputSchema
//            );
//
//            // 向客户端注册可用的工具列表
//            mcpServer.registerTools(List.of(weatherTool));
//
//            // 5. 绑定工具执行的回调逻辑
//            mcpServer.onCallTool((request) -> {
//                if ("get_weather".equals(request.name())) {
//                    Map<String, Object> arguments = request.arguments();
//                    String location = (String) arguments.get("location");
//
//                    // 执行本地业务（可以是硬件采集、视觉处理、查数据库等）
//                    String weatherResult = "[最新版SDK响应] 城市: " + location + ", 天气: 晴, 温度: 23°C";
//
//                    // 最新版返回结构：包装为 TextContent 列表并返回 CallToolResult
//                    McpSchema.TextContent textContent = new McpSchema.TextContent(weatherResult);
//                    return new McpSchema.CallToolResult(List.of(textContent), false);
//                }
//
//                throw new IllegalArgumentException("未知的 Tool 名称: " + request.name());
//            });
//
//        } catch (Exception e) {
//            System.err.println("[MCP] 注册 Tool 失败: " + e.getMessage());
//        }
    }
}