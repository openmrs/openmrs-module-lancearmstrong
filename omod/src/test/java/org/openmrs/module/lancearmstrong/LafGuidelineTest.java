package org.openmrs.module.lancearmstrong;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.api.context.Context;
import org.openmrs.module.lancearmstrong.db.*;

public class LafGuidelineTest extends BaseModuleContextSensitiveTest {
	
	private LafGuidelineDAO dao = null;
	
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
			dao = (LafGuidelineDAO) LafUtil.getService().getGuidelineDao();
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
	}
	
	@Test
	public void testGetByCancerTypeAndStage() throws Exception {
		Concept cancerType = Context.getConceptService().getConcept(6110);
		Concept cancerStage = Context.getConceptService().getConcept(6148);
	    List<LafGuideline> guidelines = dao.getLafGuideline(cancerType, cancerStage);
	    Assert.assertNotNull(guidelines);
	    Assert.assertEquals(2, guidelines.size());
	}
	
	@Override
    public Boolean useInMemoryDatabase() {
        return false;
    }	
}
