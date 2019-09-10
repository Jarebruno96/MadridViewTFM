package DirectionManager;

import com.google.android.gms.maps.model.Marker;

import Route.Route;

public class Destination {

    private Route route;
    private Marker marker;

    public Destination(Route route){
        this.route = route;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Route getRoute() {
        return route;
    }

    public Marker getMarker() {
        return marker;
    }
}
