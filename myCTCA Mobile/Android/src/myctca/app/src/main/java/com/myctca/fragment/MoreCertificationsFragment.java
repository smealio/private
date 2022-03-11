package com.myctca.fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.network.customRequests.ImageVolleyRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreCertificationsFragment extends Fragment {

    private static final String TAG = MoreCertificationsFragment.class.getSimpleName();
    private WebView wvSiteCert;
    private NetworkImageView ivSiteCert;
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
        return inflater.inflate(R.layout.fragment_more_certifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wvSiteCert = view.findViewById(R.id.wv_site_cert);
        ivSiteCert = view.findViewById(R.id.iv_site_cert);

        downloadCertificate();
        downloadCertificateImage();
    }

    private void downloadCertificate() {
        String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_site_certification);
        Log.d(TAG, "url:" + url);
        wvSiteCert.loadUrl(url);
    }

    private void downloadCertificateImage() {
        String url = BuildConfig.myctca_server + context.getString(R.string.myctca_get_site_certification_image);
        Log.d(TAG, "url:" + url);
        ImageLoader imageLoader = ImageVolleyRequest.getInstance(context)
                .getImageLoader();
        imageLoader.get(url, ImageLoader.getImageListener(ivSiteCert, R.color.white, android.R.drawable
                .ic_dialog_alert));
        ivSiteCert.setImageUrl(url, imageLoader);
    }
}
