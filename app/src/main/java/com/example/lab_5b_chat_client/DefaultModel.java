package com.example.lab_5b_chat_client;

import android.util.Log;

public class DefaultModel extends AbstractModel {
    public static final String TAG = "Default Model";
    private String outputText;

    public void initDefault() {
        Log.d(TAG, "initDefault");
        setOutputText("Click the button to send an HTTP GET request ...");
    }

    public void setOutputText(String newText) {
        Log.d(TAG, "setOutputText");
        String oldText = this.outputText;
        this.outputText = newText;

        firePropertyChange(DefaultController.ELEMENT_OUTPUT_PROPERTY, oldText, newText);
    }

    public String getOutputText() {
        Log.d(TAG, "getOutputText");
        return outputText;
    }
}