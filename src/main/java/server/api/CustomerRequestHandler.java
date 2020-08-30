package server.api;

import org.json.simple.JSONObject;

public interface CustomerRequestHandler {

    String handle(JSONObject request);
}
