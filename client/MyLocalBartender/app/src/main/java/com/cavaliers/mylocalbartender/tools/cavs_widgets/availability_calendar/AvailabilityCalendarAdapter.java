package com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cavaliers.mylocalbartender.R;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityCalendarAdapter extends RecyclerView.Adapter<AvailabilityCalendarAdapter.AvailabilityHolder> {

    public static List<AvailabilityDay> availabilityDayList = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;


    public AvailabilityCalendarAdapter(List<AvailabilityDay> availabilityDays, Context context){
        AvailabilityCalendarAdapter.availabilityDayList = availabilityDays;
        System.out.println("_____I JUST GOT CALLED CALENDAR AND THE SIZE IS="+availabilityDayList.size());
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public AvailabilityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_availability_date, parent, false);
        return new AvailabilityHolder(view);
    }

    @Override
    public void onBindViewHolder(AvailabilityHolder holder, int position) {

        AvailabilityDay day = availabilityDayList.get(position);

        holder.tv_availability_day.setText(day.getDay());
        holder.tv_availability_start_time.setText(day.getStartTime()+" "+day.getStartTimeRelation());
        holder.tv_availability_end_time.setText(day.getEndTime()+" "+day.getEndTimeRelation());

        holder.ll_overlay.setOnClickListener(new AvailabilityButtonHandler(availabilityDayList.get(position), context, this));
    }

    @Override
    public int getItemCount() {
        return availabilityDayList.size();
    }



    class AvailabilityHolder extends RecyclerView.ViewHolder {


        TextView tv_availability_day;
        TextView tv_availability_start_time;
        TextView tv_availability_end_time;
        LinearLayout ll_overlay;



        public AvailabilityHolder(View itemView) {
            super(itemView);

            tv_availability_day = (TextView) itemView.findViewById(R.id.tv_availabil_cal_day);
            tv_availability_start_time = (TextView)  itemView.findViewById(R.id.bt_availabil_cal_start);
            tv_availability_end_time = (TextView)  itemView.findViewById(R.id.bt_availabil_cal_end);
            ll_overlay = (LinearLayout) itemView.findViewById(R.id.lv_availability_overlay);


        }
    }
}

