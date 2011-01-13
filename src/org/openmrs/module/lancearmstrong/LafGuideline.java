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

import org.openmrs.Concept;


/**
 *
 */
public class LafGuideline {
	private Integer id;	
	private Concept cancerType;
	private Concept cancerStage;
	private Concept followProcedure;
	private String followYears;
	
    public Integer getId() {
    	return id;
    }
	
    public void setId(Integer id) {
    	this.id = id;
    }
	
    public Concept getCancerType() {
    	return cancerType;
    }
	
    public void setCancerType(Concept cancerType) {
    	this.cancerType = cancerType;
    }
	
    public Concept getCancerStage() {
    	return cancerStage;
    }
	
    public void setCancerStage(Concept cancerStage) {
    	this.cancerStage = cancerStage;
    }
	
    public Concept getFollowProcedure() {
    	return followProcedure;
    }
	
    public void setFollowProcedure(Concept followProcedure) {
    	this.followProcedure = followProcedure;
    }
	
    public String getFollowYears() {
    	return followYears;
    }
	
    public void setFollowYears(String followYears) {
    	this.followYears = followYears;
    }
	
 
}
