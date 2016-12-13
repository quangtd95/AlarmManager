package td.quang.alarmmanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import td.quang.alarmmanager.Activitys.EditAlarmActivity;
import td.quang.alarmmanager.Database.MyDatabase;
import td.quang.alarmmanager.Models.Alarm;
import td.quang.alarmmanager.R;
import td.quang.alarmmanager.Services.AlarmService;

/**
 * Created by Quang_TD on 12/11/2016.
 */
public class RecyclerViewAlarmAdapter extends RecyclerView.Adapter<RecyclerViewAlarmAdapter.MyHolder> {
    private Context mContext;
    private List<Alarm> alarms;

    public RecyclerViewAlarmAdapter(Context mContext, List<Alarm> alarms) {
        this.mContext = mContext;
        this.alarms = alarms;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_view_alarm, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        final Alarm alarm = alarms.get(position);
        int h = alarm.getH();
        int m = alarm.getM();
        holder.txtTime.setText(((h<10)?"0":"")+h+":"+((m<10)?"0":"")+m);
        String repeat = "";
        if (alarm.getDay().equals("0123456")) repeat = "Hằng ngày";
        else repeat = "Tùy chỉnh";
        if (alarm.getRepeat() == 0) repeat = "Một lần";
        holder.txtRepeat.setText(repeat);
        holder.txtStatus.setText((alarm.getStatus() == 0) ? "Tắt" : "Bật");
        holder.btnSwitch.setChecked((alarm.getStatus() == 1));
        holder.btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MyDatabase.getInstance(mContext).onoff(alarm.getId(), b);
                alarm.setStatus((b) ? 1 : 0);
                holder.txtStatus.setText((alarm.getStatus() == 0) ? "Tắt" : "Bật");
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditAlarmActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", alarms.get(position));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (alarms == null) ? 0 : alarms.size();
    }

    public void removeItem(int position) {
        /*Intent intent = new Intent();
        intent.setAction(AlarmService.DELETE);
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", alarms.get(position));
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent);*/
        MyDatabase.getInstance(mContext).delete(alarms.get(position).getId());
        alarms.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, alarms.size());
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtTime;
        TextView txtRepeat;
        TextView txtStatus;
        Switch btnSwitch;

        public MyHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtRepeat = (TextView) itemView.findViewById(R.id.txtRepeat);
            txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
            btnSwitch = (Switch) itemView.findViewById(R.id.btnSwitch);


        }
    }
}
