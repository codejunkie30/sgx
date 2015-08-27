select pop.tickerSymbol,
	pop.exchangeSymbol,
	kd.keyDevId as id,
	kd.announcedDate as date,
	kd.headline,
	kd.situation,
	kde.keyDevEventTypeName as type
from ciqKeyDevToObjectToEventType kdoe
	join ciqKeyDev kd on kdoe.keyDevId=kd.KeyDevId
	join ciqKeyDevEventType kde on kdoe.KeyDevEventTypeId=kde.KeyDevEventTypeId
	join ##sgxpop pop on kdoe.objectId=pop.companyId
where kd.announcedDate > dateadd(yy,-5,getdate())