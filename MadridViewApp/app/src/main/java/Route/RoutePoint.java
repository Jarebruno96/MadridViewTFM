package Route;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RoutePoint {

    private LatLng coordinates;
    private double duration;
    private double distance;
    private String description;


    public RoutePoint(LatLng coordinates, double duration, double distance, String description){
        this.coordinates = coordinates;
        this.duration = duration;
        this.distance = distance;
        this.description = description;
    }


    public static RoutePoint fromJSONObject(JSONObject routePt){

        try{

            JSONObject point = routePt.getJSONObject("geometry");
            JSONArray coords = point.getJSONArray("coordinates");
            LatLng coordinates = new LatLng(coords.getDouble(1), coords.getDouble(0));

            JSONObject properties = routePt.getJSONObject("properties");
            double duration = properties.getDouble("duration");
            double distance = properties.getDouble("distance");
            String description = properties.getString("description");

            return new RoutePoint(coordinates, duration, distance, description);

        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public double getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public String getDescription() {
        return description;
    }
}
