package com.liferay.portal.patch;

import ch.qos.logback.core.db.dialect.DBUtil;
import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.persistence.MBMessagePersistenceImpl;
import com.liferay.portlet.messageboards.util.MBUtil;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.liferay.portal.kernel.workflow.WorkflowConstants.STATUS_APPROVED;

/**
 * Created by wolfgang on 21/07/16.
 * Only purpose of this class: set initial status of newly created forum messages to 0 (approved) to make them visible
 * after page reload
 */
public class MBMessagePersistenceImplPatch extends MBMessagePersistenceImpl implements InitializingBean {
	private class MyModelListener extends BaseModelListener<MBMessage> {

		@Override
		public void onBeforeCreate(MBMessage model) throws ModelListenerException {
			model.setStatus(STATUS_APPROVED);
		}

		@Override
		public void onAfterCreate(MBMessage model) throws ModelListenerException {
			MBUtil.updateThreadMessageCount(model.getCompanyId(), model.getThreadId());
		}
	}

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();

		// add one element to raw array
		List l = new ArrayList(Arrays.asList(listeners));
		l.add(new MyModelListener());
		listeners = new ModelListener[listeners.length + 1];
		listeners = (ModelListener<MBMessage>[]) l.toArray(listeners);
	}
}
