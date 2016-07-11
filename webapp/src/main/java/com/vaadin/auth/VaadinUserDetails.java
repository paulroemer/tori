package com.vaadin.auth;

import com.liferay.portal.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class VaadinUserDetails implements UserDetails {

	Collection<GrantedAuthority> grantedAuthorities = Collections.emptySet();
	private User user;


	public VaadinUserDetails(User user) {
		this.user = user;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		if (this.user != null) {
			throw new IllegalStateException("cannot change a user once set");
		}
		this.user = user;
	}

	@Override
	public String toString() {
		return user.getEmailAddress();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return String.valueOf(user.getUserId());
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
