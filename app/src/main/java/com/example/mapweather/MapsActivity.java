package com.example.mapweather;

import android.location.Address;
import android.location.Geocoder;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    final String DEGREE  = "\u00b0";
    private GoogleMap mMap;
    private Button mBtn_go;
    private Button mWeather_btn;
    private String mPlaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //android os NetworkOnMainThreadException
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ZoomControls zoom=(ZoomControls)findViewById(R.id.zoom);
        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });
        mBtn_go = findViewById(R.id.btn_Go);
        mBtn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText location_text = findViewById(R.id.et_location);
                String loc = location_text.getText().toString();
                if(loc!=null && !loc.equals("")){
                    List<Address> addresses = null;
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addresses = geocoder.getFromLocationName(loc,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addresses.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Burası "+ capitalize(loc)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//                    Toast.makeText(MapsActivity.this, "X: "+ address.getLatitude()+ " Y: "
//                            + address.getLongitude() , Toast.LENGTH_LONG).show();
                }
            }
        });

        mWeather_btn = findViewById(R.id.weather_button);
        mWeather_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hava durumu getir toast bas
                EditText location_text = findViewById(R.id.et_location);
                String loc = location_text.getText().toString();
                if(loc!=null && !loc.equals("")){
                    List<Address> addresses = null;
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addresses = geocoder.getFromLocationName(loc,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addresses.get(0);
                    //LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    try {
                        getWeather(address.getLatitude(), address.getLongitude());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng istanbul = new LatLng(41.022482, 29.004391);
        mMap.addMarker(new MarkerOptions().position(istanbul).title("Marker in Istanbul"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(istanbul));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f )); // make zoom
    }

    public void getWeather(double lat, double lon) throws IOException, JSONException {

        Log.i("lat and lon", String.valueOf((int)lat) + String.valueOf((int)lon));
        //generated kodu kullanarak openweather api'dan json'u çekiyoruz
        OkHttpClient client = new OkHttpClient();

	// put your api key
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/data/2.5/weather?lat="
                        + (int)lat+"&lon="
                        + (int)lon
                        +"&APPID={APIKEY}")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        // reequest işliyoruz
        String jsonData = response.body().string();

        JSONObject json = new JSONObject(jsonData);

        // temperature
        JSONObject tempData = new JSONObject(json.getString("main")); // main blok
        double temp = Double.parseDouble(tempData.getString("temp")); // main-> temp aldık
        int temperature = (int) (temp -273.15); // 25 derece

        // weather description
        JSONArray weatherInfo = new JSONArray(json.getString("weather")); // weather blok
        JSONObject weatherData = weatherInfo.getJSONObject(0);
        String description = weatherData.getString("description");

        //weather -> clear sky
        // üsküdar
        mPlaceName = json.getString("name");

        Toast.makeText(MapsActivity.this, mPlaceName
                + ", Sıcaklık: " + String.valueOf(temperature)
                + DEGREE
                + "C, Durum: " + capitalize(description)
                , Toast.LENGTH_LONG).show();
    }
}





