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
package org.openmrs.module.lancearmstrong;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.notification.Alert;


/**
 * Represent a cancer Patient with data pre-populated
 * 
 * @author hxiao
 */
public class LafPatient {
	private Patient patient;
	private List<LafReminder> reminders; //follow-up care per guideline
	private List<LafReminder> remindersCompleted; //follow-up care actually performed
	private List<Alert> alerts;
	private String[] responseTypes = {"Normal", "Abnormal", "Don't know"};
	private String[] careTypes = {
			"Colonoscopy", "History and Physical",
			"CEA Tests", "CT Scan Chest/Abdomen", 
			"CT Scan Pelvis", "Flex Sigmoidoscopy"};
	
	/**
	 * Constructor
	 * @param pat OpenMRS patient
	 * @param rem reminder list
	 * @param remCompl completed reminder list
	 */
	public LafPatient(Patient pat, List<LafReminder> rem, List<LafReminder> remCompl) {
		this.patient = pat;
		this.reminders = rem;
		this.remindersCompleted = remCompl;
		
	}

	/**
	 * Constructor
	 * @param pat OpenMRS Patient object
	 * @param alerts list of alerts generated
	 */
	public LafPatient(Patient pat, List<Alert> alerts) {
		this.patient = pat;
		this.alerts = alerts;
	}
	
    /**
     * Get the list of reminders for the patient
     * 
     * @return list of reminders
     */
    public List<LafReminder> getReminders() {
    	return reminders;
    }
	
    /**
     * Set a list of reminders for the patient
     * 
     * @param reminders a list of reminders
     */
    public void setReminders(List<LafReminder> reminders) {
    	this.reminders = reminders;
    }

	
    /**
     * Get OpenMRS Patient object
     * 
     * @return OpenMRS Patient object
     */
    public Patient getPatient() {
    	return patient;
    }

	
    /**
     * Set OpenMRS object
     * 
     * @param patient openmrs object
     */
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }

	
    /**
     * Get completed reminders
     * 
     * @return reminders completed by the patient
     */
    public List<LafReminder> getRemindersCompleted() {
    	return remindersCompleted;
    }

	
    /**
     * Set reminders completed by the patient
     * 
     * @param remindersCompleted reminders completed by the patient
     */
    public void setRemindersCompleted(List<LafReminder> remindersCompleted) {
    	this.remindersCompleted = remindersCompleted;
    }

	
    /**
     * Get response types
     * 
     * @return response types
     */
    public String[] getResponseTypes() {
    	return responseTypes;
    }

	
    /**
     * Set response types
     * 
     * @param responseTypes repsone types
     */
    public void setResponseTypes(String[] responseTypes) {
    	this.responseTypes = responseTypes;
    }

	
    /**
     * Get care types
     * 
     * @return care types
     */
    public String[] getCareTypes() {
    	return careTypes;
    }

	
    /**
     * Set care types
     * 
     * @param careTypes care types
     */
    public void setCareTypes(String[] careTypes) {
    	this.careTypes = careTypes;
    }

	
    /**
     * Get all alerts for a patient
     * 
     * @return list of alerts
     */
    public List<Alert> getAlerts() {
    	return alerts;
    }

	
    /**
     * set alerts
     * 
     * @param alerts alerts for a patient
     */
    public void setAlerts(List<Alert> alerts) {
    	this.alerts = alerts;
    }
 }
