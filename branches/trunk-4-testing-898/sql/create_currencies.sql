USE [sgx]
GO

/****** Object:  Table [dbo].[currencies]    Script Date: 7/11/2016 1:20:24 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[currencies](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[currency_name] [varchar](250) NOT NULL,
	[description] [varchar](500) NULL,
	[complete] [bit] NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


