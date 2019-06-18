package com.bhavya.googlemaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                LatLng India = new LatLng(location.getLatitude(),location.getLongitude());//for south and west , use negative signs.
                mMap.clear();//clear all the markers otherwise there will be two or more
                mMap.addMarker(new MarkerOptions().position(India).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(India,20));//1to 20(max zoomed in is 20
                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address>  list=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(list!=null&&list.size()>0)
                    {
                        String address="";
                        if(list.get(0).getSubThoroughfare()!=null)
                        {
                            address+=list.get(0).getSubThoroughfare()+ " ";
                        }
                        if(list.get(0).getThoroughfare()!=null)
                        {
                            address+=list.get(0).getThoroughfare()+ " \n";
                        }
                        if(list.get(0).getLocality()!=null)
                        {
                            address+=list.get(0).getLocality()+ "\n ";
                        }
                        if(list.get(0).getPostalCode()!=null)
                        {
                            address+=list.get(0).getPostalCode()+ "\n ";
                        }
                        
                        if(list.get(0).getLocality()!=null)
                        {
                            address+=list.get(0).getLocality()+ " ";
                        }
                        if(list.get(0).getCountryName()!=null)
                        {
                            address+=list.get(0).getCountryName()+ ", ";
                        }
                        Toast.makeText(MapsActivity.this,address,Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
        };
        if(Build.VERSION.SDK_INT<23)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                //We want that the moment the app opens , it should be at last knwn location user and then update it to new locatin
                Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng last = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());//for south and west , use negative signs.
                mMap.clear();//clear all the markers otherwise there will be two or more
                mMap.addMarker(new MarkerOptions().position(last).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(last,10));//1to 20(max zoomed in is 20)

            }
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);//set the map type. This type sets the satellite image of earth as a background.

    }
}
