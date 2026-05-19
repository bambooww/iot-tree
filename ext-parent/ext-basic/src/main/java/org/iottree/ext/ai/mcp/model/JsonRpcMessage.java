package org.iottree.ext.ai.mcp.model;

import java.util.Map;

public class JsonRpcMessage {
    public String jsonrpc = "2.0";
    public String id;
    public String method;
    public Map<String, Object> params;
    public Object result;
    public JsonRpcError error;

    public static JsonRpcMessage request(String id, String method, Map<String, Object> params) {
        JsonRpcMessage msg = new JsonRpcMessage();
        msg.id = id;
        msg.method = method;
        msg.params = params;
        return msg;
    }

    public static JsonRpcMessage success(String id, Object result) {
        JsonRpcMessage msg = new JsonRpcMessage();
        msg.id = id;
        msg.result = result;
        return msg;
    }

    public static JsonRpcMessage error(String id, int code, String message) {
        JsonRpcMessage msg = new JsonRpcMessage();
        msg.id = id;
        msg.error = new JsonRpcError(code, message);
        return msg;
    }

    public static class JsonRpcError {
        public int code;
        public String message;
        public JsonRpcError(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
