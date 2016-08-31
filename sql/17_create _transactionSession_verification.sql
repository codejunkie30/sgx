USE [sgx]
GO

/****** Object:  Table [dbo].[transactionSession_verification]    Script Date: 26/08/2016 2:06:07 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[transactionSession_verification](
	[id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[token] [nvarchar](max) NOT NULL,
	[creationTime] [datetime] NOT NULL,
	[expiryTime] [datetime] NOT NULL,
	[tx_session_token_status] [bit] NOT NULL
) ON [PRIMARY]

GO

USE [sgx]
GO

INSERT INTO [dbo].[transactionSession_verification]
           ([id]
           ,[user_id]
           ,[token]
           ,[creationTime]
           ,[expiryTime]
           ,[tx_session_token_status])
     VALUES
           (1,
		   1,
		   'sgx',
		   '2016-06-30',
		   '2016-06-30',
		   1)
GO