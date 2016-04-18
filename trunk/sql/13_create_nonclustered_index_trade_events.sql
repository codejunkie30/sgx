USE [sgx]
GO
CREATE NONCLUSTERED INDEX IX_Trade_Events_Ticker_Market_LastTradeTime
ON [dbo].[trade_events] ([market],[ticker],[last_trade_time])
INCLUDE ([id],[currency],[price],[volume],[last_trade_price],[last_trade_volume],[bid],[ask],[high_price],[low_price],[open_price],[close_price],[trade_date],[previous_close_date])
GO