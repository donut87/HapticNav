package donut87.de.navigationvibe;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements IGoogleMapsClientHandler {

    private GoogleMapsClient client;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentLocation;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                textView.append("Lat " + location.getLatitude() + " Lon " + location.getLongitude() + "\n");
                Log.d("Location", "Lat " + location.getLatitude() + " Lon " + location.getLongitude());
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, locationListener);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation != null) {
            textView.setText("Lat " + currentLocation.getLatitude() + " Lon " + currentLocation.getLongitude() + "\n");
            Log.d("Location", "Cur Lat " + currentLocation.getLatitude() + "Cur Lon " + currentLocation.getLongitude());
        }
        else {
            Log.d("Location", "Current Location Unknown");
        }
    }

    public void handleNavigationSearchButton() {

        if (client == null) {

            client = new GoogleMapsClient(this);
        }
        try {

            client.getDirections("Hasenheide, Berlin, Germany", "Reuterstrasse, Berlin, Germany");
        }
        catch (IOException e) {

            Log.e("Error", e.getMessage());
        }
    }

    public void onReceiveDirections(ArrayList<Step> steps, Error error) {

        if (error == null) {


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
}
