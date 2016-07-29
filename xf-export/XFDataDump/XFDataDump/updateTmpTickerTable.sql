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
AND
	ti.tradingItemStatusId = 15;