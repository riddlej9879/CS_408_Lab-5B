package com.example.lab_5b_chat_client;

public class DefaultModel extends AbstractModel {
    public static final String TAG = "DefaultModel";
    private String outputText;

    public void initDefault() {
        setOutputText("Click the button to send an HTTP GET request ...");
    }

    public void setOutputText(String newText) {
        String oldText = this.outputText;
        this.outputText = newText;

        firePropertyChange(DefaultController.ELEMENT_OUTPUT_PROPERTY, oldText, newText);
    }

    public String getOutputText() {
        return outputText;
    }
}