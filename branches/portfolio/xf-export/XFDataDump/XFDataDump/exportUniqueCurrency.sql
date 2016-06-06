SELECT distinct ciq.ISOCode, ciq.currencyName
  FROM ciqCurrency ciq, ##sgxpop pop
 WHERE ciq.ISOCode = pop.currencyISO