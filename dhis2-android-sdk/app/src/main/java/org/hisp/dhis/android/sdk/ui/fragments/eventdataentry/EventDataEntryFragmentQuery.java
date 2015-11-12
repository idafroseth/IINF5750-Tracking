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

package org.hisp.dhis.android.sdk.ui.fragments.eventdataentry;

import android.content.Context;
import android.util.Log;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.loaders.Query;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageSection;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.Row;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.DataEntryFragmentSection;
import org.hisp.dhis.android.sdk.utils.services.ProgramIndicatorService;
import org.hisp.dhis.android.sdk.utils.support.DateUtils;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.AutoCompleteRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.CheckBoxRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.CoordinatesRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DataEntryRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DataEntryRowTypes;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DatePickerRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.EditTextRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.EventDatePickerRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.IndicatorRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.RadioButtonsRow;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.StatusRow;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;
import static org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController.getDataElement;

class EventDataEntryFragmentQuery implements Query<EventDataEntryFragmentForm> {

    private static final String CLASS_TAG = EventDataEntryFragmentQuery.class.getSimpleName();

    private static final String EMPTY_FIELD = "";
    private static final String DEFAULT_SECTION = "defaultSection";

    private final String orgUnitId;
    private final String programId;
    private final String programStageId;
    private final long eventId;
    private final long enrollmentId;

    EventDataEntryFragmentQuery(String orgUnitId, String programId, String programStageId, long eventId, long enrollmentId) {
        this.orgUnitId = orgUnitId;
        this.programId = programId;
        this.programStageId = programStageId;
        this.eventId = eventId;
        this.enrollmentId = enrollmentId;
    }

    @Override
    public EventDataEntryFragmentForm query(Context context) {
        final ProgramStage stage = MetaDataController.getProgramStage(programStageId);
        final EventDataEntryFragmentForm form = new EventDataEntryFragmentForm();

        if (stage == null || stage.getProgramStageSections() == null) {
            return form;
        }

        final String username = DhisController.getInstance().getSession().getCredentials().getUsername();
        final Event event = getEvent(
                orgUnitId, programId, eventId, enrollmentId, stage, username
        );

        form.setEvent(event);
        form.setStage(stage);
        form.setSections(new ArrayList<DataEntryFragmentSection>());
        form.setDataElementNames(new HashMap<String, String>());
        form.setDataValues(new HashMap<String, DataValue>());
        form.setIndicatorRows(new ArrayList<IndicatorRow>());
        form.setIndicatorToIndicatorRowMap(new HashMap<String, IndicatorRow>());

        if (stage.getProgramStageSections() == null || stage.getProgramStageSections().isEmpty()) {
            List<Row> rows = new ArrayList<>();
            addStatusRow(context, form, rows);
            addEventDateRow(context, form, rows);
            addCoordinateRow(form, rows);
            populateDataEntryRows(form, stage.getProgramStageDataElements(), rows, username);
            populateIndicatorRows(form, stage.getProgramIndicators(), rows);
            form.getSections().add(new DataEntryFragmentSection(DEFAULT_SECTION, null, rows));
        } else {
            for (int i = 0; i < stage.getProgramStageSections().size(); i++) {
                ProgramStageSection section = stage.getProgramStageSections().get(i);
                if (section.getProgramStageDataElements() == null) {
                    continue;
                }

                List<Row> rows = new ArrayList<>();
                if (i == 0) {
                    addStatusRow(context, form, rows);
                    addEventDateRow(context, form, rows);
                    addCoordinateRow(form, rows);
                }
                populateDataEntryRows(form, section.getProgramStageDataElements(), rows, username);
                populateIndicatorRows(form, section.getProgramIndicators(), rows);
                form.getSections().add(new DataEntryFragmentSection(section.getName(), section.getUid(), rows));
            }
        }
        return form;
    }

    private static void addStatusRow(Context context, EventDataEntryFragmentForm form,
                                     List<Row> rows) {
        Event event = form.getEvent();
        if(event==null) return;
        StatusRow row = new StatusRow(context, event);
        rows.add(row);
        form.setStatusRow(row);
    }

    private static void addEventDateRow(Context context, EventDataEntryFragmentForm form,
                                        List<Row> rows) {
        String reportDateDescription = form.getStage().getReportDateDescription()== null ?
                context.getString(R.string.report_date) : form.getStage().getReportDateDescription();
        rows.add(new EventDatePickerRow(reportDateDescription, form.getEvent()));
    }

    private static void addCoordinateRow(EventDataEntryFragmentForm form, List<Row> rows) {
        if (form.getStage() != null &&
                form.getStage().getCaptureCoordinates()) {
            rows.add(new CoordinatesRow(form.getEvent()));
        }
    }

    private static void populateDataEntryRows(EventDataEntryFragmentForm form,
                                              List<ProgramStageDataElement> dataElements,
                                              List<Row> rows, String username) {
        for (ProgramStageDataElement stageDataElement : dataElements) {
            DataValue dataValue = getDataValue(stageDataElement.getDataelement(), form.getEvent(), username);
            DataElement dataElement = getDataElement(stageDataElement.getDataelement());
            if (dataElement != null) {
                form.getDataElementNames().put(stageDataElement.getDataelement(),
                        dataElement.getDisplayName());
                form.getDataValues().put(dataValue.getDataElement(), dataValue);
                rows.add(createDataEntryRow(dataElement, dataValue));
            }
        }
    }

    private static void populateIndicatorRows(EventDataEntryFragmentForm form,
                                              List<ProgramIndicator> indicators,
                                              List<Row> rows) {
        for (ProgramIndicator programIndicator : indicators) {
            IndicatorRow indicatorRow = form.getIndicatorToIndicatorRowMap().get(programIndicator.getUid());
            if(indicatorRow==null) {
                String value = ProgramIndicatorService
                        .getProgramIndicatorValue(form.getEvent(), programIndicator);
                indicatorRow = new IndicatorRow(programIndicator, value);
                form.getIndicatorToIndicatorRowMap().put(programIndicator.getUid(), indicatorRow);
                form.getIndicatorRows().add(indicatorRow);
            }
            rows.add(indicatorRow);
        }
    }

    private Event getEvent(String orgUnitId, String programId, long eventId, long enrollmentId,
                           ProgramStage programStage, String username) {
        Event event;
        if (eventId < 0) {
            event = new Event(orgUnitId, Event.STATUS_ACTIVE, programId, programStage, null, null, null);
            if (enrollmentId > 0) {
                Enrollment enrollment = TrackerController.getEnrollment(enrollmentId);
                if (enrollment != null) {
                    event.setLocalEnrollmentId(enrollmentId);
                    event.setEnrollment(enrollment.getEnrollment());
                    event.setTrackedEntityInstance(enrollment.getTrackedEntityInstance());
                    LocalDate dueDate = new LocalDate(DateUtils.parseDate(enrollment.getDateOfEnrollment())).plusDays(programStage.getMinDaysFromStart());
                    event.setDueDate(dueDate.toString());
                }
            }
            event.save();

            List<DataValue> dataValues = new ArrayList<>();
            for (ProgramStageDataElement dataElement : programStage.getProgramStageDataElements()) {
                dataValues.add(
                        new DataValue(event, EMPTY_FIELD, dataElement.getDataelement(), false, username)
                );
            }
            event.setDataValues(dataValues);
        } else {
            event = TrackerController.getEvent(eventId);
        }

        return event;
    }

    private static Row createDataEntryRow(DataElement dataElement, DataValue dataValue) {
        Row row;

        String dataElementName;
        if (!isEmpty(dataElement.getDisplayFormName())) {
            dataElementName = dataElement.getDisplayFormName();
        } else {
            dataElementName = dataElement.getDisplayName();
        }

        if (dataElement.getOptionSet() != null) {
            OptionSet optionSet = MetaDataController.getOptionSet(dataElement.getOptionSet());
            if (optionSet == null) {
                row = new EditTextRow(dataElementName, dataValue, DataEntryRowTypes.TEXT);
            } else {
                row = new AutoCompleteRow(dataElementName, dataValue, optionSet);
            }
        } else if (dataElement.getType().equalsIgnoreCase(DataElement.VALUE_TYPE_TEXT)) {
            row = new EditTextRow(dataElementName, dataValue, DataEntryRowTypes.TEXT);
        } else if (dataElement.getType().equalsIgnoreCase(DataElement.VALUE_TYPE_LONG_TEXT)) {
            row = new EditTextRow(dataElementName, dataValue, DataEntryRowTypes.LONG_TEXT);
        } else if (dataElement.getType().equalsIgnoreCase(DataElement.VALUE_TYPE_NUMBER)) {
            row = new EditTextRow(dataElementName, dataValue, DataEntryRowTypes.NUMBER);
        } else if (dataElement.getType().equalsIgnoreCase(DataElement.VALUE_TYPE_INT)) {
            row = new EditTextRow(dataElementName, dataValue, DataEntryRowTypes.INTEGER);
        } else if (dataElement.getType().equalsIgnoreCase(DataElement.VALUE_TYPE_ZERO_OR_POSITIVE_INT)) {
            row = new EditTextRow(dataElementName, dataValue, DataEntryRowTypes.INTEGER_ZERO_OR_POSITIVE);
        } else if (dataElement.getType().equalsIgnoreCase(DataElement.VALUE_TYPE_POSITIVE_INT)) {
            row = new EditTextRow(dataElementName, dataValue, DataEntryRowTypes.INTEGER_POSITIVE);
        } else if (dataElement.getType().equalsIgnoreCase(DataElement.VALUE_TYPE_NEGATIVE_INT)) {
            row = new EditTextRow(dataElementName, dataValue, DataEntryRowTypes.INTEGER_NEGATIVE);
        } else if (dataElement.getType().equalsIgnoreCase(DataElement.VALUE_TYPE_BOOL)) {
            row = new RadioButtonsRow(dataElementName, dataValue, DataEntryRowTypes.BOOLEAN);
        } else if (dataElement.getType().equalsIgnoreCase(DataElement.VALUE_TYPE_TRUE_ONLY)) {
            row = new CheckBoxRow(dataElementName, dataValue);
        } else if (dataElement.getType().equalsIgnoreCase(DataElement.VALUE_TYPE_DATE)) {
            row = new DatePickerRow(dataElementName, dataValue);
        } else if (dataElement.getType().equalsIgnoreCase(DataElement.VALUE_TYPE_STRING)) {
            row = new EditTextRow(dataElementName, dataValue, DataEntryRowTypes.LONG_TEXT);
        } else {
            row = new EditTextRow(dataElementName, dataValue, DataEntryRowTypes.LONG_TEXT);
        }
        return row;
    }

    public static DataValue getDataValue(String dataElement, Event event,
                                         String username) {
        for (DataValue dataValue : event.getDataValues()) {
            if (dataValue.getDataElement().equals(dataElement)) {
                return dataValue;
            }
        }

        // The DataValue didn't exist for some reason. Create a new one.
        DataValue dataValue = new DataValue(
                event, EMPTY_FIELD, dataElement, false, username
        );
        event.getDataValues().add(dataValue);
        return dataValue;
    }
}
