package com.akshcabs.userloactionupdate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewAllUsers extends AppCompatActivity {

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient myclient;
    private Location lastLocation;
    private double dlatitude, dlongitude;
    boolean flag = false;
    private GoogleMap gmap;
    private List longitude;
    private List latitude;
    private List ForMarkers;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private LatLng mylatlng;
    private TextView textView;
    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);
        textView = findViewById(R.id.textView);

        longitude = new ArrayList<>();
        latitude = new ArrayList<>();
        ForMarkers = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapSecond);
        myclient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationPermission();

    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            flag = true;
            databaseReference = firebaseDatabase.getReference();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        LocationModel LM = ds.getValue(LocationModel.class);
                        longitude.add(LM.getLongitude());
                        latitude.add(LM.getLatitude());

                    }

                    for (int i = 0; i < longitude.size(); i++) {
                        dlatitude = (double) latitude.get(i);
                        dlongitude = (double) longitude.get(i);
                        //textView.append(""+dlatitude+"\t\t\t"+dlongitude+"\n");
                        storeLocation();
                    }
                    initMap();
                    //Toast.makeText(ViewAllUsers.this, "" + longitude, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ViewAllUsers.this, "Operation Cancelled !!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void storeLocation() {
        mylatlng = new LatLng(dlatitude, dlongitude);
        ForMarkers.add(mylatlng);
    }

    void initMap() {
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gmap = googleMap;
                gmap.clear();
                if (flag) {

                    MarkerOptions markerOptions = new MarkerOptions();

                    Toast.makeText(ViewAllUsers.this, "" + ForMarkers.size(), Toast.LENGTH_LONG).show();
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.male_icon);
                    for (int i = 0; i < ForMarkers.size(); i++) {
                        markerOptions.position((LatLng) ForMarkers.get(i));
                        //markerOptions.title("You are here");
                        markerOptions.icon(icon);
                        gmap.addMarker(markerOptions);
                    }

                    ForMarkers.clear();
                    longitude.clear();
                    latitude.clear();
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
                Toast.makeText(this, "Location Permission Denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
