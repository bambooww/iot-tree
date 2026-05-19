package org.iottree.ext.ai.mcp;

import org.iottree.ext.ai.mcp.model.JsonRpcMessage;
import org.iottree.ext.ai.mcp.model.ResourceDefinition;

import java.util.*;

public class McpService {

    private final Map<String, Tool> tools = new LinkedHashMap<>();
    private final Map<String, ResourceDefinition> resources = new LinkedHashMap<>();
    private final Map<String, Object> serverInfo = new LinkedHashMap<>();

    private int msgId = 0;

    public McpService(String name, String version) {
        serverInfo.put("name", name);
        serverInfo.put("version", version);
    }

    public void registerTool(Tool tool) {
        tools.put(tool.getDefinition().name, tool);
    }

    public void registerResource(ResourceDefinition resource) {
        resources.put(resource.uri, resource);
    }

    public String getServerName() {
        return (String) serverInfo.get("name");
    }

    public String getServerVersion() {
        return (String) serverInfo.get("version");
    }

    public String handleMessage(String rawJson) {
        Map<String, Object> msg = JsonUtils.parse(rawJson);
        String id = (String) msg.get("id");
        String method = (String) msg.get("method");

        if (method == null) {
            return toJson(JsonRpcMessage.error(id, -32600, "missing method"));
        }

        try {
            switch (method) {
                case "initialize":
                    return handleInitialize(id, msg);
                case "tools/list":
                    return handleListTools(id);
                case "tools/call":
                    return handleCallTool(id, msg);
                case "resources/list":
                    return handleListResources(id);
                case "prompts/list":
                    return handleListPrompts(id);
                default:
                    return toJson(JsonRpcMessage.error(id, -32601, "unsupported method: " + method));
            }
        } catch (Exception e) {
            return toJson(JsonRpcMessage.error(id, -32603, "internal error: " + e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private String handleInitialize(String id, Map<String, Object> msg) {
        Map<String, Object> params = (Map<String, Object>) msg.get("params");
        Map<String, Object> clientInfo = params != null ? (Map<String, Object>) params.get("clientInfo") : null;
        System.err.println("[MCP] client connected: "
            + (clientInfo != null ? clientInfo.get("name") + " v" + clientInfo.get("version") : "unknown"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("protocolVersion", "2024-11-05");
        result.put("serverInfo", serverInfo);
        result.put("capabilities", Map.of(
            "tools", Map.of(),
            "resources", Map.of(),
            "prompts", Map.of()
        ));
        return toJson(JsonRpcMessage.success(id, result));
    }

    private String handleListTools(String id) {
        List<Map<String, Object>> toolList = new ArrayList<>();
        for (Tool tool : tools.values()) {
            Map<String, Object> t = new LinkedHashMap<>();
            t.put("name", tool.getDefinition().name);
            t.put("description", tool.getDefinition().description);
            t.put("inputSchema", tool.getDefinition().inputSchema);
            toolList.add(t);
        }
        return toJson(JsonRpcMessage.success(id, Map.of("tools", toolList)));
    }

    @SuppressWarnings("unchecked")
    private String handleCallTool(String id, Map<String, Object> msg) {
        Map<String, Object> params = (Map<String, Object>) msg.get("params");
        String name = (String) params.get("name");
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");

        Tool tool = tools.get(name);
        if (tool == null) {
            return toJson(JsonRpcMessage.error(id, -32602, "unknown tool: " + name));
        }

        try {
            String result = tool.execute(arguments != null ? arguments : Map.of());
            Map<String, Object> content = new LinkedHashMap<>();
            content.put("type", "text");
            content.put("text", result);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("content", List.of(content));

            return toJson(JsonRpcMessage.success(id, response));
        } catch (Exception e) {
            return toJson(JsonRpcMessage.error(id, -32603, "tool execution failed: " + e.getMessage()));
        }
    }

    private String handleListResources(String id) {
        List<Map<String, Object>> resourceList = new ArrayList<>();
        for (ResourceDefinition res : resources.values()) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("uri", res.uri);
            r.put("name", res.name);
            r.put("description", res.description);
            r.put("mimeType", res.mimeType);
            resourceList.add(r);
        }
        return toJson(JsonRpcMessage.success(id, Map.of("resources", resourceList)));
    }

    private String handleListPrompts(String id) {
        List<Map<String, Object>> prompts = new ArrayList<>();
        Map<String, Object> prompt1 = new LinkedHashMap<>();
        prompt1.put("name", "analyze_data");
        prompt1.put("description", "analyze data and generate report");
        prompt1.put("arguments", List.of(
            Map.of("name", "data", "description", "data to analyze", "required", true)
        ));
        prompts.add(prompt1);
        return toJson(JsonRpcMessage.success(id, Map.of("prompts", prompts)));
    }

    public String nextId() {
        return String.valueOf(++msgId);
    }

    private String toJson(Object obj) {
        return JsonUtils.toJson(obj);
    }
}
