package com.wmsi.sgx.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wiz.enets2.transaction.umapi.CreditMerchant;
import com.wiz.enets2.transaction.umapi.Merchant;
import com.wiz.enets2.transaction.umapi.data.CreditTxnReq;
import com.wiz.enets2.transaction.umapi.data.TxnReq;
import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.Response;
import com.wmsi.sgx.model.UpdateAccountModel;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.account.ErrorCode;
import com.wmsi.sgx.model.account.PasswordChangeModel;
import com.wmsi.sgx.model.account.UserModel;
import com.wmsi.sgx.repository.UserRepository;
import com.wmsi.sgx.security.SecureTokenGenerator;
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.security.token.TokenHandler;
import com.wmsi.sgx.service.RSAKeyException;
import com.wmsi.sgx.service.RSAKeyService;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.PremiumVerificationService;
import com.wmsi.sgx.service.account.RegistrationService;
import com.wmsi.sgx.service.account.UserExistsException;
import com.wmsi.sgx.service.account.UserNotFoundException;

/**
 * The AccountController class is used for performing user Account operations
 * 
 */
@RestController
@RequestMapping("/account")
public class AccountController{

	private static final Logger log = LoggerFactory.getLogger(AccountController.class);
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private SecureTokenGenerator tokenGenerator;
	
	@Autowired 
	private RegistrationService registrationService;
	
	@Autowired
	private PremiumVerificationService premiumService;
	
	@Autowired 
	private UserRepository userRepository;
	
	@Value ("${enets.merchant.id}")
	public String merchantId;
	
	@Value ("${enets.sales.amount}")
	public String salesAmount;
	
	@Value ("${enets.success.endpoint}")
	public String success;
	@Value ("${enets.fail.endpoint}")
	public String fail;
	@Value ("${enets.cancel.endpoint}")
	public String cancel;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Autowired
	private RSAKeyService rsaKeyService;
	
	@Autowired
	private LocalValidatorFactoryBean validator;
	
	
	/**
	 * Retrieves the account information based on the X-AUTH-TOKEN sent in the
	 * request header
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return AccountModel If user account exists
	 * @throws UserExistsException
	 *             if user account doesn't exists
	 */
	@RequestMapping(value = "info", method = RequestMethod.POST)
	public @ResponseBody AccountModel account(HttpServletRequest request) throws UserExistsException{
		AccountModel ret= new AccountModel();
		if(request.getHeader("X-AUTH-TOKEN")== null){
			ret.setReason("Full authentication is required to access this resource");
			return ret;
		}
		if(findUserFromToken(request) != null){
			ret = accountService.getAccountForUsername(findUserFromToken(request).getUsername());
			ret.setToken(request.getHeader("X-AUTH-TOKEN"));
		
		if(ret.getType() == AccountType.MASTER || ret.getType() == AccountType.ADMIN)
			ret.setType(AccountType.PREMIUM);
			return ret;
		}
		ret.setReason("Authentication token not Valid");
		return ret;
	}

	/**
	 * Changes the account password
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param pass
	 *            PasswordChangeModel which contains the password and password
	 *            match
	 * @param result
	 *            If any validation errors in the details provided
	 * @return {@code true} if password change is success
	 * @throws UserNotFoundException
	 *             If user account not exists
	 * @throws MessagingException
	 *             If validation fails
	 * @throws RSAKeyException
	 *             If decryption fails
	 */
	@RequestMapping(value = "password", method = RequestMethod.POST)
	public @ResponseBody Boolean changePassword(HttpServletRequest request, @RequestBody PasswordChangeModel pass, BindingResult result) throws UserNotFoundException, MessagingException, RSAKeyException{
		decryptPasswordChangeModelParams(pass);
		validator.validate(pass, result);
		User user = findUserFromToken(request);
		String username = user.getUsername();
		
		UserModel dto = new UserModel();
		dto.setEmail(username);
		dto.setPassword(pass.getPassword());
		dto.setPasswordMatch(pass.getPasswordMatch());
		dto.setContactOptIn(user.getContactOptIn());
		return registrationService.changePassword(dto);
	}
	
	/**
	 * Updates the account details provided in UpdateAccountModel
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param dto
	 *            UpdateAccountModel
	 * @return AccountModel If user exists
	 * @throws UserNotFoundException
	 */
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public @ResponseBody AccountModel updateAccount(HttpServletRequest request, @RequestBody UpdateAccountModel dto) throws UserNotFoundException{
		User user = findUserFromToken(request);
		String username = user.getUsername();
		dto.setEmail(username);
		return accountService.updateAccount(dto,user.getId());
		
	}
	
	/**
	 * Creates a Premium token for the user which will be used during the
	 * Purchase
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param dto
	 *            UpdateAccountModel
	 * @return Response Premium token
	 * @throws UserNotFoundException
	 *             If user doesn't exists
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "premiumMessage", method = RequestMethod.POST, produces="application/json")
	public Response getMessage(HttpServletRequest request, @RequestBody UpdateAccountModel dto) throws UserNotFoundException, UnsupportedEncodingException {
		User user = findUserFromToken(request);
		String username = user.getUsername();
		User usr = userRepository.findByUsername(username);
		String token = premiumService.createPremiumToken(usr);
		
		Response response = new Response();
		response.setMessage(formMessage(token));
		return response;
	}
	
	/**
	 * Returns the message based on the error code during the Purchase of
	 * premium account
	 * 
	 * @param errorCode ErrorCode
	 * @return Response Contains the messages based on the error code
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "errorCode", method = RequestMethod.POST, produces="application/json")
	public Response getErrorCode(@RequestBody ErrorCode errorCode) throws UnsupportedEncodingException{
		String error = errorCode.getErrorCode();
		String msg =  "null";
		
		if(error != null)
			switch(error) {
				case "0":
					msg = "Cancelled";
					break;
				case "1202":
					msg = "Credit Card Number not allowed";
					break;
				case "1203":
					msg = "Credit Card Expired";
					break;
				case "1223":
					msg = "Duplicate transaction";
					break;
				default:
					msg = "null";
			}
		
		Response response = new Response();
		response.setMessage(msg);
		
		return response;
		
	}
	
	/**
	 * Creates the text which will be used during the Purchase of Premium
	 * account
	 * 
	 * @param token
	 * @return message as String
	 */
	public String formMessage(String token) {
		TxnReq req = new TxnReq();
		CreditTxnReq credReq = new CreditTxnReq();
		req.setNetsMid(merchantId);
		req.setPaymentMode("CC");
		req.setTxnAmount(salesAmount);
		req.setCurrencyCode("SGD");
		req.setMerchantTxnRef(token);
		req.setSubmissionMode("B");
		req.setMerchantCertId("1");
		req.setSuccessUrl(success);
		req.setSuccessUrlParams("");
		req.setFailureUrl(fail);
		req.setFailureUrlParams("");
		
		credReq.setPaymentType("SALE");
		credReq.setCancelUrl(cancel);

		req.setCreditTxnReq(credReq);		
		
		CreditMerchant m = (CreditMerchant) Merchant.getInstance (Merchant.MERCHANT_TYPE_CREDIT);
		
		String sMsg = m.formPayReq(req);
		return sMsg;
	}
	
	/**
	 * Retrieves the User from the X-AUTH-TOKEN provided in the
	 * HttpServletRequest header
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return User
	 */
	public User findUserFromToken(HttpServletRequest request){
		String token = request.getHeader("X-AUTH-TOKEN");
		
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		User user = null;
		if(token != null)
		 return user = tokenHandler.parseUserFromToken(token);
		return null;
	}
	
	/**
	 * Decrypts the {@link UserModel} params
	 * 
	 * @param user
	 * @throws RSAKeyException 
	 */
	private void decryptPasswordChangeModelParams(PasswordChangeModel user) throws RSAKeyException {
		try {
			user.setPassword(rsaKeyService.decrypt(user.getPassword()));
			user.setPasswordMatch(rsaKeyService.decrypt(user.getPasswordMatch()));
		} catch (RSAKeyException e) {
			log.error("Error in decrypting the UserModel");
			throw new RSAKeyException("Email, password or passwordmatch is not valid");
		}
	}
}
