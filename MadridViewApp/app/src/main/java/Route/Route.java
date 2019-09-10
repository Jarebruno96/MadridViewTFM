package Route;

import java.util.ArrayList;

public class Route {

    private double distance;
    private String departureTime;
    private String arrivalTime;
    private double duration;
    private ArrayList<Section> sections;
    private String destinationName;
    private String destinationImage;

    public Route(double distance, String departureTime, String arrivalTime, double duration){
        this.distance = distance;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;

        this.sections = new ArrayList<>();

    }

    public void addSection(Section section){
        this.sections.add(section);
    }

    public double getDistance() {
        return distance;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public double getDuration() {
        return duration;
    }

    public ArrayList<Section> getSections(){
        return this.sections;

    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void setDestinationImage(String destinationImage) {
        this.destinationImage = destinationImage;
    }

    public String getDestinationImage() {
        return destinationImage;
    }
}
