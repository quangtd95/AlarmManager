package td.quang.alarmmanager.Activitys;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import td.quang.alarmmanager.Database.MyDatabase;
import td.quang.alarmmanager.Models.Alarm;
import td.quang.alarmmanager.R;
import td.quang.alarmmanager.Services.AlarmService;

/**
 * Created by Quang_TD on 12/11/2016.
 */
public class EditAlarmActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView txtCancel, txtEdit;
    private TextView txtRepeat, txtSound;
    private EditText txtName;
    private LinearLayout llRepeat, llSound;
    private TimePicker timePicker;
    private TextView txtTemp1, txtTemp2;
    private Alarm alarm; 
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        Bundle bundle = getIntent().getExtras();
        alarm = bundle.getParcelable("data");
        addComponents();
        addEvents();

    }

    private void addEvents() {
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtEdit.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                String name = txtName.getText().toString();
                alarm.setName(name);
                alarm.setSound(R.raw.chapnhansuthat);
                alarm.setH(timePicker.getCurrentHour());
                alarm.setM(timePicker.getCurrentMinute());
                Calendar calendar = Calendar.getInstance();
                if (alarm.getRepeat() == 0) {
                    if (calendar.getTime().getHours() > alarm.getH()) {
                        alarm.setDay((calendar.get(Calendar.DAY_OF_WEEK)+1) +"");

                    } else {
                        if (calendar.getTime().getHours() == alarm.getH()) {
                            if (calendar.getTime().getMinutes() >= alarm.getM()) {
                                alarm.setDay((calendar.get(Calendar.DAY_OF_WEEK)+1) +"");
                            }
                        }
                    }

                }

                Toast.makeText(EditAlarmActivity.this, "Thanh cong", Toast.LENGTH_SHORT).show();
                alarm.setStatus(1);
                MyDatabase.getInstance(EditAlarmActivity.this).update(alarm);
                /*Intent intent = new Intent();
                intent.setAction(AlarmService.UPDATE);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data",alarm);
                intent.putExtras(bundle);
                sendBroadcast(intent);*/
                finish();
            }
        });
        llRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupDialog();
            }
        });

    }

    private void showPopupDialog() {
        PopupMenu popupMenu = new PopupMenu(EditAlarmActivity.this, txtTemp1);
        popupMenu.getMenuInflater().inflate(R.menu.menu_repeat, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.once) {
                    alarm.setRepeat(0);
                    txtRepeat.setText("Một lần");
                }
                if (item.getItemId() == R.id.everyday) {
                    alarm.setRepeat(1);
                    alarm.setDay("0123456");
                    txtRepeat.setText("Hằng ngày");
                }
                if (item.getItemId() == R.id.someday) {
                    alarm.setRepeat(1);
                    showDialogChooseDay();
                }

                return true;
            }
        });
        popupMenu.show();
    }

    private void showDialogChooseDay() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogChooseDay = layoutInflater.inflate(R.layout.dialog_choose_day, null);
        int[] checks = {R.id.check0, R.id.check1, R.id.check2, R.id.check3, R.id.check4, R.id.check5, R.id.check6};
        final CheckBox[] checkBoxes = new CheckBox[7];
        for (int j = 0; j < 7; j++) {
            checkBoxes[j] = (CheckBox) dialogChooseDay.findViewById(checks[j]);
            if (alarm.getDay().indexOf(j+"")!=-1) checkBoxes[j].setChecked(true);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa ngày báo thức");
        builder.setView(dialogChooseDay);
        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String s = "";
                for (int j = 0; j < 7; j++) {
                    if (checkBoxes[j].isChecked()) {
                        s = s + j;

                    }
                }
                alarm.setDay(s);
                String s1 = "";
                for (int j = 0; j < s.length(); j++) {
                    if (s.charAt(j) == '0') {
                        s1 += "Chủ nhật";
                    } else {
                        s1 += "Thứ " + (Integer.parseInt(s.charAt(j) + "") + 1);
                    }
                    if (j != s.length() - 1) {
                        s1 += ", ";
                    }

                }
                txtRepeat.setText(s1);


            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void addComponents() {
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        txtCancel = (TextView) findViewById(R.id.txtCancel);
        txtEdit = (TextView) findViewById(R.id.txtAdd);
        txtName = (EditText) findViewById(R.id.txtName);
        llRepeat = (LinearLayout) findViewById(R.id.llRepeat);
        llSound = (LinearLayout) findViewById(R.id.llSound);
        txtRepeat = (TextView) findViewById(R.id.txtRepeat);
        txtSound = (TextView) findViewById(R.id.txtSound);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        txtTemp1 = (TextView) findViewById(R.id.txtTemp1);
        txtTemp2 = (TextView) findViewById(R.id.txtTemp2);
        timePicker.setIs24HourView(true);
        txtName.setText(alarm.getName());
        txtEdit.setText("Sửa");
        if (alarm.getRepeat() == 0){
            txtRepeat.setText("Một lần");
        } else {
            if (alarm.getDay().equalsIgnoreCase("0123456")){
                txtRepeat.setText("Hằng ngày");
            } else txtRepeat.setText("Tùy chỉnh");
        }
        timePicker.setCurrentHour(alarm.getH());
        timePicker.setCurrentMinute(alarm.getM());


    }
}
