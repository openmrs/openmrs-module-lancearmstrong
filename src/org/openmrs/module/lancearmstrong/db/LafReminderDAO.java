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
 * Data Access Object for cancer_reminder table
 * 
 * @author hxiao
 */
public interface LafReminderDAO {

	/**
     * Get a reminder object
     * 
     * @param id id of reminder
     * @return reminder object
     */
    LafReminder getLafReminder(Integer id);

	/**
     * Save reminder object to database
     * 
     * @param reminder reminder object
     */
    LafReminder saveLafReminder(LafReminder reminder);

	/**
     * delete a reminder
     * 
     * @param reminder reminder object
     */
    void deleteLafReminder(LafReminder reminder);

	/**
     * Get all reminder entries
     * 
     * @return a list of reminder objects
     */
    List<LafReminder> getAllLafReminders();

	/**
     * Get all reminders of a given patient
     * 
     * @param pat patient object
     * @return a list of reminders for that patient
     */
    List<LafReminder> getLafReminders(Patient pat);

	/**
     * Get completed tests for a given patient
     * 
     * @param pat a given patient
     * @return completed tests of the given patient
     */
    List<LafReminder> getLafRemindersCompleted(Patient pat);

	/**
     * Get reminder object for a given patient, care type and target date 
     * 
     * @param patient patient object
     * @param careType type of care
     * @param targetDate target date
     * @return reminder object
     */
    LafReminder getLafReminder(Patient patient, Concept careType, Date targetDate);

	/**
     * Get reminders recommended by patient's providers
     * 
     * @param pat a given patient
     * @return list of reminders/follow up care recommended
     */
    List<LafReminder> getLafRemindersByProvider(Patient pat);

	/**
     * Delete recommended follow-up care
     * 
     * @param patientId patient id
     * @param targetDate target/recommended date of the care 
     * @param careType type of care
     */
    void deleteLafReminder(Integer patientId, Date targetDate, String careType);

}
