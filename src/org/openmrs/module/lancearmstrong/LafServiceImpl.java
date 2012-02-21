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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
 * Follow-up care alerting rule:
 * 
 * 1. Generate an alert for each recommended follow-up care type 30-day before corresponding next target date if and only if no schedule or snooze found for this target date and follow-up care type
 * 2. Next target date for each recommended follow-up care type is calculated as:
 *    1) Get the date of latest received care
 *    2) Match above date to one of the recommended dates
 *    3) Find next target date based on today's date, matched date above and recommended dates:
 *       (1) find two consecutive recommended dates between which today's date is
 *       (2) find the mid-date of the two consecutive recommended dates above
 *       (3) set the lower recommended date as the next target date if and only if today is before the mid-date above and the lower recommended date has not been matched by a received care  
 *       (4) set the upper recommended date as the next target date if and only if today is after the mid-date above and the upper recommended date has not been matched by a received care
 * 3. Mark recommended dates as yellow (missed date), green (matched date), red (alert date), and blue (scheduled or snoozed date) and display detail as hover texts
 * 4. Assume date format in ENG-US locale   
 * 
 * @author hxiao
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
    
    private final static int ALERT_DAYS = 60; 
    private final static int MATCH_DAYS = 7; 
	
    //ANY TREATMENT TYPE=0, CHEMOTHERAPY=1, RADIOLOGY=2, SURGERY=3
    //6225-6248: Concept ID's of side effects
    //6115-6130: Sub types of each treatment type
	protected final static HashMap<MultiKey, Integer[]> sideEffectsMap = new HashMap<MultiKey, Integer[]>();
	static {
		sideEffectsMap.put(new MultiKey(new Integer[]{0,0}), new Integer[]{6225, 6226}); //all
		sideEffectsMap.put(new MultiKey(new Integer[]{1,0}), new Integer[]{6236}); //chemotherapy
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6123}), new Integer[]{6227,6228,6231,6232,6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6124}), new Integer[]{6229,6231,6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6125}), new Integer[]{6230,6231,6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6126}), new Integer[]{6230,6231,6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6127}), new Integer[]{6231,6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6128}), new Integer[]{6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6129}), new Integer[]{6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{1,6130}), new Integer[]{6233,6234,6235});
		sideEffectsMap.put(new MultiKey(new Integer[]{2, 6134}), new Integer[]{6244,6245,6246,6247,6248}); //radiation
		sideEffectsMap.put(new MultiKey(new Integer[]{2, 6135}), new Integer[]{6244,6245,6246,6247,6248});
		sideEffectsMap.put(new MultiKey(new Integer[]{2, 6136}), new Integer[]{6244,6245,6246,6247,6248});
		sideEffectsMap.put(new MultiKey(new Integer[]{2, 6137}), new Integer[]{6244,6245,6246,6247,6248});
		sideEffectsMap.put(new MultiKey(new Integer[]{2, 6138}), new Integer[]{6244,6245,6246,6247,6248});
		sideEffectsMap.put(new MultiKey(new Integer[]{3,6112}), new Integer[]{6237}); //surgeries
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
    public List<LafReminder> getReminders(Patient pat, Date indexDate) {
	    return findReminders(pat, indexDate);
    }
    
    /**
     * @see org.openmrs.module.lancearmstrong.LafService#getReminders(org.openmrs.Patient)
     */
    @Override
    public List<LafReminder> getRemindersCompleted(Patient pat) {    	
	    return reminderDao.getLafRemindersCompleted(pat);
    }  
    
    private List<LafReminder> getRemindersByProvider(Patient pat) {    	
	    return reminderDao.getLafRemindersByProvider(pat);
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
    			if("M".equals(pat.getGender()) && sideEffId.intValue()==6248) {
    				continue; 
    			} else if("F".equals(pat.getGender()) && sideEffId.intValue()==6247) {
    				continue; 
    			} else if(isDuplicate(sideEffId, sideEffects)) {
    				continue;
    			}
    			Concept sideEffect = Context.getConceptService().getConcept(sideEffId);
    			sideEffects.add(sideEffect);//sideEffect.getName().getName(); sideEffect.getDescription().getChangedBy();
    			log.debug("side effect found: " + sideEffId + "|" + sideEffect.getName().getName() + " for key " + key);
    		}
    	}
    	
    	log.debug("Number of side effects found: " + (sideEffects==null? 0:sideEffects.size()));
	    return sideEffects;
    } 
    
    /**
     * Auto generated method comment
     * 
     * @param sideEffId
     * @param sideEffects
     * @return
     */
    private boolean isDuplicate(Integer sideEffId, List<Concept> sideEffects) {
	    // TODO Auto-generated method stub
    	for(Concept sd : sideEffects) {
    		if(sd.getId().intValue()==sideEffId.intValue()) {
    			return true;
    		}
    	}
	    return false;
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

	/*
	 * Find specific reminders for alerting purpose for a given patient at a given date based on ALERT_DAYS (e.g. 60 days) alerting rule
	 * 
	 * Alert display logic: 
     * 
     * find reminders by guideline -> find and exclude reminders completed/matched, snoozed, scheduled, or not performed -> 
     * find reminders by providers -> exclude reminders completed/matched, snoozed, scheduled, or not performed for provider-recommended reminders ->
     * add provider recommended reminder alerts (overriding corresponding guidline reminder alerts to keep just one alert of the same kind)  
     *  
     * Note: 1. one completed test can match up to two reminders, one from guideline (middle-date rule), the other from providers (7-day-proximity rule)
     *       2. alert date is based on 60-day rule for both types of reminders (by guideline, and by providers)
     *       
	 */
    private List<LafReminder> findReminders(Patient pat, Date indexDate) {
    	//find all follow-up care guidelines
    	List<LafGuideline> guidelines = findGuidelines(pat);
    	
    	//find all reminders sorted by target date
    	List<LafReminder> reminders = findAllReminders(pat);
    	
    	//find all completed reminders
    	List<LafReminder> remindersCompleted = getRemindersCompleted(pat);    	    

        //mark completed reminders (will take prevalence over other marks)
        if(remindersCompleted != null) {
	        for(int ii = 0; ii< reminders.size(); ii++) { 
	            LafReminder reminder = reminders.get(ii);
	            LafReminder nextReminder = null;
	            LafReminder previousReminder = null;
	            
	            for(int jj=ii+1; jj<reminders.size(); jj++) {  
	            	nextReminder = reminders.get(jj);
	            	if(nextReminder.getFollowProcedure().equals(reminder.getFollowProcedure())) {
	            		break;           		
	            	}
	            }
	            
	            for(int jj=ii-1; jj>=0; jj--) {  
	            	previousReminder = reminders.get(jj);
	            	if(previousReminder.getFollowProcedure().equals(reminder.getFollowProcedure())) {
	            		break;           		
	            	}
	            }            
	            
	            for(LafReminder reminderCompl : remindersCompleted) {
	            	if(reminderCompl.getFollowProcedure().equals(reminder.getFollowProcedure())) {
		               if(reminderCompl.getCompleteDate().equals(reminder.getTargetDate())) {
		            	   reminder.setFlag(LafReminder.FLAG_COMPLETED);
		            	   reminder.setResponseDate(reminderCompl.getCompleteDate());
		            	   break;
		               } else if(previousReminder==null && reminderCompl.getCompleteDate().before(reminder.getTargetDate())) {
	            		   reminder.setFlag(LafReminder.FLAG_COMPLETED);
	            		   reminder.setResponseDate(reminderCompl.getCompleteDate());
	            		   break;
	            	   } else if(nextReminder==null && reminderCompl.getCompleteDate().after(reminder.getTargetDate())) {
	            		   reminder.setFlag(LafReminder.FLAG_COMPLETED);
	               		   reminder.setResponseDate(reminderCompl.getCompleteDate());
	               		   break;
	            	   } else if(previousReminder!=null && reminderCompl.getCompleteDate().before(reminder.getTargetDate()) && reminderCompl.getCompleteDate().after(findMidDate(previousReminder.getTargetDate(), reminder.getTargetDate()))) {
	            		   reminder.setFlag(LafReminder.FLAG_COMPLETED);
	               		   reminder.setResponseDate(reminderCompl.getCompleteDate());
	               		   break;
	            	   } else if(nextReminder!=null && reminderCompl.getCompleteDate().after(reminder.getTargetDate())&& reminderCompl.getCompleteDate().before(findMidDate(reminder.getTargetDate(), nextReminder.getTargetDate()))) {
	            		   reminder.setFlag(LafReminder.FLAG_COMPLETED);
	               		   reminder.setResponseDate(reminderCompl.getCompleteDate());
	               		   break;
	            	   }            		
	            	}
	            }
	        }
        }
    	    	
    	//compare with indexDate to determine reminder/alert for each guideline
    	List<LafReminder> newReminders = new ArrayList<LafReminder>();    
    	if(guidelines != null) {
    		log.debug("Number of guidelines found = " + guidelines.size());
	    	for(LafGuideline guideline : guidelines) {
	    		Date targetDate = findNextTargetDate(reminders, guideline.getFollowProcedure()); //completed reminders have been excluded from the next target date calculation
	    		
	    		Date alertDate = findAlertDate(pat, guideline.getFollowProcedure(), targetDate); //based on default offset of 60 days and user response to earlier alert
	    		log.debug("guideline="+guideline.getFollowProcedure() + ", years = " + guideline.getFollowYears());
	    		log.debug("targetDate=" + targetDate);
	    		log.debug("alertDate=" + alertDate);
	    		    		    		
	    		if(alertDate!= null && indexDate.after(alertDate)) {
	    			log.debug("Alert is found for patient: " + pat + ", guideline=" + guideline + ", targetDate=" + targetDate + ", alertStartDate=" + alertDate);

	    			//find Not Performed flag for this reminder
	    			String notPerformedFlag = findAttribute(pat, guideline.getFollowProcedure(), targetDate, "notPerformed");
	    			if(notPerformedFlag != null && "Yes".equals(notPerformedFlag)) {
	    				continue; //no need to alert
	    			}
	    			
	    			//find snooze date or schedule date for this reminder
	    			Date ssDate = findSnoozeOrScheduleDate(pat, guideline.getFollowProcedure(), targetDate);
	    			
	    			if(ssDate == null || ssDate.before(new Date())) {
			    		LafReminder reminder = new LafReminder();
			    		reminder.setPatient(pat);
			    		reminder.setFollowProcedure(guideline.getFollowProcedure());
			    		reminder.setTargetDate(targetDate);	    		
			    		newReminders.add(reminder);
	    			}	    				    			
	    		}
	    	}   	
    	} else {
    		log.debug("No guidelines are found!");
    	}    	   	

    	log.debug("Number of alerts found = " + (newReminders==null? 0: newReminders.size()) + " on index date " + indexDate);

    	return newReminders;    	
    }    
	    
	/**
     * Match a target date with a completed date based on 7-day-proximity rule
     * 
     * @param targetDate
     * @param lastCompleted
     * @return
     */
    private boolean matchCompletedDate(Date targetDate, Date lastCompleted) {
	    // TODO Auto-generated method stub
    	Calendar calTarget1 = Calendar.getInstance();
    	calTarget1.setTime(targetDate);
    	calTarget1.add(Calendar.DATE, MATCH_DAYS);
    	Calendar calTarget2 = Calendar.getInstance();
    	calTarget2.setTime(targetDate);
    	calTarget2.add(Calendar.DATE, -MATCH_DAYS);
    	
    	Calendar calCompl = Calendar.getInstance();
    	calCompl.setTime(lastCompleted);
    	
    	if(calCompl.before(calTarget1) && calCompl.after(calTarget2)) {
    		return true;
    	}    	    	
	    return false;
    }


	/**
     * Auto generated method comment
     * 
     * @param followProcedure
     * @param targetDate
	 * @param sb 
     * @return
     */
    private Date findSnoozeOrScheduleDate(Patient patient, Concept careType, Date targetDate) {
	    LafReminder reminder = this.reminderDao.getLafReminder(patient, careType, targetDate);
	    
	    Date ssDate = null;
	    String ssType = null;
	    if(reminder != null) {
	    	String[] splits = reminder.getResponseAttributes().split("=");
	    	if(splits.length >=2) {
	    		ssType = splits[0];
	    		if("snoozeDate".equals(ssType) || "scheduleDate".equals(ssType)) {
	    			try {
	    				ssDate = Context.getDateFormat().parse(splits[1]);
	    			} catch (ParseException e) {
		    			log.error("Bad date format: ssType=" + ssType + ", ssDate=" + ssDate);	    				
	    			}
	    			log.debug("This reminder is snoozed or scheduled: ssType=" + ssType + ", ssDate=" + ssDate);
	    		}
	    	}
	    }
	    
	    //void this date if it is older than today's date
	    //if(ssDate!=null && ssDate.before(new Date())) {
	    //	ssDate = null;
	    //}
	    
	    return ssDate;
    }    
    
    private Date findSnoozeOrScheduleDate(Patient patient, Concept careType, Date targetDate, String type) {
	    LafReminder reminder = this.reminderDao.getLafReminder(patient, careType, targetDate);
	    
	    Date ssDate = null;
	    String ssType = null;
	    if(reminder != null) {
	    	String[] splits = reminder.getResponseAttributes().split("=");
	    	if(splits.length >=2) {
	    		ssType = splits[0];
	    		if(type.equals(ssType)) {
	    			try {
	    				ssDate = Context.getDateFormat().parse(splits[1]);
	    			} catch (ParseException e) {
		    			log.error("Bad date format: ssType=" + ssType + ", ssDate=" + ssDate);	    				
	    			}
	    			log.debug("This reminder is snoozed or scheduled: ssType=" + ssType + ", ssDate=" + ssDate);
	    		} 
	    	}
	    }
	    
	    //void this date if it is older than today's date
	    if(ssDate!=null && ssDate.before(new Date())) {
	    	ssDate = null;
	    }
	    
	    return ssDate;
    } 
    
    private String findAttribute(Patient patient, Concept careType, Date targetDate, String type) {
	    LafReminder reminder = this.reminderDao.getLafReminder(patient, careType, targetDate);
	    
	    if(reminder==null || reminder.getResponseAttributes()==null) {
	    	return null;
	    }
	    String[] segments = reminder.getResponseAttributes().split(";");
	    
		String ssValue = null;	    
	    for(String segment : segments) {
		    String ssType = null;
		    if(segment != null) {
		    	String[] splits = segment.split("=");
		    	if(splits.length >=2) {
		    		ssType = splits[0].trim();
		    		if(type.equals(ssType)) {
	    				ssValue = splits[1];
		    		} 
		    	}
		    }
	    }
	    
	    return ssValue;
    }     


	/**
     * Auto generated method comment
	 * @param careType 
	 * @param pat 
     * 
     * @param targetDate
     * @return
     */
    private Date findAlertDate(Patient pat, Concept careType, Date targetDate) {
	    // TODO Auto-generated method stub
    	if(targetDate==null) return null;
    	    	
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(targetDate);
    	cal.add(Calendar.DATE, -ALERT_DAYS);
		
    	log.debug("alertDate=" + cal.getTime() + " based on offset of " + ALERT_DAYS + "days");
    	
	    return cal.getTime();
    }


	/**
     * Auto generated method comment
     * 
     * @param remindersCompleted
     * @param followProcedure
     * @return
     */
    private LafReminder findLastCompleted(List<LafReminder> remindersCompleted, Concept followProcedure) {
	    // TODO Auto-generated method stub
    	LafReminder lastCompletedReminder = null;
    	if(remindersCompleted==null) {
    		return null;
    	}
    	for(LafReminder reminder : remindersCompleted) {
    		if(reminder.getFollowProcedure().getId()==followProcedure.getId()) {
    			if(lastCompletedReminder == null || lastCompletedReminder.getCompleteDate().before(reminder.getCompleteDate())) {
    				lastCompletedReminder = reminder;
    			} 
    		}
    	}
	    return lastCompletedReminder;
    }


	/**
     * Find guidelines for a given patient
     * 
     * @param pat
     */
    private List<LafGuideline>  findGuidelines(Patient pat) {
    	//find cancer type
    	Concept cancerTypeConcept = Context.getConceptService().getConcept(CANCER_TYPE);
    	Obs cancerType = findLatest(Context.getObsService().getObservationsByPersonAndConcept(pat, cancerTypeConcept));
    	Concept type = cancerType==null? null : cancerType.getValueCoded();
    	//find cancer stage
    	Concept cancerStageConcept = Context.getConceptService().getConcept(CANCER_STAGE);
    	Obs cancerStage = findLatest(Context.getObsService().getObservationsByPersonAndConcept(pat, cancerStageConcept));
    	Concept stage = cancerStage==null? null : cancerStage.getValueCoded();
        	
    	//find follow-up years guidelines
    	List<LafGuideline> guidelines = guidelineDao.getLafGuideline(type, stage);
    	
    	return guidelines;
    }

	/**
     * Find flagged reminders for a given patient for Calendar display
     * (flagged as Completed, Snoozed, Scheduled, Not Performed, or Skipped)
     * 
     * Calendar display logic: 
     * 
     * find reminders by guideline -> find and add reminders by providers -> sort reminders by date -> 
     * find and mark alerted reminders -> find and mark scheduled/snoozed reminders -> 
     * find and mark not performed reminders -> find, match and mark completed reminders (overriding) ->
     * mark skipped reminders (remaining)  
     * 
     * @param pat
     */
    private List<LafReminder>  findReminders(Patient pat) {    	
    	//find all reminders recommended by guideline and patient's providers
    	List<LafReminder> reminders = findAllReminders(pat);
    	
    	if(reminders==null || reminders.isEmpty()) {
    		return null;
    	}
    	        
    	//find completed reminders
    	List<LafReminder> remindersCompleted = getRemindersCompleted(pat);
    	
        //***********************************************
        //mark each reminder as either completed, missed, scheduled, snoozed, near-future or far-future
        //based on follow-up care received, today's date, schedule response, and snooze response.
        //***********************************************

    	//find alerted reminders
    	Date today = new Date();
    	List<LafReminder> remindersAlerted = getReminders(pat, today);
        
        //mark alerted reminders
        if(remindersAlerted != null) {
	        for(int ii = 0; ii< reminders.size(); ii++) { 
	            LafReminder reminder = reminders.get(ii);
	            for(LafReminder alert : remindersAlerted) {
	            	if(reminder.getFollowProcedure().equals(alert.getFollowProcedure()) && reminder.getTargetDate().equals(alert.getTargetDate())) {
	         		   reminder.setFlag(LafReminder.FLAG_ALERTED);            	
	            	}
	            }
	        }
        }
        
        //mark snoozed or scheduled reminders
        for(int ii = 0; ii< reminders.size(); ii++) { 
            LafReminder reminder = reminders.get(ii);
            Date scheduleDate = this.findSnoozeOrScheduleDate(pat, reminder.getFollowProcedure(), reminder.getTargetDate(), "scheduleDate");
            Date snoozeDate = this.findSnoozeOrScheduleDate(pat, reminder.getFollowProcedure(), reminder.getTargetDate(), "snoozeDate");
            if(scheduleDate!=null) {
            	reminder.setFlag(LafReminder.FLAG_SCHEDULED);
     		    reminder.setResponseDate(scheduleDate);
            } else if(snoozeDate!=null) {
            	reminder.setFlag(LafReminder.FLAG_SNOOZED);            	            	
     		    reminder.setResponseDate(snoozeDate);
            }
        } 
        
        //mark Not-Performed reminders
        for(int ii = 0; ii< reminders.size(); ii++) { 
            LafReminder reminder = reminders.get(ii);
            String yesOrNo = this.findAttribute(pat, reminder.getFollowProcedure(), reminder.getTargetDate(), "notPerformed");
            if("Yes".equalsIgnoreCase(yesOrNo)) {
            	reminder.setFlag(LafReminder.FLAG_NOT_PERFORMED_YES);
            } else if("No".equalsIgnoreCase(yesOrNo)) {
            	//reminder.setFlag(LafReminder.FLAG_NOT_PERFORMED_NO);            	            	
             }
        }        

        //mark completed reminders (will take prevalence over other marks)
        if(remindersCompleted != null) {
        	//loop through all reminders and find if that reminder is completed or not
	        for(int ii = 0; ii< reminders.size(); ii++) { 
	            LafReminder reminder = reminders.get(ii);
	            LafReminder nextReminder = null;
	            LafReminder previousReminder = null;
	            
	            //find next reminder of the same type
	            for(int jj=ii+1; jj<reminders.size(); jj++) {  
	            	nextReminder = reminders.get(jj);
	            	if(nextReminder.getFollowProcedure().equals(reminder.getFollowProcedure())) {
	            		break;           		
	            	}
	            }
	            
	            //find previous reminder of the same type
	            for(int jj=ii-1; jj>=0; jj--) {  
	            	previousReminder = reminders.get(jj);
	            	if(previousReminder.getFollowProcedure().equals(reminder.getFollowProcedure())) {
	            		break;           		
	            	}
	            }            
	            
	            //find if this reminder can be marked as completed or not
	            for(LafReminder reminderCompl : remindersCompleted) {
	            	if(reminderCompl.getFollowProcedure().equals(reminder.getFollowProcedure())) {
		               if(reminderCompl.getCompleteDate().equals(reminder.getTargetDate())) {
		            	   reminder.setFlag(LafReminder.FLAG_COMPLETED);
		            	   reminder.setResponseDate(reminderCompl.getCompleteDate());
		            	   break;
		               } else if(previousReminder==null && reminderCompl.getCompleteDate().before(reminder.getTargetDate())) {
	            		   reminder.setFlag(LafReminder.FLAG_COMPLETED);
	            		   reminder.setResponseDate(reminderCompl.getCompleteDate());
	            		   break;
	            	   } else if(nextReminder==null && reminderCompl.getCompleteDate().after(reminder.getTargetDate())) {
	            		   reminder.setFlag(LafReminder.FLAG_COMPLETED);
	               		   reminder.setResponseDate(reminderCompl.getCompleteDate());
	               		   break;
	            	   } else if(previousReminder!=null && reminderCompl.getCompleteDate().before(reminder.getTargetDate()) && reminderCompl.getCompleteDate().after(findMidDate(previousReminder.getTargetDate(), reminder.getTargetDate()))) {
	            		   reminder.setFlag(LafReminder.FLAG_COMPLETED);
	               		   reminder.setResponseDate(reminderCompl.getCompleteDate());
	               		   break;
	            	   } else if(nextReminder!=null && reminderCompl.getCompleteDate().after(reminder.getTargetDate())&& reminderCompl.getCompleteDate().before(findMidDate(reminder.getTargetDate(), nextReminder.getTargetDate()))) {
	            		   reminder.setFlag(LafReminder.FLAG_COMPLETED);
	               		   reminder.setResponseDate(reminderCompl.getCompleteDate());
	               		   break;
	            	   }            		
	            	}
	            }
	        }
        }
        
        //mark skipped reminders
        for(int ii = 0; ii< reminders.size(); ii++) { 
            LafReminder reminder = reminders.get(ii);
            if(reminder.getTargetDate().before(today) && reminder.getFlag() == null) {
            	reminder.setFlag(LafReminder.FLAG_SKIPPED);
            }
        }        
        
    	return reminders;
     }

    /**
     * 
     * Find all reminders including those by guideline and those by patient doctors
     * 
     * find reminders by guideline -> find and add reminders by providers -> sort reminders by date 
     * 
     * @param pat given patient
     * @return all reminders sorted by target date
     */
    private List<LafReminder>  findAllReminders(Patient pat) {
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
    	if(surgDate == null) {
    		log.warn("No surgery is found for this patient: " + pat);
    		return null;
    	}
    	
    	//find rediation type
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
    	
    	//add follow-up care recommended by patient's personal provider
       	List<LafReminder> remindersByProvider = getRemindersByProvider(pat);
       	if(remindersByProvider!=null && !remindersByProvider.isEmpty()) {
       		reminders.addAll(remindersByProvider);
       	}
    	
    	//sort guidelines by target date
        Collections.sort(reminders, LafReminder.getDateComparator());    	        
    	return reminders;
     }
    
    
	/**
     * Find target dates based solely on follow-up care frequency rule
     * 
     * @param surgeryDate
     * @param followYears
     * @return
     */
    private Date[] findTargetDates(Date surgDate, Concept radiationType, String followYears) {
    	String[] split1  = followYears.split(":");
    	String[] split2 = split1[0].split(",");
     	
    	if(surgDate == null) {
    		log.debug("No reminder will be generated because no surgery is found for this patient.");
    		return null;
    	}
    	
     	if(split1.length>=2 && "NO RADIATION".equals(split1[1])) {
    		if(radiationType != null) {
    			return null;
    		}
    	}
    	    	 
     	Date startDate = surgDate;
    	Date[] targetDates = new Date[split2.length];
    	for(int ii=0; ii<split2.length; ii++) {
    		targetDates[ii] = findDate(startDate, split2[ii]);    	    
    	}
	    // TODO Auto-generated method stub
	    return targetDates;
    }
    
	/**
     * Auto generated method comment
     * 
     * @param reminders
     * @param followProcedure
     * @return
     */
    private Date findNextTargetDate(List<LafReminder> reminders, Concept followProcedure) {
     	
     	Date today = new Date();
    	    	 
    	Date nextTargetDate = null;
    	LafReminder refReminder1 = null;
    	LafReminder refReminder2 = null;
    	for(LafReminder reminder : reminders) {
    		if(!followProcedure.equals(reminder.getFollowProcedure())) {
    			continue;
    		}    		
    		
    		refReminder2 = reminder;
    		
    		if(refReminder1 != null) {
	    		if(today.before(refReminder2.getTargetDate())) {
	    			Date refDate12 = findMidDate(refReminder1.getTargetDate(), refReminder2.getTargetDate());
	    			
	    			if(today.before(refDate12) && !LafReminder.FLAG_COMPLETED.equals(refReminder1.getFlag())) {
	    				//nextTargetDate = findDate(startDate, split2[ii]);
	    				nextTargetDate = refReminder1.getTargetDate();
	    			} else if (!LafReminder.FLAG_COMPLETED.equals(refReminder2.getFlag())){
	    				nextTargetDate = refReminder2.getTargetDate();;
	    			}
	    			
	    			break;
	    		}
    		}
    		
    		refReminder1 = refReminder2;    		
    	}
	    // TODO Auto-generated method stub
        return nextTargetDate;
    }
    
    
	/**
     * Auto generated method comment
     * 
     * @param refDate1
     * @param refDate2
     * @return
     */
    private Date findMidDate(Date refDate1, Date refDate2) {
	    // TODO Auto-generated method stub
    	long diffDays = (refDate2.getTime() - refDate1.getTime())/(1000*60*60*24); 
    		
    	Calendar cal = Calendar.getInstance();    	
    	cal.setTime(refDate1);
    	cal.add(Calendar.DATE, (int) diffDays/2);
    	
	    return cal.getTime();
    }


	/**
     * Auto generated method comment
     
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
