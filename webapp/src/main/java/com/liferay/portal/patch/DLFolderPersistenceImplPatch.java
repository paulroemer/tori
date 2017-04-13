package com.liferay.portal.patch;

import com.liferay.portlet.documentlibrary.service.persistence.DLFileEntryPersistence;
import com.liferay.portlet.documentlibrary.service.persistence.DLFileEntryTypePersistence;
import com.liferay.portlet.documentlibrary.service.persistence.DLFolderPersistenceImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by vaadin on 21/07/16.
 */
public class DLFolderPersistenceImplPatch extends DLFolderPersistenceImpl implements InitializingBean {
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
	}

	public DLFileEntryTypePersistence getDLFileEntryTypePersistence() {
		return dlFileEntryTypePersistence;
	}

	public void setDLFileEntryTypePersistence(DLFileEntryTypePersistence dlFileEntryTypePersistence) {
		this.dlFileEntryTypePersistence = dlFileEntryTypePersistence;
	}
}
