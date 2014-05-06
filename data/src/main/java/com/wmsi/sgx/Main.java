/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wmsi.sgx;

import java.util.Date;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

/**
 * Starts the Spring Context and will initialize the Spring Integration routes.
 *
 * @author JLee
 * @since 1.0
 *
 */
public final class Main{

	private static final Logger LOGGER = Logger.getLogger(Main.class);

	private Main(){
	}

	/**
	 * Load the Spring Integration Application Context
	 *
	 * @param args
	 *            - command line arguments
	 */
	public static void main(final String... args) {

		if(LOGGER.isInfoEnabled()){
			LOGGER.info("\n========================================================="
					+ "\n                                                         "
					+ "\n          Welcome to Spring Integration!                 "
					+ "\n                                                         "
					+ "\n    For more information please visit:                   "
					+ "\n    http://www.springsource.org/spring-integration       "
					+ "\n                                                         "
					+ "\n=========================================================");
		}

		final AbstractApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:META-INF/spring/integration/*-context.xml");

		context.registerShutdownHook();

		final Scanner scanner = new Scanner(System.in);

		if(LOGGER.isInfoEnabled()){
			LOGGER.info("\n========================================================="
					+ "\n                                                         "
					+ "\n    Please press 'q + Enter' to quit the application.    "
					+ "\n                                                         "
					+ "\n=========================================================");
		}

		System.out.print("Please enter a string and press <enter>: ");

		MessageChannel chan = (MessageChannel) context.getBean("indexRequestChannel");

		Resource companyIds = new ClassPathResource("data/sgx_companies_short.txt");
		
		chan.send(MessageBuilder.withPayload(companyIds)
				.setHeader("jobId", System.currentTimeMillis())
				.setHeader("jobDate", new Date()).build());

		try{
			while(true){

				final String input = scanner.nextLine();

				if("q".equals(input.trim())){
					break;
				}

				System.out.print("Please enter a string and press <enter>:");

			}

			if(LOGGER.isInfoEnabled()){
				LOGGER.info("Exiting application...bye.");
			}
		}
		finally{
			scanner.close();
			context.close();
		}

		System.exit(0);

	}
}
