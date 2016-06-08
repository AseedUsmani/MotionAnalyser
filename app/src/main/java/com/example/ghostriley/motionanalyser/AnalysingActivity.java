package com.example.ghostriley.motionanalyser;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
    public static int[] mCount = {0, 0, 0, 0, 0, 0, 0, 0};
    public static String mActivity[] = {
            "Driving 0 0",
            "Cycling 0 0",
            "On Foot 0 0",
            "Running 0 0",
            "Still 0 0",
            "Walking 0 0",
            "Tilting 0 0",
            "Unknown 0 0"
    };
    public TextView textView0, textView1, textView2, textView3, textView4, textView5, textView6, textView7;

    //For timer
    TextView time;
    long starttime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedtime = 0L;
    int t = 1;
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysing);

        time = (TextView) findViewById(R.id.timer);
        textView0 = (TextView) findViewById(R.id.textView0);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);
        textView7 = (TextView) findViewById(R.id.textView7);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mStartButton = (Button) findViewById(R.id.startButton);
        mFinishButton = (Button) findViewById(R.id.finishButton);

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
                for (int j = 0; j < 8; j++) {
                    mCount[j] = 0;
                }
                mApiClient.connect();
                mStartButton.setVisibility(View.INVISIBLE);
                mFinishButton.setVisibility(View.VISIBLE);

                if (t == 1) {
                    starttime = SystemClock.uptimeMillis();
                    handler.postDelayed(updateTimer, 0);
                    t = 0;
                } else {
                    time.setTextColor(Color.BLUE);
                    timeSwapBuff += timeInMilliseconds;
                    handler.removeCallbacks(updateTimer);
                    t = 1;
                }
            }
        });

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApiClient.disconnect();

                //Saving file
                Toast.makeText(AnalysingActivity.this, "Saving file...", Toast.LENGTH_SHORT
                ).show();
                try {
                    saveFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AnalysingActivity.this, "Failed to save file!", Toast.LENGTH_LONG).show();
                }

                //Resetting timer
                starttime = 0L;
                timeInMilliseconds = 0L;
                timeSwapBuff = 0L;
                updatedtime = 0L;
                t = 1;
                secs = 0;
                mins = 0;
                milliseconds = 0;
                handler.removeCallbacks(updateTimer);

                //restarting application
                Intent intent = new Intent(AnalysingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public Runnable updateTimer = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - starttime;
            updatedtime = timeSwapBuff + timeInMilliseconds;
            secs = (int) (updatedtime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            milliseconds = (int) (updatedtime % 1000);
            time.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            time.setTextColor(Color.RED);

            textView0.setText(mActivity[0]);
            textView1.setText(mActivity[1]);
            textView2.setText(mActivity[2]);
            textView3.setText(mActivity[3]);
            textView4.setText(mActivity[4]);
            textView5.setText(mActivity[5]);
            textView6.setText(mActivity[6]);
            textView7.setText(mActivity[7]);

            handler.postDelayed(this, 0);
        }
    };

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
            file = new File(path + fileName + ".txt");

            PrintWriter out = new PrintWriter(new FileWriter(file));

            //Putting confidence as heading
            out.println("Confidence Level: " + Integer.toString(confidence));
            out.println("Time: " + time.getText().toString());
            out.println("");
            out.println("");
            time.setText("00:00:00");

            // Write each string in the array on a separate line
            for (int i = 0; i < 8; i++) {
                out.println(mActivity[i] + "\n");
            }

            out.close();
            Toast.makeText(AnalysingActivity.this, "File saved.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED))
            return true;
        else return false;
    }
}

