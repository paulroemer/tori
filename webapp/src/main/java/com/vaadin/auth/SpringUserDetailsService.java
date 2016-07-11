package com.vaadin.auth;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by wolfgang on 05/07/16.
 */
public class SpringUserDetailsService implements UserDetailsService, BeanFactoryAware {
	private BeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserLocalService userLocalService = beanFactory.getBean(UserLocalService.class);
		try {
			User user = userLocalService.fetchUser(Long.valueOf(username));
			if (user == null) {
				throw new UsernameNotFoundException("no user found for screen name: '" + username + "'");
			}
			return new VaadinUserDetails(user);
		} catch (SystemException e) {
			throw new UsernameNotFoundException(e.toString());
		}
	}

}
