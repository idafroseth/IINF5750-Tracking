package org.hisp.dhis.android.sdk.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mapswithme.maps.api.MapsWithMeApi;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.GpsController;
import org.hisp.dhis.android.sdk.ui.activities.INavigationHandler;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.MapsFragment;

public class MapsSelectionFragment extends DialogFragment {
    private static final String TAG = MapsSelectionFragment.class.getSimpleName();
    private Activity context;
    private EditText mLatitude = null;
    private EditText mLongitude = null;
    protected INavigationHandler mNavigationHandler;

    public static MapsSelectionFragment newInstance(EditText lat, EditText lng){
        MapsSelectionFragment mapSelectDialog = new MapsSelectionFragment();
        mapSelectDialog.mLatitude = lat;
        mapSelectDialog.mLongitude = lng;
        return mapSelectDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            context = activity;
        } catch(ClassCastException e) {
            // class cast failed
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getActivity() instanceof INavigationHandler) {
            mNavigationHandler = (INavigationHandler) getActivity();
        } else {
            throw new IllegalArgumentException("Activity must " +
                    "implement INavigationHandler interface");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(
                R.layout.dialog_fragment_map_selection, null);
        final View onlineButton = view.findViewById(R.id.dialog_fragment_select_online);
        final View offlineButton = view.findViewById(R.id.dialog_fragment_select_offline);
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = view.getId();

                // dismiss the dialog and launch the fragment containing the selected map type
                getDialog().dismiss();

                if (i == R.id.dialog_fragment_select_online) {
                    // launch Google Maps fragment from here
                  //  Fragment mapWindow = MapsFragment.newInstance(mLatitude, mLongitude);
                    mNavigationHandler.switchFragment(
                            new MapsFragment(), MapsFragment.TAG, true);
                   /** FragmentTransaction fragTransaction = getFragmentManager().beginTransaction().replace(R.id.fragment_container, mapWindow);
                    fragTransaction.addToBackStack(null);
                    fragTransaction.commit();
               **/
                } else if(i == R.id.dialog_fragment_select_offline) {
                    // launch Maps.ME fragment from here
                    if(MapsWithMeApi.isMapsWithMeInstalled(context)) {
                        // launch as normal
                        Location location = GpsController.getLocation();
                        String name = "Current Location";
                        Intent intent = new Intent(context, MapsSelectionFragment.class);
                        PendingIntent pi = PendingIntent.getActivity(context, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        MapsWithMeApi.pickPoint(getActivity(), "Select point", pi);
//                        MapsWithMeApi.showPointOnMap(getActivity(), location.getLatitude(),
//                                location.getLongitude(), name);
                    } else {
                        // error
                    }

                }
            }
        };

        onlineButton.setOnClickListener(listener);
        offlineButton.setOnClickListener(listener);

        builder.setView(view);
        builder.setTitle(R.string.alert_map_title);
        final AlertDialog dialogWindow = builder.create();
        dialogWindow.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // hides the default buttons once the window is shown
                dialogWindow.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                dialogWindow.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
            }
        });
        return dialogWindow;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        // we need to nullify reference
        // to parent activity in order not to leak it
        if (getActivity() != null &&
                getActivity() instanceof INavigationHandler) {
            ((INavigationHandler) getActivity()).setBackPressedListener(null);
        }
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }
}