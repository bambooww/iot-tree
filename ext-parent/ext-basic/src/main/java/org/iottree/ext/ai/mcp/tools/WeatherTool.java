package org.iottree.ext.ai.mcp.tools;

import org.iottree.ext.ai.mcp.Tool;
import org.iottree.ext.ai.mcp.model.ToolDefinition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeatherTool implements Tool {

    @Override
    public ToolDefinition getDefinition() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("city", Map.of(
            "type", "string",
            "description", "city name (e.g. Beijing, Shanghai, Shenzhen)"
        ));

        return new ToolDefinition(
            "get_weather",
            "query current weather for a city",
            new ToolDefinition.InputSchema(properties, List.of("city"))
        );
    }

    @Override
    public String execute(Map<String, Object> args) {
        String city = (String) args.get("city");
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("city name cannot be empty");
        }

        Map<String, Object> weather = mockWeather(city);

        return String.format(
            "[%s] Weather\nTemperature: %s°C\nHumidity: %s%%\nCondition: %s\nWind: %s level",
            city, weather.get("temp"), weather.get("humidity"),
            weather.get("condition"), weather.get("wind")
        );
    }

    private Map<String, Object> mockWeather(String city) {
        int hash = city.hashCode();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("temp", Math.abs(hash % 40) + 5);
        data.put("humidity", Math.abs(hash % 60) + 30);
        data.put("wind", Math.abs(hash % 6) + 1);
        String[] conditions = {"Sunny", "Cloudy", "Overcast", "Light Rain", "Moderate Rain", "Heavy Snow"};
        data.put("condition", conditions[Math.abs(hash) % conditions.length]);
        return data;
    }
}
