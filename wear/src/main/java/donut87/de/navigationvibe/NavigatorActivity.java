package donut87.de.navigationvibe;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NavigatorActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final Vibration vibration = new Vibration(v);

        final Button round_button = (Button) findViewById(R.id.button);
        final Button rect_button = (Button) findViewById(R.id.button_click);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibration.vibrate(0.1, Vibration.side.LEFT);
            }
        };
        round_button.setOnClickListener(listener);

    }
}
