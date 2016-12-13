package td.quang.alarmmanager.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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


    public final static String REMAIN = "ALARM_MANAGER_REMAIN";
    /*
    báo xong
     */
    public final static String FINISH = "ALARM_MANAGER_FINISH";


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();


            if (action.equalsIgnoreCase(REMAIN)) {
                playing = false;
                Bundle bundle = intent.getExtras();
                Alarm alarm = bundle.getParcelable("data");
                myDatabase.setRemain(alarm.getId(), true);
                myDatabase.setRemainTime(alarm.getId(), alarm.getrH(), alarm.getrM());
            }
            if (action.equalsIgnoreCase(FINISH)) {
                playing = false;
                Bundle bundle = intent.getExtras();
                Alarm alarm = bundle.getParcelable("data");
                myDatabase.setRemain(alarm.getId(), false);
                if (alarm.getRepeat() == 0) {
                    myDatabase.onoff(alarm.getId(), false);
                }
            }
        }
    };
    private PowerManager.WakeLock wl;


    @Override
    public void onCreate() {
        super.onCreate();
        myDatabase = new MyDatabase(getApplicationContext());
        myDatabase.open();
        IntentFilter filter = new IntentFilter();
        filter.addAction(FINISH);
        filter.addAction(REMAIN);
        registerReceiver(receiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TAGG", "chay vao day");
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    ArrayList<Alarm> alarms = myDatabase.getAllWaiting();
                    Calendar calendar = Calendar.getInstance();
                    int d = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                    int h = calendar.get(Calendar.HOUR_OF_DAY);
                    int m = calendar.get(Calendar.MINUTE);
                    for (int i = 0; i < alarms.size(); i++) {
                        Alarm alarm = alarms.get(i);
                        //nếu báo thức đã tắt
                        if (alarm.getStatus() == 0) continue;
                        //nếu có báo lại
                        if (alarm.getRemain() == 1) {
                            if (alarm.getrH() != h) continue;
                            if (alarm.getrM() != m) continue;
                            if (playing) continue;
                            if (alarm.getSkip() == 1) continue;
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
                            if (alarm.getDay().indexOf(d + "") == -1) continue;
                            if (alarm.getH() != h) continue;
                            if (alarm.getM() != m) continue;
                            if (playing) continue;
                            if (alarm.getSkip() == 1) continue;
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

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                }

            }
        };
        thread.start();
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
        super.onDestroy();
        myDatabase.close();
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



