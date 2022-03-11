package com.myctca.interfaces;

import android.view.View;

import com.myctca.model.ImagingDoc;

/**
 * Created by tomackb on 2/16/18.
 */

public interface MoreMedDocImagingListRecyclerViewListener {

    void moreMedDocImagingListRecyclerViewClicked(View v, int position, ImagingDoc imagingDoc);
}
