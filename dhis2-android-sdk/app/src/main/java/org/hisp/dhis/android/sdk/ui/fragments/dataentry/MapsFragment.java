package org.hisp.dhis.android.sdk.ui.fragments.dataentry;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.hisp.dhis.android.sdk.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MapsFragment extends Fragment {
    GoogleMap googleMap;
    //private GoogleMap googleMap;
    Marker clickedPosition;
    Button setLocationButton;

    private OnFragmentInteractionListener mListener;


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
       // setUpMapIfNeeded();
        final Button cancelButton = (Button) view.findViewById(R.id.cancel_location);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d("maps", "cancelButtonClicked");
            }
        });

        setLocationButton = (Button) view.findViewById(R.id.set_coordinate2);
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d("maps", "SelectedCoordinate: " + clickedPosition.getPosition().toString());
            }
        });
        setLocationButton.setEnabled(false);
        return view;
    }
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            System.out.println("**************" + getFragmentManager().findFragmentById(R.id.map_element));
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_element))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        LatLng gjovik = new LatLng(60.1, 23.1);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.addMarker(new MarkerOptions().position(gjovik).title("YourPosition").alpha(0.7f));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(gjovik));
        googleMap.setOnMapClickListener(new ClickedPosition(this));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       /** try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }**/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
            clickedPosition =  googleMap.addMarker(new MarkerOptions().position(latLng).title("Clicked position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        }
    }
}
