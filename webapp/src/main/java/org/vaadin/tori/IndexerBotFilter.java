package org.vaadin.tori;

import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.CompanyThreadLocal;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalInstances;
import org.springframework.web.filter.GenericFilterBean;
import org.vaadin.tori.indexing.ToriIndexableApplication;
import org.vaadin.tori.util.*;

import javax.portlet.RenderResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.*;

import static com.liferay.portal.kernel.util.WebKeys.THEME_DISPLAY;

/**
 * Created by Ben on 08-AUG-2016.
 * Filter for esp google web bot, returns bare html of a forum thread when two conditions are satisfied:
 * 1) looks like google
 * 2) hashbangs converted to _escaped_fragment_
 * Conceived to execute before UserFromRememberMeFilter
 */
public class IndexerBotFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest;
		try {
			if (request instanceof HttpServletRequest) {
				httpRequest = (HttpServletRequest) request;
				if (ToriIndexableApplication.isIndexerBot(httpRequest) && ToriIndexableApplication.isIndexableRequest(httpRequest)) {
					Long userId = 10169L;

					ThemeDisplay td2 = org.vaadin.tori.util.ToriUtil.fakeThemeDisplay(userId);
					request.setAttribute(THEME_DISPLAY, td2);

					ThreadUser.set(userId);
					PrincipalThreadLocal.setName(userId);
					User user = UserLocalServiceUtil.getUserById(userId);
					PortalInstances.addCompanyId(user.getCompanyId());
					CompanyThreadLocal.setCompanyId(user.getCompanyId());


					final ToriIndexableApplication app = new ToriIndexableApplication(httpRequest);

					final String htmlPage = app.getResultInHtml(httpRequest);

					response.setContentType("text/html");
					final OutputStream out = response.getOutputStream();
					final PrintWriter outWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")));
					outWriter.print(htmlPage);
					outWriter.close();
					return;

				}
			}
		} catch (Exception whatever) {
		}

		try {
			chain.doFilter(request, response);
		} finally {
			ThreadUser.remove();
		}
	}

}
