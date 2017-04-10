package com.liferay.portal.patch;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.service.PersistedModelLocalServiceRegistry;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.impl.MBThreadLocalServiceImpl;
import com.liferay.portlet.messageboards.service.persistence.MBMessagePersistenceImpl;
import com.liferay.portlet.messageboards.util.MBUtil;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.liferay.portal.kernel.workflow.WorkflowConstants.STATUS_APPROVED;

/**
 * Created by vaadin on 21/07/16.
 */
public class MBThreadLocalServiceImplPatch extends MBThreadLocalServiceImpl implements InitializingBean {
	@Override
	public void afterPropertiesSet() {
		mbThreadLocalService = this;

		super.afterPropertiesSet();
	}

	public void setPersistedModelLocalServiceRegistry(PersistedModelLocalServiceRegistry persistedModelLocalServiceRegistry) {
		this.persistedModelLocalServiceRegistry = persistedModelLocalServiceRegistry;
	}

	public PersistedModelLocalServiceRegistry getPersistedModelLocalServiceRegistry() {
		return persistedModelLocalServiceRegistry;
	}
}
