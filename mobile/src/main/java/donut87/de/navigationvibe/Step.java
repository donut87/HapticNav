package donut87.de.navigationvibe;

import android.location.Location;
import android.location.LocationManager;

import java.util.Vector;

/**
 * Created by nrj on 13/06/15.
 */
public class Step {

    public String instruction = "";
    public Location location = null;
    public int currentVibrationProgress = -1;

    public Step(String instruction, double lat, double lng) {
        this.instruction = instruction;
        this.location = new Location(LocationManager.GPS_PROVIDER);
        this.location.setLatitude(lat);
        this.location.setLongitude(lng);
    }

    public String direction() {
        if (this.instruction.contains("<b>left</b>")) {
            return "left";
        }
        else if (this.instruction.contains("<b>right</b>")) {
            return "right";
        }
        return "stop";
    }
}
