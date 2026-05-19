package org.iottree.ext.ai.mcp.tools;

import org.iottree.ext.ai.mcp.Tool;
import org.iottree.ext.ai.mcp.model.ToolDefinition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalculatorTool implements Tool {

    @Override
    public ToolDefinition getDefinition() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("a", Map.of("type", "number", "description", "first number"));
        properties.put("b", Map.of("type", "number", "description", "second number"));
        properties.put("op", Map.of(
            "type", "string",
            "description", "operator (add/sub/mul/div)",
            "enum", List.of("add", "sub", "mul", "div")
        ));

        return new ToolDefinition(
            "calculator",
            "perform arithmetic operations (add/sub/mul/div)",
            new ToolDefinition.InputSchema(properties, List.of("a", "b", "op"))
        );
    }

    @Override
    public String execute(Map<String, Object> args) throws Exception {
        double a = ((Number) args.get("a")).doubleValue();
        double b = ((Number) args.get("b")).doubleValue();
        String op = (String) args.get("op");

        double result;
        switch (op) {
            case "add": result = a + b; break;
            case "sub": result = a - b; break;
            case "mul": result = a * b; break;
            case "div":
                if (b == 0) throw new IllegalArgumentException("divisor cannot be zero");
                result = a / b;
                break;
            default:
                throw new IllegalArgumentException("unsupported operator: " + op);
        }
        return String.format("%s %s %s = %s", fmt(a), op, fmt(b), fmt(result));
    }

    private String fmt(double v) {
        return v == (long) v ? String.valueOf((long) v) : String.valueOf(v);
    }
}
