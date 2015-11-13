package org.hisp.dhis.android.sdk.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {
    private Button selectPositionBtn;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    Marker clickedPosition;
    Button setLocationButton;
    public final int PICK_COORDINATE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("On create", "CREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        final Button cancelButton = (Button) findViewById(R.id.cancel_location);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d("maps", "cancelButtonClicked");
            }
        });

        setLocationButton = (Button) findViewById(R.id.set_coordinate2);
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d("maps", "SelectedCoordinate: " + clickedPosition.getPosition().toString());
            }
        });
        setLocationButton.setEnabled(false);
    }

    @Override
    protected void onResume() {
        Log.d("OnResume", "Resumed");
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //   android.os.Debug.stopMethodTracing();
    }
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_element))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
    private void setUpMap() {
        LatLng gjovik = new LatLng(60.1, 23.1);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.addMarker(new MarkerOptions().position(gjovik).title("YourPosition").alpha(0.7f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(gjovik));
        mMap.setOnMapClickListener(new ClickedPosition(this));
    }


    private GoogleMapOptions getOptions(){
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_TERRAIN)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true);
        return options;
    }

    /**
     * Listener for clicked
     */
    private class ClickedPosition implements GoogleMap.OnMapClickListener{

        MapsActivity ma;

        public ClickedPosition(MapsActivity ma){
            this.ma = ma;
        }
        @Override
        public void onMapClick(LatLng latLng) {

            //HERE WE SHOULD OPEN A NEW ACTIVITY
            if(clickedPosition != null) {
                clickedPosition.remove();
            }
            setLocationButton.setEnabled(true);
            clickedPosition =  mMap.addMarker(new MarkerOptions().position(latLng).title("Clicked position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            /**      Log.d("ClickedLatLng", latLng.toString());
             getFragmentManager()
             .beginTransaction()
             .add(R.id.map, new ChoosePositionDialog())
             .commit();**/
        }
    }
}
 /**extends FragmentActivity {






    **/
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

  /**




/**
 **/


