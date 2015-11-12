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

package org.hisp.dhis.android.sdk.persistence.models.common.state;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.models.common.base.IModel;
import org.hisp.dhis.android.sdk.models.common.state.Action;
import org.hisp.dhis.android.sdk.models.common.state.IStateStore;
import org.hisp.dhis.android.sdk.models.common.state.State;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.android.sdk.models.event.Event;
import org.hisp.dhis.android.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.android.sdk.models.interpretation.InterpretationComment;
import org.hisp.dhis.android.sdk.models.interpretation.InterpretationElement;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.models.utils.Preconditions;
import org.hisp.dhis.android.sdk.persistence.models.common.base.AbsStore;
import org.hisp.dhis.android.sdk.persistence.models.common.base.IMapper;
import org.hisp.dhis.android.sdk.persistence.models.flow.Dashboard$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.Dashboard$Flow$Table;
import org.hisp.dhis.android.sdk.persistence.models.flow.DashboardElement$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.DashboardElement$Flow$Table;
import org.hisp.dhis.android.sdk.persistence.models.flow.DashboardItem$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.DashboardItem$Flow$Table;
import org.hisp.dhis.android.sdk.persistence.models.flow.Enrollment$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.Enrollment$Flow$Table;
import org.hisp.dhis.android.sdk.persistence.models.flow.Event$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.Event$Flow$Table;
import org.hisp.dhis.android.sdk.persistence.models.flow.Interpretation$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.Interpretation$Flow$Table;
import org.hisp.dhis.android.sdk.persistence.models.flow.InterpretationComment$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.InterpretationComment$Flow$Table;
import org.hisp.dhis.android.sdk.persistence.models.flow.InterpretationElement$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.InterpretationElement$Flow$Table;
import org.hisp.dhis.android.sdk.persistence.models.flow.State$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.State$Flow$Table;
import org.hisp.dhis.android.sdk.persistence.models.flow.TrackedEntityInstance$Flow;
import org.hisp.dhis.android.sdk.persistence.models.flow.TrackedEntityInstance$Flow$Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateStore extends AbsStore<State, State$Flow> implements IStateStore {
    private final IMapper<Dashboard, Dashboard$Flow> dashboardMapper;
    private final IMapper<DashboardItem, DashboardItem$Flow> dashboardItemMapper;
    private final IMapper<DashboardElement, DashboardElement$Flow> dashboardElementMapper;

    public StateStore(IStateMapper mapper,
                      IMapper<Dashboard, Dashboard$Flow> dashboardMapper,
                      IMapper<DashboardItem, DashboardItem$Flow> dashboardItemMapper,
                      IMapper<DashboardElement, DashboardElement$Flow> dashboardElementMapper) {
        super(mapper);
        this.dashboardMapper = dashboardMapper;
        this.dashboardItemMapper = dashboardItemMapper;
        this.dashboardElementMapper = dashboardElementMapper;
    }

    @Override
    public <T extends IModel> boolean insertActionForModel(T object, Action action) {
        Preconditions.isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setItemType(object.getClass());
        state.setAction(action);

        return insert(state);
    }

    @Override
    public <T extends IModel> boolean updateActionForModel(T object, Action action) {
        Preconditions.isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setItemType(object.getClass());
        state.setAction(action);

        return update(state);
    }

    @Override
    public <T extends IModel> boolean saveActionForModel(T object, Action action) {
        Preconditions.isNull(object, "State object must not be null");

        State state = new State();
        state.setItemId(object.getId());
        state.setAction(action);
        state.setItemType(object.getClass());

        return save(state);
    }

    @Override
    public <T extends IModel> boolean deleteActionForModel(T object) {
        Preconditions.isNull(object, "State object must not be null");

        State state = queryStateForModel(object);
        return state != null && delete(state);
    }

    @Override
    public <T extends IModel> State queryStateForModel(T object) {
        Preconditions.isNull(object, "State object must not be null");

        State$Flow stateFlow = new Select()
                .from(State$Flow.class)
                .where(Condition.column(State$Flow$Table
                        .ITEMTYPE).is(getStateMapper().getRelatedModelClass(object.getClass())))
                .and(Condition.column(State$Flow$Table
                        .ITEMID).is(object.getId()))
                .querySingle();

        return getMapper().mapToModel(stateFlow);
    }

    @Override
    public <T extends IModel> Action queryActionForModel(T object) {
        Preconditions.isNull(object, "State object must not be null");

        State state = queryStateForModel(object);
        if (state == null) {
            return Action.SYNCED;
        }

        return state.getAction();
    }

    @Override
    public <T extends IModel> List<T> filterModelsByAction(Class<T> clazz, Action action) {
        return getObjectsByAction(clazz, action, false);
    }

    @Override
    public <T extends IModel> List<T> queryModelsWithAction(Class<T> clazz, Action action) {
        return getObjectsByAction(clazz, action, true);
    }

    @Override
    public <T extends IModel> List<State> queryStatesForModelClass(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }

        List<State$Flow> stateFlows = new Select()
                .from(State$Flow.class)
                .where(Condition.column(State$Flow$Table
                        .ITEMTYPE).is(getStateMapper().getRelatedModelClass(clazz)))
                .queryList();

        return getMapper().mapToModels(stateFlows);
    }

    @Override
    public <T extends IModel> Map<Long, Action> queryActionsForModel(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }

        List<State> states = queryStatesForModelClass(clazz);
        Map<Long, Action> actionMap = new HashMap<>();

        if (states != null && !states.isEmpty()) {
            for (State state : states) {
                actionMap.put(state.getItemId(), state.getAction());
            }
        }

        return actionMap;
    }

    @SuppressWarnings("unchecked")
    private <T extends IModel> List<T> getObjectsByAction(Class<T> clazz, Action action, boolean withAction) {
        if (Dashboard.class.equals(clazz)) {
            List<Dashboard$Flow> dashboardFlows = (List<Dashboard$Flow>) queryModels(
                    clazz, action, withAction, Dashboard$Flow$Table.ID);
            return (List<T>) dashboardMapper.mapToModels(dashboardFlows);
        }

        if (DashboardItem.class.equals(clazz)) {
            List<DashboardItem$Flow> dashboardItemFlows = (List<DashboardItem$Flow>) queryModels(
                    clazz, action, withAction, DashboardItem$Flow$Table.ID);
            return (List<T>) dashboardItemMapper.mapToModels(dashboardItemFlows);
        }

        if (DashboardElement.class.equals(clazz)) {
            List<DashboardElement$Flow> dashboardElementFlows = (List<DashboardElement$Flow>) queryModels(
                    clazz, action, withAction, DashboardElement$Flow$Table.ID);
            return (List<T>) dashboardElementMapper.mapToModels(dashboardElementFlows);
        }

        if (Interpretation.class.equals(clazz)) {
            List<Interpretation$Flow> interpretationFlows = (List<Interpretation$Flow>) queryModels(
                    clazz, action, withAction, Interpretation$Flow$Table.ID);
            return (List<T>) Interpretation$Flow.toModels(interpretationFlows);
        }

        if (InterpretationElement.class.equals(clazz)) {
            List<InterpretationElement$Flow> interpretationElementFlows = (List<InterpretationElement$Flow>) queryModels(
                    clazz, action, withAction, InterpretationElement$Flow$Table.ID);
            return (List<T>) InterpretationElement$Flow.toModels(interpretationElementFlows);
        }

        if (InterpretationComment.class.equals(clazz)) {
            List<InterpretationComment$Flow> interpretationCommentFlows = (List<InterpretationComment$Flow>) queryModels(
                    clazz, action, withAction, InterpretationComment$Flow$Table.ID);
            return (List<T>) InterpretationComment$Flow.toModels(interpretationCommentFlows);
        }

        if (Event.class.equals(clazz)) {
            List<Event$Flow> eventFlows = (List<Event$Flow>) queryModels(clazz, action, withAction, Event$Flow$Table.ID);
            return (List<T>) Event$Flow.toModels(eventFlows);
        }

        if (Enrollment.class.equals(clazz)) {
            List<Enrollment$Flow> enrollmentFlows = (List<Enrollment$Flow>) queryModels(clazz, action, withAction, Enrollment$Flow$Table.ID);
            return (List<T>) Enrollment$Flow.toModels(enrollmentFlows);
        }

        if (TrackedEntityInstance.class.equals(clazz)) {
            List<TrackedEntityInstance$Flow> trackedEntityInstanceFlows = (List<TrackedEntityInstance$Flow>) queryModels(clazz, action, withAction, TrackedEntityInstance$Flow$Table.ID);
            return (List<T>) TrackedEntityInstance$Flow.toModels(trackedEntityInstanceFlows);
        }

        return null;
    }

    private List<? extends Model> queryModels(Class<? extends IModel> clazz, Action action,
                                              boolean withAction, String columnName) {
        /* Creating left join on State and destination table in order to perform filtering  */
        /* Joining tables based on mime type and then filtering resulting table by action */
        From<? extends Model> from = new Select()
                .from(getStateMapper().getRelatedDatabaseEntityClass(clazz))
                .join(State$Flow.class, Join.JoinType.LEFT)
                .on(Condition.column(State$Flow$Table.ITEMID)
                        .eq(columnName));

        Where<? extends Model> where = from
                .where(Condition.column(State$Flow$Table
                        .ITEMTYPE).is(getStateMapper().getRelatedModelClass(clazz)));
        if (withAction) {
            where = where.and(Condition.column(State$Flow$Table
                    .ACTION).is(action.toString()));
        } else {
            where = where.and(Condition.column(State$Flow$Table
                    .ACTION).isNot(action.toString()));
        }

        List<? extends Model> list = where.queryList();
        System.out.println("LIST: " + list.size());
        return list;
    }

    private IStateMapper getStateMapper() {
        return (IStateMapper) getMapper();
    }
}
