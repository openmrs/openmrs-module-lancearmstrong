
/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.lancearmstrong.web;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.lancearmstrong.LafPatient;
import org.openmrs.module.lancearmstrong.LafReminder;
import org.openmrs.module.lancearmstrong.LafUtil;

/**
 *
 */
public class DWRLafService {
    protected final Log log = LogFactory.getLog(getClass());
    
	public void addFollowupCareCompleted(Integer patientId, Date completeDate, String careType, String docName, String resultType, String comments) {
		log.debug("Calling DWRLafService.addFollowupCareCompleted...patientId=" + patientId + ",completeDate=" + completeDate + 
			       ",careType=" + careType);
		LafReminder newReminder = new LafReminder();		
		newReminder.setPatient(Context.getPatientService().getPatient(patientId));
		newReminder.setId(null);
		newReminder.setCompleteDate(completeDate); //!=null if completed, =null otherwise
		newReminder.setResponseType(resultType); //no use
		newReminder.setFollowProcedure(Context.getConceptService().getConceptByName(careType)); //careType is String
		newReminder.setResponseComments(comments);
		newReminder.setResponseAttributes(docName);
		newReminder.setResponseDate(new Date());
		newReminder.setResponseUser(Context.getAuthenticatedUser());
		
		LafUtil.getService().getReminderDao().saveLafReminder(newReminder);
	}
	
	public void followupCareCompleted(Integer patientId, Date completeDate, Integer careType, String docName, String resultType, String comments) {
		log.debug("Calling DWRLafService.followupCareCompleted...patientId=" + patientId + ",completeDate=" + completeDate + 
			       ",careType=" + careType + "/" + Context.getConceptService().getConcept(careType));
		LafReminder newReminder = new LafReminder();		
		newReminder.setPatient(Context.getPatientService().getPatient(patientId));
		newReminder.setId(null);
		newReminder.setCompleteDate(completeDate); //!=null if completed, =null otherwise
		newReminder.setResponseType(resultType); //no use
		newReminder.setFollowProcedure(Context.getConceptService().getConcept(careType)); //careType is Integer
		newReminder.setResponseComments(comments);
		newReminder.setResponseAttributes(docName);
		newReminder.setResponseDate(new Date());
		newReminder.setResponseUser(Context.getAuthenticatedUser());
		
		LafUtil.getService().getReminderDao().saveLafReminder(newReminder);
	}
	
	public void followupCareScheduled(Integer patientId, Date scheduleDate, Integer careType, Date targetDate) {
		log.debug("Calling DWRLafService.followupCareScheduled...patientId=" + patientId + ", scheduleDate=" + scheduleDate + ",careType=" + careType + ", targetDate=" + targetDate);
		
		LafReminder reminder = LafUtil.getService().getReminderDao().getLafReminder(Context.getPatientService().getPatient(patientId), Context.getConceptService().getConcept(careType), targetDate);
		if(reminder != null) {
			reminder.setResponseAttributes("scheduleDate="+Context.getDateFormat().format(scheduleDate));			
		} else {		
			reminder = new LafReminder();		
			reminder.setPatient(Context.getPatientService().getPatient(patientId));
			reminder.setId(null);
			reminder.setFollowProcedure(Context.getConceptService().getConcept(careType));
			reminder.setTargetDate(targetDate); //used only for matching the response to the alert, not necessarily the target date matched dynamically
			reminder.setResponseAttributes("scheduleDate="+Context.getDateFormat().format(scheduleDate));
			reminder.setResponseDate(new Date());
			reminder.setResponseUser(Context.getAuthenticatedUser());
		}
		LafUtil.getService().getReminderDao().saveLafReminder(reminder);
	}	
	
	public void followupCareSnooze(Integer patientId, Integer snoozeDays, Integer careType, Date targetDate) {
		log.debug("Calling DWRLafService.followupCareScheduled...patientId=" + patientId + ", snoozeDays=" + snoozeDays + ",careType=" + careType + ", targetDate=" + targetDate);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, snoozeDays);
		Date scheduleDate = cal.getTime();
		
		LafReminder reminder = LafUtil.getService().getReminderDao().getLafReminder(Context.getPatientService().getPatient(patientId), Context.getConceptService().getConcept(careType), targetDate);
		if(reminder != null) {
			reminder.setResponseAttributes("snoozeDate="+Context.getDateFormat().format(scheduleDate));			
		} else {		
			reminder = new LafReminder();		
			reminder.setPatient(Context.getPatientService().getPatient(patientId));
			reminder.setId(null);
			reminder.setFollowProcedure(Context.getConceptService().getConcept(careType));
			reminder.setTargetDate(targetDate); //used only for matching the response to the alert, not necessarily the target date matched dynamically
			reminder.setResponseAttributes("snoozeDate="+Context.getDateFormat().format(scheduleDate));
			reminder.setResponseDate(new Date());
			reminder.setResponseUser(Context.getAuthenticatedUser());
		}
		LafUtil.getService().getReminderDao().saveLafReminder(reminder);
	}
	
	public void followupCareNotPerformed(Integer patientId, String yesOrNo, Integer careType, Date targetDate) {
		log.debug("Calling DWRLafService.followupCareScheduled...patientId=" + patientId + ", yesOrNo=" + yesOrNo + ",careType=" + careType + ", targetDate=" + targetDate);
				
		LafReminder reminder = LafUtil.getService().getReminderDao().getLafReminder(Context.getPatientService().getPatient(patientId), Context.getConceptService().getConcept(careType), targetDate);
		if(reminder != null) {
			reminder.setResponseAttributes("notPerformed="+yesOrNo);			
		} else {		
			reminder = new LafReminder();		
			reminder.setPatient(Context.getPatientService().getPatient(patientId));
			reminder.setId(null);
			reminder.setFollowProcedure(Context.getConceptService().getConcept(careType));
			reminder.setTargetDate(targetDate); //used only for matching the response to the alert, not necessarily the target date matched dynamically
			reminder.setResponseAttributes("notPerformed="+yesOrNo);			
			reminder.setResponseDate(new Date());
			reminder.setResponseUser(Context.getAuthenticatedUser());
		}
		LafUtil.getService().getReminderDao().saveLafReminder(reminder);
	}
	
	public LafPatient getLafPatient() {
		return new LafPatient(null, null, null);
	}
}
