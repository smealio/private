package com.myctca.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.myctca.BuildConfig;
import com.myctca.R;
import com.myctca.common.AppSessionManager;
import com.myctca.errorhandling.VolleyErrorHandler;
import com.myctca.interfaces.GetListener;
import com.myctca.model.CTCAAnalyticsConstants;
import com.myctca.model.CTCAAnalyticsManager;
import com.myctca.model.PatientReportedSymptomInventory;
import com.myctca.model.SymptomInventory;
import com.myctca.network.GetClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.myctca.util.GeneralUtil.noNull;

public class PatientReportedService implements GetListener {

    private static String TAG = PatientReportedService.class.getSimpleName();
    private static PatientReportedService patientReportedService;
    private PatientReportedServiceListener listener;
    private Context context;

    public static PatientReportedService getInstance() {
        if (patientReportedService == null) {
            patientReportedService = new PatientReportedService();
        }
        return patientReportedService;
    }

    public void downloadPatientSymptomInventory(PatientReportedServiceListener listener, Context context, String url, String purpose) {
        this.listener = listener;
        this.context = context;
        if (AppSessionManager.getInstance().getSymptomInventories().isEmpty()) {
            GetClient getClient = new GetClient(this, context);
            getClient.fetch(url, null, purpose);
        } else {
            listener.notifyFetchSuccess(AppSessionManager.getInstance().getSymptomInventories());
        }
    }

    @Override
    public void notifyFetchSuccess(String parseSuccess, String purpose) {
        try {
            PatientReportedSymptomInventory[] symptomInventories = new Gson().fromJson(parseSuccess, PatientReportedSymptomInventory[].class);
            Map<String, List<PatientReportedSymptomInventory>> inventoryMap = new HashMap<>();
            for (PatientReportedSymptomInventory inventory : symptomInventories) {
                if (inventoryMap.containsKey(inventory.getPerformedDisplayDate())) {
                    inventoryMap.get(inventory.getPerformedDisplayDate()).add(inventory);
                } else {
                    List<PatientReportedSymptomInventory> newList = new ArrayList<>();
                    newList.add(inventory);
                    inventoryMap.put(inventory.getPerformedDisplayDate(), newList);
                }
            }

            List<SymptomInventory> list = new ArrayList<>();
            for (String key :
                    inventoryMap.keySet()) {
                SymptomInventory inventory = new SymptomInventory();
                inventory.setDate(key);
                inventory.setSymptomInventories(inventoryMap.get(key));
                list.add(inventory);
            }

            AppSessionManager.getInstance().setSymptomInventories(list);
            Log.d(TAG, "System Inventories retrieved: " + symptomInventories.length);
            listener.notifyFetchSuccess(list);
        } catch (JsonParseException exception) {
            Log.e(TAG, "exception:" + exception);
            listener.notifyFetchError(context.getString(R.string.error_400), purpose);
        }
    }

    @Override
    public void notifyFetchError(VolleyError error, String purpose) {
        Log.d("Error.Response", noNull(error.getMessage()));
        final String url = BuildConfig.myctca_server + context.getString(R.string.myctca_patient_symptom_inventory);
        CTCAAnalyticsManager.createEvent("PatientReportedService:notifyFetchError", CTCAAnalyticsConstants.EXCEPTION_REST_API, error, url);
        listener.notifyFetchError(VolleyErrorHandler.handleError(error, context), purpose);
    }

    public interface PatientReportedServiceListener {
        void notifyFetchSuccess(List<SymptomInventory> symptomInventories);

        void notifyFetchError(String handleError, String purpose);
    }
}
