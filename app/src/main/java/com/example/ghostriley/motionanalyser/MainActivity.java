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
    public EditText mConfidence;
    public Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFileName = (EditText) findViewById(R.id.fileNameField);
        mConfidence = (EditText) findViewById(R.id.confidenceField);
        mNextButton = (Button) findViewById(R.id.nextButton);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Saving entered information
                String fileName = mFileName.getText().toString().trim();
                String confidence = mConfidence.getText().toString();
                int con = Integer.parseInt(mConfidence.getText().toString());

                if (fileName != "" && con >= 0 && con <= 100) {
                    Intent intent = new Intent(MainActivity.this, AnalysingActivity.class);
                    intent.putExtra("fileName", fileName);
                    intent.putExtra("confidence", confidence);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter information in correct format", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
