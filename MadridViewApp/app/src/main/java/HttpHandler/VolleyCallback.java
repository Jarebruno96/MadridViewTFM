package HttpHandler;

import org.json.JSONObject;

public interface VolleyCallback {
    void onSuccessResponse(JSONObject response);
    void onFailResponse(String error);
}
