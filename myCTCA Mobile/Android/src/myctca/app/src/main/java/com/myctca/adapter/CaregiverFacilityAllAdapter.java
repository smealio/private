package com.myctca.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.myctca.R;
import com.myctca.model.FacilityInfoAll;

public class CaregiverFacilityAllAdapter extends ListAdapter<FacilityInfoAll, CaregiverFacilityAllAdapter.CaregiverFacilityAllViewholder> {

    public CaregiverFacilityAllInterface caregiverFacilityAllInterface;

    public CaregiverFacilityAllAdapter(@NonNull DiffUtil.ItemCallback<FacilityInfoAll> diffCallback, CaregiverFacilityAllInterface caregiverFacilityAllInterface) {
        super(diffCallback);
        this.caregiverFacilityAllInterface = caregiverFacilityAllInterface;
    }

    @NonNull
    @Override
    public CaregiverFacilityAllViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_info_item, parent, false);
        return new CaregiverFacilityAllViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CaregiverFacilityAllViewholder holder, int position) {
        holder.bind(getItem(position));
    }

    public interface CaregiverFacilityAllInterface {
        void callFacility(String phoneNo);
    }

    public static class CaregiverFacilityInfoDiffUtil extends DiffUtil.ItemCallback<FacilityInfoAll> {

        @Override
        public boolean areItemsTheSame(@NonNull FacilityInfoAll oldItem, @NonNull FacilityInfoAll newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull FacilityInfoAll oldItem, @NonNull FacilityInfoAll newItem) {
            return oldItem.displayName.equals(newItem.displayName);
        }
    }

    class CaregiverFacilityAllViewholder extends RecyclerView.ViewHolder {
        private final TextView facilityInfoContact;
        private final TextView facilityInfoName;
        private LinearLayout llFacilityInfo;

        public CaregiverFacilityAllViewholder(@NonNull View itemView) {
            super(itemView);
            facilityInfoName = itemView.findViewById(R.id.facility_info_name);
            facilityInfoContact = itemView.findViewById(R.id.facility_info_contact);
            llFacilityInfo = itemView.findViewById(R.id.ll_facility_info);
        }

        protected void bind(FacilityInfoAll facilityInfoAll) {
            facilityInfoContact.setText(facilityInfoAll.mainPhone);
            facilityInfoName.setText(facilityInfoAll.displayName);
            llFacilityInfo.setOnClickListener(view -> caregiverFacilityAllInterface.callFacility(facilityInfoAll.mainPhone));
        }
    }
}
