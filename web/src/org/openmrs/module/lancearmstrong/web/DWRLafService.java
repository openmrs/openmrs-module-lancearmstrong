
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
			       ",careType=" + careType + "/" + Context.getConceptService().getConcept(careType));
		LafReminder newReminder = new LafReminder();		
		newReminder.setPatient(Context.getPatientService().getPatient(patientId));
		newReminder.setId(null);
		newReminder.setCompleteDate(completeDate);
		newReminder.setResponseType(resultType);
		newReminder.setFollowProcedure(Context.getConceptService().getConceptByName(careType));
		newReminder.setResponseComments(comments);
		newReminder.setResponseAttributes(docName);
		newReminder.setResponseDate(new Date());
		newReminder.setResponseUser(Context.getAuthenticatedUser());
		
		LafUtil.getService().getReminderDao().saveLafReminder(newReminder);
	}
	
	public LafPatient getLafPatient() {
		return new LafPatient(null, null, null);
	}
}
