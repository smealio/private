package com.myctca.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.model.ActivityLogItem;
import com.myctca.util.MyCTCADateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoreActivityLogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int DATE = 0;
    private static final int LOGS = 1;
    private List<Object> activityLogsArray = new ArrayList<>();


    public MoreActivityLogsAdapter(List<Object> activityLogArray) {
        super();
        if (activityLogArray != null)
            this.activityLogsArray = activityLogArray;
    }

    @Override
    public int getItemViewType(int position) {
        if (activityLogsArray.get(position) instanceof Date) {
            return DATE;
        } else {
            return LOGS;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout;

        if (viewType == DATE) {
            layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_activity_logs_group, parent, false);
            return new DateViewHolder(layout);
        } else {
            layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_activity_logs_item, parent, false);
            return new ActivityLogsViewHolder(layout);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {


        if (holder.getItemViewType() == DATE) {
            ((DateViewHolder) holder).tvEnteredDate.setText(MyCTCADateUtils.getDayDateStr((Date) activityLogsArray.get(position)));
        } else {
            ((ActivityLogsViewHolder) holder).tvActivityLogTime.setText(((ActivityLogItem) activityLogsArray.get(position)).getDisplayTimeStamp());
            ((ActivityLogsViewHolder) holder).tvActivityLogActivity.setText(((ActivityLogItem) activityLogsArray.get(position)).getMessage());
            ((ActivityLogsViewHolder) holder).tvActivityLogUser.setText(((ActivityLogItem) activityLogsArray.get(position)).getUsername());
        }
    }

    @Override
    public int getItemCount() {
        return activityLogsArray.size();
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {

        private TextView tvEnteredDate;

        public DateViewHolder(View itemView) {
            super(itemView);
            tvEnteredDate = itemView.findViewById(R.id.activity_logs_entered_date);
        }
    }

    public class ActivityLogsViewHolder extends RecyclerView.ViewHolder {

        private TextView tvActivityLogTime;
        private TextView tvActivityLogActivity;
        private TextView tvActivityLogUser;

        public ActivityLogsViewHolder(View itemView) {
            super(itemView);

            tvActivityLogTime = itemView.findViewById(R.id.activity_log_time);
            tvActivityLogActivity = itemView.findViewById(R.id.activity_log_activity);
            tvActivityLogUser = itemView.findViewById(R.id.activity_log_user);
        }
    }
}
