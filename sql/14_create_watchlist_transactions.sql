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
	[number_of_shares] [numeric](18, 10) NOT NULL,
	[cost_at_purchase] [numeric](18, 10) NOT NULL,
	[current_price] [numeric](18, 10) NOT NULL
) ON [PRIMARY]

GO

USE [sgx]
GO

INSERT INTO [dbo].[watchlist_transaction]
           ([id]
           ,[watchlist_id]
           ,[tickerCode]
           ,[transaction_type]
           ,[tradeDate]
           ,[number_of_shares]
           ,[cost_at_purchase]
           ,[current_price])
     VALUES
           (1,
		   1,
		   101,
		   'BUY',
		   '2016-06-30',
		   10,
		   100.25,
		   12.2)
GO