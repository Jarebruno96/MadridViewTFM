package Route;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Section {

    private double distance;
    private double duration;
    private String type;

    private ArrayList<LatLng> itinerary; //Cada una de las coordenadas por las que se pasa al hacer los pasos de route. LO QUE HAY QUE DIBUJAR
    private ArrayList<RoutePoint> route; //Cada uno de los pasos que hacer para llegar de source a destination. LO QUE HAY QUE DECIR

    private LatLng startPoint;
    private LatLng endPoint;

    private Polyline polyline;


    public Section(double distance, double duration, String type, ArrayList<LatLng> itinerary, ArrayList<RoutePoint> route, LatLng startPoint, LatLng endPoint){

        this.distance = distance;
        this.duration = duration;
        this.type = type;
        this.itinerary = itinerary;
        this.route = route;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }


    public static Section fromJSON(JSONObject section){

        try {

            double distance = section.getDouble("distance");
            double duration = section.getDouble("duration");
            String type = section.getString("type");

            JSONObject itir = section.getJSONObject("itinerary");
            JSONArray coordinates = itir.getJSONArray("coordinates");
            ArrayList<LatLng> itinerary = new ArrayList<>();

            for(int i=0;i<coordinates.length();i++){
                JSONArray coordinate = coordinates.getJSONArray(i);
                itinerary.add(new LatLng(coordinate.getDouble(1), coordinate.getDouble(0)));

            }

            JSONObject rt = section.getJSONObject("route");
            JSONArray steps = rt.getJSONArray("features");
            ArrayList<RoutePoint> route = new ArrayList<>();

            for(int i=0;i<steps.length();i++){
                JSONObject step = steps.getJSONObject(i);
                route.add(RoutePoint.fromJSONObject(step));

            }

            JSONObject source = section.getJSONObject("source");
            JSONArray srcPoint = source.getJSONObject("geometry").getJSONArray("coordinates");
            LatLng startPoint = new LatLng(srcPoint.getDouble(1), srcPoint.getDouble(0));

            JSONObject destination = section.getJSONObject("destination");
            JSONArray destinationPoint = destination.getJSONObject("geometry").getJSONArray("coordinates");
            LatLng endPoint = new LatLng(destinationPoint.getDouble(1), destinationPoint.getDouble(0));

            return new Section(distance, duration, type, itinerary, route, startPoint, endPoint);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PolylineOptions getSectionPolylineOptions(){

        PolylineOptions polylineOptions = new PolylineOptions();

        for(LatLng coordenates : itinerary){
            polylineOptions.add(coordenates);
        }
        polylineOptions.width(8f);
        polylineOptions.color(Color.BLUE);
        return polylineOptions;

    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }


    public double getDistance() {
        return distance;
    }

    public double getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }

    public ArrayList<LatLng> getItinerary() {
        return itinerary;
    }

    public ArrayList<RoutePoint> getRoute() {
        return route;
    }

    public LatLng getStartPoint() {
        return startPoint;
    }

    public LatLng getEndPoint() {
        return endPoint;
    }

    public Polyline getPolyline() {
        return polyline;
    }
}
