/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.app.Activity;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.GpsController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.ui.dialogs.MapsSelectionFragment;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

public final class CoordinatesRow extends Row {
    private static final String EMPTY_FIELD = "";
    private final Event mEvent;
    private final int MAX_INPUT_LENGTH = 9; // max input length = 9 for accepting 6 decimals in coordinates

    public CoordinatesRow(Event event) {
        mEvent = event;
    }

    @Override
    public View getView(FragmentManager fragmentManager, LayoutInflater inflater,
                        View convertView, ViewGroup container) {
        View view;
        CoordinateViewHolder holder;

        if (convertView != null && convertView.getTag() instanceof CoordinateViewHolder) {
            view = convertView;
            holder = (CoordinateViewHolder) view.getTag();
        } else {
            View root = inflater.inflate(
                    R.layout.listview_row_coordinate_picker, container, false);
            detailedInfoButton =  root.findViewById(R.id.detailed_info_button_layout);
            holder = new CoordinateViewHolder(root, detailedInfoButton);

            root.setTag(holder);
            view = root;
        }
        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));

        //input filters for coordinate row text fields
        InputFilter[] latitudeFilters = new InputFilter[2];
        InputFilter[] longitudeFilters = new InputFilter[2];
        InputFilter maxCharFilter = new InputFilter.LengthFilter(MAX_INPUT_LENGTH);
        InputFilter invalidLatitudeFilter = new InvalidLatitudeInputValueFilter(mEvent);
        InputFilter invalidLongitudeFilter = new InvalidLongitudeInputValueFilter(mEvent);
        latitudeFilters[0] = maxCharFilter;
        latitudeFilters[1] = invalidLatitudeFilter;
        longitudeFilters[0] = maxCharFilter;
        longitudeFilters[1] = invalidLongitudeFilter;

        holder.latitude.setFilters(latitudeFilters);
        holder.longitude.setFilters(longitudeFilters);
        holder.updateViews(mEvent);

        // Coordinates cannot be manually entered
        holder.latitude.setEnabled(false);
        holder.longitude.setEnabled(false);

        return view;
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.COORDINATES.ordinal();
    }

    private static class CoordinateViewHolder {
        private final EditText latitude;
        private final EditText longitude;
        private final ImageButton captureCoords;
        private final ImageButton openMapDialog;
        private final View detailedInfoButton;
        private final LatitudeWatcher latitudeWatcher;
        private final LongitudeWatcher longitudeWatcher;
        private final OnCaptureCoordsClickListener onButtonClickListener;

        public CoordinateViewHolder(View view, View detailedInfoButton) {
            final String latitudeMessage = view.getContext()
                    .getString(R.string.latitude_error_message);
            final String longitudeMessage = view.getContext()
                    .getString(R.string.longitude_error_message);

            /* views */
            latitude = (EditText) view.findViewById(R.id.latitude_edittext);
            longitude = (EditText) view.findViewById(R.id.longitude_edittext);
            captureCoords = (ImageButton) view.findViewById(R.id.capture_coordinates);
            openMapDialog = (ImageButton) view.findViewById(R.id.capture_map_coordinates);
            this.detailedInfoButton = detailedInfoButton;

            /* text watchers and click listener */
            latitudeWatcher = new LatitudeWatcher(latitude, latitudeMessage);
            longitudeWatcher = new LongitudeWatcher(longitude, longitudeMessage);

            android.app.FragmentManager fm = null;
            if(view.getContext() instanceof Activity) {
                fm = ((Activity) view.getContext()).getFragmentManager();
            }

            onButtonClickListener = new OnCaptureCoordsClickListener(fm, latitude, longitude);

            latitude.addTextChangedListener(latitudeWatcher);
            longitude.addTextChangedListener(longitudeWatcher);
            captureCoords.setOnClickListener(onButtonClickListener);
            openMapDialog.setOnClickListener(onButtonClickListener);
        }

        public void updateViews(Event event) {
            latitudeWatcher.setEvent(event);
            longitudeWatcher.setEvent(event);

            String lat = event.getLatitude() == null ? EMPTY_FIELD
                    : String.valueOf(event.getLatitude());
            String lon = event.getLongitude() == null ? EMPTY_FIELD
                    : String.valueOf(event.getLongitude());

            latitude.setText(lat);
            longitude.setText(lon);
        }
    }
    private abstract static class CoordinateWatcher extends AbsTextWatcher {
        final EditText mEditText;
        final String mCoordinateMessage;
        Event mEvent;
        double value;

        public CoordinateWatcher(EditText mEditText, String mCoordinateMessage)
        {
            this.mEditText = mEditText;
            this.mCoordinateMessage = mCoordinateMessage;
        }

        public void setEvent(Event mEvent) {
            this.mEvent = mEvent;
        }

        @Override
        public abstract void afterTextChanged(Editable s);
    }
    private static class LatitudeWatcher extends CoordinateWatcher {

        public LatitudeWatcher(EditText mLatitude, String mLatitudeMessage) {
            super(mLatitude,mLatitudeMessage);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(mEvent.getLatitude() != null)
                value = mEvent.getLatitude();

            if (s.length() > 1) {
                double newValue = Double.parseDouble(s.toString());
                if (newValue < -90 || newValue > 90) {
                    mEditText.setError(mCoordinateMessage);
                }

                if(newValue != value)
                {
                    mEvent.setLatitude(Double.valueOf(newValue));
                    DataValue dataValue = new DataValue();
                    dataValue.setValue("" + newValue);
                    Dhis2Application.getEventBus().post(new RowValueChangedEvent(dataValue, DataEntryRowTypes.COORDINATES.toString()));

                }
            }
        }
    }

    private static class LongitudeWatcher extends CoordinateWatcher {

        public LongitudeWatcher(EditText mLongitude, String mLongitudeMessage) {
            super(mLongitude, mLongitudeMessage);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(mEvent.getLongitude() != null)
                value = mEvent.getLongitude();

            if (s.length() > 1) {
                double newValue = Double.parseDouble(s.toString());
                if (newValue < -180 || newValue > 180) {
                    mEditText.setError(mCoordinateMessage);
                }

                if(newValue != value)
                {
                    mEvent.setLongitude(Double.valueOf(newValue));
                    DataValue dataValue = new DataValue();
                    dataValue.setValue("" + newValue);
                    Dhis2Application.getEventBus().post(new RowValueChangedEvent(dataValue, DataEntryRowTypes.COORDINATES.toString()));
                }
            }
        }
    }

    private static class OnCaptureCoordsClickListener implements View.OnClickListener {
        private final EditText mLatitude;
        private final EditText mLongitude;
        private final android.app.FragmentManager fragmentManager;

        public OnCaptureCoordsClickListener(android.app.FragmentManager fragmentManager,
                                            EditText latitude, EditText longitude) {
            this.fragmentManager = fragmentManager;
            mLatitude = latitude;
            mLongitude = longitude;
        }

        @Override
        public void onClick(View v) {
            System.out.println("Clicked the button...the id is " + v.getId());
            if (v.getId() == R.id.capture_coordinates) {
                Location location = GpsController.getLocation();
                mLatitude.setText(String.valueOf(location.getLatitude()));
                mLongitude.setText(String.valueOf(location.getLongitude()));
            } else if (v.getId() == R.id.capture_map_coordinates) {
                System.out.println("This window should have launched");
                MapsSelectionFragment mapsSelectionFragment =  MapsSelectionFragment.newInstance(mLatitude,mLongitude);
                mapsSelectionFragment.show(fragmentManager);
                System.out.println("Testing final launch");
            }
        }
    }

    private abstract class InvalidInputValueFilter implements InputFilter{
        final Event event;
        final String invalidValue = "0.0"; // we don't want users to overwrite existing coordinates with 0.0 - aka no network coords
        public InvalidInputValueFilter(Event event)
        {
            this.event = event;
        }
        @Override
        public abstract CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4);
    }

    private class InvalidLatitudeInputValueFilter extends InvalidInputValueFilter
    {
        public InvalidLatitudeInputValueFilter(Event event) {
            super(event);
        }

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            if(charSequence != null && charSequence.toString().trim().equals(invalidValue))
            {
                if(event.getLatitude() == null)
                    return invalidValue; //if getLat == null && location.getLat== 0.0, return 0.0
                else
                    return Double.toString(event.getLatitude());
            }

            return null;
        }
    }
    private class InvalidLongitudeInputValueFilter extends InvalidInputValueFilter
    {
        public InvalidLongitudeInputValueFilter(Event event) {
            super(event);
        }

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            if(charSequence != null && charSequence.toString().trim().equals(invalidValue))
            {
                if(event.getLongitude() == null)
                    return invalidValue; //if getLong == null && location.getLong == 0.0, return 0.0
                else
                    return Double.toString(event.getLongitude());
            }
            return null;
        }
    }
}