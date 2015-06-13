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
    private ScheduledExecutorService service;
    private ScheduledFuture<?> vibratorHandler;

    public Vibration(Vibrator vibe){
        this.vibrator = vibe;
        this.service = Executors.newScheduledThreadPool(1);
        this.vibratorHandler = null;
    }

    public void vibrate(double speed, side s){
        switch(s){
            case LEFT: vibrateLeft(speed); break;
            case RIGHT: vibrateRight(speed);
        }
    }

    private void vibrateLeft(double speed){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                vibrator.vibrate(30);
            }
        };
        this.vibratorHandler = service.scheduleAtFixedRate(task,0,100, TimeUnit.MILLISECONDS);
    }

    private void vibrateRight(double speed){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                vibrator.vibrate(70);
            }
        };
        this.vibratorHandler = service.scheduleAtFixedRate(task,0,100, TimeUnit.MILLISECONDS);
    }

    public void stopVibrating(){
        this.vibrator.cancel();
        if(vibratorHandler != null)
            vibratorHandler.cancel(true);
        vibratorHandler = null;
    }
}
