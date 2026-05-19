package org.iottree.ext.ai.mcp;

import org.iottree.ext.ai.mcp.model.ToolDefinition;

import java.util.Map;

public interface Tool {
    ToolDefinition getDefinition();
    String execute(Map<String, Object> arguments) throws Exception;
}
