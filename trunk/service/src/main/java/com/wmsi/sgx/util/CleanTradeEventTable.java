package com.wmsi.sgx.util;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.repository.TradeEventRepository;

@Service
@DisallowConcurrentExecution
public class CleanTradeEventTable implements Job{
	
	@Autowired
	private TradeEventRepository tradeEventsRepository;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println(" IN there ");
		tradeEventsRepository.truncateTradeEvent();
		
	}


}
