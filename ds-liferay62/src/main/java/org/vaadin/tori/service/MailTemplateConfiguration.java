package org.vaadin.tori.service;

public interface MailTemplateConfiguration {
	String getRESTEndpointUrl();
	String getBaseThreadUrl();
	String getEmailHeaderImageUrl();
	String getEmailFromAddress();
	String getEmailFromName();
	String getEmailReplyToAddress();
}
