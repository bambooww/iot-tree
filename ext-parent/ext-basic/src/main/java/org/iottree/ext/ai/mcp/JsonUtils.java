package org.iottree.ext.ai.mcp;

import java.lang.reflect.Field;
import java.util.*;

public class JsonUtils {

    public static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + escape((String) obj) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof Map) return mapToJson((Map<String, Object>) obj);
        if (obj instanceof List) return listToJson((List<?>) obj);
        if (obj instanceof Object[]) return listToJson(Arrays.asList((Object[]) obj));
        return beanToJson(obj);
    }

    @SuppressWarnings("unchecked")
    private static String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escape(entry.getKey())).append("\":");
            sb.append(toJson(entry.getValue()));
            first = false;
        }
        return sb.append("}").toString();
    }

    private static String listToJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : list) {
            if (!first) sb.append(",");
            sb.append(toJson(item));
            first = false;
        }
        return sb.append("]").toString();
    }

    private static String beanToJson(Object obj) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Field field : obj.getClass().getFields()) {
            if (!first) sb.append(",");
            sb.append("\"").append(field.getName()).append("\":");
            try {
                sb.append(toJson(field.get(obj)));
            } catch (Exception e) {
                sb.append("null");
            }
            first = false;
        }
        return sb.append("}").toString();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static Map<String, Object> parse(String json) {
        return new Parser(json.trim()).parseObject();
    }

    private static class Parser {
        private final String s;
        private int pos;

        Parser(String s) { this.s = s; }

        private char peek() {
            skipWs();
            return pos < s.length() ? s.charAt(pos) : '\0';
        }

        private char next() {
            skipWs();
            return s.charAt(pos++);
        }

        private void skipWs() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++;
        }

        Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            next();
            if (peek() == '}') { next(); return map; }
            while (true) {
                String key = parseString();
                next();
                map.put(key, parseValue());
                if (next() == '}') return map;
            }
        }

        private List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            next();
            if (peek() == ']') { next(); return list; }
            while (true) {
                list.add(parseValue());
                if (next() == ']') return list;
            }
        }

        private Object parseValue() {
            char c = peek();
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            if (c == '"') return parseString();
            if (c == 't' || c == 'f') return parseBoolean();
            if (c == 'n') { parseNull(); return null; }
            return parseNumber();
        }

        private String parseString() {
            next();
            StringBuilder sb = new StringBuilder();
            while (pos < s.length()) {
                char c = s.charAt(pos++);
                if (c == '"') return sb.toString();
                if (c == '\\') {
                    char next = s.charAt(pos++);
                    sb.append(next == 'n' ? '\n' : next == 'r' ? '\r' : next == 't' ? '\t' : next);
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        private Number parseNumber() {
            int start = pos;
            if (peek() == '-') pos++;
            while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) pos++;
            String num = s.substring(start, pos);
            return num.contains(".") ? Double.parseDouble(num) : Long.parseLong(num);
        }

        private boolean parseBoolean() {
            if (s.startsWith("true", pos)) { pos += 4; return true; }
            pos += 5; return false;
        }

        private void parseNull() { pos += 4; }
    }
}
