package donut87.de.navigationvibe;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by nrj on 13/06/15.
 */
public class GoogleMapsClient {

    public static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json?avoid=highways&mode=bicycling";

    public GoogleMapsClient() {
    }

    public void getDirections(String origin, String destination) throws IOException {

        String url = this.getRequestURL(origin, destination);
        HttpClient client= new DefaultHttpClient();
        HttpContext context= new BasicHttpContext();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request, context);
        HttpEntity entity = response.getEntity();

        InputStream inputStream = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        String result = sb.toString();
        Log.i("foo", result);
    }

    private String getRequestURL(String origin, String destination) {

        String str = API_URL;
        str = str.concat("&origin=").concat(URLEncoder.encode(origin));
        str = str.concat("&destination=").concat(URLEncoder.encode(destination));
        return str;
    }
}
