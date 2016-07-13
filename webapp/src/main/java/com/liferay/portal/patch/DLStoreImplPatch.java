package com.liferay.portal.patch;

import com.liferay.portlet.documentlibrary.store.DLStoreImpl;
import com.liferay.portlet.documentlibrary.store.Store;

/**
 * Created by wolfgang on 13/07/16.
 */
public class DLStoreImplPatch extends DLStoreImpl {
	public void setStore(Store store) {
		this.store = store;
	}
}
