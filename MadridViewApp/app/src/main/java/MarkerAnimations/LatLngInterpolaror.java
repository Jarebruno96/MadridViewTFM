package MarkerAnimations;

import com.google.android.gms.maps.model.LatLng;

public interface LatLngInterpolaror {

    LatLng interpolate(float fraction, LatLng latLngOrigin, LatLng latLngEnd);
}
