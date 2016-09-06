package com.wmsi.sgx.security.token;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.repository.UserRepository;
import com.wmsi.sgx.service.RSAKeyException;
import com.wmsi.sgx.service.RSAKeyService;

public final class TokenHandler {

	private static final String HMAC_ALGO = "HmacSHA256";
	private static final String SEPARATOR = "&&";
	private static final String SEPARATOR_SPLITTER = "\\&&";

	private final Mac hmac;

	@Autowired
	private RSAKeyService rsaKeyService;

	@Autowired
	private UserRepository userReposistory;

	public TokenHandler(byte[] secretKey) {
		try {
			hmac = Mac.getInstance(HMAC_ALGO);
			hmac.init(new SecretKeySpec(secretKey, HMAC_ALGO));
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new IllegalStateException("failed to initialize HMAC: " + e.getMessage(), e);
		}
	}

	public final User parseUserFromToken(String token) {
		// decrypt
		token = decryptToken(token);
		final String[] parts = token.split(SEPARATOR_SPLITTER);
		if (parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0) {
			try {
				final byte[] userBytes = fromBase64(parts[0]);
				final byte[] hash = fromBase64(parts[1]);

				boolean validHash = Arrays.equals(createHmac(userBytes), hash);
				if (validHash) {
					String[] decryptedToken = parts[0].split("\\@@");
					if (decryptedToken.length == 2 && decryptedToken[0].length() > 0
							&& decryptedToken[1].length() > 0) {
						User user = userReposistory.findByUsername(decryptedToken[0]);
						user.setExpires(Long.parseLong(decryptedToken[1]));
						if (new Date().getTime() < user.getExpires()) {
							return user;
						}
					}
				}
			} catch (IllegalArgumentException e) {
				// log tempering attempt here
			}
		}
		return null;
	}

	// TODO ENCRYPT THE TOKEN
	protected final String createTokenForUser(User user) {
		final StringBuilder sb = hashUserToken(user);
		// Encrypt the token
		return encryptToken(sb);
	}

	protected final String decryptToken(String hashStr) {
		byte[] origByte = fromBase64(hashStr);
		try {
			return rsaKeyService.decrypt(hashStr);
		} catch (RSAKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String encryptToken(StringBuilder token) {
		try {
			return toBase64(rsaKeyService.encrypt(token.toString()));
		} catch (RSAKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// rsaKeyService.decrypt(toBeDecrypted)
		return null;
	}

	private StringBuilder hashUserToken(User user) {
		String token = user.getUsername() + "@@" + user.getExpires();
		byte[] userBytes = fromBase64(token);
		byte[] hash = createHmac(userBytes);
		final StringBuilder sb = new StringBuilder(170);
		sb.append(token);
		sb.append(SEPARATOR);
		sb.append(toBase64(hash));
		return sb;
	}

	private User fromJSON(final byte[] userBytes) {
		try {
			return new ObjectMapper().readValue(new ByteArrayInputStream(userBytes), User.class);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private byte[] toJSON(User user) {
		try {
			return new ObjectMapper().writeValueAsBytes(user);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	private String toBase64(byte[] content) {
		return DatatypeConverter.printBase64Binary(content);
	}

	private byte[] fromBase64(String content) {
		return DatatypeConverter.parseBase64Binary(content);
	}

	// synchronized to guard internal hmac object
	private synchronized byte[] createHmac(byte[] content) {
		return hmac.doFinal(content);
	}

	/*
	 * public static void main(String[] args) { Date start = new Date(); byte[]
	 * secret = new byte[70]; new
	 * java.security.SecureRandom().nextBytes(secret);
	 * 
	 * TokenHandler tokenHandler = new TokenHandler(secret); for (int i = 0; i <
	 * 1000; i++) { String randomUUID =
	 * java.util.UUID.randomUUID().toString().substring(0, 8); final User user =
	 * new User(randomUUID, new Date( new Date().getTime() + 10000));
	 * user.grantRole(UserRole.ADMIN); final String token =
	 * tokenHandler.createTokenForUser(user); final User parsedUser =
	 * tokenHandler.parseUserFromToken(token); if (parsedUser == null ||
	 * parsedUser.getUsername() == null) { System.out.println("error"); } }
	 * System.out.println(System.currentTimeMillis() - start.getTime()); }
	 */
}