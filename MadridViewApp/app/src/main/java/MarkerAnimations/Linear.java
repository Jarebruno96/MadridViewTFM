package MarkerAnimations;

import com.google.android.gms.maps.model.LatLng;

public class Linear implements LatLngInterpolaror {
    @Override
    public LatLng interpolate(float fraction, LatLng latLngOrigin, LatLng latLngEnd) {

        double lat = (latLngEnd.latitude - latLngOrigin.latitude) * fraction + latLngOrigin.latitude;
        double lng = (latLngEnd.longitude - latLngOrigin.longitude) * fraction + latLngOrigin.longitude;

        return  new LatLng(lat, lng);
    }
}
