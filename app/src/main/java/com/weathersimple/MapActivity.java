package com.weathersimple;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private static final int REQUEST_PERMISSION_LOCATION = 1;
  private static final String LAT = "latitude";
  private static final String LON = "longitude";

  private GoogleMap map;
  private GoogleApiClient googleApiClient;
  private Location lastLocation;

  private double lat = 200;
  private double lon = 200;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    setLastKnownLocationIfPossible();
  }

  @Override
  protected void onStart() {
    if (googleApiClient != null) googleApiClient.connect();
    super.onStart();
  }

  @Override
  protected void onStop() {
    if (googleApiClient != null) googleApiClient.disconnect();
    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.map_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.save: {
        Intent data = new Intent();
        data.putExtra(LAT, lat);
        data.putExtra(LON, lon);
        setResult(RESULT_OK, data);
        finish();
        break;
      }
      default: break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    map = googleMap;
    setDefaultLocation();
    map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
      @Override
      public void onMarkerDragStart(Marker marker) {
      }

      @Override
      public void onMarkerDrag(Marker marker) {
      }

      @Override
      public void onMarkerDragEnd(Marker marker) {
        lat = marker.getPosition().latitude;
        lon = marker.getPosition().longitude;
      }
    });
    UiSettings settings = map.getUiSettings();
    settings.setCompassEnabled(true);
    settings.setMapToolbarEnabled(true);
    settings.setAllGesturesEnabled(true);
    settings.setMyLocationButtonEnabled(true);
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    if (lastLocation != null) {
      lat = lastLocation.getLatitude();
      lon = lastLocation.getLongitude();
      LatLng userLocation = new LatLng(lat, lon);
      map.addMarker(new MarkerOptions()
          .position(userLocation)
          .draggable(true));
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 7));
    }
  }

  @Override
  public void onConnectionSuspended(int i) {
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
  }

  private void setLastKnownLocationIfPossible() {
    checkPermissions();
  }

  private void checkPermissions() {
    if (isLocationPermissionGranted()) {
      onPermissionReceived();
    } else {
      ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
    }
  }

  private boolean isLocationPermissionGranted() {
    int locationPermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
        android.Manifest.permission.ACCESS_COARSE_LOCATION);
    return locationPermissionCheck == PackageManager.PERMISSION_GRANTED;
  }

  private void onPermissionReceived() {
    createGoogleApiClient();
  }

  private void createGoogleApiClient() {
    if (googleApiClient == null) {
      googleApiClient = new GoogleApiClient.Builder(this)
          .addConnectionCallbacks(MapActivity.this)
          .addOnConnectionFailedListener(this)
          .addApi(LocationServices.API)
          .build();
    }
  }

  private void setDefaultLocation() {
    map.addMarker(new MarkerOptions()
        .position(new LatLng(0, 0))
        .draggable(true));
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_PERMISSION_LOCATION) {
      if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(getApplicationContext(), "show here dialog with explanation", Toast.LENGTH_LONG).show();
      } else {
        onPermissionReceived();
      }
    }
  }
}