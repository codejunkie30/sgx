DECLARE @maxVal bigint;
DECLARE @seqSQL nvarchar(max);

SET @maxVal = (SELECT max(id)+1 FROM watchlist_transaction);
SET @seqSQL = N'CREATE SEQUENCE watchlist_transaction_seq AS bigint START WITH ' + cast(@maxVal as nvarchar(20)) + 'INCREMENT BY 1 NO CYCLE;';
EXEC SP_EXECUTESQL @seqSQL;

GO