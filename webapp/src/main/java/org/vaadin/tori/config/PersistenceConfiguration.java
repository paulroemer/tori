package org.vaadin.tori.config;

import com.vaadin.backend.data.DataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
// order matters!
@PropertySources({
		@PropertySource("classpath:persistence.properties"),
		@PropertySource(value = "classpath:persistence-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
})
public class PersistenceConfiguration {
	@Value("${db.portal.driver}")
	private String portalDbDriver;
	@Value("${db.portal.url}")
	private String portalDbURL;
	@Value("${db.portal.dialect}")
	private String portalSqlDialectName;
	@Value("${db.portal.jndi.name}")
	private String portalJndiName;

	@Value("${db.portal.migration.path}")
	private String portalMigrationPath;

	@Autowired
	private DataSourceFactory dataSourceFactory;

	//
	// Liferay Portal DB configuration
	//
	@Primary
	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		return dataSourceFactory.createDataSource(portalDbDriver, portalDbURL, portalJndiName, portalMigrationPath);
	}
}
