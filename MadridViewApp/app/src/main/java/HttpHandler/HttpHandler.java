package HttpHandler;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Utils.ServerDataUtils;

public class HttpHandler {

    public static void post(Context context, String url, final HashMap<String, String> parameters,  final VolleyCallback volleyCallback){
        RequestQueue requestQueue =  Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            volleyCallback.onSuccessResponse(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            volleyCallback.onSuccessResponse(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        volleyCallback.onFailResponse(""+error.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = parameters;
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public static void postWithCustomHeader(Context context, String url, final JSONObject parameters, final HashMap<String, String> header,  final VolleyCallback volleyCallback){
        RequestQueue requestQueue =  Volley.newRequestQueue(context);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                volleyCallback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               volleyCallback.onFailResponse(error.toString());
            }
        }){
            @Override
            public Map getHeaders() throws AuthFailureError{
                return header;
            }
        };

        jsonObjectRequest.setRetryPolicy( new DefaultRetryPolicy(ServerDataUtils.EMT_DEFAULT_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    public static void getWithCustomHeader(Context context, String url, final HashMap<String, String> parameters, final HashMap<String, String> header, final VolleyCallback volleyCallback){

        RequestQueue requestQueue =  Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        volleyCallback.onSuccessResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        volleyCallback.onFailResponse(error.getMessage());
                    }
                })
        {
            @Override
            public Map getHeaders() throws AuthFailureError{
                return header;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void get(Context context, String url, final HashMap<String, String> parameters, final VolleyCallback volleyCallback){

        RequestQueue requestQueue =  Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        volleyCallback.onSuccessResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        volleyCallback.onFailResponse(error.getMessage());
                    }
                }){
            protected Map<String, String> getParams() {
                Map<String, String> params = parameters;
                return params;
            };
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void post(Context context, String url, final JSONObject parameters,  final VolleyCallback volleyCallback){
        RequestQueue requestQueue =  Volley.newRequestQueue(context);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                volleyCallback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallback.onFailResponse(""+error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String,String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        requestQueue.add(stringRequest);
    }

}
