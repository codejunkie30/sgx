package com.wmsi.sgx.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;

import com.wmsi.sgx.service.account.impl.AccountExpiedCheck;
import com.wmsi.sgx.service.account.impl.HalfWayTrialEmailService;

@Configuration
public class QuartzConfig {
	
	@Autowired
	public Environment environment;
	
	@Autowired
	public DataSource dataSource;
	
	@Autowired
	public PlatformTransactionManager transactionManager;
	
	@Autowired
	public ApplicationContext applicationContext;
	
	
	@Bean
    public JobDetailFactoryBean processAccountExpiredEmailService() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(AccountExpiedCheck.class);
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }
	
	@Bean
    public JobDetailFactoryBean processHalfWayTrialEmailService() {
        JobDetailFactoryBean jobDetailFactory1 = new JobDetailFactoryBean();
        jobDetailFactory1.setJobClass(HalfWayTrialEmailService.class);
        jobDetailFactory1.setDurability(true);
        return jobDetailFactory1;
    }
	
	@Bean
    // Configure cron to fire trigger every 1 minute
    public CronTriggerFactoryBean processAccountExpiredEmailTrigger() {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setJobDetail(processAccountExpiredEmailService().getObject());
        cronTriggerFactoryBean.setCronExpression("0 50 18 1/1 * ? *");
        return cronTriggerFactoryBean;
    }
	
	@Bean
    // Configure cron to fire trigger every 1 minute
    public CronTriggerFactoryBean processHalfWayTrialEmailTrigger() {
        CronTriggerFactoryBean cronTriggerFactoryBean1 = new CronTriggerFactoryBean();
        cronTriggerFactoryBean1.setJobDetail(processHalfWayTrialEmailService().getObject());
        //cronTriggerFactoryBean1.setCronExpression("0 19 21 ? * *");
        cronTriggerFactoryBean1.setCronExpression("0 40 19 1/1 * ? *");
        return cronTriggerFactoryBean1;
    }
	
	 @Bean
    public SchedulerFactoryBean quartzScheduler() {
        SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();
        
        quartzScheduler.setQuartzProperties(getProperties());
        quartzScheduler.setDataSource(dataSource);
        quartzScheduler.setTransactionManager(transactionManager);
        quartzScheduler.setOverwriteExistingJobs(true);
 
        // Custom job factory of spring with DI support for @Autowired
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        quartzScheduler.setJobFactory(jobFactory);
 
        Trigger[] triggers = {
        		processAccountExpiredEmailTrigger().getObject(),
        		processHalfWayTrialEmailTrigger().getObject()
        };
 
        quartzScheduler.setTriggers(triggers);
 
        return quartzScheduler;
    }

	private Properties getProperties() {
		final Properties quartzProperties = new Properties();
		quartzProperties.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
		quartzProperties.put("org.quartz.scheduler.instanceId", "AUTO");
		quartzProperties.put("org.quartz.scheduler.makeSchedulerThreadDaemon", "true");
		quartzProperties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		quartzProperties.put("org.quartz.threadPool.threadCount", "1");
		quartzProperties.put("org.quartz.threadPool.makeThreadsDaemons", "true");
		quartzProperties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.MSSQLDelegate");
		quartzProperties.put("org.quartz.jobStore.isClustered", "true");
		quartzProperties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
		
		
		return quartzProperties;
	}
	
	public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory 
    implements ApplicationContextAware {
 
    private transient AutowireCapableBeanFactory beanFactory;
 
    @Override
    public void setApplicationContext(final ApplicationContext context) {
        beanFactory = context.getAutowireCapableBeanFactory();
    }
 
    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) 
        throws Exception {
        final Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }
}
}	


