SELECT DISTINCT pop.tickerSymbol, pop.exchangeSymbol, sec.securityId 
FROM ciqSecurity sec
join ##sgxpop pop on sec.companyId=pop.companyId -- Declare Pop