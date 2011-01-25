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
package org.openmrs.module.lancearmstrong.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.module.lancearmstrong.LafUtil;
import org.openmrs.web.controller.PortletController;


/**
 * Controller for the patientEncounters portlet.
 * 
 * Provides a map telling which forms have their view and edit links overridden by form entry modules  
 */
public class PatientToxicitiesPortletController extends PortletController {

	/**
     * @see org.openmrs.web.controller.PortletController#populateModel(javax.servlet.http.HttpServletRequest, java.util.Map)
     */
    @Override
    protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		log.debug("Entering PatientToxicitiesPortletController.populateModel");
		
    	Patient patient = (Patient) model.get("patient");
    	if (patient == null)
    		throw new IllegalArgumentException("This portlet may only be used in the context of a Patient");
    	model.put("sideEffects", LafUtil.getService().getSideEffects(patient));
   }

	
}
