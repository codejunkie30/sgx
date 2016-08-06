select distinct fromCurr, toCurr, pricingDate, multiplicationFactor
 from (
select fromC.ISOCode as fromCurr, toC.ISOCode as toCurr, fromEx.pricingDate, toEx.currencyRateClose/fromEx.currencyRateClose as multiplicationFactor
from ciqExchangeRate fromEx
join ciqCurrency fromC on fromEx.currencyId=fromC.currencyId
join ciqExchangeRate toEx on fromEx.pricingDate=toEx.pricingDate
join ciqCurrency toC on toEx.currencyId=toC.currencyId
where year(fromEx.PricingDate) >= year(GetDate()) - 7 --Limiting History
  and toEx.currencyId IN (SELECT distinct pop.currencyId FROM ##sgxpop pop) 
UNION
select toC.ISOCode as toCurr, fromC.ISOCode as fromCurr, toEx.pricingDate, fromEx.currencyRateClose/toEx.currencyRateClose as multiplicationFactor 
from ciqExchangeRate fromEx
join ciqCurrency fromC on fromEx.currencyId=fromC.currencyId
join ciqExchangeRate toEx on fromEx.pricingDate=toEx.pricingDate
join ciqCurrency toC on toEx.currencyId=toC.currencyId
where year(fromEx.PricingDate) >= year(GetDate()) - 7 --Limiting History
  and fromEx.currencyId IN (SELECT distinct pop.currencyId FROM ##sgxpop pop)
) as tmp