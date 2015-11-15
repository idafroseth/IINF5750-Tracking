package org.hisp.dhis.android.sdk.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import org.hisp.dhis.android.sdk.R;

public class MapsSelectionFragment extends DialogFragment {
    private static final String TAG = MapsSelectionFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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
                } else if(i == R.id.dialog_fragment_select_offline) {
                    // launch Maps.ME fragment from here
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

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, TAG);
    }
}
