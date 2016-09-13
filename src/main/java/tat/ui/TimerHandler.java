package tat.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by Tate on 20/07/2016.
 */
public class TimerHandler {

    private static TimerHandler instance;
    public static TimerHandler getInstance() {
        if(instance == null)
            instance = new TimerHandler();
        return instance;
    }

    private List<Timer> timerList = new ArrayList();

    public Timer newTimer() {
        Timer timer = new Timer();
        timerList.add(timer);
        return timer;
    }

    public void shutdownTimers() {
        for(Timer t : timerList) {
            t.cancel();
        }
    }
}
