package com.myctca.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.myctca.fragment.appointmment.AppointmentUpcomingFragment;

public class AppointmentTabAdapter extends FragmentStateAdapter {
    public AppointmentTabAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return AppointmentUpcomingFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
