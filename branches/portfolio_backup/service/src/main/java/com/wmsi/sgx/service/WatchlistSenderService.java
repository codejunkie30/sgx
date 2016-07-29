package com.wmsi.sgx.service;

import java.util.List;

import javax.mail.MessagingException;

import com.wmsi.sgx.model.AlertOption;
import com.wmsi.sgx.model.WatchlistModel;
import com.wmsi.sgx.model.search.CompanyPrice;

public interface WatchlistSenderService{

	String send(String to, String subject,
			List<AlertOption> list,
			WatchlistModel watchlist, List<CompanyPrice> companyPrices)
			throws MessagingException;

}