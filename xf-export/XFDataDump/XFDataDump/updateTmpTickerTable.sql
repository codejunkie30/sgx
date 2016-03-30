UPDATE pop SET
	pop.TradingItemId = ti.tradingItemId,
	pop.companyId = (SELECT s.companyId FROM ciqSecurity s WHERE ti.securityId = s.securityId),
	pop.currencyId = ti.currencyId,
	pop.currencyISO = (SELECT c.ISOCode FROM ciqCurrency c WHERE ti.currencyId = c.currencyId)
FROM
	##sgxpop pop
INNER JOIN
    ciqTradingItem ti
ON
    pop.tickerSymbol = ti.tickerSymbol
AND
	ti.exchangeId = (SELECT ce.exchangeId FROM ciqExchange ce WHERE ce.exchangeSymbol = pop.exchangeSymbol)
	join ciqTradingItem ti (NOLOCK) on pe.tradingItemId=ti.tradingItemId and ti.primaryFlag = 1
	join ciqSecurity cs (NOLOCK)on ti.securityId = cs.securityId and ti.primaryFlag = cs.primaryFlag