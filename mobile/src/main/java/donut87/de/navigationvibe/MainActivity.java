package donut87.de.navigationvibe;

import android.content.Context;
import android.content.Intent;
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
    private ArrayList<Step>navigationSteps;;

    private TextView textView;
    private Button leftButton;
    private Button rightButton;
    private Button buttonStop;
    private Button demoButton;
    private TextView speedTextView;
    private String nodeId;

    public static final int CONNECTION_TIME_OUT_MS = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.retrieveDeviceNode();
        setContentView(R.layout.activity_main);
        sendMessage("stop", "");
        speedTextView = (TextView) findViewById(R.id.speed_label);
        demoButton = (Button) findViewById(R.id.demo_button);
        demoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getApplicationContext(), HelpActivity.class);
                        i.putExtra("Activity", "Demo");
                        startActivity(i);
                    }
                })).start();
            }
        });
        leftButton = (Button) findViewById(R.id.button_left);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double speed = Double.parseDouble(speedTextView.getText().toString());
                if (speed == -1.0 || speed >= 1.0) {
                    speed = 0.0;
                } else {
                    speed += 0.5;
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
                    speed += 0.5;
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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (navigationSteps.size() == 0) {

                    sendMessage("stop", "");
                    return;
                }

                if (currentLocation != null) {

                    Step nextStep = navigationSteps.get(0);

                    double distanceToCurrentGoalInMeters = location.distanceTo(nextStep.location);

                    addLogMessage("Distance to turn: " + Math.floor(distanceToCurrentGoalInMeters) + " M");

                    if (distanceToCurrentGoalInMeters <= 50.0 && nextStep.currentVibrationProgress < 0) {

                        nextStep.currentVibrationProgress = 0;
                        sendMessage("vibrate_" + nextStep.direction(), "0.0");
                        addLogMessage("Turn " + nextStep.direction() + " ahead.");
                    }
                    else if (distanceToCurrentGoalInMeters <= 25.0 && nextStep.currentVibrationProgress < 1) {

                        nextStep.currentVibrationProgress = 1;
                        sendMessage("vibrate_" + nextStep.direction(), "0.5");
                        addLogMessage("Turn " + nextStep.direction() + " ahead.");
                    }
                    else if (distanceToCurrentGoalInMeters <= 15.0 && nextStep.currentVibrationProgress < 2) {

                        nextStep.currentVibrationProgress = 2;
                        sendMessage("vibrate_" + nextStep.direction(), "1.0");
                        addLogMessage("Turn " + nextStep.direction() + " ahead.");
                    }
                    else if (distanceToCurrentGoalInMeters <= 5.0 && nextStep.currentVibrationProgress < 3) {

                        nextStep.currentVibrationProgress = 3;
                        sendMessage("stop", "");
                    }
                    else if (distanceToCurrentGoalInMeters <= 5.0 && nextStep.currentVibrationProgress < 4) {

                        nextStep.currentVibrationProgress = 4;
                        sendMessage("stop", "");

                        if (navigationSteps.size() > 1) {

                            navigationSteps.remove(0);

                            addLogMessage(navigationSteps.get(0).instruction);
                        }
                        else if (navigationSteps.size() == 1) {

                            navigationSteps.remove(0);

                            addLogMessage("You're at your destination!");
                        }
                    }
                }

                currentLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

                addLogMessage("Status " + provider + " " + status);
            }

            @Override
            public void onProviderEnabled(String provider) {

                addLogMessage("Enabled" + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {

                addLogMessage("Disabled" + provider);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, Criteria.ACCURACY_FINE, locationListener);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation != null) {
            addLogMessage("Your Location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
        }
        else {
            addLogMessage("Current Location Unknown");
        }
        handleNavigationSearchButton();
    }

    public void handleNavigationSearchButton() {

        if (client == null) {

            client = new GoogleMapsClient(this);
        }
        try {

            client.getDirections("Windscheidstrasse 18, Berlin, Germany", "Friedbergstraße 7, 14057 Berlin");
        }
        catch (IOException e) {

            addLogMessage("Error: " + e.getMessage());
        }
    }

    public void onReceiveDirections(ArrayList<Step> steps, Error error) {

        if (error == null) {

            addLogMessage("Route computed!");
            Step firstStep = steps.get(0);
            addLogMessage(firstStep.instruction);
            steps.remove(0);
            navigationSteps = steps;
            Step nextStep = steps.get(0);
            addLogMessage("Next goal is: " + nextStep.location.getLatitude() + ", " + nextStep.location.getLongitude());

            for (int i = 0; i < steps.size(); i++) {

                Log.i("" + i, steps.get(i).instruction);
            }
        }
        else {

            addLogMessage("Error: " + error.getMessage());
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


    private void addLogMessage(String msg) {
        // append the new string
        if (textView == null) {
            textView = (TextView) findViewById(R.id.textView);
            textView.setMovementMethod(new ScrollingMovementMethod());
        }
        textView.append(msg + "\n");
        Log.i("INFO", msg);
    }
}
