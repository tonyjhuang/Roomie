package com.roomie.roomie.api.models;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.roomie.roomie.api.FirebaseApi;
import com.roomie.roomie.api.FirebaseApiClient;

/**
 * Created by Rina on 10/10/15.
 */
public class Location {

    public LatLng getLatLng() {
        return new LatLng(this.latLng.latitude, this.latLng.longitude);
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = new GeoLocation(latLng.latitude, latLng.longitude);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id, String placeName) {
        this.id = id + '_'+ placeName;
    }

    public Location( String id, Place place ){
        /* Pusher into the database */
        this.setId(id, place.getAddress().toString());
        this.setLatLng(place.getLatLng());
        FirebaseApiClient.geofire.setLocation(this.id, this.latLng);
        count++;
    }

    private GeoLocation latLng;
    private String id;
    private String placeName;
    private static int count = 0;

}
