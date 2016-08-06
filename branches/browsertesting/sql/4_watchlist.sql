USE [sgx]
GO
/****** Object:  Table [dbo].[watchlist]    Script Date: 10/28/2015 1:39:09 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[watchlist](
	[user_id] [bigint] NOT NULL,
	[watchlist_id] [bigint] NOT NULL,
	[date_created] [datetime] NOT NULL,
	[watchlist_name] [nvarchar](50) NOT NULL
) ON [PRIMARY]

GO

INSERT [dbo].[watchlist] ([user_id], [watchlist_id], [date_created], [watchlist_name]) VALUES (1, 1, CAST(N'2016-03-03 00:00:00.000' AS DateTime), N'New watch list')
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[watchlist_company](
	[watchlist_id] [bigint] NOT NULL,
	[tickerCode] [nvarchar](50) NOT NULL,
	[id] [bigint] NOT NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[watchlist_option]    Script Date: 10/28/2015 1:39:09 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[watchlist_option](
	[watchlist_id] [bigint] NOT NULL,
	[alert_option] [nvarchar](50) NOT NULL,
	[option_value] [nvarchar](50) NOT NULL,
	[id] [bigint] NOT NULL
) ON [PRIMARY]

GO

USE [sgx]
GO
INSERT [dbo].[watchlist_option] ([watchlist_id], [alert_option], [option_value], [id]) VALUES (1, N'option', N'option', 1)
GO