package com.liferay.portal.patch;

import com.liferay.portal.service.PersistedModelLocalServiceRegistry;
import com.liferay.portal.service.impl.PortletLocalServiceImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wolfgang on 27/05/16.
 */
public class PortletLocalServiceImplPatch extends PortletLocalServiceImpl implements InitializingBean {
	@Override
	public void afterPropertiesSet() {
		portletLocalService = this;
		super.afterPropertiesSet();
	}

	public void setPersistedModelLocalServiceRegistry(PersistedModelLocalServiceRegistry persistedModelLocalServiceRegistry) {
		this.persistedModelLocalServiceRegistry = persistedModelLocalServiceRegistry;
	}
}
