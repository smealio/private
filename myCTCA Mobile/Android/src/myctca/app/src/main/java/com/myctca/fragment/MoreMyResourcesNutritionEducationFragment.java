package com.myctca.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.activity.MyResourcesActivity;

public class MoreMyResourcesNutritionEducationFragment extends Fragment {

    private Context context;

    public static MoreMyResourcesNutritionEducationFragment newInstance() {
        return new MoreMyResourcesNutritionEducationFragment();
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
        return inflater.inflate(R.layout.fragment_more_my_resources_nutrition_education, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        TextView healthFuture = view.findViewById(R.id.tv_nutri_edu_health_future);
        healthFuture.setOnClickListener(view1 -> openHealthFuture());

        TextView nutritionBasics = view.findViewById(R.id.tv_nutri_edu_nutrition_basics);
        nutritionBasics.setOnClickListener(view1 -> openNutritionBasics());

        TextView menuEnhancement = view.findViewById(R.id.tv_nutri_edu_menu_enhancement);
        menuEnhancement.setOnClickListener(view1 -> openMenuEnhancement());

        TextView manageSymptom = view.findViewById(R.id.tv_nutri_edu_symptom_manage);
        manageSymptom.setOnClickListener(view1 -> openManageSymptom());

        TextView eatingChallenge = view.findViewById(R.id.tv_nutri_edu_eating_challenge);
        eatingChallenge.setOnClickListener(view1 -> openEatingChallenge());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_more_my_resources, menu);
        ((MyResourcesActivity) context).setToolBar(context.getString(R.string.my_resources_nutrition_education_title));
    }

    private void openEatingChallenge() {
        ((MyResourcesActivity) context).addFragment(DownloadPdfFragment.newInstance(), BuildConfig.myctca_server + context.getString(R.string.myctca_download_resources_eating_challenge), context.getString(R.string.my_resources_nutri_edu_eating_challenge),false);
    }

    private void openManageSymptom() {
        ((MyResourcesActivity) context).addFragment(DownloadPdfFragment.newInstance(), BuildConfig.myctca_server + context.getString(R.string.myctca_download_resources_symptom_management), context.getString(R.string.my_resources_nutri_edu_symptom_manage),false);
    }

    private void openMenuEnhancement() {
        ((MyResourcesActivity) context).addFragment(DownloadPdfFragment.newInstance(), BuildConfig.myctca_server + context.getString(R.string.myctca_download_resources_menu_enhancement), context.getString(R.string.my_resources_nutri_edu_menu_enhancement),false);
    }

    private void openNutritionBasics() {
        ((MyResourcesActivity) context).addFragment(DownloadPdfFragment.newInstance(), BuildConfig.myctca_server + context.getString(R.string.myctca_download_resources_nutrition_basics), context.getString(R.string.my_resources_nutri_edu_nutrition_basics),false);
    }

    private void openHealthFuture() {
        ((MyResourcesActivity) context).addFragment(DownloadPdfFragment.newInstance(), BuildConfig.myctca_server + context.getString(R.string.myctca_download_resources_health_future), context.getString(R.string.my_resources_nutri_edu_health_future),false);
    }
}