package org.hisp.dhis.android.sdk.ui.fragments.dataentry;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.controllers.GpsController;
import org.hisp.dhis.android.sdk.ui.activities.INavigationHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Ida Marie Frøseth
 */
public class MapsFragment extends Fragment {
    GoogleMap googleMap;
    /**
     * The clicked position marker
     */
    Marker clickedPosition;
    /**
     * The button to save the location in form
     */
    private Button setLocationButton;
    /**
     *mLatitude and mLongitude are fields in the rows and have to be initialized when the fragment is created
     */
    private EditText mLatitude = null;
    private EditText mLongitude = null;
    /**
     * Return the id for this
     */
    public static final String TAG = MapsFragment.class.getSimpleName();
    private INavigationHandler mNavigationHandler;
    /*
    * JSON api for getting events data
     */
    private static String url ="https://play.dhis2.org/dev/api/events.json";



    /**
     * Create the fragment and send with the lat and lng rows in the table
     * @param lat
     * @param lng
     * @return
     */
    public static Fragment newInstance(EditText lat, EditText lng){
        MapsFragment mapsFrag = new MapsFragment();
        mapsFrag.mLatitude = lat;
        mapsFrag.mLongitude = lng;
        return mapsFrag;
    }


    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Creates the view and add the map to the fragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() instanceof INavigationHandler) {
            mNavigationHandler = (INavigationHandler) getActivity();
        } else {
            throw new IllegalArgumentException("Activity must " +
                    "implement INavigationHandler interface");
        }
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        setUpMapIfNeeded();
        setSetLocationButton(view);
        setLocationButton.setEnabled(false);
        return view;
    }

    /**
     * Set the setLocationButton to listen for click and pop the stack.
     * @param view
     */
    private void setSetLocationButton(View view){
        setLocationButton =  (Button) view.findViewById(R.id.set_coordinate);
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d("maps", "SelectedCoordinate: " + clickedPosition.getPosition().toString());
                if (mLatitude != null && mLongitude != null) {
                    mLatitude.setText(Double.toString((double) clickedPosition.getPosition().latitude));
                    mLongitude.setText(Double.toString((double) clickedPosition.getPosition().longitude));
                } else {
                    Log.e("MapFragment Error", "Not initialized with callback pointers");
                }
                mNavigationHandler.onBackPressed();
            }
        });
    }
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
             googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_element)).getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
                startEventMarker();
            }
        }
    }

    /**
     * Set up the map with the camera focus on user location if available if not to central africa
     */
    private void setUpMap() {
        LatLng mapFocus = new LatLng(8, 21);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            Log.d("MapsFragment", "Permission for user location denied");
        }
        Location location = GpsController.getLocation();
        if (location != null) {
            mapFocus = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapFocus, 7));
        googleMap.setOnMapClickListener(new ClickedPosition(this));
    }

    /**
     * Release the map fragment when detached.
     */
    @Override
    public void onDetach() {
        System.out.println("******* ON detach");
        super.onDetach();
        removeMapFragment();
        // we need to nullify reference
        // to parent activity in order not to leak it
        mNavigationHandler = null;
    }

     /**
     *
     * Unregister the fragment from the dhis application eventbus
     */
    @Override
    public void onDestroyView() {
        System.out.println("******* ON destroy view");
        super.onDestroyView();
      //  Dhis2Application.getEventBus().unregister(this);
    }

    /**
     * Remove the map fragment by
     */
    private void removeMapFragment(){
        try {
            Fragment mapFrag = getChildFragmentManager().findFragmentById(R.id.map_element);
            if (mapFrag != null) {
                System.out.println("Removing the map");
                getChildFragmentManager().beginTransaction().remove(mapFrag)
                        .commit();
            }
        }catch(IllegalStateException e){
            //The state has already been saved
        }
    }

    /**
     * Initialize the menu
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        System.out.println("Menu created");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    /**
     * Add action to the menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == 16908332){
            mNavigationHandler.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @author Ida Marie Frøseth
     * Listener for clicked in the map
     */
    private class ClickedPosition implements GoogleMap.OnMapClickListener{

        MapsFragment map;

        /**
         *
         * @param map
         */
        public ClickedPosition(MapsFragment map){
            this.map = map;
        }

        /**
         * When a posistion in the map is clicked a maker will be displayed at the latLng position
         * @param latLng the clicked position
         */
        @Override
        public void onMapClick(LatLng latLng) {

            if(clickedPosition != null) {
                clickedPosition.remove();
            }
            setLocationButton.setEnabled(true);
            clickedPosition =  googleMap.addMarker(new MarkerOptions().position(latLng).title("Clicked position").alpha(0.8f));
        }
    }
    /**** Displaying Marker Options Start ****/
    /**
     * Methods for invoking marker
     */

    protected void startEventMarker() {
        System.out.println("****starting to Json****");
        DownloadJsonFile downloadJsonFile = new DownloadJsonFile();
        // Download the GeoJSON file
        downloadJsonFile.execute(url);
        //    System.out.println("invoked");
    }
    private class DownloadJsonFile extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                // Open a stream from the URL
                String userName = DhisController.getInstance().getSession().getCredentials().getUsername();
                String passWord = DhisController.getInstance().getSession().getCredentials().getPassword();
                HttpRequest request = HttpRequest.get(url).contentType("application/json").basic(userName,passWord);
                String jsonStr = request.body();

                // Convert result to JSONObject
                return new JSONObject(jsonStr.toString());
            } catch (Exception e) {
                Log.e("file cnt converted", " file could not be converted to a JSONObject");
            }
            return null;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                // De-serialize the JSON Object into an array of events objects

                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("events");
                    System.out.println("jsonArray" +jsonArray.toString());
                    System.out.println("length********" + jsonArray.length());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        // Create a marker for each city in the JSON data.
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        /**
                         * Set for example as the json url dont have sufficient coordinates to display events
                         */
                    /**
                        Double lat = 1.5;
                        Double lon = 0.0;
                       **/
                        Double lat = Double.parseDouble(jsonObj.getJSONObject("coordinate").getString("latitude"));
                        Double lng = Double.parseDouble(jsonObj.getJSONObject("coordinate").getString("longitude"));
                      //  System.out.println("JSON LAT LONG" + lat +"," +lng);
                        if(lat!=0.0 &&lng!=0.0) {
                            googleMap.addMarker(new MarkerOptions()
                                            .title("Registered Events-" + jsonObj.getString("event"))
                                            .snippet(jsonObj.getString("orgUnitName"))
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                            .position(new LatLng(lat,lng) //+ (i * 1.5), lon + (i * -0.01))
                                                    /**
                                                     * Uncommenting these two lines can get the value from the json object
                                                     */
                                                    //  Double.parseDouble(jsonObj.getJSONObject("coordinate").getString("latitude")),
                                                    //  Double.parseDouble(jsonObj.getJSONObject("coordinate").getString("longitude"))
                                            )
                            );
                        }
                    } } catch (JSONException e) {
                    Log.e("JsonException"+e.getMessage(),"");
                    e.printStackTrace();
                }
            }
        }
    }
}

