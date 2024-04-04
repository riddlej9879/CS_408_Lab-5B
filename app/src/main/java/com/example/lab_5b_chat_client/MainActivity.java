package com.example.lab_5b_chat_client;

import android.util.Log;
import android.os.Bundle;
import android.view.View;

import org.json.JSONObject;
import java.beans.PropertyChangeEvent;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab_5b_chat_client.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements AbstractView {
    public static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private DefaultController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /* Create Controller and Models */
        controller = new DefaultController();
        ExampleWebServiceModel model = new ExampleWebServiceModel();

        /* Register Activity View and Model with Controller */
        controller.addView(this);
        controller.addModel(model);

        /* Initialize Model to Default Values */
        model.initDefault();
        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.sendGetRequest();
            }
        });
        binding.clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.sendClearRequest();
            }
        });
    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        String propertyValue = evt.getNewValue().toString();

        Log.i(TAG, "New " + propertyName + " Value from Model: " + propertyValue);
        if ( propertyName.equals(DefaultController.ELEMENT_OUTPUT_PROPERTY) ) {
            String oldPropertyValue = binding.output.getText().toString();

            if (!oldPropertyValue.equals(propertyValue)) {
                String messages = "";
                try {
                    JSONObject json = new JSONObject(propertyValue);
                    messages = json.get("messages").toString();
                }
                catch (Exception e) {
                    Log.d("TAG", e.toString());
                    messages = "Error";
                }
                Log.d("TAG", messages);
                binding.output.setText(messages);
            }
        }
    }
}