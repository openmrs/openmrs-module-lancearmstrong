package org.openmrs.module.lancearmstrong.db.hibernate;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.lancearmstrong.LafReminder;
import org.openmrs.module.lancearmstrong.db.LafReminderDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
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
    	log.debug("Save reminder - reminder.getFollowProcedure()=" + reminder.getFollowProcedure() + 
    		 ", reminder.getFollowProcedureName() = " + reminder.getFollowProcedureName());
    	
    	if(reminder.getFollowProcedureName() != null) {
    		reminder.setFollowProcedure(Context.getConceptService().getConceptByName(reminder.getFollowProcedureName()));    		
        	log.debug("New reminder.getFollowProcedure()=" + reminder.getFollowProcedure()); 
    	}
    	
        Session sess = sessionFactory.openSession();
        Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.saveOrUpdate(reminder);
        tx.commit();
        //sess.flush();
        sess.close();
        //sessionFactory.getCurrentSession().saveOrUpdate(token);
        return reminder;
        
    }
    
    @Override
    public void deleteLafReminder(LafReminder reminder) {
        //sessionFactory.getCurrentSession().delete(token);
        //sessionFactory.getCurrentSession().close();
        Session sess = sessionFactory.openSession();
        Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.delete(reminder);
        tx.commit();
        sess.close();
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
        crit.add(Restrictions.isNull("completeDate"));
        crit.add(Restrictions.isNotNull("targetDate"));
        crit.addOrder(Order.asc("targetDate"));
        List<LafReminder> list = (List<LafReminder>) crit.list();
        if (list.size() >= 1)
            return list;
        else
            return null;
    }
    @Override
    public List<LafReminder> getLafRemindersCompleted(Patient pat) {       
        //Query query = sessionFactory.getCurrentSession().createQuery("from LafReminder where allowedUrl = :url ");
        //query.setParameter("url", url);
        //List list0 = query.list();        
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(LafReminder.class);
        crit.add(Restrictions.eq("patient", pat));
        crit.add(Restrictions.isNotNull("completeDate"));
        crit.add(Restrictions.isNull("targetDate"));
        crit.addOrder(Order.asc("completeDate"));
        List<LafReminder> list = (List<LafReminder>) crit.list();
        if (list.size() >= 1)
            return list;
        else
            return null;
    }

}
