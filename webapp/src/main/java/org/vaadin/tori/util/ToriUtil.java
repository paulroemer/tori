package org.vaadin.tori.util;

import com.google.gwt.thirdparty.guava.common.base.Throwables;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * Created by Ben Wilson on 8/11/2016.
 */
public class ToriUtil {

	public static ThemeDisplay fakeThemeDisplay(final Long userId) {
		ThemeDisplay themeDisplay = new ThemeDisplay();
		long scopeGroupId = 10187L;
		long siteGroupId = 10187L;
		long companyId = 10167L;
		long accountId = 10168L;
		long contactId = 10903L;

		themeDisplay.setScopeGroupId(scopeGroupId);
		themeDisplay.setSiteGroupId(siteGroupId);

		User user = new UserImpl();
		user.setCompanyId(companyId);
		user.setPrimaryKey(userId!=null?userId:0);
		user.setContactId(contactId);

		Company company = new CompanyImpl();
		company.setCompanyId(companyId);
		company.setAccountId(accountId);
		company.setWebId("vaadin.com");
		company.setKey("rO0ABXNyAB9qYXZheC5jcnlwdG8uc3BlYy5TZWNyZXRLZXlTcGVjW0cLZuIwYU0CAAJMAAlhbGdvcml0aG10ABJMamF2YS9sYW5nL1N0cmluZztbAANrZXl0AAJbQnhwdAADQUVTdXIAAltCrPMX");
		company.setMx("vaadin.com");
		company.setHomeURL("/home");
		company.setActive(true);

		try {
			themeDisplay.setUser(user);
			themeDisplay.setCompany(company);
		} catch (PortalException e) {
			Throwables.propagate(e);
		} catch (SystemException e) {
			Throwables.propagate(e);
		}

		Layout layout = new LayoutImpl();
		layout.setCompanyId(companyId);
		layout.setGroupId(scopeGroupId);
		layout.setParentLayoutId(0L);
		layout.setType("link_to_layout");
		layout.setLayoutId(78L);

		themeDisplay.setLayout(layout);
		themeDisplay.setServerName("localhost");
		return themeDisplay;
	}

}
