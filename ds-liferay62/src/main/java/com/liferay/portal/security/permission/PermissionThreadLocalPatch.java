/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.security.permission;

import com.liferay.portal.kernel.util.AutoResetThreadLocal;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class PermissionThreadLocalPatch {
	private static ThreadLocal<PermissionChecker> _permissionChecker;
	public PermissionThreadLocalPatch(final PermissionChecker permissionChecker) {
		_permissionChecker =
				new AutoResetThreadLocal<PermissionChecker>(
						PermissionThreadLocalPatch.class + "._permissionChecker", permissionChecker) {

					@Override
					protected PermissionChecker copy(
							PermissionChecker permissionChecker) {

						return permissionChecker;
					}

				};
	}
	public static PermissionChecker getPermissionChecker() {
		return _permissionChecker.get();
	}

	public static boolean isAddResource() {
		return _addResource.get();
	}

	public static boolean isFlushEnabled() {
		return _flushEnabled.get();
	}

	public static void setAddResource(boolean addResource) {
		_addResource.set(addResource);
	}

	public static void setIndexEnabled(boolean indexEnabled) {
		_flushEnabled.set(indexEnabled);
	}

	public static void setPermissionChecker(
			PermissionChecker permissionChecker) {
	}

	private static ThreadLocal<Boolean> _addResource =
			new AutoResetThreadLocal<Boolean>(
					PermissionThreadLocalPatch.class + "._addResource", true);
	private static ThreadLocal<Boolean> _flushEnabled =
			new AutoResetThreadLocal<Boolean>(
					PermissionThreadLocalPatch.class + "._flushEnabled", true);

}