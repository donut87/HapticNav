package donut87.de.navigationvibe;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Created by nrj on 13/06/15.
 */
public class GoogleMapsClient implements ITaskHandler {

    public static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json?avoid=highways&mode=bicycling";

    private IGoogleMapsClientHandler handler;

    public GoogleMapsClient(IGoogleMapsClientHandler handler) {

        this.handler = handler;
    }

    public void getDirections(String origin, String destination) throws IOException {

        String url = this.getRequestURL(origin, destination);
        new RequestTask(this).execute(url);
    }

    public void onTaskCompleted(String result) {

        try {

            JSONArray routes = new JSONObject(result).getJSONArray("routes");

            if (routes != null && routes.length() > 0) {

                JSONObject firstRoute = (JSONObject) routes.get(0);
                JSONArray legs = firstRoute.getJSONArray("legs");

                if (legs != null && legs.length() > 0) {

                    JSONObject firstLeg = (JSONObject) legs.get(0);
                    JSONArray steps = firstLeg.getJSONArray("steps");
                    ArrayList<Step> retSteps = new ArrayList<Step>();

                    for (int i = 0; i < steps.length(); i++) {

                        JSONObject s = steps.getJSONObject(i);
                        JSONObject coord = s.getJSONObject("end_location");
                        String instruction = s.getString("html_instructions");
                        retSteps.add(new Step(instruction, coord.getDouble("lat"), coord.getDouble("lng")));
                    }

                    handler.onReceiveDirections(retSteps, null);
                }
                else {

                    handler.onReceiveDirections(null, new Error("No route found."));
                }
            }
            else {

                handler.onReceiveDirections(null, new Error("No route found."));
            }
        }
        catch (JSONException e) {

            handler.onReceiveDirections(null, new Error(e.getMessage()));
        }
    }

    private String getRequestURL(String origin, String destination) {

        String str = API_URL;
        str = str.concat("&origin=").concat(URLEncoder.encode(origin));
        str = str.concat("&destination=").concat(URLEncoder.encode(destination));
        return str;
    }
}
