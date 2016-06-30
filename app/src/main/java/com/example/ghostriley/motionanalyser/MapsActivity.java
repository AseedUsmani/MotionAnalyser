package com.example.ghostriley.motionanalyser;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, android.content.DialogInterface.OnClickListener {

    private GoogleMap mMap;
    Double lati, longi; //To place markers
    String latt, longg, markerTime; //To place detected markers
    String selectedLat, selectedLong, selectedTime; //To extract location of selected marker
    Marker selectedMarker;
    int selectedIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        final SharedPreferences sharedPreferences = getSharedPreferences("Data", MODE_PRIVATE);

        // Add a marker in detected location and move the camera
        LatLng latLng;
        for (int i = 1; i < 7; i++) {
            latt = sharedPreferences.getString(Integer.toString(i) + "a", "");
            longg = sharedPreferences.getString(Integer.toString(i) + "o", "");

            if (latt != "" && longg != "") {
                lati = Double.parseDouble(latt);
                longi = Double.parseDouble(longg);
                latLng = new LatLng(lati, longi); // lati, longi are latitude and longitude of last detected location
                markerTime = sharedPreferences.getString(Integer.toString(i) + "t", "");

                //To set distinguishable marker titles
                if (i == 5) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Last Location, Time: " + markerTime));
                } else if (i == 6) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title("User Confirmed Location, Time: " + markerTime));
                } else {
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Detected Location, Time: " + markerTime));
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)             // Sets the center of the map to location user
                        .zoom(15)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                selectedMarker = marker;
                selectedLat = Double.toString(selectedMarker.getPosition().latitude);
                selectedLong = Double.toString(selectedMarker.getPosition().longitude);
                Log.e("Selected Lat, Long=", selectedLat + "," + selectedLong);

                for (int i = 1; i < 7; i++) {
                    Log.e("Marker lat, long=", sharedPreferences.getString(Integer.toString(i) + "a", "") + "," +
                            sharedPreferences.getString(Integer.toString(i) + "o", ""));
                    if (selectedLat.equals(sharedPreferences.getString(Integer.toString(i) + "a", ""))) {
                        Log.e("Lat matched=", sharedPreferences.getString(Integer.toString(i) + "a", "") + "," +
                                sharedPreferences.getString(Integer.toString(i) + "o", ""));
                        if (selectedLong.equals(sharedPreferences.getString(Integer.toString(i) + "o", ""))) {
                            Log.e("Long matched", sharedPreferences.getString(Integer.toString(i) + "a", "") + "," +
                                    sharedPreferences.getString(Integer.toString(i) + "o", ""));
                            selectedIndex = i;
                            selectedTime = sharedPreferences.getString(Integer.toString(i) + "t", "");
                        }
                    }
                }
                buildAlert();
                return true;
            }
        });
    }

    public void buildAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("Confirm Location");
        builder.setMessage("Click 'Yes' to confirm location, 'No' to delete location:");
        builder.setPositiveButton("Yes", this);
        builder.setNegativeButton("No", this);
        builder.setNeutralButton("Cancel", this);
        builder.create()
                .show();
    }

    public void onClick(DialogInterface dialog, int which) {
        final SharedPreferences sharedPreferences = getSharedPreferences("Data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Intent mapIntent = new Intent(MapsActivity.this, MapsActivity.class);
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        if (which == DialogInterface.BUTTON_NEGATIVE) {
            editor.remove(Integer.toString(selectedIndex) + "a");
            editor.remove(Integer.toString(selectedIndex) + "o");
            editor.remove(Integer.toString(selectedIndex) + "t");

            //Shifting marker index to remove blank slot from where values were deleted
            if (selectedIndex != 6) {
                int i = selectedIndex;
                while (i > 1) {
                    editor.putString(Integer.toString(i) + "a", sharedPreferences.getString(Integer.toString(i - 1) + "a", ""));
                    editor.putString(Integer.toString(i) + "o", sharedPreferences.getString(Integer.toString(i - 1) + "o", ""));
                    editor.putString(Integer.toString(i) + "t", sharedPreferences.getString(Integer.toString(i - 1) + "t", ""));
                    i--;
                }
            }
            editor.commit();
            startActivity(mapIntent);

        } else if (which == DialogInterface.BUTTON_POSITIVE) {
            editor.clear();
            editor.putString("6a", selectedLat);
            editor.putString("6o", selectedLong);
            editor.putString("6t", selectedTime);
            editor.commit();
            startActivity(mapIntent);
        } else if (which == DialogInterface.BUTTON_NEUTRAL) {
            //For now, intentionally blank
        }
    }
}
