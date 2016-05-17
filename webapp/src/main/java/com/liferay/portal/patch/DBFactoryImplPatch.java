package com.liferay.portal.patch;

import com.liferay.portal.dao.db.DBFactoryImpl;
import com.liferay.portal.kernel.dao.db.DBFactoryUtil;

/**
 * Created by wolfgang on 17/05/16.
 */
public class DBFactoryImplPatch extends DBFactoryImpl {
	// dialect cannot be injected on original implementation because setter is ambiguous
	public DBFactoryImplPatch(Object dialect) {
		setDB(dialect);
	}
}
