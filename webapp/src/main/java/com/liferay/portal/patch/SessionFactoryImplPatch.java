package com.liferay.portal.patch;

import com.liferay.portal.dao.db.DBFactoryImpl;
import com.liferay.portal.dao.orm.jpa.SessionFactoryImpl;
import com.liferay.portal.kernel.dao.orm.Dialect;

/**
 * Created by wolfgang on 17/05/16.
 */
public class SessionFactoryImplPatch extends SessionFactoryImpl {
	private Dialect dialect;

	// dialect cannot be injected on original implementation because setter is ambiguous
	public SessionFactoryImplPatch(Dialect dialect) {
		this.dialect = dialect;
	}

	@Override
	public Dialect getDialect() {
		return dialect;
	}
}
