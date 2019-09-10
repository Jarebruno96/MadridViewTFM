package MarkerAnimations;

import com.google.android.gms.maps.model.LatLng;

public class Spherical implements LatLngInterpolaror {

    @Override
    public LatLng interpolate(float fraction, LatLng latLngOrigin, LatLng latLngEnd) {

        double originLat = Math.toRadians(latLngOrigin.latitude);
        double originLng = Math.toRadians(latLngOrigin.longitude);
        double endLat = Math.toRadians(latLngEnd.latitude);
        double endLng = Math.toRadians(latLngEnd.longitude);

        double cosOriginLat = Math.cos(originLat);
        double cosEndLat = Math.cos(endLat);

        double angle = computeAngle(originLat, originLng, endLat, endLng);
        double sinAngle = Math.sin(angle);

        if(sinAngle < 1E-6){
            return latLngOrigin;
        }

        double a = Math.sin((1-fraction) * angle) / sinAngle;
        double b = Math.sin(fraction * angle) / sinAngle;

        double x = a * cosOriginLat * Math.cos(originLng) + b * cosEndLat * Math.cos(endLng);
        double y = a * cosOriginLat * Math.sin(originLng) + b * cosEndLat * Math.sin(endLng);
        double z = a * Math.sin(originLat) + b * Math.cos(endLat);

        double lat = Math.atan2(z, Math.sqrt(x * x + y * y));
        double lng = Math.atan2(x, y);

        return new LatLng(Math.toDegrees(lat), Math.toDegrees(lng));
    }


    private double computeAngle(double originLat, double originLng, double endLat, double endLng){

        double dLat = originLat - endLat;
        double dLng = originLng - endLng;

        return 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(dLat / 2), 2) + Math.cos(originLat) * Math.cos(endLat) * Math.pow(Math.sin(dLng / 2), 2)));
    }
}
