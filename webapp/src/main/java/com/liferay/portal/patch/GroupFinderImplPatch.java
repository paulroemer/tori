/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.patch;

import com.liferay.portal.NoSuchGroupException;
import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.*;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.ResourceAction;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.service.ResourceBlockLocalServiceUtil;
import com.liferay.portal.service.persistence.GroupFinder;
import com.liferay.portal.service.persistence.GroupFinderImpl;
import com.liferay.portal.service.persistence.GroupUtil;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.util.comparator.GroupNameComparator;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class GroupFinderImplPatch extends GroupFinderImpl {
	private LinkedHashMap<String, Object> _emptyLinkedHashMap =
			new LinkedHashMap<String, Object>(0);

	private Map<String, String> _findByC_C_PG_N_DSQLCache =
			new ConcurrentHashMap<String, String>();

	private volatile long[] _groupOrganizationClassNameIds;

	@Override
	public List<Group> findByC_C_PG_N_D(
			long companyId, long[] classNameIds, long parentGroupId,
			String[] names, String[] descriptions,
			LinkedHashMap<String, Object> params, boolean andOperator,
			int start, int end, OrderByComparator obc)
		throws SystemException {

		String parentGroupIdComparator = StringPool.EQUAL;

		if (parentGroupId == GroupConstants.ANY_PARENT_GROUP_ID) {
			parentGroupIdComparator = StringPool.NOT_EQUAL;
		}

		names = CustomSQLUtil.keywords(names);
		descriptions = CustomSQLUtil.keywords(descriptions);

		if (params == null) {
			params = _emptyLinkedHashMap;
		}

		LinkedHashMap<String, Object> params1 = params;

		LinkedHashMap<String, Object> params2 = null;

		LinkedHashMap<String, Object> params3 = null;

		LinkedHashMap<String, Object> params4 = null;

		Long userId = (Long)params.get("usersGroups");
		boolean inherit = GetterUtil.getBoolean(params.get("inherit"), true);

		boolean doUnion = Validator.isNotNull(userId) && inherit;

		if (doUnion) {
			params2 = new LinkedHashMap<String, Object>(params1);

			params2.remove("usersGroups");
			params2.put("groupOrg", userId);

			params3 = new LinkedHashMap<String, Object>(params1);

			params3.remove("usersGroups");
			params3.put("groupsOrgs", userId);

			params4 = new LinkedHashMap<String, Object>(params1);

			params4.remove("usersGroups");
			params4.put("groupsUserGroups", userId);
		}

		if (obc == null) {
			obc = new GroupNameComparator(true);
		}

		String sql = null;

		if (classNameIds == _getGroupOrganizationClassNameIds()) {
			String sqlKey = _buildSQLKey(
				params1, params2, params3, params4, obc, doUnion);

			sql = _findByC_C_PG_N_DSQLCache.get(sqlKey);
		}

		if (sql == null) {
			String findByC_PG_N_D_SQL = CustomSQLUtil.get(FIND_BY_C_C_PG_N_D);

			if (classNameIds == null) {
				findByC_PG_N_D_SQL = StringUtil.replace(
					findByC_PG_N_D_SQL, "AND (Group_.classNameId = ?)",
					StringPool.BLANK);
			}
			else {
				findByC_PG_N_D_SQL = StringUtil.replace(
					findByC_PG_N_D_SQL, "Group_.classNameId = ?",
					"Group_.classNameId = ".concat(
						StringUtil.merge(
							classNameIds, " OR Group_.classNameId = ")));
			}

			findByC_PG_N_D_SQL = replaceOrderBy(findByC_PG_N_D_SQL, obc);

			StringBundler sb = new StringBundler();

			sb.append(StringPool.OPEN_PARENTHESIS);
			sb.append(replaceJoinAndWhere(findByC_PG_N_D_SQL, params1));
			sb.append(StringPool.CLOSE_PARENTHESIS);

			if (doUnion) {
				sb.append(" UNION (");
				sb.append(replaceJoinAndWhere(findByC_PG_N_D_SQL, params2));
				sb.append(") UNION (");
				sb.append(replaceJoinAndWhere(findByC_PG_N_D_SQL, params3));
				sb.append(") UNION (");
				sb.append(replaceJoinAndWhere(findByC_PG_N_D_SQL, params4));
				sb.append(StringPool.CLOSE_PARENTHESIS);
			}

			if (obc != null) {
				sb.append(" ORDER BY ");
				sb.append(obc.toString());
			}

			sql = sb.toString();

			if (classNameIds == _getGroupOrganizationClassNameIds()) {
				String sqlKey = _buildSQLKey(
					params1, params2, params3, params4, obc, doUnion);

				_findByC_C_PG_N_DSQLCache.put(sqlKey, sql);
			}
		}

		sql = StringUtil.replace(
			sql, "[$PARENT_GROUP_ID_COMPARATOR$]",
			parentGroupIdComparator.equals(StringPool.EQUAL) ?
				StringPool.EQUAL : StringPool.NOT_EQUAL);
		sql = CustomSQLUtil.replaceKeywords(
			sql, "lower(Group_.name)", StringPool.LIKE, false, names);
		sql = CustomSQLUtil.replaceKeywords(
			sql, "lower(Group_.description)", StringPool.LIKE, true,
			descriptions);
		sql = CustomSQLUtil.replaceAndOperator(sql, andOperator);

		Session session = null;

		try {
			session = openSession();

			SQLQuery q = session.createSQLQuery(sql);

			q.addScalar("groupId", Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			setJoin(qPos, params1);

			qPos.add(companyId);
			qPos.add(parentGroupId);
			qPos.add(names, 2);
			qPos.add(descriptions, 2);

			if (doUnion) {
				setJoin(qPos, params2);

				qPos.add(companyId);
				qPos.add(parentGroupId);
				qPos.add(names, 2);
				qPos.add(descriptions, 2);

				setJoin(qPos, params3);

				qPos.add(companyId);
				qPos.add(parentGroupId);
				qPos.add(names, 2);
				qPos.add(descriptions, 2);

				setJoin(qPos, params4);

				qPos.add(companyId);
				qPos.add(parentGroupId);
				qPos.add(names, 2);
				qPos.add(descriptions, 2);
			}

			List<BigInteger> groupIds = (List<BigInteger>)QueryUtil.list(
				q, getDialect(), start, end);

			List<Group> groups = new ArrayList<Group>(groupIds.size());

			for (BigInteger groupId : groupIds) {
				Group group = GroupUtil.findByPrimaryKey(groupId.longValue());

				groups.add(group);
			}

			return groups;
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	private String _buildSQLKey(
			LinkedHashMap<String, Object> param1,
			LinkedHashMap<String, Object> param2,
			LinkedHashMap<String, Object> param3,
			LinkedHashMap<String, Object> param4, OrderByComparator obc,
			boolean doUnion) {

		StringBundler sb = null;

		if (doUnion) {
			sb = new StringBundler(
					param1.size() + param2.size() + param3.size() + param4.size() +
							1);

			for (String key : param1.keySet()) {
				sb.append(key);
			}

			for (String key : param2.keySet()) {
				sb.append(key);
			}

			for (String key : param3.keySet()) {
				sb.append(key);
			}

			for (String key : param4.keySet()) {
				sb.append(key);
			}
		}
		else {
			sb = new StringBundler(param1.size() + 1);

			for (String key : param1.keySet()) {
				sb.append(key);
			}
		}

		sb.append(obc.getOrderBy());

		return sb.toString();
	}

	private long[] _getGroupOrganizationClassNameIds() {
		if (_groupOrganizationClassNameIds == null) {
			_groupOrganizationClassNameIds = new long[] {
					ClassNameLocalServiceUtil.getClassNameId(Group.class),
					ClassNameLocalServiceUtil.getClassNameId(Organization.class)
			};
		}

		return _groupOrganizationClassNameIds;
	}

}
