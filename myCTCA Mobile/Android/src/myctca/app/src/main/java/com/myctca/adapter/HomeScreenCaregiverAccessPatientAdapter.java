package com.myctca.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.myctca.R;
import com.myctca.model.MyCTCAProxy;

import java.util.List;

public class HomeScreenCaregiverAccessPatientAdapter extends Adapter<HomeScreenCaregiverAccessPatientAdapter.HomeScreenCaregiverAccessPatientHolder> {

    private Context context;
    private List<MyCTCAProxy> proxies;
    private OnItemClickListner onItemClickListner;

    public HomeScreenCaregiverAccessPatientAdapter(Context context, List<MyCTCAProxy> proxies) {
        this.proxies = proxies;
        this.context = context;
    }

    @Override
    public HomeScreenCaregiverAccessPatientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeScreenCaregiverAccessPatientHolder(LayoutInflater.from(context), parent);
    }

    @Override
    public void onBindViewHolder(HomeScreenCaregiverAccessPatientHolder holder, int position) {
        holder.bind(proxies.get(position));
    }

    @Override
    public int getItemCount() {
        return proxies.size();
    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

    public interface OnItemClickListner {
        void onClick(MyCTCAProxy selectedProxy);
    }

    class HomeScreenCaregiverAccessPatientHolder extends RecyclerView.ViewHolder {

        private final TextView tvAccessPatientName;

        private HomeScreenCaregiverAccessPatientHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_caregiver_access_patient, parent, false));
            tvAccessPatientName = itemView.findViewById(R.id.tv_access_patient_name);
        }

        public void bind(MyCTCAProxy proxy) {
            String proxyName = proxy.getFullName();
            tvAccessPatientName.setText(proxyName);
            tvAccessPatientName.setOnClickListener(view -> onItemClickListner.onClick(proxy));
        }
    }

}
