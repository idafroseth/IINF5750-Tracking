package org.hisp.dhis.android.sdk.ui.activities;

import android.app.PendingIntent;

import org.hisp.dhis.android.sdk.persistence.models.Event;

public interface OfflineMapHandler {
    PendingIntent getPendingIntent(Event event);
}
