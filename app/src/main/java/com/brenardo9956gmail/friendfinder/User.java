package com.brenardo9956gmail.friendfinder;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String fList;
    public Double latitude;
    public Double longitude;
    public long time;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String fList, Double latitude, Double longitude, long time) {
        this.username = username;
        this.email = email;
        this.fList = fList;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

}
