--Company Level Estimates
select
	pop.tickerSymbol,
	pop.exchangeSymbol,
	case
		when ed.dataItemId=100180 then 'revenue'
		when ed.dataItemId=100215 then 'ebit' --(no EBITDA for estimates from mockup)
		when ed.dataItemId=100229 then 'ebt'
		when ed.dataItemId=100250 then 'netIncomeExcl'
		when ed.dataItemId=100264 then 'netIncome'
	end as WMSIApi,
	convert(varchar(max),ed.dataItemValue),
	case
		when ep.periodTypeId = 1 then 'FY'+cast(ep.fiscalYear as varchar(max))
		when ep.periodTypeId = 2 then 'FQ'+cast(ep.fiscalQuarter as varchar(max))+cast(ep.fiscalYear as varchar(max))
	end as period,
	ep.periodEndDate as date,
	cISO.ISOCode
from ciqEstimatePeriod ep
	join ciqEstimateConsensus ec on ep.estimatePeriodId=ec.estimatePeriodId
	join ciqEstimateNumericData ed on ec.estimateConsensusId=ed.estimateConsensusId
	join ##sgxpop pop on ep.companyId=pop.companyId
	left join ciqCurrency cISO on ed.currencyId=cISO.currencyId
	join (
		--Check for 3 Analysts or more
		select er.estimateConsensusId, er.dataItemId
		from ciqEstimateRevision er
			join (
			select max(asofdate) as maxDate, estimateConsensusId, dataItemId, estimateRevisionTypeId
			from ciqEstimateRevision
			where estimateConsensusId in (
				select estimateConsensusId
				from ciqEstimateConsensus ec
					join ciqEstimatePeriod ep on ec.estimatePeriodID=ep.estimatePeriodId
					join ##sgxpop pop on ep.companyId=pop.companyId
					where advanceDate is null
				)
			and estimateRevisionTypeId=4
			group by estimateConsensusId, dataItemId, estimateRevisionTypeId
			) mdate on er.estimateConsensusId=mdate.estimateConsensusId
				and er.dataItemId=mdate.dataItemId
				and er.estimateRevisionTypeId=mdate.estimateRevisionTypeId
				and er.asOfDate=mdate.maxDate
		where numAnalysts > 2
			) acheck on ed.estimateConsensusId=acheck.estimateConsensusId and ed.dataItemId=acheck.dataItemId
where ed.toDate > getDate()
	and ed.dataItemId in (
		100180 --revenue
		,100215 --ebit (no EBITDA for estimates from mockup)
		,100229 --ebt
		,100250 --netIncomeExcl
		,100264 --netIncome
		)
	and ep.periodTypeId in (1,2)
	and ep.advanceDate is null
union

--Issue-level Estimates
select
	pop.tickerSymbol,
	pop.exchangeSymbol,
	case
		when ed.dataItemId=100173 then 'normalizedEps'
		when ed.dataItemId=100278 then 'eps'
	end as WMSIApi,
	convert(varchar(max),ed.dataItemValue),
	case
		when ep.periodTypeId = 1 then 'FY'+cast(ep.fiscalYear as varchar(max))
		when ep.periodTypeId = 2 then 'FQ'+cast(ep.fiscalQuarter as varchar(max))+cast(ep.fiscalYear as varchar(max))
	end as period,
	ep.periodEndDate as date,
	cISO.ISOCode
from ciqEstimatePeriod ep
	join ciqEstimateConsensus ec on ep.estimatePeriodId=ec.estimatePeriodId
	join ciqEstimateNumericData ed on ec.estimateConsensusId=ed.estimateConsensusId
	join ##sgxpop pop on ep.companyid=pop.companyid
	left join ciqCurrency cISO on ed.currencyId=cISO.currencyId
	join (
		--Check for 3 Analysts or more
		select er.estimateConsensusId, er.dataItemId
		from ciqEstimateRevision er
			join (
			select max(asofdate) as maxDate, estimateConsensusId, dataItemId, estimateRevisionTypeId
			from ciqEstimateRevision
			where estimateConsensusId in (
				select estimateConsensusId
				from ciqEstimateConsensus ec
					join ciqEstimatePeriod ep on ec.estimatePeriodID=ep.estimatePeriodId
					join ##sgxpop pop on ep.companyId=pop.companyId
					where advanceDate is null
				)
			and estimateRevisionTypeId=4
			group by estimateConsensusId, dataItemId, estimateRevisionTypeId
			) mdate on er.estimateConsensusId=mdate.estimateConsensusId
				and er.dataItemId=mdate.dataItemId
				and er.estimateRevisionTypeId=mdate.estimateRevisionTypeId
				and er.asOfDate=mdate.maxDate
		where numAnalysts > 2
			) acheck on ed.estimateConsensusId=acheck.estimateConsensusId and ed.dataItemId=acheck.dataItemId
where ed.toDate > getDate()
	and ed.dataItemId in (
		100173 --normalizedEPS
		,100278 --eps
		)
	and ep.periodTypeId in (1,2)
	and ep.advanceDate is null
	and ec.tradingItemId in (
		select tradingitemid
		from ciqTradingItem ti
		join ciqSecurity sec on ti.securityId=sec.securityId
		where ti.primaryFlag=1
		and sec.primaryFlag=1
		and sec.companyId  in (select companyid from ##sgxpop)
		)
union

--Price Target and Consensus Rec
select
       pop.tickerSymbol,
       pop.exchangeSymbol,
       case
             when ed.dataItemId = 100160 then 'avgBrokerReq'
             when ed.dataItemId = 100161 then 'targetPrice'
             when ed.dataItemId = 100167 then 'ltgMeanEstimate'
             when ed.dataItemId = 114362 then 'industryRec'
       end as WMSIApi,
       convert(varchar(max),ed.dataItemValue) as dataItemId,
       null as period,
       ep.periodEndDate as date,
       cISO.ISOCode
from ciqEstimatePeriod ep
       join ciqEstimateConsensus ec on ep.estimatePeriodId=ec.estimatePeriodId
       join ciqEstimateNumericData ed on ec.estimateConsensusId=ed.estimateConsensusId
       join ##sgxpop pop on ep.companyid=pop.companyid
       left join ciqCurrency cISO on ed.currencyId=cISO.currencyId
       join (
             --Check for 3 Analysts or more
             select er.estimateConsensusId, er.dataItemId
             from ciqEstimateRevision er
                    join (
                    select max(asofdate) as maxDate, estimateConsensusId, dataItemId, estimateRevisionTypeId
                    from ciqEstimateRevision
                    where estimateConsensusId in (
                           select estimateConsensusId
                           from ciqEstimateConsensus ec
                                 join ciqEstimatePeriod ep on ec.estimatePeriodID=ep.estimatePeriodId
                                 join ##sgxpop pop on ep.companyId=pop.companyId
                                 where advanceDate is null
                           )
                    and estimateRevisionTypeId=4
                    group by estimateConsensusId, dataItemId, estimateRevisionTypeId
                    ) mdate on er.estimateConsensusId=mdate.estimateConsensusId
                           and er.dataItemId=mdate.dataItemId
                           and er.estimateRevisionTypeId=mdate.estimateRevisionTypeId
                           and er.asOfDate=mdate.maxDate
             where numAnalysts > 2
                    ) acheck on ed.estimateConsensusId=acheck.estimateConsensusId and ed.dataItemId=acheck.dataItemId
where ed.toDate > getdate() and 
       ed.dataItemId in (
             100160 --avgBrokerReq
             ,100161      --TP meanEstimate
             ,100167      --LTG meanEstimate
             ,114362 --Industry Recommendation
       )
	and ec.tradingItemId in (
		select tradingitemid
		from ciqTradingItem ti
		join ciqSecurity sec on ti.securityId=sec.securityId
		where ti.primaryFlag=1
		and sec.primaryFlag=1
		and sec.companyId  in (select companyid from ##sgxpop)
		)
union

select
       pop.tickerSymbol,
       pop.exchangeSymbol,
       case
             when ed.dataItemId = 100162 then 'tpMedianEstimate'
             when ed.dataItemId = 100163 then 'tpHighEstimate'
             when ed.dataItemId = 100164 then 'tpLowEstimate'
             when ed.dataItemId = 100165 then 'tpEstimateNum'
             when ed.dataItemId = 100166 then 'tpEstimateDeviation'
       end as WMSIApi,
       convert(varchar(max),ed.dataItemValue) as dataItemId,
       null as period,
       ep.periodEndDate as date,
       cISO.ISOCode
from ciqEstimatePeriod ep
       join ciqEstimateConsensus ec on ep.estimatePeriodId=ec.estimatePeriodId
       join ciqEstimateNumericData ed on ec.estimateConsensusId=ed.estimateConsensusId
       join ##sgxpop pop on ep.companyId=pop.companyId
       left join ciqCurrency cISO on ed.currencyId=cISO.currencyId
       join (
             --Check for 3 Analysts or more
             select er.estimateConsensusId, er.dataItemId
             from ciqEstimateRevision er
                    join (
                    select max(asofdate) as maxDate, estimateConsensusId, dataItemId, estimateRevisionTypeId
                    from ciqEstimateRevision
                    where estimateConsensusId in (
                           select estimateConsensusId
                           from ciqEstimateConsensus ec
                                 join ciqEstimatePeriod ep on ec.estimatePeriodID=ep.estimatePeriodId
                                 join ##sgxpop pop on ep.companyId=pop.companyId
                                 where advanceDate is null
                           )
                    and estimateRevisionTypeId=4
                    group by estimateConsensusId, dataItemId, estimateRevisionTypeId
                    ) mdate on er.estimateConsensusId=mdate.estimateConsensusId
                           and er.dataItemId=mdate.dataItemId
                           and er.estimateRevisionTypeId=mdate.estimateRevisionTypeId
                           and er.asOfDate=mdate.maxDate
             where numAnalysts > 2
                    and er.dataItemId=100161 --Only Mean carries numAnalysts
                    ) acheck on ed.estimateConsensusId=acheck.estimateConsensusId
where ed.toDate > getdate() and 
       ed.dataItemId in (
             100162 --TP medianEstimate
             ,100163      --TP highEstimate
             ,100164      --TP lowEstimate
             ,100165      --TP estimatesNum
             ,100166      --TP estimateDeviation
       )
	and ec.tradingItemId in (
		select tradingitemid
		from ciqTradingItem ti
		join ciqSecurity sec on ti.securityId=sec.securityId
		where ti.primaryFlag=1
		and sec.primaryFlag=1
		and sec.companyId  in (select companyid from ##sgxpop)
		)
union

select
       pop.tickerSymbol,
       pop.exchangeSymbol,
       case
             when ed.dataItemId = 100168 then 'ltgMedianEstimate'
             when ed.dataItemId = 100169 then 'ltgHighEstimate'
             when ed.dataItemId = 100170 then 'ltgLowEstimate'
             when ed.dataItemId = 100171 then 'ltgEstimateNum'
             when ed.dataItemId = 100172 then 'ltgEstimateDeviation'
       end as WMSIApi,
       convert(varchar(max),ed.dataItemValue) as dataItemId,
       null as period,
       ep.periodEndDate as date,
       cISO.ISOCode
from ciqEstimatePeriod ep
       join ciqEstimateConsensus ec on ep.estimatePeriodId=ec.estimatePeriodId
       join ciqEstimateNumericData ed on ec.estimateConsensusId=ed.estimateConsensusId
       join ##sgxpop pop on ep.companyid=pop.companyid
       left join ciqCurrency cISO on ed.currencyId=cISO.currencyId
       join (
             --Check for 3 Analysts or more
             select er.estimateConsensusId, er.dataItemId, NumAnalysts
             from ciqEstimateRevision er
                    join (
                    select max(asofdate) as maxDate, estimateConsensusId, dataItemId, estimateRevisionTypeId
                    from ciqEstimateRevision
                    where estimateConsensusId in (
                           select estimateConsensusId
                           from ciqEstimateConsensus ec
                                 join ciqEstimatePeriod ep on ec.estimatePeriodID=ep.estimatePeriodId
                                 join ##sgxpop pop on ep.companyId=pop.companyId
                                 where advanceDate is null
                           )
                    and estimateRevisionTypeId=4
                    group by estimateConsensusId, dataItemId, estimateRevisionTypeId
                    ) mdate on er.estimateConsensusId=mdate.estimateConsensusId
                           and er.dataItemId=mdate.dataItemId
                           and er.estimateRevisionTypeId=mdate.estimateRevisionTypeId
                           and er.asOfDate=mdate.maxDate
             where numAnalysts > 2
                    and er.dataItemId=100167 --Only Mean carries numAnalysts
                    ) acheck on ed.estimateConsensusId=acheck.estimateConsensusId
where ed.toDate > getdate() and 
       ed.dataItemId in (
             100168 --LTG medianEstimate
             ,100169 --LTG highEstimate
             ,100170      --LTG lowEstimate
             ,100171      --LTG estimatesNum
             ,100172      --LTG estimateDeviation
       )
	and ec.tradingItemId in (
		select tradingitemid
		from ciqTradingItem ti
		join ciqSecurity sec on ti.securityId=sec.securityId
		where ti.primaryFlag=1
		and sec.primaryFlag=1
		and sec.companyId  in (select companyid from ##sgxpop)
		)

union
select
	pop.tickerSymbol,
	pop.exchangeSymbol,
	case
	when ed.dataItemId = 114362 then 'industryRec'
	end as WMSIApi,
	convert(varchar(max),ed.dataItemValue) as dataItemId,
	case
		when ep.periodTypeId = 1 then 'FY'+cast(ep.fiscalYear as varchar(max))
		when ep.periodTypeId = 2 then 'FQ'+cast(ep.fiscalQuarter as varchar(max))+cast(ep.fiscalYear as varchar(max))
	end as period,
	ep.periodEndDate as date,
	cISO.ISOCode
from ##sgxpop pop
	join ciqEstimatePeriod ep on ep.companyid=pop.companyId
	join ciqEstimateConsensus ec on ep.estimatePeriodId=ec.estimatePeriodId
	join ciqEstimateNumericData ed on ec.estimateConsensusId=ed.estimateConsensusId
	left join ciqCurrency cISO on ed.currencyId=cISO.currencyId
	where ed.toDate > getdate()
	and ed.dataItemId in (114362) --Industry Recommendation
union
--Company Level Actuals from Estimates
select
	pop.tickerSymbol,
	pop.exchangeSymbol,
	case
		when ed.dataItemId=100186 then 'revenueActual'
		when ed.dataItemId=100221 then 'ebitActual' --(no EBITDA for estimates from mockup)
		when ed.dataItemId=100235 then 'ebtActual'
		when ed.dataItemId=100256 then 'netIncomeExclActual'
		when ed.dataItemId=100270 then 'netIncomeActual'
		end as WMSIApi,
	convert(varchar(max),ed.dataItemValue),
	case
		when ep.periodTypeId = 1 then 'FY'+cast(ep.fiscalYear as varchar(max))
		when ep.periodTypeId = 2 then 'FQ'+cast(ep.fiscalQuarter as varchar(max))+cast(ep.fiscalYear as varchar(max))
	end as period,
	null as date,
	cISO.ISOCode
from ciqEstimatePeriod ep
	join ciqEstimateConsensus ec on ep.estimatePeriodId=ec.estimatePeriodId
	join ciqEstimateNumericData ed on ec.estimateConsensusId=ed.estimateConsensusId
	join ciqEstimatePeriodRelConst rc on ep.estimatePeriodId=rc.estimatePeriodId and ep.periodtypeid=rc.periodtypeid
	join ##sgxpop pop on ep.companyId=pop.companyId
	left join ciqCurrency cISO on ed.currencyId=cISO.currencyId
where ed.toDate > getDate()
	and ed.dataItemId in (
		100186, --Revenue Actual
		100221, --EBIT Actual
		100235, --EBT Normalized Actual
		100256, --Net Income Normalized Actual
		100270 --Net Income (GAAP) Actual
	)
	and ep.periodTypeId in (1,2)
	and rc.relativeConstant in (500,1000)
union
--Issue Level Actuals
--Company Level Actuals from Estimates
select
	pop.tickerSymbol,
	pop.exchangeSymbol,
	case
		when ed.dataItemId=100284 then 'epsActual'
		when ed.dataItemId=100179 then 'normalizedEpsActual'
		end as WMSIApi,
	convert(varchar(max),ed.dataItemValue),
	case
		when ep.periodTypeId = 1 then 'FY'+cast(ep.fiscalYear as varchar(max))
		when ep.periodTypeId = 2 then 'FQ'+cast(ep.fiscalQuarter as varchar(max))+cast(ep.fiscalYear as varchar(max))
	end as period,
	null as date,
	cISO.ISOCode
from ciqEstimatePeriod ep
	join ciqEstimateConsensus ec on ep.estimatePeriodId=ec.estimatePeriodId
	join ciqEstimateNumericData ed on ec.estimateConsensusId=ed.estimateConsensusId
	join ciqEstimatePeriodRelConst rc on ep.estimatePeriodId=rc.estimatePeriodId and ep.periodtypeid=rc.periodtypeid
	join ##sgxpop pop on ep.companyId=pop.companyId
	left join ciqCurrency cISO on ed.currencyId=cISO.currencyId
where ed.toDate > getDate()
	and ed.dataItemId in (
		100179, --EPS Normalized Actual
		100284 --EPS (GAAP) Actual
	)
	and ep.periodTypeId in (1,2)
	and rc.relativeConstant in (500,1000)
	and ec.tradingItemId in (
		select tradingitemid
		from ciqTradingItem ti
		join ciqSecurity sec on ti.securityId=sec.securityId
		where ti.primaryFlag=1
		and sec.primaryFlag=1
		and sec.companyId  in (select companyid from ##sgxpop)
		)