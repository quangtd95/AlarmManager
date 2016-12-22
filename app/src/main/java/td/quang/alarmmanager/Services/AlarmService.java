package td.quang.alarmmanager.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import td.quang.alarmmanager.Activitys.ShowAlarmActivity;
import td.quang.alarmmanager.Database.MyDatabase;
import td.quang.alarmmanager.Models.Alarm;

/**
 * Created by Quang_TD on 12/11/2016.
 */
public class AlarmService extends Service {
    private MyDatabase myDatabase;
    private boolean playing = false;
    private Handler handler;

    public final static String UPDATE = "ALARM_MANAGER_EDIT";
    public final static String REMAIN = "ALARM_MANAGER_REMAIN";
    public final static String FINISH = "ALARM_MANAGER_FINISH";


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(UPDATE)){
                Log.e("TAGG","UPDATE alarm");
                setAlarm();
            }
            if (action.equalsIgnoreCase(REMAIN)) {
                playing = false;
                Bundle bundle = intent.getExtras();
                Alarm alarm = bundle.getParcelable("data");
                myDatabase.setRemain(alarm.getId(), true);
                myDatabase.setRemainTime(alarm.getId(), alarm.getrH(), alarm.getrM());
                setAlarm();
            }
            if (action.equalsIgnoreCase(FINISH)) {
                playing = false;
                Bundle bundle = intent.getExtras();
                Alarm alarm = bundle.getParcelable("data");
                myDatabase.setRemain(alarm.getId(), false);
                if (alarm.getRepeat() == 0) {
                    myDatabase.onoff(alarm.getId(), false);
                }
                setAlarm();
            }
        }
    };
    private PowerManager.WakeLock wl;

    public void setAlarm(){
        handler.removeCallbacksAndMessages(null);
        final Alarm alarm = myDatabase.getNearestAlarm();
        if (alarm == null) {
            Log.e("TAGG","khong co tac vu");
            return;
        }
        final long timeMin = alarm.getTimeMin() - System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //nếu báo thức đã tắt
                if (alarm.getStatus() == 0) return;
                //nếu có báo lại
                if (alarm.getRemain() == 1) {
                    if (playing) return;
                    if (alarm.getSkip() == 1) return;
                    alarm.setSkip(1);
                    myDatabase.update(alarm);
                    new ThreadSkip(myDatabase, alarm.getId()).start();
                    playing = true;
                    Intent intent1 = new Intent(AlarmService.this, ShowAlarmActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data", alarm);
                    intent1.putExtras(bundle);
                    startActivity(intent1);

                } else {
                    if (playing) return;
                    if (alarm.getSkip() == 1) return;
                    //không phát lại trong 1p.
                    alarm.setSkip(1);
                    myDatabase.update(alarm);
                    new ThreadSkip(myDatabase, alarm.getId()).start();
                    playing = true;
                        /*
                        start show alarm activity
                         */
                    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                    wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
                    wl.acquire();
                    Intent intent1 = new Intent(AlarmService.this, ShowAlarmActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data", alarm);
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                }
            }
        },timeMin);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        myDatabase = new MyDatabase(getApplicationContext());
        myDatabase.open();
        handler = new Handler();
        IntentFilter filter = new IntentFilter();
        filter.addAction(FINISH);
        filter.addAction(REMAIN);
        filter.addAction(UPDATE);
        registerReceiver(receiver, filter);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setAlarm();
        Log.e("TAGG","on run");
        return START_STICKY;

    }

    /*  public void addAlarm(Alarm alarm){
          alarms.add(alarm);
      }
      public void deleteAlarm(Alarm alarm){
          for (int i = 0 ; i < alarms.size();i++){
              if (alarms.get(i).getId() == alarm.getId()){
                  alarms.remove(i);
                  break;
              }
          }

      }
      public void updateAlarm(Alarm alarm){
          for (int i = 0 ; i < alarms.size() ; i++){
              if (alarms.get(i).getId() == alarm.getId()){
                  alarms.remove(i);
                  break;
              }
          }
          alarms.add(alarm);
      }
  */
    @Override
    public void onDestroy() {
        Log.e("TAGG","On destroy");
        myDatabase.close();
        super.onDestroy();

    }
}

class ThreadSkip extends Thread {
    private MyDatabase myDatabase;
    private int id;

    public ThreadSkip(MyDatabase myDatabase, int id) {

        this.myDatabase = myDatabase;
        this.id = id;
    }

    @Override
    public void run() {
        super.run();
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myDatabase.setSkip(id, false);
    }
}



