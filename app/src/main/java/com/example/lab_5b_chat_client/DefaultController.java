package com.example.lab_5b_chat_client;

import android.util.Log;

public class DefaultController extends AbstractController {
    public static final String ELEMENT_OUTPUT_PROPERTY = "Output";
    private String message;

    private final String TAG = "Default Controller";

    public void changeOutputText(String newText) {
        setModelProperty(ELEMENT_OUTPUT_PROPERTY, newText);
    }

    public void sendGetRequest() {
        Log.d(TAG, "sendGetRequest");
        invokeModelMethod("sendGetRequest", null);
    }

    public void sendPostRequest() {
        Log.d(TAG, "sendPostRequest");
        invokeModelMethod("sendPostRequest", null);
    }

    public void sendClearRequest() {
        Log.d(TAG, "sendClearRequest");
        invokeModelMethod("sendDeleteRequest", null);
    }

    public void setMessage(String message) {
        Log.d(TAG, "setMessage");
        this.message = message;
    }

    public String getMessage() {
        Log.d(TAG, "getMessage");
        return message;
    }
}