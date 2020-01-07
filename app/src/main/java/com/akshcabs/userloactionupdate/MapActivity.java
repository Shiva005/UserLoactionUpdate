package com.akshcabs.userloactionupdate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.akshcabs.userloactionupdate.RegistrationActivity.mAuth;

public class MapActivity extends AppCompatActivity {

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient myclient;
    private Location lastLocation;
    private double dlatitude, dlongitude;
    boolean flag = false;
    private GoogleMap gmap;
    private List longitude;
    private List latitude;
    private List ForMarkers;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LatLng mylatlng;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        textView=findViewById(R.id.textView);
        longitude = new ArrayList<>();
        latitude = new ArrayList<>();
        ForMarkers = new ArrayList<>();

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
            Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();

            myclient.getLastLocation().addOnSuccessListener
                    (new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                lastLocation = location;
                                dlatitude = lastLocation.getLatitude();
                                dlongitude = lastLocation.getLongitude();
                                sendUserDatatoFirebase();
                                storeLocation();
                            }

                            for (int i = 0; i < longitude.size(); i++) {
                                dlatitude = (double) latitude.get(i);
                                dlongitude = (double) longitude.get(i);

                                storeLocation();
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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    Toast.makeText(MapActivity.this, ""+ds, Toast.LENGTH_SHORT).show();

                    LocationModel loc = ds.getValue(LocationModel.class);
                    LatLng location = new LatLng(loc.getLatitude(), loc.getLongitude());
                    ForMarkers.add(location);
                    textView.append(location+"\n");
                    //textView.append(""+loc.getLatitude()+"\t"+loc.getLongitude()+"\n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void storeLocation() {
        LatLng mylatlng = new LatLng(dlatitude, dlongitude);
        ForMarkers.add(mylatlng);
    }

    void initMap() {
        Toast.makeText(this, "Initialize Map", Toast.LENGTH_SHORT).show();
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gmap = googleMap;
                if (flag) {

                    /*LatLng mylatlng = new LatLng(dlatitude, dlongitude);
                    ForMarkers.add(mylatlng);*/

                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylatlng, 13.0f));
                    gmap.setMyLocationEnabled(true);
                    MarkerOptions markerOptions = new MarkerOptions();

                    Toast.makeText(MapActivity.this, "" + ForMarkers.size(), Toast.LENGTH_LONG).show();

                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.male_icon);
                    for (int i = 0; i < ForMarkers.size(); i++) {
                        markerOptions.position((LatLng) ForMarkers.get(i));
                        //markerOptions.title("You are here");
                        markerOptions.icon(icon);
                        gmap.addMarker(markerOptions);
                    }
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

}
