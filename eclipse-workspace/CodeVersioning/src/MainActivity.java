package uk.ac.mmu.electricchargingproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.wang.avi.AVLoadingIndicatorView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.app.Activity;
import android.content.IntentSender;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {


    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;


    MyDataBase mydataBase;
    double latitude_current;
    double longitude_current;
    LatLng latLngCurrent;
    private ListView listView;
    AVLoadingIndicatorView avi;

    ArrayList<MyDataModel> myDataModelArrayList;

    Boolean isFirstRun;




    @Override
    protected void onCreate(Bundle savedInstanceState) {     /////////////  Main Activity get the current user location
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpGClient();                                      ///////////   http://www.digitstory.com/enable-gps-automatically-android/

        mydataBase= new MyDataBase(this);           ///////////   create object of database class
        avi=findViewById(R.id.avi);                         ///////////    link the loading bar with java from xml

        latitude_current  = 51.509865;
        longitude_current = -0.118092; // Default values

        latLngCurrent = new LatLng(latitude_current,longitude_current);




    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null)
        {
            /////////////  when the user location changed, we get the latitude and longitude of the user

            Double latitude=mylocation.getLatitude();
            Double longitude=mylocation.getLongitude();

            latitude_current =latitude;
            longitude_current=longitude;


            // Will check if connected to the internet when app is first ran
            isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
            if (isFirstRun)
            {
                // If phone is connected to the internet then it will update with the latest database from the website
                if(InternetConnection.checkConnection(this))
                {
                    update_data();
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();

                }
                // If not connected error message will appear saying that network is not available
                else {
                    Toast.makeText(this, "Network is not available ", Toast.LENGTH_SHORT).show();
                }

            }
            else
            {
                reload();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private void getMyLocation(){
        if(googleApiClient!=null)
        {
            if (googleApiClient.isConnected())
            {
                int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation =                     LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(10000);
                    locationRequest.setFastestInterval(10000);
                    locationRequest.setSmallestDisplacement(10);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(MainActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(MainActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    // If okay will get location
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                        // If not will cancel
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){ // Checking if permission has been granted
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation(); // If it does not have permission it will ask to get location
        }

    }


    //// get the location permission from user
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED)
        {
            getMyLocation();
        }


    }


    public void update_data() {


        startAnim();

        String mJSONURLString = "http://chargepoints.dft.gov.uk/api/retrieve/registry/format/json/lat/"+latitude_current+"/long/"+longitude_current+"/dist/6/";
        myDataModelArrayList = new ArrayList<>();




        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, mJSONURLString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            // Getting the whole JSON object from the response
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("ChargeDevice");

                            for(int i=0;i<array.length();i++)
                            {
                                JSONObject student = array.getJSONObject(i);

                                String station_name = student.getString("ChargeDeviceName");
                                Double station_latitude = student.getJSONObject("ChargeDeviceLocation").getDouble("Latitude");
                                Double station_longitude = student.getJSONObject("ChargeDeviceLocation").getDouble("Longitude");

                                MyDataModel myDataModel = new MyDataModel();
                                myDataModel.setName(station_name);
                                myDataModel.setLatitude(station_latitude);
                                myDataModel.setLongitude(station_longitude);

                                Log.e("wah",station_name );
                                myDataModelArrayList.add(myDataModel);
                            }

                            mydataBase.add_Station(myDataModelArrayList);
                            reload();




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("wah",error.getLocalizedMessage());
            }
        });

        // Add the request to the RequestQueue
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);




    }

    public void reload()
    {

        latLngCurrent = new LatLng(latitude_current,longitude_current);    /////  current location of the user
        ArrayList<MyDataModel> datalist =  mydataBase.getAllContacts(latLngCurrent);    /////  this will get the all data from the database

        Collections.sort(datalist, new Comparator<MyDataModel>() {     /////  this will show the nearest gas station on the top and sort
            @Override
            public int compare(MyDataModel u1, MyDataModel u2) {
                return u1.getDistance().compareTo(u2.getDistance());
            }
        });
        ///////  This is the customer adapter, will show the data in list view
        CustomAdaper customAdaper=  new CustomAdaper(getApplicationContext(),datalist,latitude_current,longitude_current);
        listView =findViewById(R.id.listview);
        listView.setAdapter(customAdaper);
        stopAnim();                                                 ////////  This will hide the loading screen


    }

    ////  Show loading screen
    void startAnim(){
        avi.show();
        // or avi.smoothToShow();
    }
    /////  Hide loading screen
    void stopAnim(){
        avi.hide();
        // or avi.smoothToHide();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        new MenuInflater(this).inflate(R.menu.mymenu, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Once you click the reload button it will check if connected and will delete the current and update the database
        if (item.getItemId() == R.id.reload)
        {
            if(InternetConnection.checkConnection(this))
            {
                mydataBase.delete_all();
                update_data();
            }
            else {
                Toast.makeText(this, "Network is not available ", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }
}