package org.vaadin.tori.service;

import java.util.List;
import java.util.Map;

/**
 * Mail message object for communication to REST endpoint
 */
public class RESTMailMessage {
	public final static String REPLACEMENTS_RECIPIENT_FIRSTNAME = "[$recipientFirstName$]";
	public final static String REPLACEMENTS_RECIPIENT_LASTNAME = "[$recipientLastName$]";
	public final static String REPLACEMENTS_RECIPIENT_USERNAME = "[$recipientUserName$]";
	public final static String REPLACEMENTS_FORUM_THREAD_URL = "[$forumThreadUrl$]";
	public final static String REPLACEMENTS_FORUM_THREAD_TITLE = "[$forumThreadTitle$]";
	public final static String REPLACEMENTS_FORUM_REPLY_PERMALINK = "[$forumReplyPermaLink$]";
	public final static String REPLACEMENTS_FORUM_REPLY_BODY = "[$forumReplyBody$]";
	public final static String REPLACEMENTS_FORUM_REPLY_USER_AVATAR_URL = "[$forumReplyUserAvatarUrl$]";
	public final static String REPLACEMENTS_FORUM_REPLY_USER_NAME = "[$forumReplyUserName$]";

	public String webContentUrlTitle;
	public String emailType;
	public String emailSubject;
	public List<String> emailRecipientIds;
	public List<String> emailRecipientAddrs;
	public String emailFromName;
	public String emailFromAddress;

	public Map<String, String> replacements;
}
