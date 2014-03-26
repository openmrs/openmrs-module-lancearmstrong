package org.openmrs.module.lancearmstrong.db.hibernate;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.lancearmstrong.LafGuideline;
import org.openmrs.module.lancearmstrong.db.LafGuidelineDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * Hibernate implementation of the Data Access Object
 */
public class HibernateLafGuidelineDAO implements LafGuidelineDAO {
    protected final Log log = LogFactory.getLog(getClass());

    private SessionFactory sessionFactory;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public LafGuideline getLafGuideline(Integer id) {
        return (LafGuideline) sessionFactory.getCurrentSession().get(LafGuideline.class, id);
    }
    
    @Override
   public LafGuideline saveLafGuideline(LafGuideline guideline) {
        sessionFactory.getCurrentSession().saveOrUpdate(guideline);
        return guideline;
    }
    
    @Override
    public void deleteLafGuideline(LafGuideline guideline) {
        sessionFactory.getCurrentSession().delete(guideline);
    }

    @Override
   @SuppressWarnings("unchecked")
    public List<LafGuideline> getAllLafGuidelines() {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(LafGuideline.class);
        //crit.addOrder(Order.asc("privilege"));
        return (List<LafGuideline>) crit.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LafGuideline> getLafGuideline(Concept cancerType, Concept cancerStage) {       
        //Query query = sessionFactory.getCurrentSession().createQuery("from LafGuideline").list();
        //query.setParameter("url", url);
        //List list0 = query.list();        
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(LafGuideline.class);
        crit.add(Restrictions.eq("cancerType", cancerType));
        crit.add(Restrictions.eq("cancerStage", cancerStage));
        List<LafGuideline> list = (List<LafGuideline>) crit.list();
        if (list.size() >= 1)
            return list;
        else
            return null;
    }

}
