package org.openmrs.module.lancearmstrong.web.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.lancearmstrong.LafPatient;
import org.openmrs.module.lancearmstrong.LafReminder;
import org.openmrs.module.lancearmstrong.LafUtil;
import org.openmrs.module.personalhr.PersonalhrUtil;
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
public class FollowupCareFormController extends SimpleFormController {
    
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
        log.debug("Entering FollowupCareFormController:formBackingObject");
        Integer patientId = null;
        Patient pat = null;
        if (request.getParameter("patientId") != null && !"".equals(request.getParameter("patientId"))) {
            patientId = PersonalhrUtil.getParamAsInteger(request.getParameter("patientId"));
            if(patientId==null) {
            	patientId = (Integer) request.getAttribute("patientId");
            }
            log.debug("patientId=" + patientId);
            
            pat = Context.getPatientService().getPatient(patientId);
        }
        
        return new LafPatient(pat, LafUtil.getService().getReminders(pat));         
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request,
            Object commandObject, BindException errors) throws Exception {
        String command = request.getParameter("command");
        log.debug("Entering FollowupCareFormController:onBindAndValidate, command=" + command);
        List<LafReminder> reminders = ((LafPatient) commandObject).getReminders();
        
        log.debug("reminders.size="+reminders.size() );
        try {
            if(command!=null && command.startsWith("Save")) {
                for(LafReminder reminder : reminders) {
                  //validate complete date
                  if(reminder.RESPONSE_COMPLETED.equals(reminder.getResponseType()) && reminder.getCompleteDate()==null) {
                    log.debug("Complete date must be entered!");  
                    errors.reject("Complete date must be entered!");  
                  } else if(reminder.RESPONSE_COMPLETED.equals(reminder.getResponseType()) && reminder.getCompleteDate().before(getEarliestCompleteDate(reminder))) {
                    log.debug("Complete date is too early!");  
                    errors.reject("Complete date is too early!");  
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
        log.debug("Entering FollowupCareFormController:onSubmit, command=" + command+", id=" + request.getParameter("reminderIdField"));
        
        List<LafReminder> reminders = ((LafPatient) commandObject).getReminders();
         
        log.debug("onSubmit: reminders.size="+reminders.size());
        try {
            if(command != null && command.startsWith("Save")) {
                Integer id = PersonalhrUtil.getParamAsInteger(request.getParameter("reminderIdField"));
                LafUtil.getService().getReminderDao().saveLafReminder(reminders.get(id));                
                log.debug("Reminder updated: " + reminders.get(id).getFollowProcedure().getName()+"/"+reminders.get(id).getTargetDate() + "/" + reminders.get(id).getResponseType() + "/" + reminders.get(id).getResponseComments());
                request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, reminders.get(id).getFollowProcedure().getName()+"/"+reminders.get(id).getTargetDate() + " " + reminders.get(id).getResponseType());
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
