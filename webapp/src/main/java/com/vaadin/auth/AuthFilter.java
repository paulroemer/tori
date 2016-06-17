package com.vaadin.auth;

import com.google.gwt.thirdparty.guava.common.base.Throwables;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.service.UserLocalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.web.filter.RequestContextFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wolfgang on 10/06/16.
 */
public class AuthFilter extends RequestContextFilter implements BeanFactoryAware {
	private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);
	private BeanFactory beanFactory;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		PrincipalThreadLocal.setName(userIdFromRequest(request));
		super.doFilterInternal(request, response, filterChain);
	}

	private long userIdFromRequest(HttpServletRequest request) {
		long result = 10169L; // default user
		UserLocalService userLocalService = beanFactory.getBean(UserLocalService.class);

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("VAADINTEST".equals(cookie.getName())) {
					try {
						User user = userLocalService.fetchUserByUuidAndCompanyId(cookie.getValue(), 10167L);
						return user.getUserId();
					} catch (SystemException e) {
						Throwables.propagate(e);
					}
				}
			}
		}
		LOG.debug("Logging in with default user id " + result);
		return result;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
