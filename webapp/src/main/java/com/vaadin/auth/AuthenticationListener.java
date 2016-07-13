package com.vaadin.auth;

import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.vaadin.server.VaadinSession;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.vaadin.tori.ToriApiLoader;
import org.vaadin.tori.service.LiferayAuthorizationService;

/**
 * Created by wolfgang on 06/07/16.
 */
public class AuthenticationListener implements ApplicationListener {
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof AuthenticationSuccessEvent) {
			AuthenticationSuccessEvent authEvent = (AuthenticationSuccessEvent)event;
			Object source = authEvent.getSource();
			if (source instanceof RememberMeAuthenticationToken) {
				Object principal = ((RememberMeAuthenticationToken)source).getPrincipal();
				if (principal instanceof VaadinUserDetails) {
					String userId = ((VaadinUserDetails) principal).getUsername();
					PrincipalThreadLocal.setName(userId);
				}
			}
		}
	}
}
