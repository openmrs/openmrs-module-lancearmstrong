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

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.User;


/**
 *
 */
public class LafReminder {
	private Integer id;	
	private Patient patient;
	private Concept followProcedure;
	private Date responseDate;
	private String responseType;
	private String responseAttributes;
	private String responseComments;
	private User responseUser;
	private Date targetDate;
	
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
	
    public void setResponseUser(User responseUser) {
    	this.responseUser = responseUser;
    }

	
    public Date getTargetDate() {
    	return targetDate;
    }

	
    public void setTargetDate(Date targetDate) {
    	this.targetDate = targetDate;
    }
	
 }
