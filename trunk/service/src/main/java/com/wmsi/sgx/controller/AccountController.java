package com.wmsi.sgx.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wiz.enets2.transaction.umapi.CreditMerchant;
import com.wiz.enets2.transaction.umapi.Merchant;
import com.wiz.enets2.transaction.umapi.data.CreditTxnReq;
import com.wiz.enets2.transaction.umapi.data.TxnReq;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.Response;
import com.wmsi.sgx.model.UpdateAccountModel;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.model.account.ErrorCode;
import com.wmsi.sgx.model.account.PasswordChangeModel;
import com.wmsi.sgx.model.account.UserModel;
import com.wmsi.sgx.repository.UserRepository;
import com.wmsi.sgx.security.SecureTokenGenerator;
import com.wmsi.sgx.security.UserDetailsWrapper;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.PremiumVerificationService;
import com.wmsi.sgx.service.account.RegistrationService;
import com.wmsi.sgx.service.account.UserExistsException;
import com.wmsi.sgx.service.account.UserNotFoundException;

@RestController
@RequestMapping("/account")
public class AccountController{

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
	
	//@Autowired
	//private AccountPurchaseService accountPurchaseService;	
	
	
	@RequestMapping(value = "info", method = RequestMethod.POST)
	public @ResponseBody AccountModel account(@AuthenticationPrincipal UserDetailsWrapper user) throws UserExistsException{
		
		return accountService.getAccountForUsername(user.getUsername());
	}

	@RequestMapping(value = "password", method = RequestMethod.POST)
	public @ResponseBody Boolean changePassword(@AuthenticationPrincipal UserDetailsWrapper user, @Valid @RequestBody PasswordChangeModel pass) throws UserNotFoundException, MessagingException{
	
		String username = user.getUsername();
		
		UserModel dto = new UserModel();
		dto.setEmail(username);
		dto.setPassword(pass.getPassword());
		dto.setPasswordMatch(pass.getPasswordMatch());
		dto.setContactOptIn(user.getUser().getContactOptIn());
		return registrationService.changePassword(dto);
	}
	
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public @ResponseBody AccountModel updateAccount(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody UpdateAccountModel dto) throws UserNotFoundException{
		
		String username = user.getUsername();
		dto.setEmail(username);
		return accountService.updateAccount(dto);
		
	}
	
	@RequestMapping(value = "premiumMessage", method = RequestMethod.POST, produces="application/json")
	public Response getMessage(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody UpdateAccountModel dto) throws UserNotFoundException, UnsupportedEncodingException {
		
		String username = user.getUsername();
		User usr = userRepository.findByUsername(username);
		String token = premiumService.createPremiumToken(usr);
		
		Response response = new Response();
		response.setMessage(formMessage(token));
		return response;
	}
	
	@RequestMapping(value = "errorCode", method = RequestMethod.POST, produces="application/json")
	public Response getErrorCode(@RequestBody ErrorCode errorCode) throws UnsupportedEncodingException{
		String error = errorCode.getErrorCode();
		String msg =  null;
		
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
	
}
