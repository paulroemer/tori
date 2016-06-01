package com.liferay.portal.patch;

import com.liferay.portal.security.permission.ResourceActionsImpl;
import com.liferay.portal.service.PortletLocalService;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wolfgang on 31/05/16.
 */
public class ResourceActionsImplPatch extends ResourceActionsImpl implements InitializingBean {
	public ResourceActionsImplPatch(PortletLocalService portletLocalService) {
		this.portletLocalService = portletLocalService;
	}

}
