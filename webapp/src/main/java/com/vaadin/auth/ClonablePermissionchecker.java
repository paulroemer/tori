package com.vaadin.auth;

import com.liferay.portal.security.permission.AdvancedPermissionChecker;

/**
 * Created by wolfgang on 20/07/16.
 */
public class ClonablePermissionchecker extends AdvancedPermissionChecker {
	@Override
	public AdvancedPermissionChecker clone() {
		AdvancedPermissionChecker result = super.clone();
		result.init(getUser());
		return result;
	}
}
