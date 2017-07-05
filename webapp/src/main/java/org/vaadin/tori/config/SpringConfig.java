package org.vaadin.tori.config;

import com.vaadin.backend.data.DataSourceFactory;
import com.vaadin.backend.service.PersistenceConfigurationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({MailConfiguration.class, PersistenceConfiguration.class})
public class SpringConfig {
	@Bean
	public DataSourceFactory dataSourceFactory() {
		return new DataSourceFactory();
	}

	@Bean
	public PersistenceConfigurationService persistenceConfigurationService() {
		return new PersistenceConfigurationService();
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();

		propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);

		return propertySourcesPlaceholderConfigurer;
	}
}
