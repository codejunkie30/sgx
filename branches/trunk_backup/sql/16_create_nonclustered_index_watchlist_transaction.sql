USE [sgx]
GO
CREATE NONCLUSTERED INDEX IX_Watchlist_Transaction_Ticker_watchlistId_tickerCode
ON [dbo].[watchlist_transaction] ([watchlist_id],[tickerCode])
INCLUDE ([transaction_type],[tradeDate],[number_of_shares],[cost_at_purchase],[current_price])
GO