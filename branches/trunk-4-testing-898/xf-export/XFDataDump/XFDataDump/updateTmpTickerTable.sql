declare @TickerLookup as table (TradingItemId int Primary Key, CurrencyId smallint, CompanyId int, ExchangeId int, ExchangeSymbol varchar(10), TickerSymbol varchar(25))

insert into @TickerLookup
--select a.tradingItemId, b.exchangeSymbol, a.tickerSymbol, b.exchangeSymbol+':'+a.tickerSymbol as 'ex-tic', c.companyId
select a.tradingItemId, a.currencyId, c.companyId, b.exchangeId, b.exchangeSymbol, a.tickerSymbol
from ciqTradingitem a
join ciqExchange b
on a.exchangeId = b.exchangeId
join ciqSecurity c
on a.securityId = c.securityId
where a.tradingItemStatusId = '15' --only active tradingitemids
--and b.exchangeId in (94,62,76,83,476,40,92) --
and b.exchangeId in (SELECT ce.exchangeId FROM ciqExchange ce WHERE ce.exchangeSymbol in (select distinct exchangeSymbol from ##sgxpop))
and tickerSymbol is not null
--and c.primaryflag=1
and c.securitySubTypeId in (1,2,22) -- 1 Common Stock, 2 Depositary Receipt (Common Stock)

UPDATE pop SET
	pop.TradingItemId = ti.tradingItemId,
	pop.companyId = ti.CompanyId,
	pop.currencyId = ti.currencyId,
	pop.currencyISO = (SELECT c.ISOCode FROM ciqCurrency c WHERE ti.currencyId = c.currencyId)
FROM
	##sgxpop pop
INNER JOIN
    @TickerLookup ti
ON
    pop.tickerSymbol = ti.TickerSymbol
AND
	ti.ExchangeSymbol = pop.exchangeSymbol;