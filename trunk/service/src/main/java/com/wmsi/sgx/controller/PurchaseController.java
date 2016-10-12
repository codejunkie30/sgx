package com.wmsi.sgx.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

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
import com.wmsi.sgx.domain.CustomAuditorAware;
import com.wmsi.sgx.domain.EnetsTransactionDetails;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.Response;
import com.wmsi.sgx.repository.EnetsRepository;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.PremiumVerificationException;
import com.wmsi.sgx.service.account.PremiumVerificationService;
import com.wmsi.sgx.service.account.VerifiedPremiumException;

/**
 * 
 * This controller is used for making purchase related operations.
 *
 */
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
	EnetsRepository enetsRepository;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	private CustomAuditorAware<User> auditorProvider;
	
	/**
	 * Places enets transactions and then returning back to SGX.
	 * 
	 * @param response
	 * @param model
	 * @throws PremiumVerificationException
	 * @throws VerifiedPremiumException
	 * @return RedirectView
	 */
	@RequestMapping(value = "success", method = RequestMethod.POST)
	public RedirectView success(@ModelAttribute("response") Response response, Model model) throws PremiumVerificationException, VerifiedPremiumException{		
				
		TxnRes res = unpack(response);					
		String token = res.getMerchantTxnRef();		
		User usr = premiumService.verifyPremiumToken(token);
		auditorProvider.setUser(usr);
		accountService.createPremiumAccount(usr);
		
		EnetsTransactionDetails enets = new EnetsTransactionDetails();
		enets.setTrans_dt(new Date());
		enets.setTrans_id(res.getMerchantTxnRef());
		enets.setUser(usr);
		enets.setActive(true);
		
		enetsRepository.save(enets);
		RedirectView view = new RedirectView();
		view.setUrl(successUrl);
		return view;
	}
	
	/**
	 * Redirects when any enets transaction gets failed and redirected to SGX.
	 * 
	 * @param response
	 * @param model
	 * @return RedirectView
	 */
	@RequestMapping(value = "fail", method = RequestMethod.POST)
	public RedirectView fail(@ModelAttribute("response") Response response, Model model){
		
		TxnRes res = unpack(response);
		model.addAttribute("ec", res.getNetsTxnRespCode().replace("-", "") );
		
		RedirectView view = new RedirectView();
		view.setUrl(failUrl);
		return view;
	}
	
	/**
	 * Cancels the enets transaction and returning back to SGX.
	 * 
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "cancel", method = RequestMethod.POST)
	public RedirectView cancel(@ModelAttribute("response") Response response, Model model){
		model.addAttribute("ec", "0");
		RedirectView view = new RedirectView();
		view.setUrl(cancelUrl);
		return view;
	}
	
	/**
	 * Posts the enets info.
	 * 
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "post", method = RequestMethod.POST)
	public void post(@ModelAttribute("response") Response response) throws IOException{
		File file = new File("/mnt/data/purchase_logging.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		TxnRes res = unpack(response);
		
		String content = "Reference: " + res.getMerchantTxnRef()
				+ " | ENets Reference " + res.getNetsTxnRef()
				+ " | Status:  " + res.getNetsTxnStatus() 
				+ " | Response Code: " + res.getNetsTxnRespCode() 
				+ " | Response Message: " + res.getNetsTxnMsg()
				+ " | Time: " + res.getNetsTxnDtm() + "\n\n";				
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
	}
	
	/**
	 * Unpacks the Credit info.
	 * 
	 * @param response
	 * @return TxnRes
	 */
	public TxnRes unpack(Response response){
		CreditMerchant m = (CreditMerchant) Merchant.getInstance(Merchant.MERCHANT_TYPE_CREDIT);
		TxnRes res = m.unpackResponse(response.getMessage());		
		return res;
		
	}

}
