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
		when ed.dataItemId=100278 then 'eps'
		when ed.dataItemId=100173 then 'normalizedEps'
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
		,100278 --eps
		,100173 --normalizedEps
		)
	and ep.periodTypeId in (1,2)
	and ep.advanceDate is null
union

--Issue-level Estimates
select
	pop.tickerSymbol,
	pop.exchangeSymbol,
	case
		when ed.dataItemId=100173 then 'normalizedEPS'
		when ed.dataItemId=100278 then 'eps'
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
	join ##sgxpop pop on ec.tradingItemId=pop.tradingItemId
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
union 

--Price Target and Consensus Rec
select
	pop.tickerSymbol,
	pop.exchangeSymbol,
	case
		when ed.dataItemId = 100160 then 'avgBrokerReq'
		when ed.dataItemId = 100161 then 'targetPrice'
		when ed.dataItemId = 100162 then 'TP medianEstimate'
		when ed.dataItemId = 100163 then 'TP highEstimate'
		when ed.dataItemId = 100164 then 'TP lowEstimate'
		when ed.dataItemId = 100165 then 'TP lowEstimate'
		when ed.dataItemId = 100166 then 'TP estimateDeviation'
		when ed.dataItemId = 100167 then 'LTG meanEstimate'
		when ed.dataItemId = 100168 then 'LTG medianEstimate'
		when ed.dataItemId = 100169 then 'LTG highEstimate'
		when ed.dataItemId = 100170 then 'LTG lowEstimate'
		when ed.dataItemId = 100171 then 'LTG estimatesNum'
		when ed.dataItemId = 100172 then 'LTG estimateDeviation'
		when ed.dataItemId = 114353 then 'VolatilityNum'
		when ed.dataItemId = 114362 then 'IndustryRecNum'
	end as WMSIApi,
	convert(varchar(max),ed.dataItemValue) as dataItemId,
	null as period,
	null as date,
	cISO.ISOCode
from ciqEstimatePeriod ep
	join ciqEstimateConsensus ec on ep.estimatePeriodId=ec.estimatePeriodId
	join ciqEstimateNumericData ed on ec.estimateConsensusId=ed.estimateConsensusId
	join ##sgxpop pop on ec.tradingItemId=pop.tradingItemId
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
where ed.toDate > getdate()
	and ed.dataItemId in (
		100160 --avgBrokerReq
		,100161	--TP meanEstimate
		,100162	--TP medianEstimate
		,100163	--TP highEstimate
		,100164	--TP lowEstimate
		,100165	--TP lowEstimate
		,100166	--TP estimateDeviation
		,100167	--LTG meanEstimate
		,100168 --LTG medianEstimate
		,100169 --LTG highEstimate
		,100170	--LTG lowEstimate
		,100171	--LTG estimatesNum
		,100172	--LTG estimateDeviation
		,114353 --VolatilityNum
		,114362 --Industry Recommendation
	)