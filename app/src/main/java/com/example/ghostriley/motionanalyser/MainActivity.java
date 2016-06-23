package com.example.ghostriley.motionanalyser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public EditText mFileName;
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

        mFileName = (EditText) findViewById(R.id.editText);
        mNextButton = (Button) findViewById(R.id.nextButton);


        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Saving entered information

                String name = mFileName.getText().toString();
                if (name != null) {
                    Intent intent = new Intent(MainActivity.this, AnalysingActivity.class);
                    intent.putExtra("fileName", name);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter information in correct format", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}