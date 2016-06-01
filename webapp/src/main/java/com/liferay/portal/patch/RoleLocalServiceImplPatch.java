package com.liferay.portal.patch;

import com.liferay.portal.service.PersistedModelLocalServiceRegistry;
import com.liferay.portal.service.impl.RoleLocalServiceImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wolfgang on 31/05/16.
 */
public class RoleLocalServiceImplPatch extends RoleLocalServiceImpl implements InitializingBean {
	public RoleLocalServiceImplPatch(PersistedModelLocalServiceRegistry persistedModelLocalServiceRegistry) {
		this.persistedModelLocalServiceRegistry = persistedModelLocalServiceRegistry;
	}
	@Override
	public void afterPropertiesSet() {
		roleLocalService = this;
		super.afterPropertiesSet();
	}
}
