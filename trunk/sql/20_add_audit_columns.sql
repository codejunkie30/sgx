ALTER TABLE watchlist_transaction
    ADD created_by bigint NOT NULL DEFAULT 1,
        created_dt datetime2(3) NOT NULL DEFAULT GETDATE(),
        updated_by bigint NOT NULL DEFAULT 1,
		updated_dt datetime2(3) NOT NULL DEFAULT GETDATE()

ALTER TABLE configurations
    ADD ChangedDt datetime2(3) NOT NULL DEFAULT GETDATE()
    
ALTER TABLE premium_verification
    ADD updated_dt datetime2(3) NOT NULL DEFAULT GETDATE()
    
ALTER TABLE user_verification
    ADD updated_dt datetime2(3) NOT NULL DEFAULT GETDATE()
    
ALTER TABLE watchlist
    ADD updated_dt datetime2(3) NOT NULL DEFAULT GETDATE()

CREATE TABLE [dbo].[accounts_audit](
	[audit_id] [bigint] NOT NULL,
	[id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[type] [nvarchar](50) NOT NULL,
	[active] [bit] NOT NULL,
	[always_active] [bit] NOT NULL,
	[contact_opt_in] [bit] NOT NULL,
	[start_dt] [datetime] NOT NULL,
	[expiration_dt] [datetime] NULL,
	[created_by] [bigint] NOT NULL,
	[created_dt] [datetime] NOT NULL,
	[updated_by] [bigint] NOT NULL,
	[updated_dt] [datetime] NOT NULL,
	[currency] [nvarchar](8) NULL Default('SGD')
) ON [PRIMARY]

INSERT [dbo].[accounts_audit] ([audit_id], [id], [user_id], [type], [active], [always_active], [contact_opt_in], [start_dt], [expiration_dt], [created_by], [created_dt], [updated_by], [updated_dt], [currency]) 
VALUES (1, 1, 1, N'MASTER', 1, 1, 0, CAST(N'2015-09-03 13:42:45.300' AS DateTime), CAST(N'2015-09-17 13:42:45.307' AS DateTime), 1, CAST(N'2015-09-03 13:42:45.347' AS DateTime), 1, CAST(N'2015-09-03 13:42:45.347' AS DateTime), 'SGD')

DECLARE @maxVal bigint;
DECLARE @seqSQL nvarchar(max);

SET @maxVal = (SELECT max(audit_id)+1 FROM accounts_audit);
SET @seqSQL = N'CREATE SEQUENCE accounts_audit_seq AS bigint START WITH ' + cast(@maxVal as nvarchar(20)) + 'INCREMENT BY 1 NO CYCLE;';
EXEC SP_EXECUTESQL @seqSQL;


CREATE TABLE [dbo].[configurations_audit](
	[audit_id] [bigint] NOT NULL,
	[id] [nvarchar](50) NOT NULL,
	[Property] [nvarchar](50) NOT NULL,
	[Value] [varchar](50) NOT NULL,
	[ChangedBy] [nvarchar](50) NOT NULL,
	[ChangedDt] [datetime] NOT NULL,
) ON [PRIMARY]

INSERT [dbo].[configurations_audit] ([audit_id], [id], [Property], [Value], [ChangedBy], [ChangedDt]) 
VALUES (1, N'1', N'dummy', N'dummy', N'dummy',GETDATE())

SET @maxVal = (SELECT max(audit_id)+1 FROM configurations_audit);
SET @seqSQL = N'CREATE SEQUENCE configurations_audit_seq AS bigint START WITH ' + cast(@maxVal as nvarchar(20)) + 'INCREMENT BY 1 NO CYCLE;';
EXEC SP_EXECUTESQL @seqSQL;