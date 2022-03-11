package com.myctca.fragment.appointmment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.myctca.MyCTCA;
import com.myctca.R;
import com.myctca.activity.NavActivity;
import com.myctca.adapter.AppointmentSectionAdapter;
import com.myctca.adapter.AppointmentTabAdapter;
import com.myctca.model.UserPermissions;
import com.myctca.service.SessionFacade;

public class AppointmentFragment extends Fragment {

    private static final String APPT_PAST = "PAST";
    private static final String APPT_UPCOMING = "UPCOMING";
    TabLayout tabLayout;
    ViewPager2 viewPager;
    AppointmentTabAdapter appointmentTabAdapter;
    private SessionFacade sessionFacade;
    private MenuItem downloadAppt;
    private Context context;

    public void setMenuButtons() {
        if (downloadAppt != null) {
            if (sessionFacade.isAppointmentsEmpty()) {
                downloadAppt.getIcon().setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.LTGRAY, BlendModeCompat.SRC_ATOP));
                downloadAppt.setEnabled(false);
            } else {
                downloadAppt.getIcon().setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(context, R.color.colorPrimary), BlendModeCompat.SRC_ATOP));
                downloadAppt.setEnabled(true);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.viewPager);
        appointmentTabAdapter = new AppointmentTabAdapter(this);
        viewPager.setAdapter(appointmentTabAdapter);

        setViewPager();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (MyCTCA.isIsInForeground())
            AppointmentSectionAdapter.stopTimer();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        ((NavActivity) context).setToolBar(context.getString(R.string.appts_title));
        inflater.inflate(R.menu.menu_appt, menu);
        sessionFacade = new SessionFacade();
        downloadAppt = menu.findItem(R.id.toolbar_appts_download);
        MenuItem requestAppt = menu.findItem(R.id.toolbar_appts_new);
        requestAppt.setVisible(sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.REQUEST_APPOINTMENT));
        setMenuButtons();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setViewPager() {
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Past"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //do nothing
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0)
                    AppointmentUpcomingFragment.setAppointmentType(APPT_UPCOMING);
                else
                    AppointmentUpcomingFragment.setAppointmentType(APPT_PAST);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
}