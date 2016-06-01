package com.liferay.portal.patch;

import com.liferay.portal.service.PersistedModelLocalServiceRegistry;
import com.liferay.portlet.ratings.service.impl.RatingsStatsLocalServiceImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wolfgang on 31/05/16.
 */
public class RatingsStatsLocalServiceImplPatch extends RatingsStatsLocalServiceImpl implements InitializingBean {
	@Override
	public void afterPropertiesSet() {
		ratingsStatsLocalService = this;
		super.afterPropertiesSet();
	}

	public void setPersistedModelLocalServiceRegistry(PersistedModelLocalServiceRegistry persistedModelLocalServiceRegistry) {
		this.persistedModelLocalServiceRegistry = persistedModelLocalServiceRegistry;
	}
}
