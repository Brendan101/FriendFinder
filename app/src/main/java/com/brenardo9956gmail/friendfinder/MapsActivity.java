package com.brenardo9956gmail.friendfinder;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.renderscript.Sampler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener{

    public final String TAG = "FirebaseDB: ";
    public final long UPDATE_INTERVAL = 30000; //30 seconds
    public final int REQ_CODE = 1000;

    private GoogleMap mMap;
    LocationManager locMan;
    private DatabaseReference mDatabase;

    Button friendButton;

    boolean mapReady, firstLoc, userReady;
    String uid, username, email, fList;
    Double latitude, longitude;
    long time;
    long lastUserUpdate;
    long lastFriendsUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        friendButton = (Button) findViewById(R.id.friendButton);
        friendButton.setOnClickListener(this);

        locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mapReady = false;
        firstLoc = true;
        userReady = false;
        latitude = 0d;
        longitude = 0d;
        time = 0;
        lastUserUpdate = 0;
        lastFriendsUpdate = 0;

        Intent intent = getIntent();
        getUserInfo(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locMan.removeUpdates(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void getUserInfo(Intent intent){

        String type = intent.getStringExtra("userType");

        if(type.equals(MainActivity.NEW_USER)){

            uid = intent.getStringExtra("uid");
            username = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
            fList = intent.getStringExtra("fList");
            userReady = true;

        }else if(type.equals(MainActivity.RETURN_USER)){

            uid = intent.getStringExtra("uid");

            //get the rest of the info from database
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    username = user.username;
                    email = user.email;
                    fList = user.fList;
                    userReady = true;
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "Error getting user " + uid);
                }
            });

        }

    }

    private void getUsersFromDatabase(){

        mDatabase.child("users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get all users in and show them on map
                        showUsersOnMap((Map<String,User>) dataSnapshot.getValue());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error getting users");
                    }
                });

    }

    private void showUsersOnMap(Map<String, User> users){

        //remove any previous markers, update all
        mMap.clear();
        Location userLoc = new Location("");
        userLoc.setLatitude(latitude);
        userLoc.setLongitude(longitude);

        //loop thru the entries
        for (Map.Entry<String, User> entry : users.entrySet()) {

            //Get this user's friends list
            Map user = (Map) entry.getValue();
            String uFList = (String) user.get("fList");

            //first check if users are friends
            if(uFList.contains(email)){

                //get friend's info
                String uname = (String) user.get("username");
                String uemail = (String) user.get("email");
                Double lat = (Double) user.get("latitude");
                Double lon = (Double) user.get("longitude");
                LatLng userPos = new LatLng(lat, lon);

                //no access to uid in this loop. But if username is same as
                //current user, change the title to "You"
                if (uemail.equals(email)) {
                    uname = "You";
                }

                //put this friend on the map
                mMap.addMarker(new MarkerOptions().position(userPos).title(uname));

            }

        }

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
        mapReady = true;
    }

    @Override
    public void onLocationChanged(Location loc) {

        Double lat = loc.getLatitude();
        Double lon = loc.getLongitude();

        long currentTime = System.currentTimeMillis();

        //update the user's info
        if(userReady && currentTime > lastUserUpdate + UPDATE_INTERVAL) {

            latitude = lat;
            longitude = lon;
            time = currentTime;

            updateUser();

            lastUserUpdate = currentTime;
        }

        //update the map
        if(mapReady && firstLoc) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
            mMap.setMinZoomPreference(13.0f);
            mMap.setMaxZoomPreference(20.0f);
            firstLoc = false;
        }

        if(mapReady && userReady && currentTime > lastFriendsUpdate + UPDATE_INTERVAL){

            getUsersFromDatabase();

            lastFriendsUpdate = currentTime;
        }

    }

    private void updateUser() {

        //update this user's information in the database
        User userForDatabase = new User(username, email, fList, latitude, longitude, time);
        mDatabase.child("users").child(uid).setValue(userForDatabase);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.friendButton:

                //open up friends list
                Intent friendIntent = new Intent(this, FriendsListActivity.class);
                friendIntent.putExtra("fList", fList);
                startActivityForResult(friendIntent, REQ_CODE);
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //get updated friends list from friends edit activity
        if (requestCode == REQ_CODE) {
            if(resultCode == Activity.RESULT_OK){
                fList = data.getStringExtra("fList");
                updateUser();
            }
            if (resultCode == Activity.RESULT_CANCELED) {/* EMPTY */}
        }
    }


}