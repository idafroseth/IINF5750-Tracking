/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.eventcapture;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.mapswithme.maps.api.MWMResponse;

import org.hisp.dhis.android.eventcapture.fragments.SelectProgramFragment;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.controllers.LoadingController;
import org.hisp.dhis.android.sdk.controllers.PeriodicSynchronizerController;
import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.ui.activities.INavigationHandler;
import org.hisp.dhis.android.sdk.ui.activities.OfflineMapHandler;
import org.hisp.dhis.android.sdk.ui.activities.OnBackPressedListener;
import org.hisp.dhis.android.sdk.ui.fragments.loading.LoadingFragment;
import org.hisp.dhis.android.sdk.utils.UiUtils;

public class MainActivity extends AppCompatActivity implements INavigationHandler, OfflineMapHandler {
    public final static String TAG = MainActivity.class.getSimpleName();
    public static String EXTRA_FROM_MWM = "from-maps-with-me";
    public static String EXTRA_EVENT_ID = "event-id";
    private OnBackPressedListener mBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadingController.enableLoading(this, ResourceType.ASSIGNEDPROGRAMS);
        LoadingController.enableLoading(this, ResourceType.OPTIONSETS);
        LoadingController.enableLoading(this, ResourceType.PROGRAMS);
        LoadingController.enableLoading(this, ResourceType.CONSTANTS);
        LoadingController.enableLoading(this, ResourceType.PROGRAMRULES);
        LoadingController.enableLoading(this, ResourceType.PROGRAMRULEVARIABLES);
        LoadingController.enableLoading(this, ResourceType.PROGRAMRULEACTIONS);
        LoadingController.enableLoading(this, ResourceType.RELATIONSHIPTYPES);
        LoadingController.enableLoading(this, ResourceType.EVENTS);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PeriodicSynchronizerController.activatePeriodicSynchronizer(this);
        showSelectProgramFragment();

        handleIntent(getIntent());
    }

    @Override
    public void onPause() {
        super.onPause();
        Dhis2Application.getEventBus().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInitialData();
    }

    public void loadInitialData() {
        String message = getString(org.hisp.dhis.android.sdk.R.string.finishing_up);
        UiUtils.postProgressMessage(message);
        DhisService.loadInitialData(MainActivity.this);
    }

    public void showLoadingFragment() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle("Loading initial data");
            }
        });
        switchFragment(new LoadingFragment(), LoadingFragment.TAG, false);
    }

    public void showSelectProgramFragment() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle("Event Capture");
            }
        });
        switchFragment(new SelectProgramFragment(), SelectProgramFragment.TAG, true);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedListener != null) {
            mBackPressedListener.doBack();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            finish();
        }
    }

    @Override
    public void setBackPressedListener(OnBackPressedListener listener) {
        mBackPressedListener = listener;
    }

    @Override
    public void switchFragment(Fragment fragment, String fragmentTag, boolean addToBackStack) {
        Log.d("***FRAGMENT Switch", fragment.toString());
        if (fragment != null) {
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();

            transaction
                    .setCustomAnimations(R.anim.open_enter, R.anim.open_exit)
                    .replace(R.id.fragment_container, fragment);
            if (addToBackStack) {
                transaction = transaction
                        .addToBackStack(fragmentTag);
            }

            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public PendingIntent getPendingIntent(Context context, Event event) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra(EXTRA_FROM_MWM, true);
        i.putExtra(EXTRA_EVENT_ID, event.getLocalId());
        return PendingIntent.getActivity(context, 0, i, 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if(intent.getBooleanExtra(EXTRA_FROM_MWM, false)) {
//            System.out.println(intent.getExtras().toString());
            final MWMResponse response = MWMResponse.extractFromIntent(this, intent);
            Event event = TrackerController.getEvent(intent.getLongExtra(EXTRA_EVENT_ID, 0L));
            double lat = response.getPoint().getLat();
            double lon = response.getPoint().getLon();
            event.setLatitude(lat);
            event.setLongitude(lon);
            event.save();

            Toast.makeText(this, R.string.coordinates_updated, Toast.LENGTH_LONG).show();
        }
    }
}
