package Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import AppSettings.AppSettings;
import HttpHandler.VolleyCallback;
import HttpHandler.HttpHandler;
import PubSub.Publisher;
import Utils.AppUtils;
import Utils.ServerDataUtils;

public class LocationService extends Service implements LocationListener{

    private Timer timer;
    private static String TAG = "LocationService";
    private final int INTERVAL = 30 * 1000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try{

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "No se tienen los permisos suficientes");
                return;
            }

            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000,  10, this);



        }catch (NullPointerException e){
            e.printStackTrace();
        }


        if (timer != null) {
            timer.cancel();
        } else {
            timer = new Timer();
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try{

                    Log.d(TAG, "Buscando items cercanos encontrados");

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "No se tienen permisos suficientes");
                        return;
                    }

                    LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                    Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location locationNET = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Location location;

                    if (locationGPS == null && locationNET != null) {
                        location = locationNET;
                    } else if (locationGPS != null && locationNET == null) {
                        location = locationGPS;
                    } else {
                        if (locationGPS.getTime() < locationNET.getTime()) {
                            location = locationGPS;
                        } else {
                            location = locationNET;
                        }
                    }

                    //JSONObject params = new JSONObject();

                    //params.put("latitude", ""+Double.toString(location.getLatitude()));
                    //params.put("longitude", ""+ Double.toString(location.getLongitude()));

                    try {


                        AppSettings appSettings = AppSettings.getInstance();

                        JSONObject params = new JSONObject();

                        params.put("latitude", ""+Double.toString(location.getLatitude()));
                        params.put("longitude", ""+ Double.toString(location.getLongitude()));
                        params.put("distance", Float.toString(appSettings.getCloseDistance(getApplicationContext())));
                        //params.put("distance", Float.toString(500));

                        params.put("searchChurch", Integer.toString(appSettings.searchChurchesEnabled(getApplicationContext())? 1:0));
                        params.put("searchMuseum", Integer.toString(appSettings.searchMuseumsEnabled(getApplicationContext())? 1:0));
                        params.put("searchMonument", Integer.toString(appSettings.searchMonumentsEnabled(getApplicationContext())? 1:0));
                        params.put("searchTouristOffice", Integer.toString(appSettings.searchTouristOfficeEnabled(getApplicationContext())? 1:0));
                        params.put("searchPark", Integer.toString(appSettings.searchParksEnabled(getApplicationContext())? 1:0));

                        HttpHandler.post(getApplicationContext(), ServerDataUtils.SEARCH_CLOSE_ITEMS_URL, params, new VolleyCallback() {
                            @Override
                            public void onSuccessResponse(JSONObject response) {
                                try{
                                    if(response!=null){
                                        int code = response.getInt("success");

                                        if(code!=1){
                                            Toast.makeText(getApplicationContext(), response.getString("error"),Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            Publisher.publish(getApplicationContext(), AppUtils.CLOSE_ITEMS_FOUND_ACTION, response);
                                        }
                                    }
                                }catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailResponse(String error) {
                                Log.e(TAG, error);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }, 0, INTERVAL);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "New location");
        try{

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", location.getLatitude());
            jsonObject.put("longitude", location.getLongitude());

            Publisher.publish(getApplicationContext(), AppUtils.ON_MY_LOCATION_CHANGED_ACTION, jsonObject);
            Log.d(TAG, "New location sent");

        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
