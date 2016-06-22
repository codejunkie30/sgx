DECLARE @maxVal bigint;
DECLARE @seqSQL nvarchar(max);

SET @maxVal = (SELECT max(id)+1 FROM email_audit);
SET @seqSQL = N'CREATE SEQUENCE email_audit_seq AS bigint START WITH ' + cast(@maxVal as nvarchar(20)) + 'INCREMENT BY 1 NO CYCLE;';
EXEC SP_EXECUTESQL @seqSQL;

GO