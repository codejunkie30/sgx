package com.wmsi.sgx.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Data configuration file
 */

@Configuration
@EnableJpaRepositories(basePackages = { "com.wmsi.sgx.repositories" })
@EnableTransactionManagement
public class DataConfig{

	@Autowired
	public Environment env;
	
	@Bean
	public DataSource dataSource() {

		BasicDataSource source = new BasicDataSource();
		source.setDriverClassName(env.getProperty("database.driver"));
		source.setUrl(env.getProperty("database.url"));
		source.setUsername(env.getProperty("database.user"));
		source.setPassword(env.getProperty("database.password"));

		return source;
	}

	@Bean
	public SessionFactory sessionFactory() {

		LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
		factory.setDataSource(dataSource());
		return factory.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager manager = new JpaTransactionManager();
		manager.setEntityManagerFactory(emf);
		manager.setJpaDialect(new HibernateJpaDialect());

		return manager;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.SQL_SERVER);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource());
		factory.setPackagesToScan("com.wmsi.sgx.domain"); // Might not be needed
		factory.setJpaVendorAdapter(adapter);
		factory.setPersistenceProvider(new HibernatePersistenceProvider());

		return factory;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

}
