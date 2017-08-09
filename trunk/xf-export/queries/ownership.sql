select
	pop.tickerSymbol,
	pop.exchangeSymbol,
case when companyName is null
then firstName+' '+lastName
else companyName end as name,
	a.sharesHeld as shares,
	a.percentOfSharesOutstanding as "percent"
from (
	select a.ownerObjectId, a.ownedCompanyId, a.sharesHeld, a.percentOfSharesOutstanding,rank()
	over (Partition by ownedcompanyId order by a.sharesheld desc) as rank
		from ciqOwnCompanyHolding a 
		join ciqOwnHoldingPeriod b on a.periodid=b.periodid
		where periodEndDate > getdate()
		and a.ownedCompanyId in (select companyId from ##sgxpop)
		and not exists (select NULL from ciqCompany cc where cc.companyID = a.ownerObjectID and cc.companyTypeID in (9,13) )
	) a
left join ciqPerson per on a.ownerObjectId=per.personId
left join ciqCompany co on a.ownerObjectId=co.companyId
join ##sgxpop pop on a.ownedCompanyId=pop.companyId
where rank <=5
order by ownedCompanyId, sharesHeld desc