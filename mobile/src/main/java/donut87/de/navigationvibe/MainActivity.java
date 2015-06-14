package donut87.de.navigationvibe;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.internal.constants.Capability;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity implements IGoogleMapsClientHandler {

    private GoogleMapsClient client;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentLocation;
    private Location currentGoalLocation;
    private TextView textView;
    private Button leftButton;
    private Button rightButton;
    private Button buttonStop;
    private TextView speedTextView;
    private String nodeId;

    public static final int CONNECTION_TIME_OUT_MS = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.retrieveDeviceNode();
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        speedTextView = (TextView) findViewById(R.id.speed_label);
        leftButton = (Button) findViewById(R.id.button_left);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double speed = Double.parseDouble(speedTextView.getText().toString());
                if (speed == -1.0 || speed >= 1.0) {
                    speed = 0.0;
                }
                else {
                    speed += 0.1;
                }
                sendMessage("vibrate_left", "" + speed);
                speedTextView.setText("" + speed);
            }
        });
        rightButton = (Button) findViewById(R.id.button_right);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double speed = Double.parseDouble(speedTextView.getText().toString());
                if (speed == -1.0 || speed >= 1.0) {
                    speed = 0.0;
                }
                else {
                    speed += 0.1;
                }
                sendMessage("vibrate_right", "" + speed);
                speedTextView.setText("" + speed);
            }
        });
        buttonStop = (Button) findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("stop", "");
                speedTextView.setText("-1.0");
            }
        });
        textView.setMovementMethod(new ScrollingMovementMethod());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                textView.append("Your Location: " + location.getLatitude() + ", " + location.getLongitude() + "\n");
                Log.d("Location", "Lat " + location.getLatitude() + " Lon " + location.getLongitude());

                if (currentLocation != null) {

                    double travelDiffX = currentLocation.getLatitude() - location.getLatitude();
                    double travelDiffY = currentLocation.getLongitude() - location.getLongitude();
                    Vector2 travelDiff = new Vector2(travelDiffX, travelDiffY);
                    Vector2 travelNorm = travelDiff.normalized();
                    double travelDirection = Math.toDegrees(Math.atan2(travelNorm.y, travelNorm.x));

                    double goalDiffX = currentGoalLocation.getLatitude() - location.getLatitude();
                    double goalDiffY = currentGoalLocation.getLongitude() - location.getLongitude();
                    Vector2 goalDiff = new Vector2(goalDiffX, goalDiffY);
                    Vector2 goalNorm = goalDiff.normalized();
                    double goalDirection = Math.toDegrees(Math.atan2(goalNorm.y, goalNorm.x));

//                    double differenceInDirection = Math.abs(goalDirection - travelDirection);

                    double distanceToCurrentGoalInMeters = location.distanceTo(currentGoalLocation);

                    textView.append("Distance to goal: " + distanceToCurrentGoalInMeters + " M");

                    if (distanceToCurrentGoalInMeters <= 5.0) {

                        textView.append("You made it!");
                    }
                }

                currentLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

                textView.append("Status " + provider + " " + status + "\n");
                Log.d("Status", provider + " " + status);
            }

            @Override
            public void onProviderEnabled(String provider) {

                textView.append("Enabled" + provider + "\n");
                Log.d("Enabled", provider);
            }

            @Override
            public void onProviderDisabled(String provider) {

                textView.append("Disabled" + provider + "\n");
                Log.d("Disabled", provider);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, Criteria.ACCURACY_FINE, locationListener);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation != null) {
            textView.setText("Your Location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude() + "\n");
            Log.d("Location", "Cur Lat " + currentLocation.getLatitude() + "Cur Lon " + currentLocation.getLongitude());
        }
        else {
            textView.setText("Current Location Unknown\n");
            Log.d("Location", "Current Location Unknown");
        }
        handleNavigationSearchButton();
    }

    public void handleNavigationSearchButton() {

        if (client == null) {

            client = new GoogleMapsClient(this);
        }
        try {

            client.getDirections("Windscheidstrasse 18, Berlin, Germany", "Stuttgarter Pl. 21, Berlin, Germany");
        }
        catch (IOException e) {

            Log.e("Error", e.getMessage());
        }
    }

    public void onReceiveDirections(ArrayList<Step> steps, Error error) {

        if (error == null) {

            Step firstStep = steps.get(1);

            /*

            currentGoalLocation = new Location(LocationManager.GPS_PROVIDER);
            currentGoalLocation.setLatitude(firstStep.location.coordinate.x);
            currentGoalLocation.setLongitude(firstStep.coordinate.y);

            */

            textView.append("Next goal is: " + currentGoalLocation.getLatitude() + ", " + currentGoalLocation.getLongitude());
        }
        else {

            Log.e("Error", error.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void retrieveDeviceNode() {
        final GoogleApiClient client = getGoogleApiClient(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    MainActivity.this.nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    private void sendMessage(final String action, final String value){
        final GoogleApiClient client = getGoogleApiClient(this);
        if(this.nodeId != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, action, value.getBytes());
                    client.disconnect();
                }
            }).start();
        }
    }
}


        /*
        52.50599976, 13.29747649
52.50600543, 13.29746742
52.50593616, 13.29752356
525059909  , 13.29751055
5250600051 , 13.29751699
52.5059302 , 13.29776906
52.50593219, 13.29793243
52.50539013, 13.29312255
5250539354 , 13.29320313
5250590696 , 13.29322133
52.5059113 , 13.29325332
52.50591503, 13.29330779
5250590707 , 13.29342336
5250534534 , 13.29351452
5250532337 , 13.29356315
5250573504 , 13.29356253
5250574037 , 13.29357316
5250573914 , 13.29363529
5250576956 , 13.29333237
5250534092 , 13.29394637
5250539293 , 13.29900343
5250592395 , 13.29909031
5250597676 , 13.29912744
5250604436 , 13.29913107
5250606705 , 13.2992724
5250609494 , 13.29934266
52.50610521, 13.2993447
52.50616797, 13.29937394
52.50615455, 13.29940432
52.50616326, 13.2994146
52.50609715, 13.29951733
5250606379 , 13.29951359
         */
