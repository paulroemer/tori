/*
 * Copyright 2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vaadin.tori;

import javax.portlet.PortletMode;

import com.google.gwt.thirdparty.guava.common.base.Throwables;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.*;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.vaadin.server.VaadinServletRequest;
import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;
import org.vaadin.tori.component.Breadcrumbs;
import org.vaadin.tori.component.DebugControlPanel;
import org.vaadin.tori.component.RecentBar;
import org.vaadin.tori.service.AuthorizationService;
import org.vaadin.tori.service.DebugAuthorizationService;
import org.vaadin.tori.util.ComponentUtil;
import org.vaadin.tori.util.InputCacheUtil;
import org.vaadin.tori.util.UrlConverter;
import org.vaadin.tori.view.edit.EditViewImpl;
import org.vaadin.tori.widgetset.client.ui.ToriUIServerRpc;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinPortletRequest;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import java.io.Serializable;
import java.security.Key;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.liferay.portal.kernel.util.WebKeys.THEME_DISPLAY;

@SuppressWarnings("serial")
@Widgetset("org.vaadin.tori.widgetset.ToriWidgetset")
public class ToriUI extends UI implements ToriUIServerRpc {

	public static final int DEFAULT_POLL_INTERVAL = 1000 * 10;

	private VerticalLayout mainLayout;

	private GoogleAnalyticsTracker analytics;

	private RecentBar recentBar;
	private Breadcrumbs breadcrumbs;

	private ToriApiLoader apiLoader;
	private InputCacheUtil inputCacheUtil;

	@Override
	protected void init(final VaadinRequest request) {
		setId("tori-ui");
		setPollInterval(DEFAULT_POLL_INTERVAL);
		registerRpc(this);

		fakeRequestAttributes(request);

		ToriApiLoader.init(request);
		apiLoader = ToriApiLoader.getCurrent();
		inputCacheUtil = new InputCacheUtil();
		addExtension(inputCacheUtil.getExtension());
		checkUrl();

		final String trackerId = apiLoader.getDataSource().getConfiguration()
				.getGoogleAnalyticsTrackerId();
		if (trackerId != null && !trackerId.isEmpty()) {
			analytics = new GoogleAnalyticsTracker(trackerId);
			analytics.setAllowAnchor(true);
			analytics.extend(ToriUI.this);
		}

		mainLayout = new VerticalLayout();
		mainLayout.setMargin(false);
		setContent(mainLayout);

		VerticalLayout navigatorContent = new VerticalLayout();
		setNavigator(new ToriNavigator(navigatorContent));
		breadcrumbs = new Breadcrumbs();

		addControlPanelIfInDevelopment();
		recentBar = new RecentBar();
		mainLayout.addComponent(recentBar);
		mainLayout.addComponent(breadcrumbs);
		mainLayout.addComponent(navigatorContent);

		if (request instanceof VaadinPortletRequest) {
			final VaadinPortletRequest r = (VaadinPortletRequest) request;
			setPortletMode(r.getPortletRequest().getPortletMode());
		}

		ConfirmDialog.setFactory(ComponentUtil.getConfirmDialogFactory());
	}

	private void fakeRequestAttributes(final VaadinRequest request) {
		ThemeDisplay themeDisplay = new ThemeDisplay();
		long scopeGroupId = 10187L;
		long siteGroupId = 10187L;
		long companyId = 10167L;
		long accountId = 10168L;
		long userId = 10902L;
		long contactId = 10903L;

		themeDisplay.setScopeGroupId(scopeGroupId);
		themeDisplay.setSiteGroupId(siteGroupId);

		User user = new UserImpl();
		user.setCompanyId(companyId);
		user.setPrimaryKey(userId);
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
		request.setAttribute(THEME_DISPLAY, themeDisplay);
	}

	private void checkUrl() {
		UrlConverter uc = apiLoader.getUrlConverter();
		if (uc != null) {
			String currentUrl = Page.getCurrent().getLocation().toString();
			String convertedUrl = uc.convertUrlToToriForm(currentUrl);
			if (!currentUrl.equals(convertedUrl)) {
				Page.getCurrent().setLocation(convertedUrl);
			}
		}
	}

	public final void setPortletMode(final PortletMode portletMode) {
		if (portletMode == PortletMode.EDIT) {
			final EditViewImpl editView = new EditViewImpl(
					apiLoader.getDataSource(),
					apiLoader.getAuthorizationService());
			editView.init();
			setContent(editView);
		} else {
			setContent(mainLayout);
		}
	}

	private void addControlPanelIfInDevelopment() {
		final AuthorizationService authorizationService = apiLoader
				.getAuthorizationService();
		if (authorizationService instanceof DebugAuthorizationService) {
			DebugControlPanel debugControlPanel = new DebugControlPanel(
					(DebugAuthorizationService) authorizationService);
			mainLayout.addComponent(debugControlPanel);
			mainLayout.setComponentAlignment(debugControlPanel,
					Alignment.TOP_RIGHT);
		}
	}

	/**
	 * Send data to Google Analytics about what the user is doing.
	 *
	 * @param action the action performed in the path. <code>null</code> to ignore.
	 *               E.g. "reply"
	 */
	public void trackAction(final String action) {
		if (analytics != null) {

			String fragment = Page.getCurrent().getUriFragment();
			StringBuilder sb = new StringBuilder(apiLoader.getDataSource()
					.getPathRoot() + "/forum/#");
			sb.append(fragment != null ? fragment : "");
			if (action != null) {
				sb.append("/" + action);
			}
			analytics.trackPageview(sb.toString());
		} else {
			getLogger()
					.debug("Can't track an action - no analytics configured");
		}
	}

	public RecentBar getRecentBar() {
		return recentBar;
	}

	public Breadcrumbs getBreadcrumbs() {
		return breadcrumbs;
	}

	public InputCacheUtil getInputCacheUtil() {
		return inputCacheUtil;
	}

	private static Logger getLogger() {
		return Logger.getLogger(ToriUI.class);
	}

	public static ToriUI getCurrent() {
		return (ToriUI) UI.getCurrent();
	}

	@Override
	public void userInactive() {
		setPollInterval(0);
	}

	@Override
	public void userActive() {
		setPollInterval(DEFAULT_POLL_INTERVAL);
	}
}
