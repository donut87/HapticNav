package donut87.de.navigationvibe;

import android.content.Context;
import android.os.Vibrator;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by christian on 13.06.15.
 */
public class ListenerService extends WearableListenerService {
    String lastMessage = "";

    Vibration vibration;

    @Override
    public void onCreate() {
        super.onCreate();
        this.vibration = new Vibration((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if(messageEvent.getPath().equals("vibrate_left")) {
            String speedString = new String(messageEvent.getData());
            double speedValue = Double.parseDouble(speedString);
            vibration.stopVibrating();
            vibration.vibrate(speedValue, Vibration.side.LEFT);
        }
        else if (messageEvent.getPath().equals("vibrate_right")) {
            String speedString = new String(messageEvent.getData());
            double speedValue = Double.parseDouble(speedString);
            vibration.stopVibrating();
            vibration.vibrate(speedValue, Vibration.side.RIGHT);
        }
        else if (messageEvent.getPath().equals("stop")) {
            vibration.stopVibrating();
        }
    }
}
