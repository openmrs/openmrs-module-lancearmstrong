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
package org.openmrs.module.lancearmstrong.extension.html;

import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;

/**
 * This class defines the links that will appear on the administration page under the
 * "basicmodule.title" heading. This extension is enabled by defining (uncommenting) it in the
 * /metadata/config.xml file.
 */
public class PatientTreatmentSummary extends LAPatientDashboardTabExt {
	
	/**
	 * default constructor: set display order attribute
	 */
	public PatientTreatmentSummary() {
		super();
		String order = Context.getAdministrationService().getGlobalProperty("lancearmstrong.PatientTreatmentSummary.displayorder");
		this.setOrder(Integer.valueOf(order==null? "1": order));
	}


	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getMediaType()
	 */
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	/**
     * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getPortletUrl()
     */
    @Override
    public String getPortletUrl() {
	    // TODO Auto-generated method stub
	    return "patientTreatmentSummary";
    }

	/**
     * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getRequiredPrivilege()
     */
    @Override
    public String getRequiredPrivilege() {
	    // TODO Auto-generated method stub
	    return "View Treatment Summary";
    }

	/**
     * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getTabId()
     */
    @Override
    public String getTabId() {
	    // TODO Auto-generated method stub
	    return "patientTreatmentSummary";
    }

	/**
     * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getTabName()
     */
    @Override
    public String getTabName() {
	    // TODO Auto-generated method stub
	    return "My Treatment History";
    }
	
}
