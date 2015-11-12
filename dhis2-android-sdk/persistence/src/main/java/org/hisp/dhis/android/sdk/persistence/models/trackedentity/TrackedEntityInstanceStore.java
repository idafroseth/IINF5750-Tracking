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

package org.hisp.dhis.android.sdk.persistence.models.trackedentity;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.models.relationship.IRelationshipStore;
import org.hisp.dhis.android.sdk.models.trackedentity.ITrackedEntityAttributeValueStore;
import org.hisp.dhis.android.sdk.models.trackedentity.ITrackedEntityInstanceStore;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.persistence.models.flow.Relationship$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.TrackedEntityAttributeValue$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.TrackedEntityInstance$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.TrackedEntityInstance$Flow$Table;

import java.util.List;

public final class TrackedEntityInstanceStore implements ITrackedEntityInstanceStore {

    private final IRelationshipStore relationshipStore;
    private final ITrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    public TrackedEntityInstanceStore(IRelationshipStore relationshipStore, ITrackedEntityAttributeValueStore trackedEntityAttributeValueStore) {
        this.relationshipStore = relationshipStore;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
    }

    @Override
    public boolean insert(TrackedEntityInstance object) {
        TrackedEntityInstance$Flow trackedEntityInstanceFlow =
                TrackedEntityInstance$Flow.fromModel(object);
        trackedEntityInstanceFlow.insert();
        return true;
    }

    @Override
    public boolean update(TrackedEntityInstance object) {
        //making sure uid is not overwritten with blank value in case uid was updated from server while object was loaded in memory
        if (object.getTrackedEntityInstanceUid() == null || object.getTrackedEntityInstanceUid().isEmpty()) {
            TrackedEntityInstance$Flow persisted = new Select()
                    .from(TrackedEntityInstance$Flow.class).where(Condition
                            .column(TrackedEntityInstance$Flow$Table.ID).is(object.getId()))
                    .querySingle();
            if (persisted != null) {
                object.setTrackedEntityInstanceUid(persisted.getTrackedEntityInstanceUid());
            }
        }
        TrackedEntityInstance$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(TrackedEntityInstance object) {
        //making sure uid is not overwritten with blank value in case uid was updated from server while object was loaded in memory
        if (object.getTrackedEntityInstanceUid() == null || object.getTrackedEntityInstanceUid().isEmpty()) {
            TrackedEntityInstance$Flow persisted = new Select()
                    .from(TrackedEntityInstance$Flow.class).where(Condition
                            .column(TrackedEntityInstance$Flow$Table.ID).is(object.getId()))
                    .querySingle();
            if (persisted != null) {
                object.setTrackedEntityInstanceUid(persisted.getTrackedEntityInstanceUid());
            }
        }
        TrackedEntityInstance$Flow.fromModel(object).update();
        // make sure uid is not overwritten!!
        TrackedEntityInstance$Flow trackedEntityInstanceFlow =
                TrackedEntityInstance$Flow.fromModel(object);
        trackedEntityInstanceFlow.save();
        return true;
    }

    @Override
    public boolean delete(TrackedEntityInstance object) {
        TrackedEntityInstance$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public TrackedEntityInstance queryById(long id) {
        return null;
    }

    @Override
    public List<TrackedEntityInstance> queryAll() {
        List<TrackedEntityInstance$Flow> trackedEntityInstanceFlows = new Select()
                .from(TrackedEntityInstance$Flow.class)
                .queryList();
        for (TrackedEntityInstance$Flow trackedEntityInstanceFlow : trackedEntityInstanceFlows) {
            setRelationships(trackedEntityInstanceFlow);
            setTrackedEntityAttributeValuess(trackedEntityInstanceFlow);
        }
        return TrackedEntityInstance$Flow.toModels(trackedEntityInstanceFlows);
    }

    @Override
    public TrackedEntityInstance query(long id) {
        TrackedEntityInstance$Flow trackedEntityInstanceFlow = new Select().from(TrackedEntityInstance$Flow
                .class).where(Condition.column(TrackedEntityInstance$Flow$Table.ID).is(id))
                .querySingle();
        setRelationships(trackedEntityInstanceFlow);
        setTrackedEntityAttributeValuess(trackedEntityInstanceFlow);
        return TrackedEntityInstance$Flow.toModel(trackedEntityInstanceFlow);
    }

    @Override
    public TrackedEntityInstance query(String uid) {
        TrackedEntityInstance$Flow trackedEntityInstanceFlow = new Select().from(TrackedEntityInstance$Flow
                .class).where(Condition.column(TrackedEntityInstance$Flow$Table.TRACKEDENTITYINSTANCEUID).is(uid))
                .querySingle();
        setRelationships(trackedEntityInstanceFlow);
        setTrackedEntityAttributeValuess(trackedEntityInstanceFlow);
        return TrackedEntityInstance$Flow.toModel(trackedEntityInstanceFlow);
    }

    private void setRelationships(TrackedEntityInstance$Flow trackedEntityInstanceFlow) {
        if (trackedEntityInstanceFlow == null) {
            return;
        }
        trackedEntityInstanceFlow.setRelationships(Relationship$Flow.
                fromModels(relationshipStore.query(TrackedEntityInstance$Flow.
                        toModel(trackedEntityInstanceFlow))));
    }

    private void setTrackedEntityAttributeValuess(TrackedEntityInstance$Flow trackedEntityInstanceFlow) {
        if (trackedEntityInstanceFlow == null) {
            return;
        }
        trackedEntityInstanceFlow.setAttributes(TrackedEntityAttributeValue$Flow.
                fromModels(trackedEntityAttributeValueStore.query(TrackedEntityInstance$Flow.
                        toModel(trackedEntityInstanceFlow))));
    }
}
