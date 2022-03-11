package com.myctca.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;

import java.util.ArrayList;
import java.util.List;

public class HomeAlertMessagesAdapter extends RecyclerView.Adapter<HomeAlertMessagesAdapter.ViewHolder> {

    private List<String> alertMessages = new ArrayList<>();

    public HomeAlertMessagesAdapter(List<String> alertMessages) {
        super();
        if (alertMessages != null)
            this.alertMessages = alertMessages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_alert, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvAlertMessage.setText(alertMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return alertMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAlertMessage;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlertMessage = itemView.findViewById(R.id.alert_message);
        }
    }
}
