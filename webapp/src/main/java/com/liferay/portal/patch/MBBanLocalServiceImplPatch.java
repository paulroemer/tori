package com.liferay.portal.patch;

import com.liferay.portal.service.PersistedModelLocalServiceRegistry;
import com.liferay.portlet.messageboards.service.impl.MBBanLocalServiceImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by vaadin on 21/07/16.
 */
public class MBBanLocalServiceImplPatch extends MBBanLocalServiceImpl implements InitializingBean {
	@Override
	public void afterPropertiesSet() {
		mbBanLocalService = this;

		super.afterPropertiesSet();
	}

	public void setPersistedModelLocalServiceRegistry(PersistedModelLocalServiceRegistry persistedModelLocalServiceRegistry) {
		this.persistedModelLocalServiceRegistry = persistedModelLocalServiceRegistry;
	}

	public PersistedModelLocalServiceRegistry getPersistedModelLocalServiceRegistry() {
		return persistedModelLocalServiceRegistry;
	}
}
