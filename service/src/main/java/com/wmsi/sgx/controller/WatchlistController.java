package com.wmsi.sgx.controller;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.Response;
import com.wmsi.sgx.model.WatchlistAddCompany;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.model.WatchlistRenameModel;
import com.wmsi.sgx.repository.UserRepository;
import com.wmsi.sgx.repository.WatchlistRepository;
import com.wmsi.sgx.security.UserDetailsWrapper;
import com.wmsi.sgx.service.CompanyServiceException;
import com.wmsi.sgx.service.account.QuanthouseServiceException;
import com.wmsi.sgx.service.account.WatchlistEmailService;
import com.wmsi.sgx.service.account.WatchlistService;
import com.wmsi.sgx.service.search.SearchServiceException;

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
	
	@RequestMapping(value="watchlist/sendEmail")
	public void sendEmail(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody Response response) throws QuanthouseServiceException, CompanyServiceException, SearchServiceException, MessagingException{
		User usr = userRepository.findByUsername(user.getUsername());
		emailService.getEmailsForUser(usr);
	}
	
	@RequestMapping(value = "watchlist/create")
	public List<WatchlistModel> createWatchlist(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody Response response){
		User usr = userRepository.findByUsername(user.getUsername());		
		String watchlistName = response.getMessage();
		
		return watchlistService.createWatchlist(usr, watchlistName);		
	}
	
	@RequestMapping(value = "watchlist/delete")
	public List<WatchlistModel> deleteWatchlist(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody Response response){
		User usr = userRepository.findByUsername(user.getUsername());		
		String id = response.getMessage();
		watchlistService.deleteWatchlist(usr, id);		
		
		return watchlistService.getWatchlist(usr);
	}
	@RequestMapping(value = "watchlist/get")
	public List<WatchlistModel> getAllWatchList(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody Response response){
		User usr = userRepository.findByUsername(user.getUsername());
		
		return watchlistService.getWatchlist(usr);		
	}
	
	@RequestMapping(value = "watchlist/edit")
	public List<WatchlistModel> editWatchList(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody WatchlistModel model){
		User usr = userRepository.findByUsername(user.getUsername());
		watchlistService.editWatchlist(usr, model);
		
		return watchlistService.getWatchlist(usr);		
	}
	
	@RequestMapping(value = "watchlist/rename")
	public List<WatchlistModel> renameWatchList(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody WatchlistRenameModel model){
		User usr = userRepository.findByUsername(user.getUsername());
		String name = model.getWatchlistName();
		String id = model.getId();
		watchlistService.renameWatchlist(usr, name, id );
		
		return watchlistService.getWatchlist(usr);
	}	
	
	@RequestMapping(value = "watchlist/addCompanies")
	public Response addCompany(@AuthenticationPrincipal UserDetailsWrapper user, @RequestBody WatchlistAddCompany model){
		User usr = userRepository.findByUsername(user.getUsername());
		String id = model.getId();
		List<String> companies = model.getCompanies();
		
		return watchlistService.addCompanies(usr, id, companies);
		
	}
}
