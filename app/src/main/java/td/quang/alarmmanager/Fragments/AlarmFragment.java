package td.quang.alarmmanager.Fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import td.quang.alarmmanager.Activitys.AddAlarmActivity;
import td.quang.alarmmanager.Adapters.RecyclerViewAlarmAdapter;
import td.quang.alarmmanager.Database.MyDatabase;
import td.quang.alarmmanager.Models.Alarm;
import td.quang.alarmmanager.R;

/**
 * Created by Quang_TD on 12/10/2016.
 */
public class AlarmFragment extends MyFragment {
    private RecyclerView mRecyclerView;
    private ArrayList<Alarm> alarms;
    private RecyclerViewAlarmAdapter alarmAdapter;

    private TextView txtCurrentTime;
    private TextView txtAdd;

    private String sm = " ";
    public AlarmFragment() {
        setName("Báo Thức");
        alarms = new ArrayList<>();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 500);
                Calendar calendar = Calendar.getInstance();
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                int m = calendar.get(Calendar.MINUTE);
                int s = calendar.get(Calendar.SECOND);
                String hh = (h<10)?"0"+h:h+"";
                String mm = (m<10)?"0"+m:m+"";
                String ss = (s<10)?"0"+s:s+"";
                sm = (sm.equals(":"))?" ":":";
                txtCurrentTime.setText(hh + ":" + mm + sm + ss);

            }
        }, 500);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        alarmAdapter = new RecyclerViewAlarmAdapter(getContext(), alarms);
        mRecyclerView.setAdapter(alarmAdapter);

        txtCurrentTime = (TextView) view.findViewById(R.id.txtCurrentTime);

        txtAdd = (TextView) view.findViewById(R.id.txtAdd);
        txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddAlarmActivity.class));
            }
        });
        initSwipe();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        alarms.clear();
        alarms.addAll(MyDatabase.getInstance(getContext()).getAll());
        alarmAdapter.notifyDataSetChanged();

    }
    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                alarmAdapter.removeItem(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

}
