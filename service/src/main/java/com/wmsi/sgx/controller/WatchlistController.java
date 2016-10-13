package com.wmsi.sgx.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.domain.CustomAuditorAware;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.CompanyWatchlistTransactionHistoryModel;
import com.wmsi.sgx.model.Response;
import com.wmsi.sgx.model.WatchlistAddCompany;
import com.wmsi.sgx.model.WatchlistAddTransaction;
import com.wmsi.sgx.model.WatchlistDeleteTransaction;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.model.WatchlistRenameModel;
import com.wmsi.sgx.model.WatchlistTransactionModel;
import com.wmsi.sgx.repository.UserRepository;
import com.wmsi.sgx.repository.WatchlistRepository;
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.security.token.TokenHandler;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.account.QuanthouseServiceException;
import com.wmsi.sgx.service.account.WatchlistEmailService;
import com.wmsi.sgx.service.account.WatchlistService;
import com.wmsi.sgx.service.search.SearchServiceException;

/**
 * This controller is used for performimg various actions on user watchlists.
 * 
 *
 */
@RestController
@RequestMapping(method = RequestMethod.POST, produces="application/json" )
public class WatchlistController {
	
	@Autowired
	private WatchlistService watchlistService;
	
	@Autowired
	private WatchlistRepository watchlistRepository;
	
	@Autowired 
	private UserRepository userRepository;
	
	@Autowired
	private WatchlistEmailService emailService;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Autowired
	private CustomAuditorAware<User> auditorProvider;
	
	/**
	 * Sends email after user performs any action on the watchlist.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            Response
	 * 
	 * @throws QuanthouseServiceException
	 * @throws CompanyServiceException
	 * @throws SearchServiceException
	 * @throws MessagingException
	 */
	@RequestMapping(value="watchlist/sendEmail")
	public void sendEmail(HttpServletRequest request, @RequestBody Response response) throws QuanthouseServiceException, CompanyServiceException, SearchServiceException, MessagingException{
		User usr = userRepository.findByUsername(findUserFromToken(request).getUsername());
		emailService.getEmailsForUser(usr);
	}
	
	/**
	 * Creates watchlist to the user.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            Response
	 * @return List WatchlistModel list
	 */
	@RequestMapping(value = "watchlist/create")
	public List<WatchlistModel> createWatchlist(HttpServletRequest request, @RequestBody Response response){
		User usr = userRepository.findByUsername(findUserFromToken(request).getUsername());		
		String watchlistName = response.getMessage();
		
		return watchlistService.createWatchlist(usr, watchlistName);		
	}
	
	/**
	 * Deletes the user waitchlist.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            Response
	 * @return List WatchlistModel list
	 */
	@RequestMapping(value = "watchlist/delete")
	public List<WatchlistModel> deleteWatchlist(HttpServletRequest request, @RequestBody Response response){
		User usr = userRepository.findByUsername(findUserFromToken(request).getUsername());		
		String id = response.getMessage();
		watchlistService.deleteWatchlist(usr, id);		
		
		return watchlistService.getWatchlist(usr);
	}
	
	/**
	 * Retrieves the watchlists of the user.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            Response
	 * @return Map capturing the removed ones and existing ones.
	 */
	@RequestMapping(value = "watchlist/get")
	public Map<String, Object> getAllWatchList(HttpServletRequest request, @RequestBody Response response){
		User usr = userRepository.findByUsername(findUserFromToken(request).getUsername());
		Map<String, Object> ret = new HashMap<String,Object>();
		ret.put("removed", watchlistService.cleanWatchlist(usr));
		ret.put("watchlists", watchlistService.getWatchlist(usr));		
		return ret;
	}
	
	/**
	 * Modifies the watchlists of the user.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            WatchlistModel
	 * @return List WatchlistModel list
	 */
	@RequestMapping(value = "watchlist/edit")
	public List<WatchlistModel> editWatchList(HttpServletRequest request, @RequestBody WatchlistModel model){
		User usr = userRepository.findByUsername(findUserFromToken(request).getUsername());
		watchlistService.editWatchlist(usr, model);
		
		return watchlistService.getWatchlist(usr);		
	}
	
	/**
	 * Renames the watchlist of the user.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            WatchlistModel
	 * @return List WatchlistModel list
	 */
	@RequestMapping(value = "watchlist/rename")
	public List<WatchlistModel> renameWatchList(HttpServletRequest request, @RequestBody WatchlistRenameModel model){
		User usr = userRepository.findByUsername(findUserFromToken(request).getUsername());
		String name = model.getWatchlistName();
		String id = model.getId();
		watchlistService.renameWatchlist(usr, name, id );
		
		return watchlistService.getWatchlist(usr);
	}	
	
	/**
	 * Adds companies to the user created watchlist
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            WatchlistAddCompany
	 * @return Response
	 */
	@RequestMapping(value = "watchlist/addCompanies")
	public Response addCompany(HttpServletRequest request, @RequestBody WatchlistAddCompany model){
		User usr = userRepository.findByUsername(findUserFromToken(request).getUsername());
		String id = model.getId();
		List<String> companies = model.getCompanies();
		
		return watchlistService.addCompanies(usr, id, companies);
		
	}
	
	/**
	 * Adds watchlist transactions.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            WatchlistAddCompany
	 * @return Response
	 */
	@RequestMapping(value = "watchlist/addTransaction")
	public Response addTransaction(HttpServletRequest request, @RequestBody WatchlistAddTransaction model){
		User usr = userRepository.findByUsername(findUserFromToken(request).getUsername());
		String id = model.getId();
		List<WatchlistTransactionModel> transactions = model.getTransactions();
		auditorProvider.setUser(usr);
		return watchlistService.addTransactions(usr, id, transactions);
		
	}
	
	/**
	 * Deletes the transactions that are done for the watchlist.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            WatchlistAddCompany
	 * @return Response
	 */
	@RequestMapping(value = "watchlist/deleteTransaction")
	public Response deleteTransaction(HttpServletRequest request, @RequestBody WatchlistDeleteTransaction model){
		User usr = userRepository.findByUsername(findUserFromToken(request).getUsername());
		
		return watchlistService.deleteTransactions(usr,model.getId(), model.getTransactionId());
		
	}
	
	/**
	 * Retrieves the watchlist transactions that are there for the user.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            Response
	 * @return Map
	 */
	@RequestMapping(value = "watchlist/transactions")
	public Map<String, List<WatchlistTransactionModel>> getTransactions(HttpServletRequest request, @RequestBody Response response){
		User usr = userRepository.findByUsername(findUserFromToken(request).getUsername());
		String id = response.getMessage();
		return watchlistService.getTransactions(usr,id);
		
	}
	
	/**
	 * Retrieves the transactions that are assigned to a watchlist.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            Response
	 * @return CompanyWatchlistTransactionHistoryModel
	 */
	@RequestMapping(value = "watchlist/watchlistTransactions")
	public CompanyWatchlistTransactionHistoryModel getWatchListTransactions(HttpServletRequest request, @RequestBody Response response){
		User usr = userRepository.findByUsername(findUserFromToken(request).getUsername());
		String id = response.getMessage();
		return watchlistService.getWatchListTransactions(usr,id);
		
	}
	
	/**
	 * Retrieves the user information from the X-AUTH-TOKEN.
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
}
