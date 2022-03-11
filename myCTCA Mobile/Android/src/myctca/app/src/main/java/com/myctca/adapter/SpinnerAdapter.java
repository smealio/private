package com.myctca.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.core.content.ContextCompat;

import com.myctca.R;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {
    private int mSelectedIndex = -1;


    public SpinnerAdapter(Context context, int resource, List<String> arrayList) {
        super(context, resource, arrayList);
    }

    public void setSelection(int position) {
        mSelectedIndex = position;
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View itemView = super.getDropDownView(position, convertView, parent);

        if (position == mSelectedIndex) {
            itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.viewLightGrey));
        } else {
            itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        return itemView;
    }
}
