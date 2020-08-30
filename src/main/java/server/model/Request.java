package server.model;

import mutual.model.enums.Requester;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Request {

    private Requester requester;

    private JSONObject jsonObject;

    private boolean closeConnection = false;

    public Request(String jsonRequest) {
        JSONParser parser = new JSONParser();
        try {
            this.jsonObject = (JSONObject) parser.parse(jsonRequest);
            if ((jsonObject.get("request")).equals("close_connection")) {
                closeConnection = true;
                return;
            }
            this.requester = Requester.valueOf((String) jsonObject.get("requester"));
        }
        catch (ParseException | IllegalArgumentException |ClassCastException e) {
            throw new RuntimeException("Invalid client request");
        }
    }

    public Requester getRequester() {
        return requester;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public boolean shouldCloseConnection() {
        return closeConnection;
    }

    public void setCloseConnection(boolean closeConnection) {
        this.closeConnection = closeConnection;
    }
}
