package PubSub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractSet;
import java.util.concurrent.CopyOnWriteArraySet;

import ListenerInterfaces.OnLocationPermissionGranted;
import ListenerInterfaces.OnMySupportMapFragmentTouched;
import ListenerInterfaces.OnItemEventListener;
import ListenerInterfaces.OnMyLocationChangedEventListener;
import ListenerInterfaces.OnNewRouteCalculated;
import ListenerInterfaces.OnSearchFavouriteItemListener;
import Utils.AppUtils;

public class EventNotifier extends BroadcastReceiver
        implements OnItemEventListener,
        OnMyLocationChangedEventListener,
        OnMySupportMapFragmentTouched,
        OnLocationPermissionGranted,
        OnNewRouteCalculated,
        OnSearchFavouriteItemListener {

    private static AbstractSet<OnItemEventListener> onItemEventListeners = new CopyOnWriteArraySet<>();
    private static AbstractSet<OnMyLocationChangedEventListener> onMyLocationChangedEventListeners = new CopyOnWriteArraySet<>();
    private static AbstractSet<OnMySupportMapFragmentTouched> onFollowLocationChangedListeners = new CopyOnWriteArraySet<>();
    private static AbstractSet<OnLocationPermissionGranted> onLocationPermissionGrantedListeners = new CopyOnWriteArraySet<>();
    private static AbstractSet<OnNewRouteCalculated> onNewRouteCalculatedListeners = new CopyOnWriteArraySet<>();
    private static AbstractSet<OnSearchFavouriteItemListener> onSearchFavouriteItemListeners = new CopyOnWriteArraySet<>();

    private static final String TAG = "EventNotifier";
    
    @Override
    public void onReceive(Context context, Intent intent) {

        try{
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();

            Log.d(TAG, "Action received: "+action);

            switch (action){

                case AppUtils.CLOSE_ITEMS_FOUND_ACTION:
                    try {
                        String textItem = bundle.getString(AppUtils.ITEM);
                        JSONObject item = new JSONObject(textItem);
                        Log.d(TAG, textItem);
                        onItemPublished(item);
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                break;

                case AppUtils.ON_MY_LOCATION_CHANGED_ACTION:

                    try {
                        String textItem = bundle.getString(AppUtils.ITEM);
                        JSONObject item = new JSONObject(textItem);
                        Log.d(TAG, textItem);
                        onMyLocationPublished(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                break;

                case AppUtils.ON_MY_SUPPORT_MAP_FRAGMENT_TOUCHED:
                    try {
                        String textItem = bundle.getString(AppUtils.ITEM);
                        JSONObject item = new JSONObject(textItem);
                        Log.d(TAG, textItem);
                        onMySupportMapFragmentTouched(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                break;

                case AppUtils.LOCATION_PERMISSION_GRANTED_ACTION:
                    try {
                        String textItem = bundle.getString(AppUtils.ITEM);
                        JSONObject item = new JSONObject(textItem);
                        Log.d(TAG, textItem);
                        onLocationPermissionGranted(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                break;

                case AppUtils.ON_NEW_ROUTE_CALCULATED:
                    try {
                        String textItem = bundle.getString(AppUtils.ITEM);
                        JSONObject item = new JSONObject(textItem);
                        Log.d(TAG, textItem);
                        onNewRouteCalculated(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                break;

                case AppUtils.ON_SEARCH_FAVOURITE_ITEM:
                    try {
                        String textItem = bundle.getString(AppUtils.ITEM);
                        JSONObject item = new JSONObject(textItem);
                        Log.d(TAG, textItem);
                        onSearchFavouriteItem(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }

    public static void subscribe(Object listener, String action){

        switch (action){
            case AppUtils.CLOSE_ITEMS_FOUND_ACTION:
                if (listener instanceof OnItemEventListener){
                    Log.d(TAG, "New On OnItem Subscriber");
                    onItemEventListeners.add((OnItemEventListener) listener);
                }
            break;
            case AppUtils.ON_MY_LOCATION_CHANGED_ACTION:
                if (listener instanceof OnMyLocationChangedEventListener){
                    Log.d(TAG, "New OnMyLocationChanged Subscriber");
                    onMyLocationChangedEventListeners.add((OnMyLocationChangedEventListener) listener);
                }
            break;
            case AppUtils.ON_MY_SUPPORT_MAP_FRAGMENT_TOUCHED:
                if (listener instanceof OnMySupportMapFragmentTouched){
                    Log.d(TAG, "New OnMyLocationChanged Subscriber");
                    onFollowLocationChangedListeners.add((OnMySupportMapFragmentTouched) listener);
                }
            break;
            case AppUtils.LOCATION_PERMISSION_GRANTED_ACTION:
                if (listener instanceof OnLocationPermissionGranted){
                    Log.d(TAG, "New OnMyLocationChanged Subscriber");
                    onLocationPermissionGrantedListeners.add((OnLocationPermissionGranted) listener);
                }
            break;
            case AppUtils.ON_NEW_ROUTE_CALCULATED:
                if (listener instanceof OnNewRouteCalculated){
                    Log.d(TAG, "New OnMyLocationChanged Subscriber");
                    onNewRouteCalculatedListeners.add((OnNewRouteCalculated) listener);
                }
            break;
            case AppUtils.ON_SEARCH_FAVOURITE_ITEM:
                if (listener instanceof OnSearchFavouriteItemListener){
                    Log.d(TAG, "New OnMyLocationChanged Subscriber");
                    onSearchFavouriteItemListeners.add((OnSearchFavouriteItemListener) listener);
                }
            break;
        }
    }

    public static void unSubscribe(Object listener, String action){

        switch (action){
            case AppUtils.CLOSE_ITEMS_FOUND_ACTION:
                onItemEventListeners.remove(listener);
                break;
            case AppUtils.ON_MY_LOCATION_CHANGED_ACTION:
                onMyLocationChangedEventListeners.remove(listener);
                break;
            case AppUtils.ON_MY_SUPPORT_MAP_FRAGMENT_TOUCHED:
                onFollowLocationChangedListeners.remove(listener);
                break;
            case AppUtils.LOCATION_PERMISSION_GRANTED_ACTION:
                onLocationPermissionGrantedListeners.remove(listener);
                break;
            case AppUtils.ON_NEW_ROUTE_CALCULATED:
                onNewRouteCalculatedListeners.remove(listener);
            break;
            case AppUtils.ON_SEARCH_FAVOURITE_ITEM:
                onSearchFavouriteItemListeners.remove(listener);
                break;

        }
    }

    @Override
    public void onMyLocationPublished(JSONObject location) {
        Log.d(TAG, "Trying to send new location");
        for (OnMyLocationChangedEventListener listener : onMyLocationChangedEventListeners) {
            Log.d(TAG, "Sending new location to subscribers");
            listener.onMyLocationPublished(location);
        }
    }

    @Override
    public void onItemPublished(JSONObject item) {
        for (OnItemEventListener listener : onItemEventListeners) {
            listener.onItemPublished(item);
        }
    }

    @Override
    public void onMySupportMapFragmentTouched(JSONObject item) {
        for(OnMySupportMapFragmentTouched listener: onFollowLocationChangedListeners){
            listener.onMySupportMapFragmentTouched(item);
        }
    }

    @Override
    public void onLocationPermissionGranted(JSONObject item) {
        for(OnLocationPermissionGranted listener: onLocationPermissionGrantedListeners){
            listener.onLocationPermissionGranted(item);
        }
    }

    @Override
    public void onNewRouteCalculated(JSONObject item) {
        for(OnNewRouteCalculated listener: onNewRouteCalculatedListeners){
            listener.onNewRouteCalculated(item);
        }
    }

    @Override
    public void onSearchFavouriteItem(JSONObject item) {
        for(OnSearchFavouriteItemListener listener: onSearchFavouriteItemListeners){
            listener.onSearchFavouriteItem(item);
        }
    }
}
