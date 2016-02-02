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

public class MapActivity extends FragmentActivity{
    private static final String TAG = "";
    private static final float ZOOM_LEVEL = 11.5f;
    private GoogleMap mMap;
    private Location location;
    private LatLng currentPosition;
    private String provider;
    private LocationManager locationManager;

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
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        getLocation();
        getLatLong();

                mMap.setOnMapClickListener(
                        new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                double lat = currentPosition.latitude;
                                double lng = currentPosition.longitude;

                                new Thread(new Client(lat,lng)).start();
                                if(Client.getIList()!=null) {
                                    for (String s : Client.getIList()) {
                                        Log.e(TAG, "Input ArrayList received from serverIS NOT NULL " + s);
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

        public Criteria setupCriteria(){
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            return criteria;
        }

        public void getLocation(){
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            provider = locationManager.getBestProvider(setupCriteria(),true);
            location = locationManager.getLastKnownLocation(provider);
        }

        public void getLatLong(){
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            getCurrentPosition(lat, lon);
        }

        public void getCurrentPosition(double lat, double lon){
            currentPosition = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(currentPosition).title("CURRENT"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, ZOOM_LEVEL));
            Toast.makeText(getApplicationContext(), "lat: " + lat + " lon: " + lon, Toast.LENGTH_LONG).show();
        }
    }