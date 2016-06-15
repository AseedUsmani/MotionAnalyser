package com.example.ghostriley.motionanalyser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public Spinner mSpinner;
    public EditText mConfidence, mDelayTime;
    public Button mNextButton;

    public String[] taskData = {
            "Not specific",
            "Driving",
            "On bike",
            "Walking",
            "Running",
            "Cycling"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpinner = (Spinner) findViewById(R.id.taskSpinner);
        mConfidence = (EditText) findViewById(R.id.confidenceField);
        mDelayTime = (EditText) findViewById(R.id.delayTimeField);
        mNextButton = (Button) findViewById(R.id.nextButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_layout, taskData);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        mSpinner.setAdapter(adapter);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Saving entered information
                String fileName = mSpinner.getSelectedItem().toString();
                String confidence = mConfidence.getText().toString();
                String delayTime = mDelayTime.getText().toString();
                int con = Integer.parseInt(mConfidence.getText().toString());

                if (con >= 0 && con <= 100) {
                    Intent intent = new Intent(MainActivity.this, AnalysingActivity.class);
                    intent.putExtra("fileName", fileName);
                    intent.putExtra("confidence", confidence);
                    intent.putExtra("delayTime", delayTime);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter information in correct format", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}