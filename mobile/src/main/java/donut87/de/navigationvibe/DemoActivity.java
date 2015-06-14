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


public class DemoActivity extends ActionBarActivity {

    Thread retrievingNodes;
    String nodeId;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        startDemo();
    }

    private void startDemo(){
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(5000);
                    sendMessage("left", "0.0");
                    Thread.sleep(2000);
                    sendMessage("left", "0.3");
                    Thread.sleep(2000);
                    sendMessage("left", "0.6");
                    Thread.sleep(2000);
                    sendMessage("left", "1.0");
                    Thread.sleep(2000);
                    sendMessage("stop", "");
                    Thread.sleep(6000);
                    sendMessage("right", "0.0");
                    Thread.sleep(2000);
                    sendMessage("right", "0.3");
                    Thread.sleep(2000);
                    sendMessage("right", "0.6");
                    Thread.sleep(2000);
                    sendMessage("right", "1.0");
                    Thread.sleep(6000);
                    sendMessage("stop", "");
                    sendMessage("left", "0.0");
                    Thread.sleep(1500);
                    sendMessage("left", "0.3");
                    Thread.sleep(1500);
                    sendMessage("left", "0.6");
                    Thread.sleep(1500);
                    sendMessage("left", "1.0");
                    Thread.sleep(1500);
                    sendMessage("stop", "");
                    Thread.sleep(2000);
                    Intent i = new Intent(getApplicationContext(), RatingActivity.class);
                    startActivity(i);
                } catch (InterruptedException e){}
            }
        });
        t1.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo, menu);
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
                    DemoActivity.this.nodeId = nodes.get(0).getId();
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

    private void setText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DemoActivity.this.textView.setText(text);
            }
        });

    }
}
