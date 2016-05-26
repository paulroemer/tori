package com.liferay.portlet.expando.service.impl;

import com.liferay.portal.service.PersistedModelLocalServiceRegistry;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wolfgang on 26/05/16.
 */
public class ExpandoValueLocalServiceImplPatch extends ExpandoValueLocalServiceImpl implements InitializingBean {
	public void setPersistedModelLocalServiceRegistry(PersistedModelLocalServiceRegistry persistedModelLocalServiceRegistry) {
		this.persistedModelLocalServiceRegistry = persistedModelLocalServiceRegistry;
	}

	@Override
	public void afterPropertiesSet() {
		expandoValueLocalService = this;
		super.afterPropertiesSet();
	}
}
