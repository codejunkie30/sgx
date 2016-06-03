package com.wmsi.sgx.security;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.wmsi.sgx.service.EmailService;

/**
 * @author dt84327
 */
@Component
public final class RSAKeysValidator {

	private static final Logger log = LoggerFactory.getLogger(RSAKeysValidator.class);

	private static final String KEYS_MISSING = "RSA key files are missing";

	private static final String MESSAGE_BODY = "RSA key files are missing in the server.The IP address is : ";

	@Value("${rsakey.filepath.public}")
	private String publicPath;

	@Value("${rsakey.filepath.private}")
	private String privatePath;

	@Autowired
	private EmailService emailService;

	@Value("${email.rsakeysmissing.reciever}")
	private String reciever;

	@PostConstruct
	public void init() {
		File publicFile = new File(publicPath);
		File privateFile = new File(privatePath);

		if (!publicFile.exists() || !privateFile.exists()) {
			log.error("RSA key files are missing");
			try {
				InetAddress localHost = InetAddress.getLocalHost();
				emailService.send(reciever, KEYS_MISSING, MESSAGE_BODY + localHost.getHostName());
			} catch (UnknownHostException e) {
				log.error("Unable to get the hostname to notify the missing keys in the server to " + reciever);
			} catch (MessagingException e) {
				log.error("Unable to notify the missing keys in the server to " + reciever);
			}
			throw new RuntimeException(KEYS_MISSING);
		}
	}
}
