package org.iottree.ext.ai.mcp.model;

import java.util.List;
import java.util.Map;

public class ToolDefinition {
    public String name;
    public String description;
    public InputSchema inputSchema;

    public ToolDefinition(String name, String description, InputSchema inputSchema) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
    }

    public static class InputSchema {
        public String type = "object";
        public Map<String, Object> properties;
        public List<String> required;

        public InputSchema(Map<String, Object> properties, List<String> required) {
            this.properties = properties;
            this.required = required;
        }
    }
}
