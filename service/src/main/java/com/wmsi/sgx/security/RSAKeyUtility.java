/**
 * 
 */
package com.wmsi.sgx.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

import com.wmsi.sgx.service.RSAKeyException;

/**
 * RSAKeyUtility class to handle RSA keys
 * 
 * @author dt84327
 */
public class RSAKeyUtility {

	private static final String ALGORITHAM_NAME = "RSA";

	private static final String TRANSAFORMATION_NAME = "RSA/ECB/PKCS1Padding";

	private RSAKeyUtility() {

	}

	/**
	 * Decrypts the string using the private key file path
	 * 
	 * @param toBeDecrypted
	 * @param privateKeyPath
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String toBeDecrypted, String privateKeyPath) throws RSAKeyException {
		byte[] dectyptedText;
		final Cipher cipher;

		if (toBeDecrypted == null) {
			return null;
		}

		dectyptedText = null;
		try {
			// get an RSA cipher object and print the provider
			cipher = Cipher.getInstance(TRANSAFORMATION_NAME);
			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKeyPath));
			dectyptedText = cipher.doFinal(Base64.decodeBase64(toBeDecrypted.getBytes()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
				| InvalidKeyException e) {
			throw new RSAKeyException("Error in decrypt the string", e);
		}
		return new String(dectyptedText);
	}

	public static String getEncodedPubkey(String publicPath) throws RSAKeyException {
		return Base64.encodeBase64String(getPublicKey(publicPath).getEncoded());
	}

	/**
	 * Retrieves the public key
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private static PrivateKey getPrivateKey(String fileName) throws RSAKeyException {
		FileInputStream fis = null;
		byte[] keyData;
		try {
			fis = new FileInputStream(fileName);
			keyData = new byte[fis.available()];
			fis.read(keyData);

			KeyFactory kf = KeyFactory.getInstance(ALGORITHAM_NAME);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyData);
			return kf.generatePrivate(keySpec);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RSAKeyException("Error in retrieving public key", e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					throw new RSAKeyException("Error in closing the file input stream", e);
				}
		}
	}

	/**
	 * Retrieves the public key
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(String fileName) throws RSAKeyException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
			byte[] keyData = new byte[fis.available()];
			fis.read(keyData);

			KeyFactory kf = KeyFactory.getInstance(ALGORITHAM_NAME);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyData);
			return kf.generatePublic(keySpec);

		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RSAKeyException("Error in retrieving public key", e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					throw new RSAKeyException("Error in closing the file input stream", e);
				}
		}
	}

	public static byte[] encrypt(String text, String publicKeyFileName) throws RSAKeyException {
		byte[] cipherText = null;
		// get an RSA cipher object and print the provider
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(TRANSAFORMATION_NAME);
			cipher.init(Cipher.ENCRYPT_MODE, getPublicKey( publicKeyFileName));
			cipherText = cipher.doFinal((text.getBytes()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cipherText;
	} 
}
