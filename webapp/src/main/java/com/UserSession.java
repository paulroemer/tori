package com;

import javax.persistence.EntityManager;

public class UserSession extends com.liferay.portal.dao.orm.jpa.SessionImpl {

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
