package com.liferay.portal.patch;

import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portlet.messageboards.service.impl.MBMessageServiceImpl;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wolfgang on 27/05/16.
 */
public class MBMessageServiceImplPatch extends MBMessageServiceImpl {
	private PermissionChecker permissionChecker;

	public MBMessageServiceImplPatch(PermissionChecker permissionChecker) {
		this.permissionChecker = permissionChecker;
	}

	@Override
	public PermissionChecker getPermissionChecker() throws PrincipalException {
		return permissionChecker;
	}
}
