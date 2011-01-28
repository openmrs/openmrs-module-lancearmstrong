/**
 * The contents of this file are subject to the Regenstrief Public License
 * Version 1.0 (the "License"); you may not use this file except in compliance with the License.
 * Please contact Regenstrief Institute if you would like to obtain a copy of the license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) Regenstrief Institute.  All Rights Reserved.
 */
package org.openmrs.module.lancearmstrong;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.lancearmstrong.db.*;

/**
 *
 */
public class LafServiceImpl extends BaseOpenmrsService implements LafService {
    protected final Log log = LogFactory.getLog(getClass());
    
    private LafGuidelineDAO guidelineDao;
    private LafReminderDAO reminderDao;
    
    private final static Integer CANCER_TYPE = 6145; 
    private final static Integer CANCER_STAGE = 6146; 
    private final static Integer SURGERY_TYPE = 6152; 
    private final static Integer SURGERY_DATE = 6118; 
    private final static Integer DIAGNOSIS_DATE = 6101; 
    private final static Integer RADIATION_TYPE = 6154; 
    private final static Integer RADIATION_START_DATE = 6132; 
    private final static Integer CHEMOTHERAPY_MEDS = 6156; 
    
    private final static String  CANCER_TREATMENT_SUMMARY_ENCOUNTER = "CANCER TREATMENT SUMMARY"; 
    private final static String  RADIATION_ENCOUNTER = "CANCER TREATMENT - RADIATION"; 
    private final static String  CHEMOTHERAPY_ENCOUNTER = "CANCER TREATMENT - CHEMOTHERAPY"; 
    private final static String  SURGERY_ENCOUNTER = "CANCER TREATMENT - SURGERY"; 
	
    //ANY TREATMENT TYPE=0, CHEMOTHERAPY=1, RADIOLOGY=2, SURGERY=3
    //6225-6248: Concept ID's of side effects
    //6115-6130: Sub types of each treatment type
	protected final static HashMap<MultiKey, Integer[]> sideEffectsMap = new HashMap<MultiKey, Integer[]>();
	static {
		sideEffectsMap.put(new MultiKey(new Integer[]{0,0}), new Integer[]{6225, 6226});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,0}), new Integer[]{6236});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6123}), new Integer[]{6227,6228,6231,6232,6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6124}), new Integer[]{6229,6231,6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6125}), new Integer[]{6230,6231,6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6126}), new Integer[]{6230,6231,6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6127}), new Integer[]{6231,6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6128}), new Integer[]{6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6129}), new Integer[]{6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6130}), new Integer[]{6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{2, 6134}), new Integer[]{6244,6245,6246,6247,6248});
		sideEffectsMap.put(new MultiKey(new Integer[]{2, 6135}), new Integer[]{6244,6245,6246,6247,6248});
		sideEffectsMap.put(new MultiKey(new Integer[]{2, 6136}), new Integer[]{6244,6245,6246,6247,6248});
		sideEffectsMap.put(new MultiKey(new Integer[]{2, 6137}), new Integer[]{6244,6245,6246,6247,6248});
		sideEffectsMap.put(new MultiKey(new Integer[]{2, 6138}), new Integer[]{6244,6245,6246,6247,6248});
		sideEffectsMap.put(new MultiKey(new Integer[]{3,6112}), new Integer[]{6237});
		sideEffectsMap.put(new MultiKey(new Integer[]{3,6113}), new Integer[]{6238});
		sideEffectsMap.put(new MultiKey(new Integer[]{3,6114}), new Integer[]{6239});
		sideEffectsMap.put(new MultiKey(new Integer[]{3,6115}), new Integer[]{6240});
		sideEffectsMap.put(new MultiKey(new Integer[]{3,6116}), new Integer[]{6241});
		sideEffectsMap.put(new MultiKey(new Integer[]{3,6117}), new Integer[]{6242});
	}
    
    @Override
    public LafGuidelineDAO getGuidelineDao() {
    	return guidelineDao;
    }
	

	@Override
    public void setGuidelineDao(LafGuidelineDAO guidelineDao) {
    	this.guidelineDao = guidelineDao;
    }
	
    @Override
    public LafReminderDAO getReminderDao() {
    	return reminderDao;
    }
	
    @Override
    public void setReminderDao(LafReminderDAO reminderDao) {
    	this.reminderDao = reminderDao;
    }

	/**
     * @see org.openmrs.module.lancearmstrong.LafService#getReminders(org.openmrs.Patient)
     */
    @Override
    public List<LafReminder> getReminders(Patient pat) {
	    return findReminders(pat);
    }
    
    /**
     * @see org.openmrs.module.lancearmstrong.LafService#getReminders(org.openmrs.Patient)
     */
    @Override
    public List<LafReminder> getRemindersCompleted(Patient pat) {    	
	    return reminderDao.getLafRemindersCompleted(pat);
    }    
    
	/**
     * @see org.openmrs.module.lancearmstrong.LafService#getReminders(org.openmrs.Patient)
     */
    @Override
    public List<Concept> getSideEffects(Patient pat) {
    	log.debug("Calling LafServiceImpl:getSideEffects for patient " + pat);
	    // TODO Auto-generated method stub
    	List<Concept> sideEffects = new ArrayList<Concept>();
    	
    	//Key set
    	List<MultiKey> keys = new ArrayList<MultiKey>();
    	keys.add(new MultiKey(new Integer[]{0,0}));

    	//get the patient's cancer treatment types and sub-types
    	
    	//find  Chemotherapy meds used
    	Encounter enc = findCancerTreatment(pat, this.CHEMOTHERAPY_ENCOUNTER);
    	if(enc != null) {
        	keys.add(new MultiKey(new Integer[]{1,0}));
    		
        	Concept chemoMedsConcept = Context.getConceptService().getConcept(this.CHEMOTHERAPY_MEDS);
        	List<Obs> meds = Context.getObsService().getObservationsByPersonAndConcept(pat, chemoMedsConcept);
        	if(meds != null) {
        		for(Obs med : meds) {
        			if(med.getEncounter().getId() != enc.getId()) {
        				continue;
        			} 
        			keys.add(new MultiKey(new Integer[]{1, med.getValueCoded().getId()}));
        			log.debug("Chemotherapy med added: " + med + ", id=" + med.getValueCoded().getId());
        		}
        		if(keys.size()==1) {
           			log.debug("No chemotherapy meds are found for encounter " + enc);    		        			
        		}
        	} else {
       			log.debug("No chemotherapy meds are found.");    		
        	}
    	} else {
   			log.debug("No chemotherapy is found.");    		
    	}
    	
    	//find  Radiation types
    	enc = findCancerTreatment(pat, this.RADIATION_ENCOUNTER);
    	if(enc != null) {
        	//keys.add(new Integer[]{2,0});
    		
        	Concept radioTypeConcept = Context.getConceptService().getConcept(this.RADIATION_TYPE);
        	List<Obs> types = Context.getObsService().getObservationsByPersonAndConcept(pat, radioTypeConcept);
        	if(types != null) {
        		for(Obs type : types) {
        			if(type.getEncounter().getId() != enc.getId()) {
        				continue;
        			} 
        			keys.add(new MultiKey(new Integer[]{2, type.getValueCoded().getId()}));
        			
        			log.debug("Radiation type added: " + type + ", id=" + type.getValueCoded().getId());
        		}
        	}
    	} 
    	
      	//find  Surgery types
    	enc = findCancerTreatment(pat, this.SURGERY_ENCOUNTER);
    	if(enc != null) {
        	//keys.add(new Integer[]{3,0});
    		
        	Concept surgeryTypeConcept = Context.getConceptService().getConcept(this.SURGERY_TYPE);
        	List<Obs> types = Context.getObsService().getObservationsByPersonAndConcept(pat, surgeryTypeConcept);
        	if(types != null) {
        		for(Obs type : types) {
        			if(type.getEncounter().getId() != enc.getId()) {
        				continue;
        			} 
        			keys.add(new MultiKey(new Integer[]{3, type.getValueCoded().getId()}));
        			log.debug("Surgery type added: " + type + ", id=" + type.getValueCoded().getId());
        		}
        	}
    	} 
    	
    	//query side effect concepts' id's from a hash map and add corresponding Concept objects to the return list
    	for(MultiKey key : keys) {
    		Integer[] sideEffectIds = this.sideEffectsMap.get(key);
    		if(sideEffectIds == null) {
    			log.debug("Key skipped: " + key);
    			continue;
    		}
    		for(Integer sideEffId : sideEffectIds) {
    			Concept sideEffect = Context.getConceptService().getConcept(sideEffId);
    			sideEffects.add(sideEffect);//sideEffect.getName().getName(); sideEffect.getDescription().getChangedBy();
    			log.debug("side effect found: " + sideEffId + "|" + sideEffect.getName().getName() + " for key " + key);
    		}
    	}
    	
    	log.debug("Number of side effects found: " + (sideEffects==null? 0:sideEffects.size()));
	    return sideEffects;
    } 
    
    private Encounter findCancerTreatment(Patient pat, String encounterType) {
    	//find cancer treatment summary encounter	
		List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(pat);    	    
		Integer encId = null;
		Date encDate = null;
		Encounter latestEncounter = null;
		for(Encounter enc : encs) {    		
			if(!enc.isVoided() && encounterType.equals(enc.getEncounterType().getName())) {
				if((encId == null || enc.getEncounterDatetime().after(encDate))) {
					encId = enc.getId();
					encDate = enc.getEncounterDatetime();
					enc.getObs();
					latestEncounter = enc;
				}   			
			}
		}
		
		return latestEncounter;
		
	}
	    

	/**
     * Auto generated method comment
     * 
     * @param pat
     */
    private List<LafReminder>  findReminders(Patient pat) {
    	//find cancer treatment summary encounter
    	/*
    	List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(pat);    	    
    	Integer encId = null;
    	Date encDate = null;
    	for(Encounter enc : encs) {    		
    		if(!enc.isVoided() && CANCER_TREATMENT_SUMMARY_ENCOUNTER.equals(enc.getEncounterType().getName())) {
    			if((encId == null || enc.getEncounterDatetime().after(encDate))) {
    				encId = enc.getId();
    				encDate = enc.getEncounterDatetime();
    				enc.getObs();
    			}   			
    		}
    	}
    	*/
    	
	    //find surgery date
    	Concept surgeryDateConcept = Context.getConceptService().getConcept(SURGERY_DATE);
    	Obs surgeryDate = findLatest(Context.getObsService().getObservationsByPersonAndConcept(pat, surgeryDateConcept));
    	Date surgDate = surgeryDate==null? null : surgeryDate.getValueDatetime();
    	//find rediation start date
    	Concept radiationTypeConcept = Context.getConceptService().getConcept(RADIATION_TYPE);
    	Obs radiationType = findLatest(Context.getObsService().getObservationsByPersonAndConcept(pat, radiationTypeConcept));
    	Concept radType = radiationType==null? null : radiationType.getValueCoded();
    	
    	//find cancer type
    	Concept cancerTypeConcept = Context.getConceptService().getConcept(CANCER_TYPE);
    	Obs cancerType = findLatest(Context.getObsService().getObservationsByPersonAndConcept(pat, cancerTypeConcept));
    	Concept type = cancerType==null? null : cancerType.getValueCoded();
    	//find cancer stage
    	Concept cancerStageConcept = Context.getConceptService().getConcept(CANCER_STAGE);
    	Obs cancerStage = findLatest(Context.getObsService().getObservationsByPersonAndConcept(pat, cancerStageConcept));
    	Concept stage = cancerStage==null? null : cancerStage.getValueCoded();
        	
    	//find follow-up years
    	List<LafGuideline> guidelines = guidelineDao.getLafGuideline(type, stage);
    	
    	log.debug("Get guidelins: type=" + type + ", stage=" + stage + ", guidelines=" + guidelines);
    	//create reminder entries
    	List<LafReminder> reminders = new ArrayList<LafReminder>();
    	if(guidelines != null) {
	    	for(LafGuideline guideline : guidelines) {
	    		Date[] dates = findTargetDates(surgDate, radType, guideline.getFollowYears());
	    		
	    		for(Date dt: dates) {
		    		LafReminder reminder = new LafReminder();
		    		reminder.setPatient(pat);
		    		reminder.setFollowProcedure(guideline.getFollowProcedure());
		    		reminder.setTargetDate(dt);
		    	    //update cancer_patient_reminder table
		        	//reminderDao.saveLafReminder(reminder);
		        	reminders.add(reminder);
	    		}
	    	}   	
    	} else {
    		log.error("Guideline is not found for cancer type:" + type + " and cancer stage: "+ stage);
    	}
    	
    	return reminders;
     }

	/**
     * Auto generated method comment
     * 
     * @param surgeryDate
     * @param followYears
     * @return
     */
    private Date[] findTargetDates(Date surgDate, Concept radiationType, String followYears) {
    	String[] split1  = followYears.split(":");
    	String[] split2 = split1[0].split(",");
     	Date startDate = surgDate;
    	if(split1.length>=2 && "NO SURGERY".equals(split1[1])) {
    		if(surgDate == null) {
    			startDate = new Date();
    		} 
    	} else if(split1.length>=2 && "NO RADIATION".equals(split1[1])) {
    		if(radiationType != null) {
    			return null;
    		}
    	}
    	
    	if(startDate == null) {
    		return null;
    	}
    	 
    	Date[] targetDates = new Date[split2.length];
    	int index = 0;
    	for(int ii=0; ii<split2.length; ii++) {
    		targetDates[ii] = findDate(startDate, split2[ii]);    	    
    	}
	    // TODO Auto-generated method stub
	    return targetDates;
    }

	/**
     * Auto generated method comment
     * 
     * @param startDate
     * @param string
     * @return
     */
    private Date findDate(Date startDate, String yearsAfter) {
    	float yrs = Float.parseFloat(yearsAfter);
    	int months = (int)(yrs * 12.0);
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(startDate);
    	cal.add(Calendar.MONTH, months);
    	
	    // TODO Auto-generated method stub
	    return cal.getTime();
    }

	/**
     * Auto generated method comment
     * 
     * @param observationsByPersonAndConcept
     * @return
     */
    private Obs findLatest(List<Obs> observations) {
    	Obs latest = null;
    	
    	if(observations != null) {
    		for (Obs obs : observations) {
    			if(obs != null && !obs.isVoided()) {
    				if(latest == null || latest.getDateCreated().before(obs.getDateCreated())) {
    					latest = obs;
    				}
    			  
    			}
    		}
    	}
    	
	    return latest;
    }      
}
