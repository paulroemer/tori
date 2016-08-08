package org.vaadin.tori;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.CompanyThreadLocal;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalInstances;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import ru.xpoft.vaadin.SpringApplicationContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by wolfgang on 22/06/16.
 * Used to communicate to RememberMeService from within a filter (where VaadinService's currentRequest/response aren't valid yet
 */
public class UserFromRememberMeFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			Long userId = getCurrentUser((HttpServletRequest) request);
			if (userId == null) {
				// use default aka guest user (see https://web.liferay.com/community/wiki/-/wiki/Main/The+%22Guest%22%20user)
				userId = 10169L;
			}
			ThreadUser.set(userId);

			ApplicationContext ctx = SpringApplicationContext.getApplicationContext();
			if (ctx != null) {
				PermissionChecker permissionChecker = ctx.getBean(PermissionChecker.class);
				try {
					User user = UserLocalServiceUtil.getUserById(userId);
					permissionChecker.init(user);
					PrincipalThreadLocal.setName(userId);
					PortalInstances.addCompanyId(user.getCompanyId());
					CompanyThreadLocal.setCompanyId(user.getCompanyId());
				} catch (PortalException e) {
					e.printStackTrace();
				} catch (SystemException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			chain.doFilter(request, response);
		} finally {
			ThreadUser.remove();
		}
	}

	private Long getCurrentUser(final HttpServletRequest request) {
		// getCookies() returns an array of cookies or null if there are no cookies
		// prevent 500 error from npe by returning immediately here
		if (request.getCookies() == null) {
			return null;
		}
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("remember-me")) {
				String val = cookie.getValue();
				if (val != null && val.length() > 0) {
					String[] rememberMeCookieElements = decodeCookie(val);
					if (rememberMeCookieElements.length == 3) {
						String userIdString = rememberMeCookieElements[0];
						if (org.apache.commons.lang3.StringUtils.isNumeric(userIdString)) {
							return Long.valueOf(userIdString);
						}
					}
				}
			}
		}

		return null;
	}

	private String[] decodeCookie(String cookieValue) {
		for (int j = 0; j < cookieValue.length() % 4; j++) {
			cookieValue = cookieValue + "=";
		}

		if (!Base64.isBase64(cookieValue.getBytes())) {
			throw new InvalidCookieException(
					"Cookie token was not Base64 encoded; value was '" + cookieValue
							+ "'");
		}

		String cookieAsPlainText = new String(Base64.decode(cookieValue.getBytes()));

		String[] tokens = StringUtils.delimitedListToStringArray(cookieAsPlainText,
				":");

		if ((tokens[0].equalsIgnoreCase("http") || tokens[0].equalsIgnoreCase("https"))
				&& tokens[1].startsWith("//")) {
			// Assume we've accidentally split a URL (OpenID identifier)
			String[] newTokens = new String[tokens.length - 1];
			newTokens[0] = tokens[0] + ":" + tokens[1];
			System.arraycopy(tokens, 2, newTokens, 1, newTokens.length - 1);
			tokens = newTokens;
		}

		return tokens;
	}


}
