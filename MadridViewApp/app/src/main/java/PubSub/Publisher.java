package PubSub;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import Utils.AppUtils;

public class Publisher {

    private static final String TAG = "Publisher";
    public static void publish(Context context, String action, JSONObject item){

        Intent intent = new Intent();
        intent.setAction(action);

        if(item != null){
            intent.putExtra(AppUtils.ITEM, item.toString());
        }
        context.sendBroadcast(intent);
    }
}
