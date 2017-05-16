--Trading Price
select
	tickerSymbol,
	exchangeSymbol,
	WMSIApi,
	convert(varchar(max),dataItemValue) as dataItemValue,
	null as period,
	PricingDate as date,
	currency
from (
	select pop.tickerSymbol,
		pop.exchangeSymbol,
		pe.PricingDate,
		pe.priceOpen as openPrice,
		pe.priceClose as closePrice,
		pe.priceHigh as highPrice,
		pe.priceLow as lowPrice,
		cISO.ISOCode as currency
	from ciqPriceEquity pe
		join ##sgxpop pop on pe.tradingItemId=pop.tradingItemId -- Declare Pop
		join ciqTradingItem ti on pe.tradingItemId=ti.tradingItemId
		join ciqCurrency cISO on ti.currencyId=cISO.currencyId
	where pe.pricingdate > dateadd(yyyy,-5,getdate())
	) pvt
unpivot
	(dataItemValue for WMSIAPI in (openPrice, closePrice, highPrice, lowPrice)) as unpvt
union

--Trading Volume
select pop.tickerSymbol, pop.exchangeSymbol, 'volume' as WMISApi, convert(varchar(max),pe.Volume), null, pe.PricingDate, null
from ciqPriceEquity pe
	join ##sgxpop pop on pe.tradingItemId=pop.tradingItemId -- Declare Pop
where pe.pricingdate > dateadd(yyyy,-5,getdate())
union

--Trading Price Year High and Low
select tickerSymbol, exchangeSymbol, WMSIApi, convert(varchar(max),dataItemValue), null, null, ISOCode
from (
	select pop.tickerSymbol, pop.exchangeSymbol, max(pe.priceClose) as yearHigh, min(pe.priceClose) as yearLow, cISO.ISOCode
	from ciqPriceEquity pe
		join ##sgxpop pop on pe.tradingItemId=pop.tradingItemId -- Declare Pop
		join ciqTradingItem ti on pe.tradingItemId=ti.tradingItemId
		join ciqCurrency cISO on ti.currencyId=cISO.currencyId
	where pe.pricingdate > dateadd(ww,-52,getdate())
	group by pop.tickerSymbol, pop.exchangeSymbol, cISO.ISOCode
	) pvt
unpivot
	(dataItemValue for WMSIAPI in (yearHigh, yearLow)) as unpvt
union

--3Mo Averages
select pop.tickerSymbol, pop.exchangeSymbol, 'avgTradedVolM3', convert(varchar(max),avg(priceClose * volume)), null, null, cISO.ISOCode
from ciqPriceEquity pe
	join ##sgxpop pop on pe.tradingItemId=pop.tradingItemId -- Declare Pop
	join ciqTradingItem ti on pe.tradingItemId=ti.tradingItemId
	join ciqCurrency cISO on ti.currencyId=cISO.currencyId
where pe.pricingdate > dateadd(m,-3,getdate())
group by pop.tickerSymbol, pop.exchangeSymbol, cISO.ISOCode
union 
select pop.tickerSymbol, pop.exchangeSymbol, 'avgVolumeM3', convert(varchar(max),avg(volume)), null, null, cISO.ISOCode
from ciqPriceEquity pe
	join ##sgxpop pop on pe.tradingItemId=pop.tradingItemId -- Declare Pop
	join ciqTradingItem ti on pe.tradingItemId=ti.tradingItemId
	join ciqCurrency cISO on ti.currencyId=cISO.currencyId
where pe.pricingdate > dateadd(m,-3,getdate())
group by pop.tickerSymbol, pop.exchangeSymbol, cISO.ISOCode
union

--Shares Out
select DISTINCT pop.tickerSymbol, pop.exchangeSymbol, 'sharesOutstanding', convert(varchar(max),opf.sharesOutstanding), null, null, null
from ciqOwnPublicFloatCompanyHst opf
join ##sgxpop pop on opf.ownedCompanyId=pop.companyId -- Declare Pop
where opf.latestflag=1

union

--Float 
select DISTINCT pop.tickerSymbol, pop.exchangeSymbol, 'floatPercentage', convert(varchar(max),opf.publicFloat), null, null, null
from ciqOwnPublicFloatCompanyHst opf
join ##sgxpop pop on opf.ownedCompanyId=pop.companyId -- Declare Pop
where opf.latestflag=1

union

--Fiscal Period End Dates
select pop.tickerSymbol,
	pop.exchangeSymbol,
	'periodEndDate' as WMSIApi,
	convert(varchar(max),fp.periodEndDate,101),
	case
		when fp.periodTypeId = 1 then 'FY'+cast(fp.fiscalYear as varchar(max))
		when fp.periodTypeId = 4 then 'LTM'+cast(fp.fiscalQuarter as varchar(max))+cast(fp.fiscalYear as varchar(max))
	end as period,
	fp.periodEndDate as date,
	null as currency
from ciqLatestInstanceFinPeriod fp
	join ##SGXPop pop on fp.companyId=pop.companyId
	join (
		--Pull Last 5 FY
		select fpfy.financialPeriodId, rp.relativePeriod*-1 as relativePeriod
		from ciqLatestInstanceFinPeriod fpfy
			join (
				--Determine Relative Periods
				select companyId, fiscalYear, maxFilingDate, rank()
					over (Partition by companyId order by fiscalYear desc) -1 as relativePeriod
				from (
					--Latest Filing per FY
					select max(filingDate) as maxFilingDate, companyId, fiscalYear
					from ciqLatestInstanceFinPeriod
					where periodTypeId = 1 --Annual
					and companyId in (select companyId from ##sgxpop) --Declare Population
					group by companyId, fiscalYear
					) mxdt
				) rp on fpfy.companyId=rp.companyId and fpfy.fiscalYear=rp.FiscalYear and fpfy.filingDate=rp.maxFilingDate
		where 1=1 --Spacer
			and fpfy.periodtypeid = 1 --FY
			and rp.relativePeriod < 5 --Relative Period Declare
		union
		--Pull Last LTM
		select fpltm.financialPeriodId, NULL
		from ciqLatestInstanceFinPeriod fpltm
		where 1=1 --Spacer
			and fpltm.periodtypeId = 4 --LTM
			and fpltm.latestPeriodFlag = 1
			and fpltm.companyId in (select companyId from ##sgxpop)
		) finpop on fp.financialPeriodId=finpop.financialPeriodId
union

--Currency Items Fin Statement - 5 Years
select pop.tickerSymbol,
	pop.exchangeSymbol,
	case
		when dataItemId=28 then 'totalRevenue'
		when dataItemId=10 then 'grossProfit'
		when dataItemId=15 then 'netIncome'
		when dataItemId=4051 then 'ebitda'
		when dataItemId=400 then 'ebit'
		when dataItemId=4380 then 'eps'
		when dataItemId=3058 then 'dividendsPerShare'
		when dataItemId=1002 then 'cashInvestments'
		when dataItemId=1007 then 'totalAssets'
		when dataItemId=1008 then 'totalCurrentAssets'
		when dataItemId=1004 then 'netPpe'
		when dataItemId=1009 then 'totalCurrentLiabily'
		when dataItemId=1049 then 'longTermDebt'
		when dataItemId=1276 then 'totalLiability'
		when dataItemId=1222 then 'retainedEarnings'
		when dataItemId=1103 then 'commonStock'
		when dataItemId=1275 then 'totalEquity'
		when dataItemId=1052 then 'minorityInterest'
		when dataItemId=2006 then 'cashOperations'
		when dataItemId=2005 then 'cashInvesting'
		when dataItemId=2004 then 'cashFinancing'
		when dataItemId=2093 then 'netChange'
	end as WMSIApi,
	case when nmflag=1 then null else convert(varchar(max),fd.dataItemValue) end as dataitemvalue,
	case
		when fp.periodTypeId = 1 then 'FY'+cast(fp.fiscalYear as varchar(max))
		when fp.periodTypeId = 4 then 'LTM'+cast(fp.fiscalQuarter as varchar(max))+cast(fp.fiscalYear as varchar(max))
	end as period,
	fp.periodEndDate as date,
	cISO.ISOCode as currency
from ciqLatestInstanceFinPeriod fp
	join ##SGXPop pop on fp.companyId=pop.companyId
	join ciqFinancialData fd on fp.financialPeriodId=fd.financialPeriodId
	join (
		--Pull Last 5 FY
		select fpfy.financialPeriodId, rp.relativePeriod*-1 as relativePeriod
		from ciqLatestInstanceFinPeriod fpfy
			join (
				--Determine Relative Periods
				select companyId, fiscalYear, maxFilingDate, rank()
					over (Partition by companyId order by fiscalYear desc) -1 as relativePeriod
				from (
					--Latest Filing per FY
					select max(filingDate) as maxFilingDate, companyId, fiscalYear
					from ciqLatestInstanceFinPeriod
					where periodTypeId = 1 --Annual
					and companyId in (select companyId from ##sgxpop) --Declare Population
					group by companyId, fiscalYear
					) mxdt
				) rp on fpfy.companyId=rp.companyId and fpfy.fiscalYear=rp.FiscalYear and fpfy.filingDate=rp.maxFilingDate
		where 1=1 --Spacer
			and fpfy.periodtypeid = 1 --FY
			and rp.relativePeriod < 5 --Relative Period Declare
		union
		--Pull Last LTM
		select fpltm.financialPeriodId, NULL
		from ciqLatestInstanceFinPeriod fpltm
		where 1=1 --Spacer
			and fpltm.periodtypeId = 4 --LTM
			and fpltm.latestPeriodFlag = 1
			and fpltm.companyId in (select companyId from ##sgxpop)
		) finpop on fp.financialPeriodId=finpop.financialPeriodId
	join (
		select companyid, currencyid
		from ciqLatestInstanceFinPeriod
		where periodtypeid=4
		and latestperiodflag=1
		and companyid in (select companyid from ##sgxpop)
		) fincurr on fincurr.companyid=fp.companyid
	join ciqCurrency cISO on fp.currencyId=cISO.currencyId
where fd.dataitemId in (
	28--totalRevenue
	,10--grossProfit
	,15--netIncome
	,4051--ebitda
	,400 --ebit
	,4380--eps
	,3058--dividendsPerShare
	,1002--cashInvestments
	,1007--totalAssets
	,1008--totalCurrentAssets
	,1004--netPpe
	,1009--totalCurrentLiabily
	,1049--longTermDebt
	,1276--totalLiability
	,1222--retainedEarnings
	,1103--commonStock
	,1275--totalEquity
	,1052--minorityInterest
	,2006--cashOperations
	,2005--cashInvesting
	,2004--cashFinancing
	,2093--netChange
	)
union

--Non Currency Items Fin Statement - 5 Years
select pop.tickerSymbol,
	pop.exchangeSymbol,
	case
		when dataItemId=4377 then 'payoutRatio'
		when dataItemId=4178 then 'returnAssets'
		when dataItemId=4363 then 'returnCapital'
		when dataItemId=4128 then 'returnOnEquity'
		when dataItemId=4074 then 'grossMargin'
		when dataItemId=4047 then 'ebitdaMargin'
		when dataItemId=4094 then 'netProfitMargin'
		when dataItemId=4177 then 'assetTurns'
		when dataItemId=4030 then 'currentRatio'
		when dataItemId=4121 then 'quickRatio'
		when dataItemId=4035 then 'avgDaysInventory'
		when dataItemId=4183 then 'avgDaysPayable'
		when dataItemId=4184 then 'cashConversion'
		when dataItemId=4034 then 'totalDebtEquity'
		when dataItemId=4192 then 'totalDebtEbitda'
		when dataItemId=4190 then 'ebitdaInterest'
		when dataItemId=4194 then 'totalRev1YrAnnGrowth'
		when dataItemId=4196 then 'ebitda1YrAnnGrowth'
		when dataItemId=4199 then 'netIncome1YrAnnGrowth'
		when dataItemId=4200 then 'eps1YrAnnGrowth'
		when dataItemId=4202 then 'commonEquity1YrAnnGrowth'
	end as WMSIApi,
	case when nmflag=1 then null else convert(varchar(max),fd.dataItemValue) end as dataitemvalue,
	case
		when fp.periodTypeId = 1 then 'FY'+cast(fp.fiscalYear as varchar(max))
		when fp.periodTypeId = 4 then 'LTM'+cast(fp.fiscalQuarter as varchar(max))+cast(fp.fiscalYear as varchar(max))
	end as period,
	null as date,
	null as currency
from ciqLatestInstanceFinPeriod fp
	join ##SGXPop pop on fp.companyId=pop.companyId
	join ciqFinancialData fd on fp.financialPeriodId=fd.financialPeriodId
	join (
		--Pull Last 5 FY
		select fpfy.financialPeriodId, rp.relativePeriod*-1 as relativePeriod
		from ciqLatestInstanceFinPeriod fpfy
			join (
				--Determine Relative Periods
				select companyId, fiscalYear, maxFilingDate, rank()
					over (Partition by companyId order by fiscalYear desc) -1 as relativePeriod
				from (
					--Latest Filing per FY
					select max(filingDate) as maxFilingDate, companyId, fiscalYear
					from ciqLatestInstanceFinPeriod
					where periodTypeId = 1 --Annual
					and companyId in (select companyId from ##sgxpop) --Declare Population
					group by companyId, fiscalYear
					) mxdt
				) rp on fpfy.companyId=rp.companyId and fpfy.fiscalYear=rp.FiscalYear and fpfy.filingDate=rp.maxFilingDate
		where 1=1 --Spacer
			and fpfy.periodtypeid = 1 --FY
			and rp.relativePeriod < 5 --Relative Period Declare
		union
		--Pull Last LTM
		select fpltm.financialPeriodId, NULL
		from ciqLatestInstanceFinPeriod fpltm
		where 1=1 --Spacer
			and fpltm.periodtypeId = 4 --LTM
			and fpltm.latestPeriodFlag = 1
			and fpltm.companyId in (select companyId from ##sgxpop)
		) finpop on fp.financialPeriodId=finpop.financialPeriodId
	join (
		select companyid, currencyid
		from ciqLatestInstanceFinPeriod
		where periodtypeid=4
		and latestperiodflag=1
		and companyid in (select companyid from ##sgxpop)
		) fincurr on fincurr.companyid=fp.companyid
where fd.dataitemId in (
	4377--payoutRatio
	,4178--returnAssets
	,4363--returnCapital
	,4128--returnEquity
	,4074--grossMargin
	,4047--ebitdaMargin
	,4094--netIncomeMargin
	,4177--assetTurns
	,4030--currentRatio
	,4121--quickRatio
	,4035--avgDaysInventory
	,4183--avgDaysPayable
	,4184--cashConversion
	,4034--totalDebtEquity
	,4192--totalDebtEbitda
	,4190--ebitdaInterest
	,4194--totalRevenue1YrAnnGrowth
	,4196--ebitda1YrAnnGrowth
	,4199--netIncome1YrAnnGrowth
	,4200--eps1YrAnnGrowth
	,4202--commonEquity1YrAnnGrowth
	)
union

---Misc. Financials for Screener
select pop.tickerSymbol, pop.exchangeSymbol, 
	case
		when fd.dataItemId = 4017 then 'beta5Yr'
		when fd.dataItemId = 4220 then 'totalRev3YrAnnGrowth'
		when fd.dataItemId = 4233 then 'totalRev5YrAnnGrowth'
		when fd.dataItemId = 9 then 'basicEpsIncl'
		when fd.dataItemId = 3058 then 'divShare'
		when fd.dataItemId = 4020 then 'bvShare'
		when fd.dataItemId = 2021 then 'capitalExpenditures'
		when fd.dataItemId = 4173 then 'totalDebt'
		when fd.dataItemId = 1310 then 'tbv'
		when fd.dataItemId = 4364 then 'netDebt'
	end as WMSIApi,
	case when nmflag=1 then null else convert(varchar(max),fd.dataItemValue) end as dataitemvalue,
	'LTM'+cast(fp.fiscalQuarter as varchar(max))+cast(fp.fiscalYear as varchar(max)),
	fp.periodEndDate as date,
	case when fd.dataItemId in (9, 3058, 4020, 2021, 4173, 1310, 4364) then cISO.ISOCode end
from ciqLatestInstanceFinPeriod fp
	join ciqFinancialData fd on fp.FinancialPeriodId=fd.FinancialPeriodId
	join ##sgxpop pop on fp.companyId=pop.companyId
	join ciqCurrency cISO on fp.currencyId=cISO.currencyId
where 1=1 --Spacer
	and fp.periodtypeid = 4 --LTM
	and fp.latestPeriodFlag = 1
	and fd.dataitemId in (
		4017 --beta5Yr
		,4220 --totalRev3YrAnnGrowth
		,4233 --totalRev5YrAnnGrowth
		,9 --basicEpsIncl
		,3058 --divShare
		,4020 --bvShare
		,2021 --capitalExpenditures
		,4173 --totalDebt
		,1310 --tbv
		,4364 --netDebt
		)

union

--Descriptive Information
select tickerSymbol, exchangeSymbol, WMSIApi, dataItemValue, null as period, null as date, null as ISOCode
from (
	select pop.tickerSymbol,
		pop.exchangeSymbol,
		convert(varchar(max),pop.tickerSymbol) as tickerCode,
		convert(varchar(max),c.companyName) as companyName,
		convert(varchar(max),
			case when streetAddress is null then '' else streetAddress end +
			case when streetAddress2 is null then '' else ', ' + streetAddress2 end +
			case when streetAddress3 is null then '' else ', ' + streetAddress3 end +
			case when streetAddress4 is null then '' else ', ' + streetAddress4 end +
			case when city is null then '' else ', ' + city end +
			case when st.state is null then '' else ', ' + st.state end +
			case when zipcode is null then '' else ' ,' + zipcode end +
			case when cty.country is null then '' else ', ' + cty.country end) as companyAddress,
		convert(varchar(max),c.yearFounded) as yearFounded,
		convert(varchar(max),fp.periodEndDate,101) as fiscalYearEnd,
		convert(varchar(max),cISO.ISOCode) as filingCurrency,
		convert(varchar(max),c.webpage) as companyWebsite,
		convert(varchar(max),bd.BusinessDescription) as businessDescription
	from ciqCompany c
		join ##sgxpop pop on c.companyId=pop.companyId
		left join ciqState st on c.stateId=st.stateId
		left join ciqCountryGeo cty on c.countryId=cty.countryId
		left join ciqLatestInstanceFinPeriod fp on c.companyId=fp.companyId and fp.latestPeriodFlag=1 and fp.periodTypeId=1
		left join ciqCurrency cISO on fp.currencyId=cISO.currencyId
		left join ciqBusinessDescription bd on c.companyId=bd.companyId
	) pvt
unpivot
	(dataItemValue for WMSIAPI in (tickerCode, companyName, companyAddress, yearFounded, fiscalYearEnd, filingCurrency, companyWebsite, businessDescription)) as unpvt
union

--Industry and Industry Group
select pop.tickerSymbol,
	pop.exchangeSymbol,
	case
		when childlevel=2 then 'industryGroup'
		when childlevel=3 then 'industry' 
	end as WMSIApi,
	convert(varchar(max),b.subTypeValue) as dataItemValue,
	null,
	null,
	null
from ciqCompanyIndustryTree a
join ciqSubType b on a.subTypeId=b.subTypeId
join ##SGXPop pop on a.companyId=pop.companyId
where a.primaryflag=1
and b.childLevel in (2,3)

union

select
pop.tickerSymbol,
pop.exchangeSymbol,
'marketCap',
convert(varchar(max),mc.marketCap),
null,
pricingDate,
curr.ISOCode
from ciqmarketcap mc
join (
select companyid, max(pricingdate) as maxdate
from ciqmarketcap
group by companyid) maxdt on mc.companyId=maxdt.companyId and mc.pricingDate=maxdt.maxdate
join ciqsecurity s on mc.companyId=s.companyId
join ciqTradingItem ti on s.securityId=ti.securityId
join ciqCurrency curr on ti.currencyId=curr.currencyId
join ##SGXPop pop on s.companyid=pop.companyid
where s.primaryFlag=1
and ti.primaryFlag=1


union

select 	
	pop.tickerSymbol,
	pop.exchangeSymbol,
	'filingDate',
	null,
	null,
	cp.filingdate,
	null
from ciqLatestInstanceFinPeriod cp
join ##SGXPop pop on cp.companyId=pop.companyId
where cp.latestPeriodFlag=1 --Last LTM
and cp.periodTypeId=4 --LTM

union

SELECT DISTINCT 
	pop.tickerSymbol, 
	pop.exchangeSymbol,
	'gvKey',
	convert(varchar(max),sec.securityId),
	null,
	null,
	null
FROM ciqSecurity sec
join ##sgxpop pop on sec.companyId=pop.companyId -- Declare Pop

union

--target price num of analysts
select 
	pop.tickerSymbol, 
	pop.exchangeSymbol,
	'targetPriceNum',
	convert(varchar(max),numAnalysts),
	null,
	null,
	null
from ciqEstimateRevision er
	join ciqEstimateConsensus ec on er.estimateConsensusId=ec.estimateConsensusId
	join ciqEstimatePeriod ep on ec.estimatePeriodId=ep.estimatePeriodId
	join ##sgxpop pop on ep.companyId = pop.companyId
	join (
		select max(asofdate) as maxDate, estimateConsensusId, dataItemId, estimateRevisionTypeId
		from ciqEstimateRevision
		where estimateConsensusId in (
			select estimateConsensusId
			from ciqEstimateConsensus ec
				join ciqEstimatePeriod ep on ec.estimatePeriodID=ep.estimatePeriodId
			where ep.companyid in (select companyid from ##sgxpop)
				and advanceDate is null
			)
			and estimateRevisionTypeId=4
			group by estimateConsensusId, dataItemId, estimateRevisionTypeId
		) mdate on er.estimateConsensusId=mdate.estimateConsensusId
			and er.dataItemId=mdate.dataItemId
			and er.estimateRevisionTypeId=mdate.estimateRevisionTypeId
			and er.asOfDate=mdate.maxDate
			and er.dataitemid=100161
where ec.tradingItemId in (
		select tradingitemid
		from ciqTradingItem ti
		join ciqSecurity sec on ti.securityId=sec.securityId
		where ti.primaryFlag=1
		and sec.primaryFlag=1
		and sec.companyId  in (select companyid from ##sgxpop)
		)
		
union		

-- evebitda
select
	pop.tickerSymbol, 
	pop.exchangeSymbol,
	'evEbitData',
	convert(varchar(max),mkc.tev/(fd.dataitemvalue/exfrom.currencyRateClose*exto.currencyRateClose)),
	null,
	null,
	null
from ciqmarketcap mkc
join (
select companyid, max(pricingdate) as maxdate
from ciqmarketcap a
group by companyid
) mxdt on mxdt.companyid=mkc.companyid and mxdt.maxdate=mkc.pricingdate
join ciqLatestInstanceFinPeriod fp on fp.companyid=mkc.companyid
join ciqFinancialData fd on fd.financialPeriodId=fp.financialPeriodId
join ciqSecurity sec on mkc.companyid=sec.companyid and sec.primaryflag=1
join ciqTradingItem ti on sec.securityid=ti.securityid and ti.primaryflag=1
join ciqExchangeRate exfrom on fp.currencyid=exfrom.currencyid and exfrom.latestflag=1
join ciqExchangeRate exto on ti.currencyid=exto.currencyid and exto.latestflag=1
join ##sgxpop pop on mkc.companyid = pop.companyid
where fp.latestperiodflag=1 --Latest Period
and fp.periodtypeid=4 --LTM
and fd.dataitemid=21677 --EBITDA incl other income

union

--Volatility
select
pop.tickerSymbol,
pop.exchangeSymbol,
case
when ed.dataItemId=114353 then 'volatility'
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
where ed.toDate > getDate()
and ed.dataItemId in (114353) --Volatility
and ep.advanceDate is null
and ec.tradingItemId in (
select tradingitemid
from ciqTradingItem ti
join ciqSecurity sec on ti.securityId=sec.securityId
where ti.primaryFlag=1
and sec.primaryFlag=1
and sec.companyId in (select companyid from ##sgxpop)
)

union

select
	pop.tickerSymbol, 
	pop.exchangeSymbol,
	'peRatio',
	convert(varchar(max),prc.priceclose/(fd.dataitemvalue/exfrom.currencyRateClose*exto.currencyRateClose)),
	null,
	null,
	null
from ciqPriceEquity prc
join (
select tradingitemid, max(pricingdate) as maxdate
from ciqPriceEquity a
group by tradingitemid
) mxdt on mxdt.tradingitemid=prc.tradingitemid and mxdt.maxdate=prc.pricingdate
join ciqTradingItem ti on prc.tradingitemid=ti.tradingitemid and ti.primaryflag=1
join ciqSecurity sec on sec.securityid=ti.securityid and sec.primaryflag=1
join ciqLatestInstanceFinPeriod fp on fp.companyid=sec.companyid
join ciqFinancialData fd on fd.financialPeriodId=fp.financialPeriodId
join ciqExchangeRate exfrom on fp.currencyid=exfrom.currencyid and exfrom.latestflag=1
join ciqExchangeRate exto on ti.currencyid=exto.currencyid and exto.latestflag=1
join ##sgxpop pop on prc.tradingitemid=pop.tradingitemid
where fp.latestperiodflag=1 --Latest Period
and fp.periodtypeid=4 --LTM
and fd.dataitemid=142 --Diluted EPS Excl

union

--1-Year Price Volatility Calculation
select pop.tickerSymbol, pop.exchangeSymbol, 'priceVolHistYr', convert(varchar(max),stdev(weeklyreturn)*sqrt(rcnt)*100) as dataItemValue, null, null, null
from ##sgxpop pop
	join (
		select sdate.tradingItemId, log(edate.priceClose/sdate.priceClose) as weeklyreturn
		from (
			select pe.tradingItemId, pe.pricingDate, pe.priceClose, rank() over (partition by pe.tradingItemId order by pe.pricingDate) as mathalignment
			from ciqPriceEquity pe
			join (
				select pe.tradingItemId, max(pe.pricingDate) as pricingDate, eow.saturday
				from ciqPriceEquity pe
					join ##SGXPop pop on pe.tradingItemId=pop.tradingItemId
					join ( --find week ranges
						select dateadd(day,number-5,dateadd(week, -53, getdate())) sunday, dateadd(day,number+1,dateadd(week, -53, getdate())) saturday
						from master..spt_values
						WHERE type = 'P'
						AND DATEADD(DAY,number+1,dateadd(week, -53, getdate())) < getdate()
						and DATEPART(dw,DATEADD(DAY,number+1,dateadd(week, -53, getdate()))) = 7
						) eow on pe.pricingDate between eow.sunday and eow.saturday
				where pe.pricingDate > getdate()-400
				group by pe.tradingItemId, eow.saturday
				) wkly on pe.tradingItemId=wkly.tradingItemId and wkly.pricingDate=pe.pricingDate
			) sdate
		join (
			select pe.tradingItemId, pe.pricingDate, pe.priceClose, rank() over (partition by pe.tradingItemId order by pe.pricingDate) -1 as mathalignment
			from ciqPriceEquity pe
			join (
				select pe.tradingItemId, max(pe.pricingDate) as pricingDate, eow.saturday
				from ciqPriceEquity pe
					join ##SGXPop pop on pe.tradingItemId=pop.tradingItemId
					join ( --find week ranges
						select dateadd(day,number-5,dateadd(week, -53, getdate())) sunday, dateadd(day,number+1,dateadd(week, -53, getdate())) saturday
						from master..spt_values
						WHERE type = 'P'
						AND DATEADD(DAY,number+1,dateadd(week, -53, getdate())) < getdate()
						and DATEPART(dw,DATEADD(DAY,number+1,dateadd(week, -53, getdate()))) = 7
						) eow on pe.pricingDate between eow.sunday and eow.saturday
				where pe.pricingDate > getdate()-400
				group by pe.tradingItemId, eow.saturday
				) wkly on pe.tradingItemId=wkly.tradingItemId and wkly.pricingDate=pe.pricingDate
			) edate on sdate.tradingItemId=edate.tradingItemId and sdate.mathalignment=edate.mathalignment
		) sprice on pop.tradingItemId=sprice.tradingItemId
	join (
		select cnt.tradingitemid, count(pricingdate) as rcnt
		from ( 
			select pe.tradingItemId, max(pe.pricingDate) as pricingDate, eow.saturday
			from ciqPriceEquity pe
				join ##SGXPop pop on pe.tradingItemId=pop.tradingItemId
				join ( --find week ranges
					select dateadd(day,number-5,dateadd(week, -53, getdate())) sunday, dateadd(day,number+1,dateadd(week, -53, getdate())) saturday
					from master..spt_values
					WHERE type = 'P'
					AND DATEADD(DAY,number+1,dateadd(week, -53, getdate())) < getdate()
					and DATEPART(dw,DATEADD(DAY,number+1,dateadd(week, -53, getdate()))) = 7
					) eow on pe.pricingDate between eow.sunday and eow.saturday
			where pe.pricingDate > getdate()-400
			group by pe.tradingItemId, eow.saturday 
			) cnt
			group by cnt.tradingitemid
		) rcnt on pop.tradingitemid=rcnt.tradingitemid
group by pop.tickerSymbol, pop.exchangeSymbol, rcnt.rcnt

