package td.quang.alarmmanager.Activitys;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import td.quang.alarmmanager.Models.Alarm;
import td.quang.alarmmanager.R;
import td.quang.alarmmanager.Services.AlarmService;

/**
 * Created by Quang_TD on 12/11/2016.
 */
public class ShowAlarmActivity extends AppCompatActivity {
    private TextView txtName,txtTime;
    private Button btnSnooze,btnStop;
    private Alarm alarm;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_alarm);
        final Bundle bundle = getIntent().getExtras();
        alarm = bundle.getParcelable("data");
        addComponents();
        addEvents();
        mediaPlayer = MediaPlayer.create(this,R.raw.chapnhansuthat);
        mediaPlayer.start();
        // nếu hết bài mà vẫn chưa tắt thì báo lại
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sendBroadCastRemain();
                finish();
            }
        });

    }

    private void addEvents() {
        //nhấn báo lại thì remain.
        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBroadCastRemain();
                finish();
            }
        });
        //nếu nhấn dừng thì ngừng báo lại
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
               sendBroadCastStop();
                finish();

            }
        });
    }

    private void addComponents() {
        txtName = (TextView) findViewById(R.id.txtName);
        txtTime = (TextView) findViewById(R.id.txtTime);
        btnSnooze = (Button) findViewById(R.id.btnSnooze);
        btnStop = (Button) findViewById(R.id.btnStop);
        txtName.setText(alarm.getName());
        Calendar calendar = Calendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m  = calendar.get(Calendar.MINUTE);
        txtTime.setText(((h<10)?"0":"")+h+":"+((m<10)?"0":"")+m);
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();

    }
    private void sendBroadCastStop(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data",alarm);
        intent.putExtras(bundle);
        intent.setAction(AlarmService.FINISH);
        sendBroadcast(intent);
    }
    private void sendBroadCastRemain() {
        Intent intent = new Intent();
        intent.setAction(AlarmService.REMAIN);
        Calendar calendar =Calendar.getInstance();
        int rH = calendar.get(Calendar.HOUR_OF_DAY);
        int rM = calendar.get(Calendar.MINUTE);
        rM = rM + 5;
        if (rM >= 60) {
            rM -= 60;
            rH ++;
            if (rH >23) {
                rH = 0;
            }
        }
        alarm.setrM(rM);
        alarm.setrH(rH);
        alarm.setRemain(1);

        Bundle bundle1 = new Bundle();
        bundle1.putParcelable("data",alarm);
        intent.putExtras(bundle1);
        sendBroadcast(intent);
        Toast.makeText(ShowAlarmActivity.this, "Báo lại trong 5 phút", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

    }
}
