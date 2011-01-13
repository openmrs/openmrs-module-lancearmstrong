package org.openmrs.module.lancearmstrong.db.hibernate;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.module.lancearmstrong.LafReminder;
import org.openmrs.module.lancearmstrong.db.LafReminderDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * Hibernate implementation of the Data Access Object
 */
public class HibernateLafReminderDAO implements LafReminderDAO {
    protected final Log log = LogFactory.getLog(getClass());

    private SessionFactory sessionFactory;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public LafReminder getLafReminder(Integer id) {
        return (LafReminder) sessionFactory.getCurrentSession().get(LafReminder.class, id);
    }
    
    @Override
   public LafReminder saveLafReminder(LafReminder reminder) {
        sessionFactory.getCurrentSession().saveOrUpdate(reminder);
        return reminder;
    }
    
    @Override
    public void deleteLafReminder(LafReminder reminder) {
        sessionFactory.getCurrentSession().delete(reminder);
    }

    @Override
    public List<LafReminder> getAllLafReminders() {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(LafReminder.class);
        //crit.addOrder(Order.asc("privilege"));
        return (List<LafReminder>) crit.list();
    }

    @Override
    public List<LafReminder> getLafReminders(Patient pat) {       
        //Query query = sessionFactory.getCurrentSession().createQuery("from LafReminder where allowedUrl = :url ");
        //query.setParameter("url", url);
        //List list0 = query.list();        
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(LafReminder.class);
        crit.add(Restrictions.eq("patient", pat));
        List<LafReminder> list = (List<LafReminder>) crit.list();
        if (list.size() >= 1)
            return list;
        else
            return null;
    }

}
