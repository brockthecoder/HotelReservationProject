package server.api;

import org.json.simple.JSONObject;

public interface ManagementRequestHandler {

    String handle(JSONObject request);
}
