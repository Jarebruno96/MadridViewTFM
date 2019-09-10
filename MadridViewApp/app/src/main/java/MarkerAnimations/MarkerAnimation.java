package MarkerAnimations;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerAnimation {

    public static final String TAG = "MarkerAnimation";

    public static void moveLocationMarker(final Marker marker, final Circle circle, final LatLng endLatLng, final LatLngInterpolaror latLngInterpolaror) {

        final LatLng startLatLng = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float duration = 2000;

        handler.post(new Runnable() {
            long elapsed;
            float t, v;

            @Override
            public void run() {

                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / duration;
                v = interpolator.getInterpolation(t);

                LatLng currentLatLng = latLngInterpolaror.interpolate(v, startLatLng, endLatLng);

                marker.setPosition(currentLatLng);
                circle.setCenter(currentLatLng);

                //Log.d(TAG, "Current [Lat:"+ currentLatLng.latitude +" Lng: "+ currentLatLng.longitude+"]");
                Log.d(TAG, "Posición del marker [Lat:"+marker.getPosition().latitude +" Lng: "+ marker.getPosition().longitude+"]");
                //Log.d(TAG, "Posición del circle [Lat:"+circle.getCenter().latitude +" Lng: "+ circle.getCenter().longitude+"]");

                if(t < 1){
                    handler.postDelayed(this, 16);
                }
                else{
                    Log.d(TAG, "Fin del movimiento");
                }

            }
        });

    }
}
