package com.example.locations;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class rithik extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locaman;
    LocationListener loclis;
    @Override
    public void onMapLongClick(LatLng latLng)
    {Geocoder geo=new Geocoder(getApplicationContext(),Locale.getDefault());
    String ad=" ";
    try {
        List<Address> l = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
        if(l != null && l.size()>0)
        {
            if(l.get(0).getThoroughfare() != null)
                if(l.get(0).getSubThoroughfare() != null)
                {
                    ad=ad+l.get(0).getSubThoroughfare()+" ";
                    ad=ad+l.get(0).getThoroughfare();
                }
        }
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    if(ad==" ")
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
         ad=sdf.format(new Date());
    }

        mMap.addMarker(new MarkerOptions().position(latLng).title(ad));
    MainActivity.places.add(ad);
    MainActivity.los.add(latLng);
    MainActivity.arrayAdapter.notifyDataSetChanged();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locaman.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,loclis);
            }
    }
    public void centerMaponLocation(Location location,String s) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        if (s != "Your location") {
            mMap.addMarker(new MarkerOptions().position(loc).title(s));

        }


        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rithik);
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
        mMap.setOnMapLongClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        Intent intent=getIntent();
        if(intent.getIntExtra("placenum",0)==0) {


            locaman = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            loclis = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {


                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locaman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, loclis);
                Location lastKnown = locaman.getLastKnownLocation(locaman.GPS_PROVIDER);
                while (lastKnown == null) {
                    Log.i("dist", "stop");
                }
                centerMaponLocation(lastKnown, "Your location");


            }
        }
        else
        {
            mMap.clear();
            Location rit=new Location(LocationManager.GPS_PROVIDER);
            rit.setLatitude(MainActivity.los.get(intent.getIntExtra("placenum",0)).latitude);
            rit.setLongitude(MainActivity.los.get(intent.getIntExtra("placenum",0)).longitude);
            centerMaponLocation(rit,MainActivity.places.get(intent.getIntExtra("placenum",0)));

        }

        // Add a marker in Sydney and move the camera


    }
}
