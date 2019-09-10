package DirectionManager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.juan.madridview.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import AccessTokens.AccessToken;
import HttpHandler.HttpHandler;
import HttpHandler.VolleyCallback;
import Item.ItemTag;
import PubSub.Publisher;
import Utils.AppUtils;
import Utils.ServerDataUtils;

public class DirectionManager {


    private static final String TAG = "DirectionManager";

    private Context context;

    private static AccessToken emtAccessToken = null;
    private final String email = "juanbare@ucm.es";
    private final String password = "MadridView1234";
    private static Handler handler;
    private static int seconds = 1;
    private static Runnable renovateAccessTokenRunnable;

    public DirectionManager(Context context){

        this.context = context;

        if(emtAccessToken==null){
            handler = new Handler();
            renovateAccessTokenRunnable = new Runnable() {
                @Override
                public void run() {
                    generateAccessToken();
                }
            };
            generateAccessToken();
        }

    }

    private void generateAccessToken(){

        HashMap header = new HashMap();
        header.put("email", email);
        header.put("password", password);


        HttpHandler.getWithCustomHeader(context, ServerDataUtils.EMT_LOGIN_URL, null, header, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) {

                Log.d(TAG, response.toString());

                try{

                    JSONObject data = response.getJSONArray("data").getJSONObject(0);

                    String token = data.getString("accessToken");
                    int tokenSecExpiration = seconds = data.getInt("tokenSecExpiration");

                    emtAccessToken = new AccessToken(token, new Date(), tokenSecExpiration);
                    Log.d(TAG, "New access token");

                    handler.postDelayed(renovateAccessTokenRunnable, seconds * 1000);


                }catch(JSONException e){
                    e.printStackTrace();
                    emtAccessToken = null;
                }
            }

            @Override
            public void onFailResponse(String error) {
                if(error!=null)
                    Log.d(TAG, error);
                emtAccessToken = null;
                generateAccessToken();

            }
        });
    }

    public void getDirection(LatLng origin, LatLng destination, final ItemTag tag){

        if(emtAccessToken!=null && emtAccessToken.isValid()){

            Toast.makeText(context,  context.getResources().getString(R.string.searchingRoute), Toast.LENGTH_LONG).show();

            JSONObject params = new JSONObject();
            try {
                params.put("routeType", "P");
                params.put("itinerary", true);
                params.put("allowBus", true);
                params.put("culture", "en");
                params.put("coordinateYFrom", origin.latitude);
                params.put("coordinateXFrom", origin.longitude);
                params.put("coordinateYTo", destination.latitude);
                params.put("coordinateXTo", destination.longitude);

                Log.d(TAG, params.toString());

                HashMap<String, String> header = new HashMap<>();
                header.put("accessToken", emtAccessToken.getAccessToken());

                HttpHandler.postWithCustomHeader(context, ServerDataUtils.EMT_TRAVEL_PLAN_URL, params, header, new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            response.getJSONObject("data").put("tag",tag.toJSON());
                            Publisher.publish(context, AppUtils.ON_NEW_ROUTE_CALCULATED, response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailResponse(String error) {
                        Log.d(TAG, error);
                        Toast.makeText(context,  context.getResources().getString(R.string.tryAgain), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static AccessToken getAccessToken(){
        return emtAccessToken;
    }




}
