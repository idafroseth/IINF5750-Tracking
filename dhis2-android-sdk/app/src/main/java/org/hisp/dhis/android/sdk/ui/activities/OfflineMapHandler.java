package org.hisp.dhis.android.sdk.ui.activities;

import android.app.PendingIntent;
import android.content.Context;

import org.hisp.dhis.android.sdk.persistence.models.Event;

public interface OfflineMapHandler {
    PendingIntent getPendingIntent(Context context, Event event);
    void updateProgramId(String programId);
    void updateOrgUnitId(String orgUnitId);
    String getProgramId();
    String getOrgUnitId();
}
