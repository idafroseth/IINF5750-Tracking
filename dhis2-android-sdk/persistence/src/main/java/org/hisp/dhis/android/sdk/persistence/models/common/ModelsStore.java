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

package org.hisp.dhis.android.sdk.persistence.models.common;

import com.raizlabs.android.dbflow.sql.language.Delete;

import org.hisp.dhis.android.sdk.persistence.models.flow.Dashboard$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.DashboardElement$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.DashboardItem$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.DashboardContent$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.Interpretation$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.InterpretationComment$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.InterpretationElement$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.User$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.UserAccount$Flow;
import org.hisp.dhis.android.sdk.models.common.IModelsStore;

public class ModelsStore implements IModelsStore {

    public ModelsStore() {
        // empty constructor
    }

    @Override
    public void deleteAllTables() {
        Delete.tables(
                Dashboard$Flow.class,
                DashboardItem$Flow.class,
                DashboardElement$Flow.class,
                DashboardContent$Flow.class,
                Interpretation$Flow.class,
                InterpretationComment$Flow.class,
                InterpretationElement$Flow.class,
                UserAccount$Flow.class,
                User$Flow.class
        );
    }
}
