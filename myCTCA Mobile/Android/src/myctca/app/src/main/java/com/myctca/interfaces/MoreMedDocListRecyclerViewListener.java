package com.myctca.interfaces;

import android.view.View;

import com.myctca.model.MedDoc;

/**
 * Created by tomackb on 2/15/18.
 */

public interface MoreMedDocListRecyclerViewListener {

    void moreMedDocListRecyclerViewClicked(View v, int position, MedDoc medDoc);
}
