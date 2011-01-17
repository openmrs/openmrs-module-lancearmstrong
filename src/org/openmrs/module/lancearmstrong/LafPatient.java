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


/**
 *
 */
public class LafPatient {
	private Integer patientId;
	private List<LafReminder> reminders;
	
	LafPatient(Integer patId) {
		this.patientId = patId;
	}
	
    public Integer getPatientId() {
    	return patientId;
    }
	
    public void setPatientId(Integer patientId) {
    	this.patientId = patientId;
    }
	
    public List<LafReminder> getReminders() {
    	return reminders;
    }
	
    public void setReminders(List<LafReminder> reminders) {
    	this.reminders = reminders;
    }

 }
