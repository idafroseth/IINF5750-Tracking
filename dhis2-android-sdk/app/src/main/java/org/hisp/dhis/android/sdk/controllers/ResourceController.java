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

package org.hisp.dhis.android.sdk.controllers;

import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.BaseIdentifiableObject;
import org.hisp.dhis.android.sdk.persistence.models.meta.DbOperation;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.utils.DbUtils;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * @author Simen Skogly Russnes on 24.08.15.
 */
public abstract class ResourceController {

    public static <T extends BaseIdentifiableObject> void saveResourceDataFromServer(ResourceType resourceType, DhisApi dhisApi,
                                                                                     List<T> updatedItems,
                                                                                     List<T> persistedItems,
                                                                                     DateTime serverDateTime) {
        saveResourceDataFromServer(resourceType, null, dhisApi, updatedItems, persistedItems, serverDateTime, true);
    }

    public static <T extends BaseIdentifiableObject> void saveResourceDataFromServer(ResourceType resourceType, DhisApi dhisApi,
                                                                                     List<T> updatedItems,
                                                                                     List<T> persistedItems,
                                                                                     DateTime serverDateTime, boolean keepOldValues) {
        saveResourceDataFromServer(resourceType, null, dhisApi, updatedItems, persistedItems, serverDateTime, keepOldValues);
    }

    public static <T extends BaseIdentifiableObject> void saveResourceDataFromServer(ResourceType resourceType, String salt, DhisApi dhisApi,
                                                                                     List<T> updatedItems,
                                                                                     List<T> persistedItems,
                                                                                     DateTime serverDateTime) {
        saveResourceDataFromServer(resourceType, salt, dhisApi, updatedItems, persistedItems, serverDateTime, true);
    }

    public static <T extends BaseIdentifiableObject> void saveResourceDataFromServer(ResourceType resourceType, String salt, DhisApi dhisApi,
                                                                                     List<T> updatedItems,
                                                                                     List<T> persistedItems,
                                                                                     DateTime serverDateTime, boolean keepOldValues) {
        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(persistedItems, updatedItems, keepOldValues));
        DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(resourceType, salt, serverDateTime);
    }

    /**
     * determines if a meta data item should be loaded. Either because it hasnt been loaded before,
     * or because it needs to be updated based on time.
     * @return
     */
    public static boolean shouldLoad( DhisApi dhisApi, ResourceType resource, String salt ) {
        DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(resource, salt);
        DateTime serverDateTime = dhisApi.getSystemInfo()
                .getServerDate();
        if( lastUpdated == null ) {
            return true;
        } else if ( lastUpdated.isBefore( serverDateTime ) ) {
            return true;
        }
        return false;
    }

    /**
     * determines if a meta data item should be loaded. Either because it hasnt been loaded before,
     * or because it needs to be updated based on time.
     * @return
     */
    public static boolean shouldLoad( DhisApi dhisApi, ResourceType resource ) {
        return shouldLoad(dhisApi, resource, null);
    }

    public static Map<String, String> getBasicQueryMap(DateTime lastUpdated) {
        final Map<String, String> map = new HashMap<>();
        map.put("fields", "[:all]");
        if (lastUpdated != null) {
            map.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }
        return map;
    }
}
