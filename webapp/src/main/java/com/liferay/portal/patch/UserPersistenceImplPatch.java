package com.liferay.portal.patch;

import com.liferay.portal.service.persistence.GroupPersistence;
import com.liferay.portal.service.persistence.RolePersistence;
import com.liferay.portal.service.persistence.UserPersistenceImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wolfgang on 23/05/16.
 */
public class UserPersistenceImplPatch extends UserPersistenceImpl implements InitializingBean {
	public void setGroupPersistence(GroupPersistence groupPersistence) {
		this.groupPersistence = groupPersistence;
	}
	public void setRolePersistence(RolePersistence rolePersistence) {
		this.rolePersistence = rolePersistence;
	}

}
