package org.vaadin.tori;

import org.springframework.web.filter.GenericFilterBean;
import org.vaadin.tori.indexing.ToriIndexableApplication;

import javax.portlet.RenderResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * Created by wolfgang on 22/06/16.
 * Used to communicate to RememberMeService from within a filter (where VaadinService's currentRequest/response aren't valid yet
 */
public class IndexerBotFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest;
		try {
			if (request instanceof HttpServletRequest) {
				httpRequest = (HttpServletRequest) request;
				if (ToriIndexableApplication.isIndexerBot(httpRequest) && ToriIndexableApplication.isIndexableRequest(httpRequest)) {
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
			//whatever.printStackTrace();
		}

		try {
			chain.doFilter(request, response);
		} finally {
			ThreadUser.remove();
		}
	}

}
