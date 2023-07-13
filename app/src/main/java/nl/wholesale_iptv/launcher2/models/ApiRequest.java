package nl.wholesale_iptv.launcher2.models;

import org.json.JSONObject;

import java.util.HashMap;

import nl.wholesale_iptv.launcher2.enums.RequestMethod;

public class ApiRequest {
    private final HashMap<String, String> headers;
    private RequestMethod method = RequestMethod.GET;
    private final String path;
    private JSONObject body;

    public ApiRequest(String path) {
        this.path = path;
        headers = new HashMap<>();
    }

    public ApiRequest setMethod(RequestMethod method) {
        this.method = method;
        return this;
    }

    public ApiRequest setHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public ApiRequest setBody(JSONObject body) {
        this.body = body;
        return this;
    }

    public JSONObject getBody() {
        return body;
    }

    public String getPath() {
        return (path.startsWith("/") ? path : ("/" + path));
    }

    public RequestMethod getMethod() {
        return method;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }
}
