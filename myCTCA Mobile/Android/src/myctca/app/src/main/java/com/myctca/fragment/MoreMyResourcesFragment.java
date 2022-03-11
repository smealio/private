package com.myctca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MyResourcesActivity;
import com.myctca.model.UserPermissions;
import com.myctca.service.SessionFacade;

public class MoreMyResourcesFragment extends Fragment {

    private SessionFacade sessionFacade;
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
        return inflater.inflate(R.layout.fragment_more_my_resources, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        TextView userGuide = view.findViewById(R.id.tv_my_resources_user_guide);
        userGuide.setOnClickListener(view1 -> openUserGuide());

        TextView faqs = view.findViewById(R.id.tv_my_resources_faqs);
        faqs.setOnClickListener(view1 -> openFaqs());

        TextView nutritionEducation = view.findViewById(R.id.tv_my_resources_nutrition_education);
        View nutritionEducationSeparator = view.findViewById(R.id.my_resources_nutrition_education_separator);

        TextView externalLinks = view.findViewById(R.id.tv_my_resources_external_links);
        View externalLinksSeparator = view.findViewById(R.id.my_resources_external_links_separator);

        if (!sessionFacade.getMyCtcaUserProfile().userCan(UserPermissions.VIEW_EXTERNAL_LINKS)) {
            nutritionEducation.setVisibility(View.GONE);
            nutritionEducationSeparator.setVisibility(View.GONE);

            externalLinks.setVisibility(View.GONE);
            externalLinksSeparator.setVisibility(View.GONE);
        }
        nutritionEducation.setOnClickListener(view1 -> openNutritionEducation());
        externalLinks.setOnClickListener(view1 -> openExternalLinks());
    }

    private void openExternalLinks() {
        ((MyResourcesActivity) context).addFragment(new MoreMyResourcesExternalLinksFragment(), "", context.getString(R.string.my_resources_external_links_title), false);
    }

    private void openNutritionEducation() {
        ((MyResourcesActivity) context).addFragment(MoreMyResourcesNutritionEducationFragment.newInstance(), "", "", false);
    }

    private void openFaqs() {
        ((MyResourcesActivity) context).addFragment(MoreMyResourcesWebViewsFragment.newInstance(), BuildConfig.myctca_server + context.getString(R.string.myctca_download_resources_faqs), context.getString(R.string.my_resources_faqs_short_title), false);
    }

    private void openUserGuide() {
        ((MyResourcesActivity) context).addFragment(DownloadPdfFragment.newInstance(), BuildConfig.myctca_server + context.getString(R.string.myctca_download_resources_user_guide), context.getString(R.string.my_resources_user_guide_title), false);
    }
}