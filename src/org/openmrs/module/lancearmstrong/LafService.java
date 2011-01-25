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

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.module.lancearmstrong.db.LafGuidelineDAO;
import org.openmrs.module.lancearmstrong.db.LafReminderDAO;


/**
 *
 */
public interface LafService {

	/**
     * Auto generated method comment
     * 
     * @return
     */
    LafGuidelineDAO getGuidelineDao();

	/**
     * Auto generated method comment
     * 
     * @param guidelineDao
     */
    void setGuidelineDao(LafGuidelineDAO guidelineDao);

	/**
     * Auto generated method comment
     * 
     * @return
     */
    LafReminderDAO getReminderDao();

	/**
     * Auto generated method comment
     * 
     * @param reminderDao
     */
    void setReminderDao(LafReminderDAO reminderDao);

	/**
     * Auto generated method comment
     * 
     * @param pat
     * @return
     */
    List<LafReminder> getReminders(Patient pat);

	/**
     * Auto generated method comment
     * 
     * @param pat
     * @return
     */
    List<Concept> getSideEffects(Patient pat);

}
