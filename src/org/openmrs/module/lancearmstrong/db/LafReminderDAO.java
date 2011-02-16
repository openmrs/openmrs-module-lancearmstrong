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
package org.openmrs.module.lancearmstrong.db;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.module.lancearmstrong.LafReminder;


/**
 *
 */
public interface LafReminderDAO {

	/**
     * Auto generated method comment
     * 
     * @param id
     * @return
     */
    LafReminder getLafReminder(Integer id);

	/**
     * Auto generated method comment
     * 
     * @param reminder
     * @return
     */
    LafReminder saveLafReminder(LafReminder reminder);

	/**
     * Auto generated method comment
     * 
     * @param reminder
     */
    void deleteLafReminder(LafReminder reminder);

	/**
     * Auto generated method comment
     * 
     * @return
     */
    List<LafReminder> getAllLafReminders();

	/**
     * Auto generated method comment
     * 
     * @param pat
     * @return
     */
    List<LafReminder> getLafReminders(Patient pat);

	/**
     * Auto generated method comment
     * 
     * @param pat
     * @return
     */
    List<LafReminder> getLafRemindersCompleted(Patient pat);

	/**
     * Auto generated method comment
     * 
     * @param patient
     * @param careType
     * @param targetDate
     * @return
     */
    LafReminder getLafReminder(Patient patient, Concept careType, Date targetDate);

}
