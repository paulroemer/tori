package com.liferay.portal.patch;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portal.model.ModelListener;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.service.persistence.MBThreadPersistenceImpl;
import com.liferay.portlet.messageboards.util.MBUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.liferay.portal.kernel.workflow.WorkflowConstants.STATUS_APPROVED;

/**
 * Created by wolfgang on 21/07/16.
 * Only purpose of this class: set initial status of newly created forum messages to 0 (approved) to make them visible
 * after page reload
 */
public class MBThreadPersistenceImplPatch extends MBThreadPersistenceImpl {
	public MBThreadPersistenceImplPatch() {
		List l = new ArrayList(Arrays.asList(listeners));
		l.add(new MBThreadPersistenceImplPatch.MyModelListener());
		listeners = new ModelListener[listeners.length + 1];
		listeners = (ModelListener<MBThread>[]) l.toArray(listeners);
	}

	private class MyModelListener extends BaseModelListener<MBThread> {

		@Override
		public void onBeforeCreate(MBThread model) throws ModelListenerException {
			model.setStatus(STATUS_APPROVED);
		}

		@Override
		public void onAfterCreate(MBThread model) throws ModelListenerException {
			super.onAfterCreate(model);
		}
	}
}
