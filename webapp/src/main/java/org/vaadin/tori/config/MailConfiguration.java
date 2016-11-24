package org.vaadin.tori.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jndi.JndiTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Session;
import javax.naming.NamingException;

/**
 * Created by vaadin on 23.11.16.
 */
@Configuration
@PropertySource("classpath:mail.properties")
public class MailConfiguration {

	@Value("${mail.smtprelay.usedummy:false}")
	private boolean useDummy;

	@Value("${mail.smtprelay.jndi:}")
	private String jndiName;

	@Value("${mail.smtprelay.host:localhost}")
	private String host;

	@Value("${mail.smtprelay.port:25}")
	private Integer port;

	@Value("${mail.smtprelay.protocol:smtp}")
	private String protocol;

	@Bean
	public MailSender mailSender() {
		Session mailSession = null;
		if(!jndiName.isEmpty() && (mailSession = session()) != null) {
			JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
			javaMailSender.setSession(mailSession);

			return javaMailSender;
		}

		if(useDummy) {
			return new MailSenderDummy();
		}

		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

		javaMailSender.setHost(host);
		javaMailSender.setPort(port);
		javaMailSender.setProtocol(protocol);

		return javaMailSender;
	}

	public Session session(){
		JndiTemplate template = new JndiTemplate();
		Session session = null;
		try {
			session = (Session) template.lookup("java:comp/env/" + jndiName);
		} catch (NamingException e) {
			System.err.println("Lookup of JNDI failed: " + e.getExplanation());
		}

		return session;
	}

	private class MailSenderDummy implements MailSender {

		@Override
		public void send(SimpleMailMessage simpleMailMessage) throws MailException {
			System.out.println(simpleMailMessage.toString());
		}

		@Override
		public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
			for(SimpleMailMessage mailMessage : simpleMailMessages) {
				send(mailMessage);
			}
		}
	}

	@Value("${mail.template.basethreadurl:}")
	private String baseThreadUrl;

	@Value("${mail.template.headerimageurl:}")
	private String headerImgUrl;

	@Value("${mail.template.from:no-reply@vaadin.com}")
	private String from;

	@Value("${mail.template.fromname:no-reply@vaadin.com}")
	private String fromName;

	@Value("${mail.template.replyto:no-reply@vaadin.com}")
	private String replyTo;

	@Bean
	public org.vaadin.tori.service.MailTemplateConfiguration mailTemplateConfiguration() {
		return new org.vaadin.tori.service.MailTemplateConfiguration() {
			@Override
			public String getBaseThreadUrl() {
				return baseThreadUrl;
			}

			@Override
			public String getEmailHeaderImageUrl() {
				return headerImgUrl;
			}

			@Override
			public String getEmailFromAddress() {
				return from;
			}

			@Override
			public String getEmailFromName() {
				return fromName;
			}

			@Override
			public String getEmailReplyToAddress() {
				return replyTo;
			}
		};
	}
}
