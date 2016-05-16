package com.wmsi.sgx.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author dt84327
 */
@Component
public final class RSAKeyGenerator {

	private static final Logger log = LoggerFactory.getLogger(RSAKeyGenerator.class);

	private static final String ERROR_GENERATING_KEYS = "Error in generating key files.";

	private static final String ALGORITHAM_NAME = "RSA";

	/**
	 * Size of the key modulus in bits.
	 */
	private static final int KEY_SIZE = 2048;

	/**
	 * Path to store the public key
	 */
	@Value("${rsakey.filepath.public}")
	private String publicPath;

	/**
	 * Path to store private key
	 */
	@Value("${rsakey.filepath.private}")
	private String privatePath;

	@PostConstruct
	public void init() {
		File publicFile = new File(publicPath);
		File privateFile = new File(privatePath);

		if (publicFile.exists() && privateFile.exists()) {
			log.info("Key files are already exists");
		} else {
			log.info("Generating RSA keypair with a " + KEY_SIZE + "-bit modulus.");
			log.info("Please wait...");

			FileOutputStream fos = null;

			try {
				File parentDirectory = new File(publicFile.getParent());
				if (!parentDirectory.exists()) {
					parentDirectory.mkdirs();
				}
				// Get and RSA keypair generator
				KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHAM_NAME);

				// initialize the keysize
				gen.initialize(KEY_SIZE);

				// generate the keys
				KeyPair pair = gen.generateKeyPair();

				PublicKey pubKey = pair.getPublic();
				PrivateKey priKey = pair.getPrivate();

				// write public
				fos = new FileOutputStream(publicFile.getAbsolutePath());
				fos.write(pubKey.getEncoded());
				fos.close();

				// write private
				fos = new FileOutputStream(privateFile.getAbsolutePath());
				fos.write(priKey.getEncoded());
				fos.close();

				log.info("RSA key generation successful!");

			} catch (Exception e) {
				log.error("RSA key generation failed:",e);
				throw new RuntimeException(ERROR_GENERATING_KEYS,e);
			} finally {
				try {
					if (fos != null)
						fos.close();
				} catch (IOException e) {
				}

			}
		}
	}
}
