package org.hisp.dhis.android.sdk.ui.fragments.dataentry;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MapsFragment extends Fragment {
    MapFragment mapFragment;
    GoogleMap googleMap;
    //private GoogleMap googleMap;
    Marker clickedPosition;
    private Button setLocationButton;
    private EditText mLatitude = null;
    private EditText mLongitude = null;
    public static final String TAG = MapsFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

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

        System.out.println("creating the fragment");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("OnCreateView", "createView");
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        setUpMapIfNeeded();
        setLocationButton = (Button) view.findViewById(R.id.set_coordinate);
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d("maps", "SelectedCoordinate: " + clickedPosition.getPosition().toString());
                mLatitude.setText(Double.toString((double) clickedPosition.getPosition().latitude));
                mLongitude.setText(Double.toString((double) clickedPosition.getPosition().longitude));
                getFragmentManager().popBackStack();
                /// /mListener.onSetCoordinateClicked();
            }
        });
        setLocationButton.setEnabled(false);
        return view;
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            System.out.println("**************" + getChildFragmentManager().findFragmentById(R.id.map_element));
            googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_element)).getMap();
      //      googleMap = mapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
    //    googleMap.addMarker(new MarkerOptions().position(gjovik).title("YourPosition").alpha(0.7f));
        LatLng mapFocus = new LatLng(8, 21);
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mapFocus));
      //  googleMap.moveCamera(CameraUpdateFactory.zoomBy(3));
        googleMap.setOnMapClickListener(new ClickedPosition(this));
    }

    @Override
    public void onDetach() {
        System.out.println("******* ON detach");
        super.onDetach();
        mListener = null;
        removeMapFragment();
    }

    @Override
    public void onResume() {
        System.out.println("******* ON resume");
        super.onResume();
    }

    @Override
    public void onPause() {
        System.out.println("******* ON pause");
        super.onPause();
        removeMapFragment();
        Dhis2Application.getEventBus().unregister(this);
    }

    @Override
    public void onDestroy() {
        System.out.println("******* ON destroy");
        super.onDestroy();
        removeMapFragment();
        Dhis2Application.getEventBus().unregister(this);
    }

    @Override
    public void onDestroyView() {
        System.out.println("******* ON destroy view");
        super.onDestroyView();
        removeMapFragment();
        Dhis2Application.getEventBus().unregister(this);
    }

    private void removeMapFragment(){
        super.onDestroyView();

        Fragment mapFrag = getFragmentManager().findFragmentById(R.id.map_element);
        if (mapFrag != null) {

            getFragmentManager().beginTransaction().remove(mapFrag)
                    .commit();
            mapFrag = null;
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void onSetCoordinateClicked();
    }
    /**
     * Listener for clicked
     */
    private class ClickedPosition implements GoogleMap.OnMapClickListener{

        MapsFragment ma;

        public ClickedPosition(MapsFragment ma){
            this.ma = ma;
        }
        @Override
        public void onMapClick(LatLng latLng) {

            //HERE WE SHOULD OPEN A NEW ACTIVITY
            if(clickedPosition != null) {
                clickedPosition.remove();
            }
            setLocationButton.setEnabled(true);
            System.out.println(latLng);
            clickedPosition =  googleMap.addMarker(new MarkerOptions().position(latLng).title("Clicked position").alpha(0.7f));
        }
    }
}
