package com;

import com.liferay.portal.security.permission.BasePermissionChecker;
import com.liferay.portal.security.permission.PermissionChecker;
import org.apache.commons.collections.Transformer;
import org.vaadin.tori.util.XStreamCopyTransformer;

/**
 * Created by wolfgang on 20/05/16.
 */
public class NoPermissionChecker extends BasePermissionChecker {
	private static final Transformer CLONE_TRANSFORMER = new XStreamCopyTransformer();

	private long userId = 12213L;	// TODO use real userIds - from session presumably

	@Override
	public long getUserId() {
		return userId;
	}

	public NoPermissionChecker() {
		System.out.println("# # # #");
	}

	@Override
	public PermissionChecker clone() {
		return (PermissionChecker) CLONE_TRANSFORMER.transform(this);
	}

	@Override
	public boolean hasOwnerPermission(long companyId, String name, String primKey, long ownerId, String actionId) {
		return true;
	}

	@Override
	public boolean hasPermission(long groupId, String name, String primKey, String actionId) {
		return true;
	}

	@Override
	public boolean hasUserPermission(long groupId, String name, String primKey, String actionId, boolean checkAdmin) {
		return true;
	}

	@Override
	public boolean isCompanyAdmin() {
		return true;
	}

	@Override
	public boolean isCompanyAdmin(long companyId) {
		return true;
	}

	@Override
	public boolean isGroupAdmin(long groupId) {
		return true;
	}

	@Override
	public boolean isGroupMember(long groupId) {
		return true;
	}

	@Override
	public boolean isGroupOwner(long groupId) {
		return true;
	}

	@Override
	public boolean isOrganizationAdmin(long organizationId) {
		return true;
	}

	@Override
	public boolean isOrganizationOwner(long organizationId) {
		return true;
	}
}
