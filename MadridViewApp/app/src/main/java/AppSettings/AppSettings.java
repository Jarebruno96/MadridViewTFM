package AppSettings;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.juan.madridview.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import Utils.AppUtils;

public class AppSettings {

    private final boolean defaultSearchMuseums = true;
    private final boolean defaultSearchMonuments = true;
    private final boolean defaultSearchChurches = true;
    private final boolean defaultSearchParks = true;
    private final boolean defaultSearchTouristOffice = true;

    private final float defaultParkMarkerColor = BitmapDescriptorFactory.HUE_GREEN;
    private final float defaultMuseumMarkerColor = BitmapDescriptorFactory.HUE_ORANGE;
    private final float defaultMonumentMarkerColor = BitmapDescriptorFactory.HUE_RED;
    private final float defaultTouristOfficeMarkerColor = BitmapDescriptorFactory.HUE_BLUE;
    private final float defaultChurchMarkerColor = BitmapDescriptorFactory.HUE_YELLOW;
    private final float defaultDestinationMarkerColor = BitmapDescriptorFactory.HUE_MAGENTA;

    private final float defaultCloseDistance = 1500;

    private static final AppSettings appSettings = new AppSettings();

    public static AppSettings getInstance() {
        return appSettings;
    }

    private AppSettings() {
    }

    public boolean searchMuseumsEnabled(Context context){
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.searchMuseums), defaultSearchMuseums);
    }

    public void setSearchMuseums(Context context, boolean enabled){
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(context.getResources().getString(R.string.searchMuseums), enabled);
        editor.apply();
    }

    public boolean searchMonumentsEnabled(Context context){
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.searchMonuments), defaultSearchMonuments);
    }

    public void setSearchMonuments(Context context, boolean enabled){
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(context.getResources().getString(R.string.searchMonuments), enabled);
        editor.apply();
    }

    public boolean searchChurchesEnabled(Context context){
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.searchChurches), defaultSearchChurches);
    }

    public void setSearchChurches(Context context, boolean enabled){
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(context.getResources().getString(R.string.searchChurches), enabled);
        editor.apply();
    }

    public boolean searchParksEnabled(Context context){
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.searchParks), defaultSearchParks);
    }

    public void setSearchParks(Context context, boolean enabled){
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(context.getResources().getString(R.string.searchParks), enabled);
        editor.apply();
    }

    public boolean searchTouristOfficeEnabled(Context context){
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.searchTouristOffice), defaultSearchTouristOffice);
    }

    public void setSearchTouristOffice(Context context, boolean enabled){
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(context.getResources().getString(R.string.searchTouristOffice), enabled);
        editor.apply();
    }

    public float getParkMarkerColor(Context context) {
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(context.getResources().getString(R.string.parkMarkerColor), defaultParkMarkerColor);
    }

    public void setParkMarkerColor(Context context, float parkMarkerColor) {

        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putFloat(context.getResources().getString(R.string.parkMarkerColor), parkMarkerColor);
        editor.apply();
    }

    public float getMuseumMarkerColor(Context context) {
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(context.getResources().getString(R.string.museumMarkerColor), defaultMuseumMarkerColor);
    }

    public void setMuseumMarkerColor(Context context, float museumMarkerColor) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putFloat(context.getResources().getString(R.string.museumMarkerColor), museumMarkerColor);
        editor.apply();
    }

    public float getTouristOfficeMarkerColor(Context context) {
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(context.getResources().getString(R.string.touristOfficeMarkerColor), defaultTouristOfficeMarkerColor);
    }

    public void setTouristOfficeMarkerColor(Context context, float touristOfficeMarkerColor) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putFloat(context.getResources().getString(R.string.touristOfficeMarkerColor), touristOfficeMarkerColor);
        editor.apply();
    }

    public float getMonumentMarkerColor(Context context) {
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(context.getResources().getString(R.string.monumentMarkerColor), defaultMonumentMarkerColor);
    }

    public void setMonumentMarkerColor(Context context, float monumentMarkerColor) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putFloat(context.getResources().getString(R.string.monumentMarkerColor), monumentMarkerColor);
        editor.apply();
    }

    public float getChurchMarkerColor(Context context) {
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(context.getResources().getString(R.string.churchMarkerColor), defaultChurchMarkerColor);
    }

    public void setChurchMarkerColor(Context context, float churchMarkerColor) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putFloat(context.getResources().getString(R.string.churchMarkerColor), churchMarkerColor);
        editor.apply();
    }

    public float getDestinationMarkerColor(Context context) {
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(context.getResources().getString(R.string.destinationMarkerColor), defaultDestinationMarkerColor);
    }

    public void setDestinationMarkerColor(Context context, float destinationMarkerColor) {
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putFloat(context.getResources().getString(R.string.destinationMarkerColor), destinationMarkerColor);
        editor.apply();
    }

    public void setCloseDistance(Context context, float distance){
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putFloat(context.getResources().getString(R.string.closeDistance), distance);
        editor.apply();
    }

    public float getCloseDistance(Context context){
        SharedPreferences  sharedPreferences= context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(context.getResources().getString(R.string.closeDistance), defaultCloseDistance);
    }

    public float getItemTypeMarkerColor(Context context, String type){

        if(type.equals(context.getResources().getString(R.string.parkType))){
            return getParkMarkerColor(context);
        }
        else if(type.equals(context.getResources().getString(R.string.museumType))){
            return getMuseumMarkerColor(context);
        }
        else if(type.equals(context.getResources().getString(R.string.touristOfficeType))){
            return getTouristOfficeMarkerColor(context);

        }else if(type.equals(context.getResources().getString(R.string.churchType))){
            return getChurchMarkerColor(context);

        }else if(type.equals(context.getResources().getString(R.string.monumentType))){
            return getMonumentMarkerColor(context);
        }
        else{
            return BitmapDescriptorFactory.HUE_RED;
        }
    }

}
