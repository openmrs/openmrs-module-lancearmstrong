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


/**
 *
 */
public class LafPatient {
	private Patient patient;
	private List<LafReminder> reminders; //follow-up care per guideline
	private List<LafReminder> remindersCompleted; //follow-up care actually performed
	private String[] responseTypes = {"Normal", "Abnormal", "Don't know"};
	private String[] careTypes = {
			"Colonoscopy", "History and Physical",
			"CEA Tests", "CT Scan Chest/Abdomen", 
			"CT Scan Pelvis", "Flex Sigmoidoscopy"};
	
	public LafPatient(Patient pat, List<LafReminder> rem, List<LafReminder> remCompl) {
		this.patient = pat;
		this.reminders = rem;
		this.remindersCompleted = remCompl;
		
	}
			
    public List<LafReminder> getReminders() {
    	return reminders;
    }
	
    public void setReminders(List<LafReminder> reminders) {
    	this.reminders = reminders;
    }

	
    public Patient getPatient() {
    	return patient;
    }

	
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }

	
    public List<LafReminder> getRemindersCompleted() {
    	return remindersCompleted;
    }

	
    public void setRemindersCompleted(List<LafReminder> remindersCompleted) {
    	this.remindersCompleted = remindersCompleted;
    }

	
    public String[] getResponseTypes() {
    	return responseTypes;
    }

	
    public void setResponseTypes(String[] responseTypes) {
    	this.responseTypes = responseTypes;
    }

	
    public String[] getCareTypes() {
    	return careTypes;
    }

	
    public void setCareTypes(String[] careTypes) {
    	this.careTypes = careTypes;
    }
 }
