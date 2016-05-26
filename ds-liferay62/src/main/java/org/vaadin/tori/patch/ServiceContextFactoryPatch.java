package org.vaadin.tori.patch;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.util.ExpandoBridgeFactoryUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by wolfgang on 25/05/16.
 */
public class ServiceContextFactoryPatch extends ServiceContextFactory {
	public static ServiceContext getInstance(
			String className, HttpServletRequest servletRequest)
			throws PortalException, SystemException {

		ServiceContext serviceContext = getInstance(servletRequest);

		// Permissions

		String[] groupPermissions = PortalUtil.getGroupPermissions(
				servletRequest, className);
		String[] guestPermissions = PortalUtil.getGuestPermissions(
				servletRequest, className);

		if (groupPermissions != null) {
			serviceContext.setGroupPermissions(groupPermissions);
		}

		if (guestPermissions != null) {
			serviceContext.setGuestPermissions(guestPermissions);
		}

		// Expando

		Map<String, Serializable> expandoBridgeAttributes =
				PortalUtliPatch.getExpandoBridgeAttributes(
						ExpandoBridgeFactoryUtil.getExpandoBridge(
								serviceContext.getCompanyId(), className),
						servletRequest);

		serviceContext.setExpandoBridgeAttributes(expandoBridgeAttributes);

		return serviceContext;
	}
}
