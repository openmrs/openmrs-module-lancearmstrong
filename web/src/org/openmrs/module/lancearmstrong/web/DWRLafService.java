
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.lancearmstrong.LafPatient;
import org.openmrs.module.lancearmstrong.LafReminder;
import org.openmrs.module.lancearmstrong.LafUtil;

/**
 * DWR methods called directly from jsp pages
 * 
 * @author hxiao
 */
public class DWRLafService {
	
	/**
	 * identify if the response is entered by patient's provider or not
	 */
	public static final String RESPONSE_TYPE_PROVIDER="PHR_PROVIDER";  
    protected final Log log = LogFactory.getLog(getClass());
     
	/**
	 * Add a completed follow up test
	 * 
	 * @param patientId patient ID
	 * @param completeDate completion date
	 * @param careType type name of follow up care
	 * @param docName name of doctor
	 * @param resultType type of result
	 * @param comments any comments entered by the patient
	 */
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
	
	/**
	 * Add a follow up test recommended by the patient's provider
	 * 
	 * @param patientId patient ID
	 * @param recommendedDate recommended date
	 * @param careType type name of follow up care
	 * @param resultType type of result
	 * @param comments any comments entered by the patient
	 */
	public void addFollowupCareRecommended(Integer patientId, Date recommendedDate, String careType, String resultType, String comments) {
		log.debug("Calling DWRLafService.addFollowupCareCompleted...patientId=" + patientId + ",reommendedDate=" + recommendedDate + 
			       ",careType=" + careType);
		LafReminder newReminder = new LafReminder();		
		newReminder.setPatient(Context.getPatientService().getPatient(patientId));
		newReminder.setId(null);
		newReminder.setTargetDate(recommendedDate); //!=null if completed, =null otherwise
		newReminder.setResponseType(RESPONSE_TYPE_PROVIDER); //!=null if this is a care recommended by patient's personal provider
		newReminder.setFollowProcedure(Context.getConceptService().getConceptByName(careType)); //careType is String
		newReminder.setResponseComments(Context.getAuthenticatedUser().getPersonName().getFullName() + ": " + comments);
		newReminder.setResponseAttributes("provider_user_id="+Context.getAuthenticatedUser().getUserId());
		newReminder.setResponseDate(new Date());
		newReminder.setResponseUser(Context.getAuthenticatedUser());

		LafUtil.getService().getReminderDao().saveLafReminder(newReminder);
	}
	
	/**
	 * Delete a follow up test recommended by guideline or by the patient's provider
	 * 
	 * @param patientId patient ID
	 * @param targetDate target/recommended date
	 * @param careType type name of follow up care
	 * @param responseType identify if a response is entered by patient's provider or not
	 */
	public void deleteFollowupCareRecommended(Integer patientId, Date targetDate, String careType, String responseType) {
		log.debug("Calling DWRLafService.addFollowupCareCompleted...patientId=" + patientId + 
			       ",careType=" + careType);
		if(RESPONSE_TYPE_PROVIDER.equals(responseType)) {
			//delete follow up care recommended by patient's providers
			LafUtil.getService().getReminderDao().deleteLafReminder(patientId, targetDate, careType);
		} else {
			//mark the care as Not Performed & Deleted internally
			followupCareNotPerformed(patientId, "Yes;deleted=Yes", Context.getConceptService().getConceptByName(careType).getConceptId(), targetDate);
		}
	}	
	
	/**
	 * Add a completed follow up test given an integer type code
	 * 
	 * @param patientId patient ID
	 * @param completeDate completion date
	 * @param careType type code of follow up care
	 * @param docName name of doctor
	 * @param resultType type of result
	 * @param comments any comments entered by the patient
	 */
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
	
	/**
	 * Mark a follow up care as scheduled
	 * 
	 * @param patientId patient ID
	 * @param scheduleDate schedule date
	 * @param careType type code of follow up care
	 * @param targetDate target/recommended date of this care
	 */
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
	
	/**
	 * Mark a follow up care as snoozed
	 * 
	 * @param patientId patient ID
	 * @param snoozeDays number of days to snooze for
	 * @param careType type code of follow up care
	 * @param targetDate target/recommended date of this care
	 */
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
	
	/**
	 * Mark a follow up care as not performed
	 * 
	 * @param patientId patient ID
	 * @param yesOrNo whether the care should be marked as Not Performed or not
	 * @param careType type code of follow up care
	 * @param targetDate target/recommended date of this care
	 */
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
	
	/**
	 * Get a LafPatient object
	 * 
	 * @return LafPatient object
	 */
	public LafPatient getLafPatient() {
		return new LafPatient(null, null, null);
	}
}
