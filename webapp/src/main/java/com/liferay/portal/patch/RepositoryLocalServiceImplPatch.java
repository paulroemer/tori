package com.liferay.portal.patch;

import com.liferay.portal.service.PersistedModelLocalServiceRegistry;
import com.liferay.portal.service.impl.RepositoryLocalServiceImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wolfgang on 12/07/16.
 */
public class RepositoryLocalServiceImplPatch extends RepositoryLocalServiceImpl implements InitializingBean {
	@Override
	public void afterPropertiesSet() {
		setRepositoryLocalService(this);
		super.afterPropertiesSet();
	}

	public void setPersistedModelLocalServiceRegistry(PersistedModelLocalServiceRegistry persistedModelLocalServiceRegistry) {
		this.persistedModelLocalServiceRegistry = persistedModelLocalServiceRegistry;
	}
}
