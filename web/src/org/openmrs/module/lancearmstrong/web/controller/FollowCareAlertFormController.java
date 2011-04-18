package org.openmrs.module.lancearmstrong.web.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.module.lancearmstrong.LafPatient;
import org.openmrs.module.lancearmstrong.LafReminder;
import org.openmrs.module.lancearmstrong.LafUtil;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.notification.Alert;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * The controller for entering/viewing a form. This should always be set to sessionForm=false.
 * <p/>
 * Handles {@code htmlFormEntry.form} requests. Renders view {@code htmlFormEntry.jsp}.
 * <p/>
 * TODO: This has a bit too much logic in the onSubmit method. Move that into the FormEntrySession.
 */
public class FollowCareAlertFormController extends SimpleFormController {
    
    protected final Log log = LogFactory.getLog(getClass());
      
    /**
     * Allows for Integers to be used as values in input tags. Normally, only strings and lists are
     * expected
     * 
     * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest,
     *      org.springframework.web.bind.ServletRequestDataBinder)
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
        binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, true));
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
    }
    
    @Override
    protected LafPatient formBackingObject(HttpServletRequest request) throws Exception {
        log.debug("Entering FollowCareAlertFormController:formBackingObject");
		
		User user = Context.getAuthenticatedUser();
		log.debug("Logged in as user: " + (user == null ? null : user.getUsername()));
		
        Integer patientId = null;
        Patient pat = null;
        if(user != null) {
	        if (request.getParameter("patientId") != null && !"".equals(request.getParameter("patientId"))) {
	            patientId = PersonalhrUtil.getParamAsInteger(request.getParameter("patientId"));
	            if(patientId==null) {
	            	patientId = (Integer) request.getAttribute("patientId");
	            }
	            log.debug("patientId=" + patientId);
	            
	            pat = Context.getPatientService().getPatient(patientId);
	        }
        }
        
        return new LafPatient(pat, getAlerts());         
    }

	private List<Alert> getAlerts() {				
		List<Alert> alertList = new ArrayList<Alert>();
		try {			
			User user = Context.getAuthenticatedUser();
			
			log.debug("Logged in as user: " + (user == null ? null : user.getUsername()));
			
			if(user!=null) {
    			Patient pat = Context.getPatientService().getPatient(user.getPerson().getId());
    			if(pat == null) {
    				log.debug("Current user is not a patient!");
    			}
    				
    			log.debug("Parsing logic rule...");
    			LogicService ls = Context.getLogicService();
    			Result result = ls.eval(pat, ls.parse("\"Follow-up Care Alert\""));
    			
    			if(result != null) {
        			ListIterator<Result> iter = result.listIterator();
        			int alertId = 0;
        			while(iter.hasNext()) {
        			    Result res = iter.next();
        			    Alert alert = new Alert();
        			    Datatype dataType = res.getDatatype();
        			    if(Datatype.CODED==dataType) {
        			    	alertId = res.toConcept().getId();
                            log.debug("Alert found (" + alertId + "): " + res.toConcept().getRetireReason());
                            alert.setAlertId(alertId);
                            alert.setDateToExpire(res.toConcept().getDateRetired());
        			        alert.setText(res.toConcept().getRetireReason());        			        
        			        alertList.add(alert);
        			    } else {
        			        log.debug("Non coded datatype: " + dataType);
        			    }
        			}
        			
    			}
			}
		}
		catch (Exception e) {
			log.error(e);
		}
		
		return alertList;
	}
    
    @Override
    protected void onBindAndValidate(HttpServletRequest request,
            Object commandObject, BindException errors) throws Exception {
        String command = request.getParameter("command");
        log.debug("Entering FollowCareAlertFormController:onBindAndValidate, command=" + command);
        List<LafReminder> reminders = ((LafPatient) commandObject).getRemindersCompleted();
        
        try {
            if(command!=null && command.startsWith("Save")) {
                for(LafReminder reminder : reminders) {
                  //validate complete date
                  if(reminder.getCompleteDate()==null || reminder.getCompleteDate().before(new Date(1900, 1, 1))) {
                    log.debug("Complete date cannot be empty or too early!");  
                    errors.reject("Complete date cannot be empty or too early!");  
                  }                  
                }
            }                  
        } catch (Exception ex) {
            log.error("Exception during form validation", ex);
            errors.reject("Exception during form validation, see log for more details: " + ex);
        }
    }

    /**
     * Auto generated method comment
     * 
     * @param reminders
     * @param reminder
     * @return
     */
    private Date getEarliestCompleteDate(LafReminder reminder) {
	    // TODO Auto-generated method stub
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(reminder.getTargetDate());
    	cal.add(Calendar.MONDAY, -3);
    	return cal.getTime();    	
    }

	@Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object commandObject, BindException errors)
            throws Exception {
        String command = request.getParameter("command");
        log.debug("Entering FollowCareAlertFormController:onSubmit, command=" + command+", id=" + request.getParameter("reminderIdField"));
        
        List<LafReminder> reminders = ((LafPatient) commandObject).getRemindersCompleted();
         
        log.debug("onSubmit: reminders.size="+(reminders==null? 0:reminders.size()));
        try {
            Integer id = PersonalhrUtil.getParamAsInteger(request.getParameter("reminderIdField"));
            if(command != null && command.startsWith("Save")) {
                 if(id >= 0) {
                	LafUtil.getService().getReminderDao().saveLafReminder(reminders.get(id));                
                	log.debug("Reminder updated: " + reminders.get(id).getFollowProcedure().getName()+"/"+reminders.get(id).getCompleteDate() + "/" + reminders.get(id).getResponseType() + "/" + reminders.get(id).getResponseComments());
                	request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, reminders.get(id).getFollowProcedure().getName()+"/"+reminders.get(id).getCompleteDate() + " " + reminders.get(id).getResponseType());
                } else {
                	log.debug("Nothing is updated. command=" + command);
                }
            } else if(command != null && command.startsWith("Delete")) {
                if(id >= 0) {
                	LafUtil.getService().getReminderDao().deleteLafReminder(reminders.get(id));                
                	log.debug("Reminder deleted: " + reminders.get(id).getFollowProcedure().getName()+"/"+reminders.get(id).getCompleteDate() + "/" + reminders.get(id).getResponseType() + "/" + reminders.get(id).getResponseComments());
                	request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, reminders.get(id).getFollowProcedure().getName()+"/"+reminders.get(id).getCompleteDate() + " " + reminders.get(id).getResponseType());
                } else {
                	log.debug("Nothing is deleted. command=" + command);
                }
            } 
            
            String successView = getSuccessView() + "?patientId=" + PersonalhrUtil.getParamAsInteger(request.getParameter("patientId"));
            return new ModelAndView(new RedirectView(successView));
            
        } catch (Exception ex) {
            log.error("Exception trying to submit form", ex);
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            errors.reject("Exception! " + ex.getMessage() + "<br/>" + sw.toString());
            return showForm(request, response, errors);
        }
    }
    
}
