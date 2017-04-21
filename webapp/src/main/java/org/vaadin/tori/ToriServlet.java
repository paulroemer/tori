/*
 * Copyright 2012 Vaadin Ltd.
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

import com.google.gwt.thirdparty.guava.common.base.Throwables;
import com.liferay.portal.bean.BeanLocatorImpl;
import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ResourceActionLocalServiceUtil;
import com.vaadin.server.*;
import org.springframework.context.ApplicationContext;
import ru.xpoft.vaadin.SpringApplicationContext;
import ru.xpoft.vaadin.SpringVaadinServlet;

import java.util.List;

@SuppressWarnings("serial")
public class ToriServlet extends SpringVaadinServlet {
	public class ToriServletService extends VaadinServletService {

		public ToriServletService(final ToriServlet servlet,
								  final DeploymentConfiguration deploymentConfiguration)
				throws ServiceException {
			super(servlet, deploymentConfiguration);
		}

		@Override
		protected List<RequestHandler> createRequestHandlers()
				throws ServiceException {
			final List<RequestHandler> requestHandlers = super
					.createRequestHandlers();
			requestHandlers.add(new UnsupportedDeviceHandler());
			return requestHandlers;
		}

		@Override
		public String getConfiguredTheme(VaadinRequest request) {
			return getInitParameter("theme");
		}
	}


	@Override
	protected VaadinServletService createServletService(
			final DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {
		final ToriServletService servletService = new ToriServletService(this,
				deploymentConfiguration);
		servletService.init();
		return servletService;
	}

	@Override
	protected void servletInitialized() {
		getService().setSystemMessagesProvider(ToriSystemMessagesProvider.get());
//        InitUtil.initWithSpring();
		ApplicationContext springContext = SpringApplicationContext.getApplicationContext();
		BeanLocator beanLocator = new BeanLocatorImpl(Thread.currentThread().getContextClassLoader(), springContext);
		PortalBeanLocatorUtil.setBeanLocator(beanLocator);

		try {
			ResourceActionLocalServiceUtil.checkResourceActions();
		} catch (SystemException e) {
			Throwables.propagate(e);
		}
	}

}
