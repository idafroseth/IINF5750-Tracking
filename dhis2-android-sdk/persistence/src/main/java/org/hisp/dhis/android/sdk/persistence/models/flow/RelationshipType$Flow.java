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
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.relationship.RelationshipType;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class RelationshipType$Flow extends BaseIdentifiableObject$Flow {

    @Column
    String aIsToB;

    @Column
    String bIsToA;

    public String getaIsToB() {
        return aIsToB;
    }

    public void setaIsToB(String aIsToB) {
        this.aIsToB = aIsToB;
    }

    public String getbIsToA() {
        return bIsToA;
    }

    public void setbIsToA(String bIsToA) {
        this.bIsToA = bIsToA;
    }

    public RelationshipType$Flow() {
        // empty constructor
    }

    public static RelationshipType toModel(RelationshipType$Flow relationshipTypeFlow) {
        if (relationshipTypeFlow == null) {
            return null;
        }

        RelationshipType relationshipType = new RelationshipType();
        relationshipType.setId(relationshipTypeFlow.getId());
        relationshipType.setUId(relationshipTypeFlow.getUId());
        relationshipType.setCreated(relationshipTypeFlow.getCreated());
        relationshipType.setLastUpdated(relationshipTypeFlow.getLastUpdated());
        relationshipType.setName(relationshipTypeFlow.getName());
        relationshipType.setDisplayName(relationshipTypeFlow.getDisplayName());
        relationshipType.setAccess(relationshipTypeFlow.getAccess());
        relationshipType.setaIsToB(relationshipTypeFlow.getaIsToB());
        relationshipType.setbIsToA(relationshipTypeFlow.getbIsToA());
        return relationshipType;
    }

    public static RelationshipType$Flow fromModel(RelationshipType relationshipType) {
        if (relationshipType == null) {
            return null;
        }

        RelationshipType$Flow relationshipTypeFlow = new RelationshipType$Flow();
        relationshipTypeFlow.setId(relationshipType.getId());
        relationshipTypeFlow.setUId(relationshipType.getUId());
        relationshipTypeFlow.setCreated(relationshipType.getCreated());
        relationshipTypeFlow.setLastUpdated(relationshipType.getLastUpdated());
        relationshipTypeFlow.setName(relationshipType.getName());
        relationshipTypeFlow.setDisplayName(relationshipType.getDisplayName());
        relationshipTypeFlow.setAccess(relationshipType.getAccess());
        relationshipTypeFlow.setaIsToB(relationshipType.getaIsToB());
        relationshipTypeFlow.setbIsToA(relationshipType.getbIsToA());
        return relationshipTypeFlow;
    }

    public static List<RelationshipType> toModels(List<RelationshipType$Flow> relationshipTypeFlows) {
        List<RelationshipType> relationshipTypes = new ArrayList<>();

        if (relationshipTypeFlows != null && !relationshipTypeFlows.isEmpty()) {
            for (RelationshipType$Flow relationshipTypeFlow : relationshipTypeFlows) {
                relationshipTypes.add(toModel(relationshipTypeFlow));
            }
        }

        return relationshipTypes;
    }

    public static List<RelationshipType$Flow> fromModels(List<RelationshipType> relationshipTypes) {
        List<RelationshipType$Flow> relationshipTypeFlows = new ArrayList<>();

        if (relationshipTypes != null && !relationshipTypes.isEmpty()) {
            for (RelationshipType relationshipType : relationshipTypes) {
                relationshipTypeFlows.add(fromModel(relationshipType));
            }
        }

        return relationshipTypeFlows;
    }
}
