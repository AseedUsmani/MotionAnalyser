package com.example.ghostriley.motionanalyser;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AnalysingActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public GoogleApiClient mApiClient;

    protected String mFileName;
    protected String mTask;
    protected String mConfidence;
    public Button mStartButton;
    public Button mFinishButton;
    public static int confidence;
    public ListView mListView;
    //ArrayAdapter<String> adapter=new ArrayAdapter<String>(AnalysingActivity.this, android.R.layout.simple_list_item_1, mActivity);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysing);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mStartButton=(Button)findViewById(R.id.startButton);
        mFinishButton=(Button)findViewById(R.id.finishButton);
        mListView=(ListView)findViewById(R.id.activityList);

        /*if(mListView!=null) {
            mListView.setAdapter(adapter);
        }*/

        //Retrieving information
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                mFileName = null;
                mTask=null;
                mConfidence=null;
            } else {
                mFileName = extras.getString("fileName");
                mTask = extras.getString("task");
                mConfidence=extras.getString("confidence");
                confidence=Integer.parseInt(mConfidence);

            }
        } else {
            mFileName = (String) savedInstanceState.getSerializable("fileName");
            mTask = (String) savedInstanceState.getSerializable("task");
            mConfidence = (String) savedInstanceState.getSerializable("confidence");
        } //information retrieved*/
        confidence=Integer.parseInt(mConfidence);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartButton.setVisibility(View.INVISIBLE);
                mFinishButton.setVisibility(View.VISIBLE);
                mApiClient.connect();
                }
        });

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApiClient.disconnect();
                //Setting name for the file
                /*java.util.Date now = new java.util.Date();
                String timestamp = new SimpleDateFormat("ddMM_HHmm", Locale.US).format(now);
                String fileName=mFileName+"_"+timestamp;*/

                //Saving file
                /*try {
                    saveFile(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                Intent intent=new Intent(AnalysingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent (this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);

        //updating data set for list activity
        /*mActivity=mObject.mActivity;
        adapter.notifyDataSetChanged();*/
        
    }


    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(AnalysingActivity.this, "Connection to Google Services suspended!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(AnalysingActivity.this, "Connection to Google Services failed!", Toast.LENGTH_LONG).show();
    }
}
