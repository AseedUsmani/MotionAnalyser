package com.example.ghostriley.motionanalyser;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AnalysingActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public GoogleApiClient mApiClient;
    public static final String TAG = MainActivity.class.getSimpleName();

    protected String mFileName;
    protected String mConfidence;
    public Button mStartButton;
    public Button mFinishButton;
    public static int confidence;
    public ListView mListView;
    public static int flag;
    /*
    flag=0 -> Connect API
    flag=1 -> Disconnect API, try to save file, reset counter
    flag=2 -> File saved, wait for start
    */
    public static int[] mCount = {0, 0, 0, 0, 0, 0, 0};
    public static String mActivity[] = {
            "Default Data2",
            "Default Data2",
            "Default Data2",
            "Default Data2",
            "Default Data2",
            "Default Data2",
            "Default Data2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysing);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        flag = 2;

        mStartButton = (Button) findViewById(R.id.startButton);
        mFinishButton = (Button) findViewById(R.id.finishButton);
        mListView = (ListView) findViewById(R.id.activityList);

        //Retrieving information
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                mFileName = null;
                mConfidence = null;
            } else {
                mFileName = extras.getString("fileName");
                mConfidence = extras.getString("confidence");
                confidence = Integer.parseInt(mConfidence);

            }
        } else {
            mFileName = (String) savedInstanceState.getSerializable("fileName");
            mConfidence = (String) savedInstanceState.getSerializable("confidence");
        } //information retrieved*/
        confidence = Integer.parseInt(mConfidence);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resetting counter
                for (int j = 0; j < 7; j++) {
                    mCount[j] = 0;
                }
                mStartButton.setVisibility(View.INVISIBLE);
                mFinishButton.setVisibility(View.VISIBLE);
                flag = 0;
                mApiClient.connect();
            }
        });

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApiClient.disconnect();

                //Saving file
                Toast.makeText(AnalysingActivity.this, "Saving file...", Toast.LENGTH_LONG).show();
                try {
                    saveFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AnalysingActivity.this, "Failed to save file!", Toast.LENGTH_LONG).show();
                }
                flag = 1;

                //restarting application
                Intent intent = new Intent(AnalysingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(AnalysingActivity.this, "Connection to Google Services suspended!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(AnalysingActivity.this, "Connection to Google Services failed!", Toast.LENGTH_LONG).show();
    }

    public void saveFile() throws IOException {
        String appName = getString(R.string.app_name);

        if (isExternalStorageAvailable()) {
            String externalPath = Environment.getExternalStorageDirectory().toString();
            File mediaStorageDir = new File(externalPath, appName);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdir()) {
                    Log.e(TAG, "Failed to create directory");
                }
            }
            File file;
            java.util.Date now = new java.util.Date();
            String timestamp = new SimpleDateFormat("ddMM_HHmm", Locale.US).format(now);
            String path = mediaStorageDir.getPath() + File.separator;
            String fileName = mFileName + "_" + timestamp;
            file = new File(path + fileName + "_" + timestamp + ".txt");

            PrintWriter out = new PrintWriter(new FileWriter(file));

            // Write each string in the array on a separate line
            for (int i = 0; i < 7; i++) {
                out.println(mActivity[i] + "\n");
            }

            out.close();
            Toast.makeText(AnalysingActivity.this, "File saved.", Toast.LENGTH_LONG).show();
            flag = 2;
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED))
            return true;
        else return false;
    }
}
