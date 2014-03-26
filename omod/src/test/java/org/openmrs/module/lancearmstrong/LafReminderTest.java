package org.openmrs.module.lancearmstrong;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.module.lancearmstrong.db.*;

public class LafReminderTest extends BaseModuleContextSensitiveTest {
	
	private LafReminderDAO dao = null;
	private PatientDAO patientDao;
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {		
	    //org.hibernate.impl.SessionFactoryImpl sessionFactory = (org.hibernate.impl.SessionFactoryImpl) applicationContext.getBean("sessionFactory");
	    //String[] mappingRes = new String[1];
	    //mappingRes[0]="lancearmstrong.hbm.xml";
	    //sessionFactory.setMappingResources(mappingRes);
	    
		if (dao == null)
			// fetch the dao from PhrSeucrity service, rather than from the spring application context
			// this bean name matches the name in /metadata/spring/applicationContext-service.xml
			dao = (LafReminderDAO) LafUtil.getService().getReminderDao();
		if(patientDao == null) {
		    patientDao = (PatientDAO) applicationContext.getBean("patientDAO");		    
		}
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_ENCOUNTERS);
	}
	
	@Test
	public void testGetRemindersFromDAO() throws Exception {
	    List<LafReminder> reminders = dao.getLafReminders(patientDao.getPatient(4));//larmstrong2-4, msmith3-5, hxiao4-7
	    Assert.assertNotNull(reminders);
	    Assert.assertEquals(2, reminders.size());
	}
	
	@Test
	public void testGetRemindersFromService() throws Exception {
		Patient pat = patientDao.getPatient(4); //larmstrong2-4, msmith3-5, hxiao4-7
	    List<LafReminder> reminders = LafUtil.getService().getReminders(pat);
	    
	    Assert.assertNotNull(reminders);
	    Assert.assertEquals(10, reminders.size());
	}	
	
	@Override
    public Boolean useInMemoryDatabase() {
        return false;
    }	
}
