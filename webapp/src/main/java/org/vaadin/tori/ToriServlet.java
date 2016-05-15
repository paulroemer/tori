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

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.bean.BeanLocatorImpl;
import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.util.InitUtil;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import org.springframework.context.ApplicationContext;
import ru.xpoft.vaadin.SpringApplicationContext;
import ru.xpoft.vaadin.SpringVaadinServlet;

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
    protected void service(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {
        super.service(request, response);
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
        getService()
                .setSystemMessagesProvider(ToriSystemMessagesProvider.get());
//        InitUtil.initWithSpring();
        ApplicationContext springContext = SpringApplicationContext.getApplicationContext();
        BeanLocator beanLocator = new BeanLocatorImpl(Thread.currentThread().getContextClassLoader(), springContext);
        PortalBeanLocatorUtil.setBeanLocator(beanLocator);
    }

}
