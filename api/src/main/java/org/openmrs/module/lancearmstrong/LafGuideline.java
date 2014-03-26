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
 * Data object to represent a follow up care guide line for a given procedure
 * 
 * @author hxiao
 */
public class LafGuideline {
	private Integer id;	
	private Concept cancerType;
	private Concept cancerStage;
	private Concept followProcedure;
	private String followYears;
	
    /**
     * get id of the guideline
     * 
     * @return id of the guideline
     */
    public Integer getId() {
    	return id;
    }
	
    /**
     * Set id of the guideline
     * 
     * @param id guideline id
     */
    public void setId(Integer id) {
    	this.id = id;
    }
	
    /**
     * Get cancer type of the guideline
     * 
     * @return cancer type
     */
    public Concept getCancerType() {
    	return cancerType;
    }
	
    /**
     * ASet cancer type of the guideline
     * 
     * @param cancerType cancer type
     */
    public void setCancerType(Concept cancerType) {
    	this.cancerType = cancerType;
    }
	
    /**
     * Get cancer stage of the guideline
     * 
     * @return cancer stage
     */
    public Concept getCancerStage() {
    	return cancerStage;
    }
	
    /**
     * Set cancer stage
     * 
     * @param cancerStage stage
     */
    public void setCancerStage(Concept cancerStage) {
    	this.cancerStage = cancerStage;
    }
	
    /**
     * Get follow up care procedure
     * 
     * @return follow up care procedure
     */
    public Concept getFollowProcedure() {
    	return followProcedure;
    }
	
    /**
     * Set follow up care procedure
     * 
     * @param followProcedure follow up care procedure
     */
    public void setFollowProcedure(Concept followProcedure) {
    	this.followProcedure = followProcedure;
    }
	
    /**
     * Get the after-surgery years recommended for a given follow up care procedure 
     * 
     * @return after-surgery years for the follow up procedure
     */
    public String getFollowYears() {
    	return followYears;
    }
	
    /**
     * Set the after-surgery years recommended for a given follow up care procedure 
     * 
     * @param followYears after-surgery years for the follow up procedure
     */
    public void setFollowYears(String followYears) {
    	this.followYears = followYears;
    }
	
 
}
