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

import java.util.Comparator;
import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;


/**
 *
 */
public class LafReminder {
	public static final String RESPONSE_COMPLETED = "Completed";
	public static final String RESPONSE_SKIPPED = "Skipped";
	public static String FLAG_COMPLETED = "COMPLETED";
	public static String FLAG_SCHEDULED = "SCHEDULED";
	public static String FLAG_SNOOZED = "SNOOZED";
	public static String FLAG_ALERTED = "ALERTED";
	public static String FLAG_SKIPPED = "SKIPPED";
	private Integer id;	
	private Patient patient;
	private Concept followProcedure;
	private String followProcedureName;
	private Date responseDate;
	private String responseType; //follow-up care results
	private String responseAttributes;
	private String responseComments;
	private User responseUser;
	private Date targetDate;
	private Date CompleteDate;
	private String doctorName; 
	private String flag;
    /**
     * Sort by start_date
     * 
     * @return date comparator
     */
    public static Comparator<LafReminder> getDateComparator() {
        return new Comparator<LafReminder>() {
            
        	//in ascending order
            @Override 
            public int compare(final LafReminder g1, final LafReminder g2) {
                return (g2.getTargetDate()==null||g1.getTargetDate()==null) ? 1 : g1.getTargetDate().compareTo(g2.getTargetDate());
            }
        };
    }
    
    public Integer getId() {
    	return id;
    }
	
    public void setId(Integer id) {
    	this.id = id;
    }
	
    public Patient getPatient() {
    	return patient;
    }
	
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }
	
    public Concept getFollowProcedure() {
    	return followProcedure;
    }
	
    public void setFollowProcedure(Concept followProcedure) {
    	this.followProcedure = followProcedure;
    }
	
    public Date getResponseDate() {
    	return responseDate;
    }
	
    public void setResponseDate(Date responseDate) {
    	this.responseDate = responseDate;
    }
	
    public String getResponseType() {
    	return responseType;
    }
	
    public void setResponseType(String responseType) {
    	this.responseType = responseType;
    }
	
    public String getResponseAttributes() {
    	return responseAttributes;
    }
	
    public void setResponseAttributes(String responseAttributes) {
    	this.responseAttributes = responseAttributes;
    }
	
    public String getResponseComments() {
    	return responseComments;
    }
	
    public void setResponseComments(String responseComments) {
    	this.responseComments = responseComments;
    }
	
    public User getResponseUser() {
    	return responseUser;
    }
	
    
    public String getFlag() {
    	return flag;
    }

	
    public void setFlag(String flag) {
    	this.flag = flag;
    }

	public void setResponseUser(User responseUser) {
    	this.responseUser = responseUser;
    }

	
    public Date getTargetDate() {
    	return targetDate;
    }

	
    public void setTargetDate(Date targetDate) {
    	this.targetDate = targetDate;
    }

	
    public Date getCompleteDate() {
    	return CompleteDate;
    }

	public void setCompleteDate(Date completeDate) {
    	CompleteDate = completeDate;
    }	
    
    public String getDoctorName() {
    	this.doctorName = responseAttributes;
    	return doctorName;
    }

	
    public void setDoctorName(String doctorName) {
    	this.doctorName = doctorName;
    	this.responseAttributes = doctorName;
    }

	
    public String getFollowProcedureName() {
    	if(followProcedureName == null && followProcedure != null) {
    		followProcedureName = followProcedure.getName().getName();
    	}
    	return followProcedureName;
    }

	
    public void setFollowProcedureName(String followProcedureName) {
    	this.followProcedureName = followProcedureName;
    }
	
 }
