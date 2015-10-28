package com.wmsi.sgx.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.Response;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.model.WatchlistRenameModel;
import com.wmsi.sgx.repository.UserRepository;
import com.wmsi.sgx.repository.WatchlistRepository;
import com.wmsi.sgx.security.UserDetailsWrapper;
import com.wmsi.sgx.service.account.WatchlistService;

@RestController
@RequestMapping(method = RequestMethod.POST, produces="application/json" )
public class WatchlistController {
	
	@Autowired
	private WatchlistService watchlistService;
	
	@Autowired
	private WatchlistRepository watchlistRepository;
	
	@Autowired 
	private UserRepository userRepository;
	
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
}
