package User;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.juan.madridview.R;

import Utils.AppUtils;

public class User {


    private static final User user = new User();


    public static User getInstance() {
        return user;
    }

    private User() {
    }

    public String getUserName(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(context.getResources().getString(R.string.userNameSharedPreferences), null);
    }

    public void setUserName(Context context, String userName){

        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putString(context.getResources().getString(R.string.userNameSharedPreferences), userName);
        editor.apply();
    }

    public String getUserDateIn(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(context.getResources().getString(R.string.dateInSharedPreferences), null);
    }

    public void setUserDateIn(Context context, String dateIn){

        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putString(context.getResources().getString(R.string.dateInSharedPreferences), dateIn);
        editor.apply();
    }

    public String getMail(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(context.getResources().getString(R.string.mailSharedPreferences), null);
    }

    public void setMail(Context context, String mail){

        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putString(context.getResources().getString(R.string.mailSharedPreferences), mail);
        editor.apply();
    }

    public int getActivated(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(context.getResources().getString(R.string.activatedSharedPreferences), -1);
    }

    public void setActivated(Context context, int activated){

        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putInt(context.getResources().getString(R.string.activatedSharedPreferences), activated);
        editor.apply();
    }

    public int getVerified(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(context.getResources().getString(R.string.verifiedSharedPreferences), -1);
    }

    public void setVerified(Context context, int verified){

        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.putInt(context.getResources().getString(R.string.verifiedSharedPreferences), verified);
        editor.apply();
    }

    public void removeUserName(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.remove(context.getResources().getString(R.string.userNameSharedPreferences));
        editor.apply();
    }

    public void removeMail(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.remove(context.getResources().getString(R.string.mailSharedPreferences));
        editor.apply();
    }

    public void removeDateIn(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.remove(context.getResources().getString(R.string.dateInSharedPreferences));
        editor.apply();
    }

    public void removeVerified(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.remove(context.getResources().getString(R.string.verifiedSharedPreferences));
        editor.apply();
    }

    public void removeActivated(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(AppUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.remove(context.getResources().getString(R.string.activatedSharedPreferences));
        editor.apply();
    }
}
