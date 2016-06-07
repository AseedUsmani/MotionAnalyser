package com.example.ghostriley.motionanalyser;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by GhostRiley on 28/05/2016.
 */


public class ActivityRecognizedService extends IntentService {

    AnalysingActivity mObject=new AnalysingActivity();


        public static String mActivity[]= {
                "Default Data2",
                "Default Data2",
                "Default Data2",
                "Default Data2",
                "Default Data2",
                "Default Data2",
                "Default Data2"};

        public static int[] count={0, 0, 0, 0, 0, 0, 0};
        int confidence;


        public ActivityRecognizedService() {
            super("ActivityRecognizedService");
        }

        public ActivityRecognizedService(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            if (ActivityRecognitionResult.hasResult(intent)) {
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                handleDetectedActivities(result.getProbableActivities());
            }
        }

        private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
            confidence=mObject.confidence;
            for (DetectedActivity activity : probableActivities) {
                switch (activity.getType()) {
                    case DetectedActivity.IN_VEHICLE: {
                        if(activity.getConfidence()>=confidence) {
                            count[0]++;
                        }
                        mActivity[0]="In Vehicle: "+Integer.toString(activity.getConfidence())+" "+Integer.toString(count[0]);
                        Log.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence()+" "+Integer.toString(count[0]));
                        break;
                    }
                    case DetectedActivity.ON_BICYCLE: {
                        if(activity.getConfidence()>=confidence) {
                            count[1]++;
                        }
                        mActivity[1]="Cycling: "+Integer.toString(activity.getConfidence())+" "+Integer.toString(count[1]);
                        Log.e("ActivityRecogition", "Cycling: " + activity.getConfidence()+" "+Integer.toString(count[1]));
                        break;
                    }
                    case DetectedActivity.ON_FOOT: {
                        if(activity.getConfidence()>=confidence) {
                            count[2]++;
                        }
                        mActivity[2]="On Foot: "+Integer.toString(activity.getConfidence())+" "+Integer.toString(count[2]);
                        Log.e("ActivityRecogition", "On foot: " + activity.getConfidence()+" "+Integer.toString(count[2]));
                        break;
                    }
                    case DetectedActivity.RUNNING: {
                        if(activity.getConfidence()>=confidence) {
                            count[3]++;
                        }
                        mActivity[3]="Running: "+Integer.toString(activity.getConfidence())+" "+Integer.toString(count[3]);
                        Log.e("ActivityRecogition", "Running: " + activity.getConfidence()+" "+Integer.toString(count[3]));
                        break;
                    }
                    case DetectedActivity.STILL: {
                        if(activity.getConfidence()>=confidence) {
                            count[4]++;
                        }
                        mActivity[4]="Still: "+Integer.toString(activity.getConfidence())+" "+Integer.toString(count[4]);
                        Log.e("ActivityRecogition", "Still: " + activity.getConfidence()+" "+Integer.toString(count[4]));
                        break;
                    }

                    case DetectedActivity.WALKING: {
                        if(activity.getConfidence()>=confidence) {
                            count[5]++;
                        }
                        mActivity[5]="Walking: "+Integer.toString(activity.getConfidence())+" "+Integer.toString(count[5]);
                        Log.e("ActivityRecogition", "Walking: " + activity.getConfidence()+" "+Integer.toString(count[5]));
                        break;
                    }
                    case DetectedActivity.UNKNOWN: {
                        if(activity.getConfidence()>=confidence) {
                            count[6]++;
                        }
                        mActivity[6]="Unknown: "+Integer.toString(activity.getConfidence())+" "+Integer.toString(count[6]);
                        Log.e("ActivityRecogition", "Unknown: " + activity.getConfidence()+" "+Integer.toString(count[6]));
                        break;
                    }
                }
            }
        }
}
