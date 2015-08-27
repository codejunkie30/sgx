select pop.tickerSymbol,
	pop.exchangeSymbol,
	a.dividenddate as dividendExDate,
	a.paydate,
	a.divAmount,
	d.dividendPaymentTypeName,
	cISO.ISOCode
from ciqDividendCache a
join ciqTradingItem b on a.tradingitemid=b.tradingitemid
join ciqSecurity c on b.securityId=c.securityId
join ciqDividendPaymentType d on a.dividendPaymentTypeId=d.dividendPaymentTypeid
left join ciqcurrency cISO on a.currencyId=cISO.currencyId
join ##sgxpop pop on b.tradingItemId=pop.tradingItemId
where dividendDate > dateadd(yy,-5,getdate())