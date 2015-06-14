package donut87.de.navigationvibe;

import android.content.Context;
import android.os.Vibrator;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by christian on 13.06.15.
 */
public class Vibration {

    public enum side{
        LEFT, RIGHT;
    }

    private Vibrator vibrator;

    public Vibration(Vibrator vibe){
        this.vibrator = vibe;
    }

    public void vibrate(double speed, side s){
        switch(s){
            case LEFT: vibrateLeft(speed); break;
            case RIGHT: vibrateRight(speed);
        }
    }

    private void vibrateLeft(double speed) {
        final long startDelay = 900L;
        final long finalDelay = 100L;
        final double delay = lerp(startDelay, finalDelay, speed);

        long arr[] = {0L, 100L, (long)delay};
        vibrator.vibrate(arr, 0);
    }

    private void vibrateRight(double speed){
        final long startDelay = 750L;
        final long finalDelay = 100L;
        final double delay = lerp(startDelay, finalDelay, speed);

        long arr[] = {0L, 50L, 50L, 50L, 50L, 50L, (long)delay};
        vibrator.vibrate(arr, 0);
    }

    public void stopVibrating() {

        vibrator.cancel();
    }

    private double lerp(double a, double b, double t) {
        return a + clamp(t, 0.0, 1.0) * (b - a);
    }

    private double clamp(double val, double min, double max) {
        if (val > max) {
            return max;
        }
        else if (val < min) {
            return min;
        }
        return val;
    }
}
