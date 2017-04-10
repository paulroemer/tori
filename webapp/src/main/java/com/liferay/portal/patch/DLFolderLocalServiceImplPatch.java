package com.liferay.portal.patch;

import com.liferay.portal.service.PersistedModelLocalServiceRegistry;
import com.liferay.portlet.documentlibrary.service.impl.DLFolderLocalServiceImpl;
import com.liferay.portlet.messageboards.service.impl.MBThreadLocalServiceImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by vaadin on 21/07/16.
 */
public class DLFolderLocalServiceImplPatch extends DLFolderLocalServiceImpl implements InitializingBean {
	@Override
	public void afterPropertiesSet() {
		dlFolderLocalService = this;

		super.afterPropertiesSet();
	}

	public void setPersistedModelLocalServiceRegistry(PersistedModelLocalServiceRegistry persistedModelLocalServiceRegistry) {
		this.persistedModelLocalServiceRegistry = persistedModelLocalServiceRegistry;
	}

	public PersistedModelLocalServiceRegistry getPersistedModelLocalServiceRegistry() {
		return persistedModelLocalServiceRegistry;
	}
}
