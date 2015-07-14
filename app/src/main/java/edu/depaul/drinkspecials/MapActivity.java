package edu.depaul.drinkspecials;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity{
    private static final String TAG = "";
    private GoogleMap mMap;
    private Location location;
    private LatLng currentPosition;
    private VisibleRegion cPP;
    private String provider;
    private LocationManager locationManager;
    private ArrayList<String> qString;
    private static final float ZOOM_LEVEL = 11.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

        provider = locationManager.getBestProvider(criteria,true);
        location = locationManager.getLastKnownLocation(provider);

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        currentPosition = new LatLng(lat, lon);

        mMap.addMarker(new MarkerOptions().position(currentPosition).title("CURRENT"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, ZOOM_LEVEL));
        Toast.makeText(getApplicationContext(), "lat: " + lat + " lon: " + lon, Toast.LENGTH_LONG).show();

                mMap.setOnMapClickListener(
                        new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                cPP = mMap.getProjection().getVisibleRegion();
                                double lat = currentPosition.latitude;
                                double lng = currentPosition.longitude;

                                new Thread(new Client(lat,lng)).start();


                                qString = Client.getIList();

                                if(qString!=null) {
                                    for (String s : qString) {
                                        Log.e(TAG, "Input ArrayList received from serverIS NOT NULL " + s);
                                        //s = s.substring(2);
                                        String[] spl = s.split(";");
                                        String[] cords = spl[0].split(",");
                                        lat = Double.parseDouble(cords[0]);
                                        lng = Double.parseDouble(cords[1]);
                                        String name = spl[1];
                                        String specials = spl[2];
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(name).snippet(specials));
                                    }
                                }
                                else{
                                    Log.e(TAG, "Input ArrayList received from server==NULL");
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(currentPosition.latitude+1,currentPosition.longitude+1)).title("ArrayList==NULL"));
                                }
                             }
                         }
                );
    }
    }
