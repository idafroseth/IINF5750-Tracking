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

package org.hisp.dhis.android.sdk.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;

import org.hisp.dhis.android.sdk.models.common.Access;
import org.hisp.dhis.android.sdk.models.common.base.BaseIdentifiableObject;
import org.hisp.dhis.android.sdk.models.common.base.IdentifiableObject;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseIdentifiableObject$Flow extends BaseModel$Flow implements IdentifiableObject {
    public static final String COLUMN_UID = "uId";

    @Column(name = COLUMN_UID)
    String uId;

    @Column(name = "name")
    String name;

    @Column(name = "displayName")
    String displayName;

    @Column(name = "created")
    DateTime created;

    @Column(name = "lastUpdated")
    DateTime lastUpdated;

    @Column(name = "access")
    Access access;

    @Override
    public final String getUId() {
        return uId;
    }

    @Override
    public final void setUId(String uId) {
        this.uId = uId;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public final String getDisplayName() {
        return displayName;
    }

    @Override
    public final void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public final DateTime getCreated() {
        return created;
    }

    @Override
    public final void setCreated(DateTime created) {
        this.created = created;
    }

    @Override
    public final DateTime getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public final void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public final Access getAccess() {
        return access;
    }

    @Override
    public final void setAccess(Access access) {
        this.access = access;
    }

    public static <T extends IdentifiableObject> Map<String, T> toMap(Collection<T> objects) {
        Map<String, T> map = new HashMap<>();
        if (objects != null && objects.size() > 0) {
            for (T object : objects) {
                if (object.getUId() != null) {
                    map.put(object.getUId(), object);
                }
            }
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseIdentifiableObject> T toModel(BaseIdentifiableObject$Flow flow) {
        if (flow == null) {
            return null;
        }

        BaseIdentifiableObject object = new BaseIdentifiableObject();
        object.setId(flow.getId());
        object.setUId(flow.getUId());
        object.setCreated(flow.getCreated());
        object.setLastUpdated(flow.getLastUpdated());
        object.setName(flow.getName());
        object.setDisplayName(flow.getDisplayName());
        object.setAccess(flow.getAccess());
        return (T) object;
    }
}
