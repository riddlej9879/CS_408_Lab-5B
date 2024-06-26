package com.example.lab_5b_chat_client;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

public class ExampleWebServiceModel extends AbstractModel {
    private static final String TAG = "ExampleWebServiceModel";
    private static final String GET_URL = "https://testbed.jaysnellen.com:8443/SimpleChat/Board";
    private static final String POST_URL = "https://testbed.jaysnellen.com:8443/SimpleChat/Board";
    private static final String USERNAME = "Josh Riddle";

    private MutableLiveData<JSONObject> jsonData;
    private String outputText;
    private String message;

    private final ExecutorService requestThreadExecutor;
    private final Runnable httpGetRequestThread, httpPostRequestThread, httpDeleteRequestThread;
    private Future<?> pending;

    public ExampleWebServiceModel() {
        Log.d(TAG, "Constructor");
        requestThreadExecutor = Executors.newSingleThreadExecutor();

        httpGetRequestThread = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Get");
                /* If a previous request is still pending, cancel it */
                if (pending != null) {
                    pending.cancel(true);
                }
                /* Begin new request now, but don't wait for it */
                try {
                    pending = requestThreadExecutor.submit(new HTTPRequestTask("GET", GET_URL));
                }
                catch (Exception e) {
                    Log.e(TAG, " Exception: ", e);
                }
            }
        };

        httpPostRequestThread = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Post");
                /* If a previous request is still pending, cancel it */
                if (pending != null) {
                    pending.cancel(true);
                }
                /* Begin new request now, but don't wait for it */
                try {
                    pending = requestThreadExecutor.submit(new HTTPRequestTask("POST", POST_URL));
                }
                catch (Exception e) {
                    Log.e(TAG, " Exception: ", e);
                }
            }
        };

        httpDeleteRequestThread = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Delete");
                if (pending != null) {
                    pending.cancel(true);
                }

                try {
                    pending = requestThreadExecutor.submit(new HTTPRequestTask("DELETE", POST_URL));
                }
                catch (Exception e) {
                    Log.e(TAG, " Exception: ", e);
                }
            }
        };
    }

    public void initDefault() {
        Log.d(TAG, "initDefault");
        try {
            sendGetRequest();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getOutputText() {
        Log.d(TAG, "getOutputText");
        return outputText;
    }

    public void setOutputText(String newText) {
        Log.d(TAG, "setOutputText");
        String oldText = this.outputText;
        this.outputText = newText;

        firePropertyChange(DefaultController.ELEMENT_OUTPUT_PROPERTY, oldText, newText);
    }

    // Start GET Request (called from Controller)
    public void sendGetRequest() {
        Log.d(TAG, "sendGetRequest");
        httpGetRequestThread.run();
    }

    // Start POST Request (called from Controller)
    public void sendPostRequest(String new_message) {
        Log.d(TAG, "sendPostRequest");
        setMessage(new_message);
        httpPostRequestThread.run();
    }

    public void sendDeleteRequest() {
        Log.d(TAG, "sendDeleteRequest");
        httpDeleteRequestThread.run();
    }

    // Setter / Getter Methods for JSON LiveData
    private void setJsonData(JSONObject json) {
        Log.d(TAG, "setJsonData");
        this.getJsonData().postValue(json);
        setOutputText(json.toString());
    }

    public MutableLiveData<JSONObject> getJsonData() {
        Log.d(TAG, "getJsonData");
        if (jsonData == null) {
            jsonData = new MutableLiveData<>();
        }
        return jsonData;
    }

    private void setMessage(String message) {
        Log.d(TAG, "setMessage");
        this.message = message;
    }

    public String getMessage() {
        Log.d(TAG, "getMessage");
        return message;
    }

    // Private Class for HTTP Request Threads
    private class HTTPRequestTask implements Runnable {
        private static final String TAG = "HTTPRequestTask";
        private final String method, urlString;

        HTTPRequestTask(String method, String urlString) {
            this.method = method;
            this.urlString = urlString;
        }

        @Override
        public void run() {
            Log.d(TAG, "HTTPRequestTask run");
            JSONObject results = doRequest(urlString);
            // Log.d(TAG, "Run Line 161. Results: " + results.toString());
            setJsonData(results);
        }

        /* Create and Send Request */
        private JSONObject doRequest(String urlString) {
            Log.d(TAG, "HTTPRequestTask doRequest");
            StringBuilder r = new StringBuilder();
            String line;

            HttpURLConnection conn = null;
            JSONObject results = null;

            /* Log Request Data */
            try {
                /* Check if task has been interrupted */
                if (Thread.interrupted()) {
                    Log.d(TAG, "Line 187");
                    throw new InterruptedException();
                }

                /* Create Request */
                URL url = new URL(urlString);

                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000); /* ten seconds */
                conn.setConnectTimeout(15000); /* fifteen seconds */
                conn.setRequestMethod(method);
                conn.setDoInput(true);

                /* Add Request Parameters (if any) */
                if (method.equals("POST") ) {
                    Log.d(TAG, "doRequest POST");
                    conn.setDoOutput(true);

                    // Create request parameters (these will be echoed back by the example API)
                    JSONObject json = new JSONObject();
                    json.put("message", message);
                    json.put("name", USERNAME);

                    Log.i(TAG, json.toString());

                    // Write parameters to request body
                    OutputStream out = conn.getOutputStream();
                    out.write(json.toString().getBytes());
                    out.flush();
                    out.close();
                    Log.d(TAG, "Conn Response: " + String.valueOf(conn.getResponseCode()));
                }

                if (method.equals("DELETE")) {
                    Log.d(TAG, "doRequest DELETE");
                    conn.setRequestMethod("DELETE");
                    int responseCode = conn.getResponseCode();
                }

                /* Send Request */
                conn.connect();

                /* Check if task has been interrupted */
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                /* Get Reader for Results */
                int code = conn.getResponseCode();

                if (code == HttpsURLConnection.HTTP_OK || code == HttpsURLConnection.HTTP_CREATED) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    /* Read Response Into StringBuilder */
                    do {
                        line = reader.readLine();
                        if (line != null)
                            r.append(line);
                    }
                    while (line != null);
                }

                /* Check if task has been interrupted */
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                /* Parse response */
                Log.i("WebService", r.toString());
                results = new JSONObject(r.toString());
            }
            catch (Exception e) {
                Log.e(TAG, " Exception: ", e);
            }
            finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            /* Finished; Log and Return Results */
            Log.d(TAG, " JSON: " + r.toString());
            return results;
        }
    }
}