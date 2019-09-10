package Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import PubSub.Publisher;

public class AppUtils  {
    public final static String PREFERENCES_FILE = "PREF_FILE";
    public final static String CLOSE_ITEMS_FOUND_ACTION = "CLOSE_ITEMS_FOUND_ACTION";
    public final static String ON_MY_LOCATION_CHANGED_ACTION = "ON_MY_LOCATION_CHANGED_ACTION";
    public final static String LOCATION_PERMISSION_GRANTED_ACTION = "LOCATION_PERMISSION_GRANTED_ACTION";
    public final static String ON_MY_SUPPORT_MAP_FRAGMENT_TOUCHED = "ON_MY_SUPPORT_MAP_FRAGMENT_TOUCHED";
    public final static String ON_NEW_ROUTE_CALCULATED = "ON_NEW_ROUTE_CALCULATED";
    public final static String ON_SEARCH_FAVOURITE_ITEM = "ON_SEARCH_FAVOURITE_ITEM";

    public final static String ITEM = "ITEM";

    public final static int LOCATION_REQUEST_CODE = 0;

    public static boolean checkPermissionsGranted(Context context){

        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);

    }

    public static void requestPermissions(Activity activity){

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

}
