package com.myctca.service;

import android.content.Context;

import com.android.volley.VolleyError;
import com.myctca.interfaces.GetPdfClientListener;
import com.myctca.interfaces.PostPdfClientListener;
import com.myctca.model.MyCTCATask;
import com.myctca.network.GetPDFClient;
import com.myctca.network.PostPDFClient;

import java.util.Map;

public class PdfService implements GetPdfClientListener, PostPdfClientListener {
    private static PdfService pdfService;
    private PdfServiceListener listener;

    public static PdfService getInstance() {
        if (pdfService == null) {
            return new PdfService();
        }
        return pdfService;
    }

    @Override
    public void notifyGetPdfSuccess(byte[] response) {
        listener.notifyGetPdfSuccess(response);
    }

    @Override
    public void notifyGetPdfError(VolleyError error) {
        listener.notifyGetPdfError(error);
    }

    public void downloadPdfFile(PdfServiceListener listener, Context context, String mUrl, Map<String, String> params) {
        this.listener = listener;
        GetPDFClient getPDFClient = new GetPDFClient(this, context);
        getPDFClient.downloadPdfFile(mUrl, params);
    }

    public void downloadAppointmentSchedule(PdfServiceListener listener, Context context, String mUrl, String body) {
        this.listener = listener;
        PostPDFClient postPDFClient = new PostPDFClient(this, context, MyCTCATask.APPOINTMENT_SCHEDULE);
        postPDFClient.downloadPdf(mUrl, body);
    }

    @Override
    public void notifyPostPdfSuccess(byte[] response, int task) {
        listener.notifyGetPdfSuccess(response);
    }

    @Override
    public void notifyPostPdfError(VolleyError error, int task) {
        listener.notifyGetPdfError(error);
    }

    public interface PdfServiceListener {
        void notifyGetPdfSuccess(byte[] response);

        void notifyGetPdfError(VolleyError error);
    }
}
