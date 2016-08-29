DECLARE @maxVal bigint;
DECLARE @seqSQL nvarchar(max);

SET @maxVal = (SELECT max(id)+1 FROM transactionSession_verification);
SET @seqSQL = N'CREATE SEQUENCE transactionSession_verification_seq AS bigint START WITH ' + cast(@maxVal as nvarchar(20)) + 'INCREMENT BY 1 NO CYCLE;';
EXEC SP_EXECUTESQL @seqSQL;

GO