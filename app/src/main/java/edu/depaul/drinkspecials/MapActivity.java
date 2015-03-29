package edu.depaul.drinkspecials;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import http.helper.HttpRequests;

public class MapActivity extends FragmentActivity {

    private static final String TAG = "";

    private GoogleMap mMap;
    private Location location;
    private LatLng currentPosition;
    private VisibleRegion cPP;
    private LatLngBounds cpBounds;
    private String provider;
    private LocationManager locationManager;

    private static final float ZOOM_LEVEL = 11.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

        provider = locationManager.getBestProvider(criteria,true);
        location = locationManager.getLastKnownLocation(provider);


        double lat = location.getLatitude();
        double lon = location.getLongitude();

        currentPosition = new LatLng(lat, lon);

        //mMap.addMarker(new MarkerOptions().position(currentPosition).title("CURRENT"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, ZOOM_LEVEL));
        Toast.makeText(getApplicationContext(), "lat: " + lat + " lon: " + lon, Toast.LENGTH_LONG).show();

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng latLng) {

                        cPP = mMap.getProjection().getVisibleRegion();
                        cpBounds = cPP.latLngBounds;


                        try {
                            HashMap<String, LinkedList<Double>> map = HttpRequests.getLocalBars(cpBounds.southwest.latitude, cpBounds.northeast.latitude, cpBounds.southwest.longitude, cpBounds.northeast.longitude);
                            for (Map.Entry<String, LinkedList<Double>> bar : map.entrySet()) {
                                String barName = bar.getKey();
                                LinkedList<Double> list = bar.getValue();
                                Double lon = list.pop();
                                Double lat = list.pop();
                                int bid = list.pop().intValue();
                                StringBuilder sb = new StringBuilder();
                                for(String special : HttpRequests.getBarSpecials(bid)){
                                    sb.append(special + " ");
                                }
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(barName).snippet(sb.toString()));
                            }

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
    }
    }
