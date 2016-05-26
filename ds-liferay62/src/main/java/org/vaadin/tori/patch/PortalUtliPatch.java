package org.vaadin.tori.patch;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.model.ExpandoBridge;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by wolfgang on 25/05/16.
 */
public class PortalUtliPatch extends PortalUtil {
	public static Map<String, Serializable> getExpandoBridgeAttributes(
			ExpandoBridge expandoBridge, HttpServletRequest servletRequest)
			throws PortalException, SystemException {
		System.out.println("# # # # portal: " + getPortal());
return null;
//		return getPortal().getExpandoBridgeAttributes(
//				expandoBridge, servletRequest);
	}

}
