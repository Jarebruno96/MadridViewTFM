package AccessTokens;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AccessToken {

    private static final String TAG = "AccessToken";

    private String accessToken = "";
    private int tokenSecExpiration = 0;
    private Date createdAt;

    public AccessToken(String accessToken, Date updatedAt, int tokenSecExpiration){
        this.accessToken = accessToken;
        this.createdAt = updatedAt;
        this.tokenSecExpiration = tokenSecExpiration;
    }

    public boolean isValid(){

        if(accessToken.equals("")){
            Log.d(TAG, "Token vacio");
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Log.d(TAG, new Date().toString());
        Log.d(TAG, createdAt.toString());

        if(tokenSecExpiration < ((new Date().getTime() - createdAt.getTime())/1000)){
            Log.d(TAG, "Token expirado");

            return false;
        }
        Log.d(TAG, "Token valido");
        return true;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getTokenSecExpiration() {
        return tokenSecExpiration;
    }

    public void setTokenSecExpiration(int tokenSecExpiration) {
        this.tokenSecExpiration = tokenSecExpiration;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
