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

package org.vaadin.tori.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.exception.NestableException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.*;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Subscription;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.SubscriptionLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.Logger;
import org.vaadin.tori.HttpServletRequestAware;
import org.vaadin.tori.data.LiferayDataSource;
import org.vaadin.tori.data.entity.LiferayEntityFactoryUtil;
import org.vaadin.tori.patch.ServiceContextReflectionFactory;
import org.vaadin.tori.util.ToriMailService;
import org.xml.sax.SAXException;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class LiferayToriMailService implements ToriMailService,
		HttpServletRequestAware {

	private static final Logger LOG = Logger.getLogger(LiferayDataSource.class);

	private String imagePath;
	private ServiceContext mbMessageServiceContext;

	private MailTemplateConfiguration mailTemplateConfiguration;

	private static final String POP_PORTLET_PREFIX = "mb.";

	public LiferayToriMailService() {
		mailTemplateConfiguration = (MailTemplateConfiguration) PortalBeanLocatorUtil.getBeanLocator().locate("mailTemplateConfiguration");

		Unirest.setObjectMapper(new ObjectMapper() {
			private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
					= new com.fasterxml.jackson.databind.ObjectMapper();

			public <T> T readValue(String value, Class<T> valueType) {
				try {
					return jacksonObjectMapper.readValue(value, valueType);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			public String writeValue(Object value) {
				try {
					return jacksonObjectMapper.writeValueAsString(value);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	@Override
	public void sendUserAuthored(final long postId,
								 final String formattedPostBody) {
		try {
			MBMessage mbMessage = MBMessageLocalServiceUtil
					.getMBMessage(postId);

			InternetAddress[] bulkAddresses = parseAddresses(mbMessage);

			if (bulkAddresses.length > 0) {
				String mailId = getMailId(mbMessage.getCompanyId(),
						mbMessage.getCategoryId(), mbMessage.getMessageId());

				String subject = "[" + mbMessage.getCategory().getName() + "] "
						+ mbMessage.getSubject();
				Company company = CompanyLocalServiceUtil.getCompany(mbMessage
						.getCompanyId());
				String companyEmail = company.getEmailAddress();

				String fromAddress = mailTemplateConfiguration.getEmailFromAddress();
				if (fromAddress == null) {
					fromAddress = companyEmail;
				}

				String fromName = mailTemplateConfiguration.getEmailFromName();
				if (fromName == null) {
					fromName = company.getName() + " forums";
				}

				// not supported by the email community portlet
//				String replyToAddress = mailTemplateConfiguration.getEmailReplyToAddress();
//				if (replyToAddress == null) {
//					replyToAddress = fromAddress;
//				}

				Map<String, String> replacementsMap = buildReplacementsMap(mbMessage, formattedPostBody);

				RESTMailMessage restMailMessage = new RESTMailMessage();
				restMailMessage.emailFromAddress = fromAddress;
				restMailMessage.emailFromName = fromName;
				restMailMessage.emailRecipientAddrs = Arrays.stream(bulkAddresses).map(InternetAddress::toString).collect(Collectors.toList());
				restMailMessage.emailSubject = subject;
				restMailMessage.emailType = "";
				restMailMessage.webContentUrlTitle = "community-emails-forum-post-reply";
				restMailMessage.emailRecipientIds = new ArrayList<>();
				restMailMessage.replacements = replacementsMap;

				Unirest.post(mailTemplateConfiguration.getRESTEndpointUrl())
						.header("accept", "application/text")
						.header("Content-Type", "application/json; charset=utf8")
						.body(restMailMessage)
						.asJsonAsync(new Callback<JsonNode>() {
							public void failed(UnirestException e) {

								try {
									LOG.warn("Error sending mail via REST endpoint: " + new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(restMailMessage));
								} catch (JsonProcessingException e1) {
									LOG.error("Could not serialize RESTMailMessage - This really, really should never happen...");
								}
							}

							public void completed(HttpResponse<JsonNode> response) {
								int code = response.getStatus();
								LOG.debug("REST call completed and returned: " + code);
								// ignore and just be happy
							}

							public void cancelled() {
								LOG.debug("REST call cancelled");
							}
						}).get();
			}
		} catch (Exception e) {
			getLogger().warn("Unable to form email notification", e);
		}
	}

	private Map<String, String> buildReplacementsMap(final MBMessage mbMessage,
													 final String formattedPostBody) throws IOException, SAXException,
			PortalException, SystemException {

		User user = null;
		String avatarUrl = "";
		try {
			user = UserLocalServiceUtil.getUser(mbMessage.getUserId());
			String userAvatarUrl = LiferayEntityFactoryUtil.getAvatarUrl(
					user.getPortraitId(), imagePath, user.isFemale());
			if (userAvatarUrl != null) {
				avatarUrl = mbMessageServiceContext.getPortalURL()
						+ userAvatarUrl;
			}
		} catch (NestableException e) {
			// Ignore
		}

		MBMessage rootMessage = MBMessageLocalServiceUtil.getMessage(mbMessage
				.getThread().getRootMessageId());
		String threadTopic = rootMessage.getSubject();
		threadTopic = stripTags(threadTopic);

		String userDisplayName = user != null ? user.getFullName()
				: "Anonymous";
		if (user != null
				&& LiferayEntityFactoryUtil.usesScreennameOnTori(user)) {
			userDisplayName = user.getScreenName();
		}
		userDisplayName = stripTags(userDisplayName);

		String threadUrl = mailTemplateConfiguration.getBaseThreadUrl()
				+ "#!/thread/" + mbMessage.getThreadId();

		String permaLink = threadUrl + "/" + mbMessage.getMessageId();

		Map<String, String> result = new HashMap<>();

		result.put(RESTMailMessage.REPLACEMENTS_RECIPIENT_FIRSTNAME, user != null ? user.getFirstName() : "Unknown");
		result.put(RESTMailMessage.REPLACEMENTS_RECIPIENT_LASTNAME, user != null ? user.getLastName() : "Unknown");
		result.put(RESTMailMessage.REPLACEMENTS_RECIPIENT_USERNAME, userDisplayName);
		result.put(RESTMailMessage.REPLACEMENTS_FORUM_THREAD_URL, threadUrl);
		result.put(RESTMailMessage.REPLACEMENTS_FORUM_THREAD_TITLE, threadTopic);
		result.put(RESTMailMessage.REPLACEMENTS_FORUM_REPLY_PERMALINK, permaLink);
		result.put(RESTMailMessage.REPLACEMENTS_FORUM_REPLY_BODY, formattedPostBody);
		result.put(RESTMailMessage.REPLACEMENTS_FORUM_REPLY_USER_AVATAR_URL, avatarUrl);
		result.put(RESTMailMessage.REPLACEMENTS_FORUM_REPLY_USER_NAME, rootMessage.getUserName());

		return result;
	}

	private InternetAddress[] parseAddresses(final MBMessage mbMessage) {

		List<Subscription> subscriptions = new ArrayList<Subscription>();

		// Threads
		try {
			subscriptions.addAll(SubscriptionLocalServiceUtil.getSubscriptions(
					mbMessage.getCompanyId(), MBThread.class.getName(),
					mbMessage.getThreadId()));
		} catch (SystemException e1) {
			getLogger().warn(
					"Unable to get thread subscriptions for thread "
							+ mbMessage.getThreadId(), e1);
		}

		Collection<Long> sent = new HashSet<Long>();
		List<InternetAddress> addresses = new ArrayList<InternetAddress>();

		for (Subscription subscription : subscriptions) {
			long subscribedUserId = subscription.getUserId();

			// Don't send email to the message author
			if (subscribedUserId == mbMessage.getUserId()
					|| sent.contains(subscribedUserId)) {
				continue;
			} else {
				sent.add(subscribedUserId);
			}

			try {
				User user = UserLocalServiceUtil.getUserById(subscribedUserId);
				if (user.isActive()) {
					InternetAddress userAddress = new InternetAddress(
							user.getEmailAddress(), user.getFullName());

					addresses.add(userAddress);
				}
			} catch (NoSuchUserException nsue) {
				if (getLogger().isInfoEnabled()) {
					getLogger().info(
							"Subscription " + subscription.getSubscriptionId()
									+ " is stale and will be deleted");
				}

				try {
					SubscriptionLocalServiceUtil
							.deleteSubscription(subscription
									.getSubscriptionId());
				} catch (NestableException e) {
					getLogger().warn("Unable to delete subscription", e);
				}
			} catch (NestableException | UnsupportedEncodingException e) {
				getLogger().warn(
						"Unable to parse address for userId "
								+ subscribedUserId, e);
			}
		}

		return addresses.toArray(new InternetAddress[addresses.size()]);

	}

	private static String getMailId(final long companyId,
									final long categoryId, final long messageId)
			throws PortalException, SystemException {

		Company company = CompanyLocalServiceUtil.getCompany(companyId);
		String mx = company.getMx();
		StringBundler sb = new StringBundler(10);

		sb.append(StringPool.LESS_THAN);
		sb.append(POP_PORTLET_PREFIX);
		sb.append(categoryId);
		sb.append(StringPool.PERIOD);
		sb.append(messageId);
		sb.append(StringPool.AT);

		String sd = PropsUtil.get(PropsKeys.POP_SERVER_SUBDOMAIN);
		if (sd != null && !"null".equals(sd.toLowerCase())) {
			sb.append(sd);
			sb.append(StringPool.PERIOD);
		}

		sb.append(mx);
		sb.append(StringPool.GREATER_THAN);

		return sb.toString();
	}

	private Logger getLogger() {
		return Logger.getLogger(LiferayToriMailService.class);
	}

	@Override
	public void setRequest(final HttpServletRequest request) {
		try {
			imagePath = ((ThemeDisplay) request
					.getAttribute(WebKeys.THEME_DISPLAY)).getPathImage();
			mbMessageServiceContext = ServiceContextReflectionFactory.getInstance(MBMessage.class.getName(), request);
//		} catch (NestableException e) {
//			getLogger().error("Unable to initialize mail service", e);
		} catch (NullPointerException e) {
			getLogger().error("Unable to initialize mail service", e);
		}
	}

	private String stripTags(final String html) {
		return html.replaceAll("\\<.*?>", "");
	}

}
