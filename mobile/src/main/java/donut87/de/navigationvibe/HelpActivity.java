package donut87.de.navigationvibe;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class HelpActivity extends ActionBarActivity {

    private String nodeId = null;
    private Thread retrievingNodes;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        this.textView = (TextView) findViewById(R.id.this_is_text);
        retrieveDeviceNode();
        (new Thread(new Runnable() {
            @Override
            public void run() {
                displayStuff();
            }
        })).start();

    }

    private void displayStuff() {
        final Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    retrievingNodes.join(2000);
                    setText("This is 'left'");
                    sendMessage("left", "0.0");
                    Thread.sleep(3500);
                    sendMessage("stop", "");
                    Thread.sleep(1000);
                    setText("This is 'right'");
                    sendMessage("right", "0.0");
                    Thread.sleep(3500);
                    sendMessage("stop", "");
                    setText("Enjoy you ride");
                    Thread.sleep(1500);
                    Intent old = getIntent();
                    String activity = old.getStringExtra("Activity");
                    Intent i = null;
                    if(activity != null && activity.equals("Demo")){
                        i = new Intent(getApplicationContext(), DemoActivity.class);
                    } else {
                        i = new Intent(getApplicationContext(), NavigationActivity.class);
                    }
                    startActivity(i);
                } catch (InterruptedException e) {
                }
            }
        });
        t2.start();
    }

    private void setText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HelpActivity.this.textView.setText(text);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
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
        retrievingNodes = new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(MainActivity.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    HelpActivity.this.nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        });
        retrievingNodes.start();
    }

    private void sendMessage(final String action, final String value){
        final GoogleApiClient client = getGoogleApiClient(this);
        if(this.nodeId != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(MainActivity.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, action, value.getBytes());
                    client.disconnect();
                }
            }).start();
        }
    }
}
