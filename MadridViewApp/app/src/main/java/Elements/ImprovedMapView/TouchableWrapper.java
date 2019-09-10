package Elements.ImprovedMapView;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import org.json.JSONException;
import org.json.JSONObject;

import PubSub.Publisher;
import Utils.AppUtils;

public class TouchableWrapper extends FrameLayout {

    public final String TAG = "TouchableWrapper";
    private Context context;
    private boolean sent;

    public TouchableWrapper(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if(!sent) {
                    try {
                        sent = true;
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("touched", true);
                        Publisher.publish(context, AppUtils.ON_MY_SUPPORT_MAP_FRAGMENT_TOUCHED, jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                Log.d(TAG, "No hacer nada");
                sent = false;
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
