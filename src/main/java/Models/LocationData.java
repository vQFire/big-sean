package Models;

import java.util.List;
import java.util.Map;

public class LocationData {
    public List<Map<String, Object>> locations;
    public List<Map<String, Object>> roadLocations;

    public LocationData() {};

    public List<Map<String, Object>> getLocations() {
        return locations;
    }

    public void setLocations(List<Map<String, Object>> locations) {
        this.locations = locations;
    }

    public List<Map<String, Object>> getRoadLocations() {
        return roadLocations;
    }

    public void setRoadLocations(List<Map<String, Object>> roadLocations) {
        this.roadLocations = roadLocations;
    }
}
