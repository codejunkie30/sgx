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
/****** Object:  Table [dbo].[watchlist_company]    Script Date: 10/28/2015 1:39:09 PM ******/
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
