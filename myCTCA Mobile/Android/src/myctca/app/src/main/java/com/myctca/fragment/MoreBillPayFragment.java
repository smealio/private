package com.myctca.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.myctca.R;
import com.myctca.activity.DisplayWebViewActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreBillPayFragment extends Fragment {

    private static final String TAG = "myCTCA-MOREBILLPAY";
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more_bill_pay, container, false);

        Button mBillPayLaunchButton = view.findViewById(R.id.launch_site_button);
        mBillPayLaunchButton.setOnClickListener(v -> {
            Log.d(TAG, "Tap that butt…med Docs");
            billPay();
        });

        return view;
    }

    private void billPay() {
        Intent myIntent = new Intent(context, DisplayWebViewActivity.class);
        String url;
        url = context.getString(R.string.server_bill_pay);
        myIntent.putExtra("type", context.getString(R.string.more_bill_pay));
        myIntent.putExtra("url", url);
        startActivity(myIntent);
    }

}
