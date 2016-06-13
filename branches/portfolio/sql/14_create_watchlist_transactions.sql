USE [sgx]
GO

/****** Object:  Table [dbo].[watchlist_transactions]    Script Date: 6/7/2016 2:06:07 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[watchlist_transaction](
	[id] [bigint] NOT NULL,
	[watchlist_id] [bigint] NOT NULL,
	[tickerCode] [nvarchar](50) NOT NULL,
	[transaction_type] [nvarchar](50) NOT NULL,
	[tradeDate] [date] NOT NULL,
	[number_of_shares] [bigint] NOT NULL,
	[cost_at_purchase] [numeric](18, 10) NOT NULL,
	[current_price] [numeric](18, 10) NOT NULL
) ON [PRIMARY]

GO