package com.wmsi.sgx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.wiz.enets2.transaction.umapi.CreditMerchant;
import com.wiz.enets2.transaction.umapi.Merchant;
import com.wiz.enets2.transaction.umapi.data.TxnRes;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.Response;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.PremiumVerificationException;
import com.wmsi.sgx.service.account.PremiumVerificationService;
import com.wmsi.sgx.service.account.VerifiedPremiumException;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {
	
	private static final Logger log = LoggerFactory.getLogger(PurchaseController.class);
	
	@Value ("${enets.success.redirect}")
	public String successUrl;
	
	@Value ("${enets.fail.redirect}")
	public String failUrl;
	
	@Value ("${enets.cancel.redirect}")
	public String cancelUrl;
	
	@Autowired
	PremiumVerificationService premiumService;
	
	@Autowired
	AccountService accountService;
	
	@RequestMapping(value = "success", method = RequestMethod.POST)
	public RedirectView success(@ModelAttribute("response") Response response, Model model) throws PremiumVerificationException, VerifiedPremiumException{		
				
		TxnRes res = unpack(response);					
		String token = res.getMerchantTxnRef();		
		User usr = premiumService.verifyPremiumToken(token);
		accountService.createPremiumAccount(usr);
		
		RedirectView view = new RedirectView();
		view.setUrl(successUrl);
		return view;
	}
	
	@RequestMapping(value = "fail", method = RequestMethod.POST)
	public RedirectView fail(@ModelAttribute("response") Response response, Model model){
		
		TxnRes res = unpack(response);
		model.addAttribute("ec", res.getNetsTxnRespCode().replace("-", "") );
		
		RedirectView view = new RedirectView();
		view.setUrl(failUrl);
		return view;
	}
	
	@RequestMapping(value = "cancel", method = RequestMethod.POST)
	public RedirectView cancel(@ModelAttribute("response") Response response, Model model){
		model.addAttribute("ec", "0");
		RedirectView view = new RedirectView();
		view.setUrl(cancelUrl);
		return view;
	}
	
	public TxnRes unpack(Response response){
		CreditMerchant m = (CreditMerchant) Merchant.getInstance(Merchant.MERCHANT_TYPE_CREDIT);
		TxnRes res = m.unpackResponse(response.getMessage());		
		return res;
		
	}

}
