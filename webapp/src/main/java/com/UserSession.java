package com;

import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.patch.SessionFactoryImplPatch;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;

public class UserSession extends com.liferay.portal.dao.orm.jpa.SessionImpl {


    private SessionFactory hibernateSessionFactory;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setHibernateSessionFactory(SessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    public SessionFactory getHibernateSessionFactory() {
        return hibernateSessionFactory;
    }

    @Override
    public Object getWrappedSession() throws ORMException {
        return hibernateSessionFactory.openSession();
    }
}
