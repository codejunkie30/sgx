SELECT DISTINCT cti.tickerSymbol, ce.exchangeSymbol, cpe.adjustmentFactor as WMISApi, convert(varchar(max),cpe.volume) as MaxVolume, null, cpe.PricingDate, null 
FROM ciqPriceEquity cpe (NOLOCK)
JOIN ciqTradingItem cti(NOLOCK) 
      on cti.tradingItemId=cpe.tradingItemId
JOIN ciqSecurity cs (NOLOCK) 
      on cs.securityId=cti.securityId
--AND cs.primaryFlag = cti.primaryFlag
JOIN ciqExchange ce(NOLOCK) 
      ON ce.exchangeId = cti.exchangeId
WHERE pricingDate >= CAST(DATEADD(month, -6, GETDATE()) as DATE)
AND ce.exchangeSymbol in ('SGX','Catalist')
and cti.tradingitemstatusid = '15'