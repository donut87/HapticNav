package donut87.de.navigationvibe;

import java.util.ArrayList;

/**
 * Created by nrj on 13/06/15.
 */
public interface IGoogleMapsClientHandler {
    void onReceiveDirections(ArrayList<Step> steps, Error error);
}
