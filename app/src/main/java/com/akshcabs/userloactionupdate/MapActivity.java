package com.akshcabs.userloactionupdate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.akshcabs.userloactionupdate.RegistrationActivity.mAuth;

public class MapActivity extends AppCompatActivity {

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient myclient;
    private Location lastLocation;
    private double dlatitude, dlongitude;
    boolean flag = false;
    private GoogleMap gmap;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LatLng mylatlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        firebaseDatabase = FirebaseDatabase.getInstance();

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        myclient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationPermission();

    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            flag = true;
            //Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();

            myclient.getLastLocation().addOnSuccessListener
                    (new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                lastLocation = location;
                                dlatitude = lastLocation.getLatitude();
                                dlongitude = lastLocation.getLongitude();
                                sendUserDatatoFirebase();
                            }
                            initMap();
                        }
                    });

        }
    }

    private void sendUserDatatoFirebase() {
        databaseReference = firebaseDatabase.getReference(mAuth.getUid());
        mylatlng = new LatLng(dlatitude, dlongitude);
        databaseReference.setValue(mylatlng);
    }

    void initMap() {
        //Toast.makeText(this, "Initialize Map", Toast.LENGTH_SHORT).show();
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gmap = googleMap;
                if (flag) {

                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylatlng, 13.0f));
                    gmap.setMyLocationEnabled(true);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title("You are Here");
                    markerOptions.position(mylatlng);
                    gmap.addMarker(markerOptions);

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void ViewAll(View view) {
        startActivity(new Intent(MapActivity.this,ViewAllUsers.class));
    }

    public void SignOut(View view) {
        mAuth.signOut();
        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        //removes popup type of screen
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(MapActivity.this,RegistrationActivity.class));
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent service=new Intent(MapActivity.this,LiveUpdateService.class);
        startService(service);
        Toast.makeText(this, "Tracking Enabled.", Toast.LENGTH_SHORT).show();
    }
}
