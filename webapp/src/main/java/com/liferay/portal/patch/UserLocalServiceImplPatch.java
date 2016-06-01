package com.liferay.portal.patch;

import com.liferay.portal.service.PersistedModelLocalServiceRegistry;
import com.liferay.portal.service.impl.UserLocalServiceImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wolfgang on 31/05/16.
 */
public class UserLocalServiceImplPatch extends UserLocalServiceImpl implements InitializingBean {
	public UserLocalServiceImplPatch(PersistedModelLocalServiceRegistry persistedModelLocalServiceRegistry) {
		this.persistedModelLocalServiceRegistry = persistedModelLocalServiceRegistry;
	}

	@Override
	public void afterPropertiesSet() {
		userLocalService = this;
		super.afterPropertiesSet();
	}
}
