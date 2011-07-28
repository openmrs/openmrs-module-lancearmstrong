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

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;


/**
 *
 */
public class LafUtil {
	/** Logger for this class and subclasses */
	private final static Log log = LogFactory.getLog(LafUtil.class);
	
		
    public static LafService getService() {
        return Context.getService(LafService.class);
    }

    public static void main(final String[] args) {
    }
    
    public static Date clearDate(Date dateTime) {
    	if(dateTime == null) {
    		return null;
    	}
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dateWithoutTime = cal.getTime();
        
        return dateWithoutTime;
    }
}
