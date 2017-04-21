package com;

import com.liferay.portal.kernel.dao.orm.ORMException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.io.Serializable;

public class UserSession extends com.liferay.portal.dao.orm.jpa.SessionImpl {

	private EntityManagerFactory emf;
	private EntityManagerHolder emh;
	private SessionFactory hibernateSessionFactory;

	public void setEntityManagerHolder(EntityManagerHolder emh) {
		this.emh = emh;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
		this.emh = new EntityManagerHolder(emf.createEntityManager());
	}

	public void setHibernateSessionFactory(SessionFactory hibernateSessionFactory) {
		this.hibernateSessionFactory = hibernateSessionFactory;
	}

	public SessionFactory getHibernateSessionFactory() {
		return hibernateSessionFactory;
	}

	@Override
	public Object getWrappedSession() throws ORMException {
		if (session == null) {
			session = hibernateSessionFactory.openSession();
		}
		return session;
	}

	private Session session = null;

	@Override
	public Object merge(Object object) throws ORMException {
		Object result = null;
		TransactionSynchronizationManager.bindResource(emf, emh);
		try {
			EntityManager localManager = emh.getEntityManager();
			EntityTransaction tx = localManager.getTransaction();
			tx.begin();
			result = super.merge(object);
			localManager.flush();
			tx.commit();
		} finally {
			TransactionSynchronizationManager.unbindResource(emf);
		}
		return result;
	}

	@Override
	public void delete(Object object) throws ORMException {
		TransactionSynchronizationManager.bindResource(emf, emh);
		try {
			EntityManager localManager = emh.getEntityManager();
			EntityTransaction tx = localManager.getTransaction();
			tx.begin();
			super.delete(super.merge(object));
			localManager.flush();
			tx.commit();
		} finally {
			TransactionSynchronizationManager.unbindResource(emf);
		}
	}

	@Override
	public void saveOrUpdate(Object object) throws ORMException {
		TransactionSynchronizationManager.bindResource(emf, emh);
		try {
			EntityManager localManager = emh.getEntityManager();
			EntityTransaction tx = localManager.getTransaction();
			tx.begin();
			super.saveOrUpdate(object);
			localManager.flush();
			tx.commit();
		} finally {
			TransactionSynchronizationManager.unbindResource(emf);
		}
	}

	@Override
	public Serializable save(Object object) throws ORMException {
		Serializable result = null;
		TransactionSynchronizationManager.bindResource(emf, emh);
		try {
			EntityManager localManager = emh.getEntityManager();
			EntityTransaction tx = localManager.getTransaction();
			tx.begin();
			result = super.save(object);
			localManager.flush();
			tx.commit();
		} finally {
			TransactionSynchronizationManager.unbindResource(emf);
		}
		return result;
	}

	@Override
	public void flush() throws ORMException {
		// do nothing, flush is called in save/saveOrUpdate;
	}

}
