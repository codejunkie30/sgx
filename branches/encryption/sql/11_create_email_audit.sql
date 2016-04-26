USE [SGX]
GO
/****** Object:  Table [dbo].[email_audit]    Script Date: 4/6/2016 5:09:50 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[email_audit](
	[id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[email] [nvarchar](50) NOT NULL,
	[created_dt] [datetime] NOT NULL,
	[body] [nvarchar](max) NULL,
	[watchlist_name] [nvarchar](50) NULL,
	[subject] [nvarchar](50) NOT NULL,
	[status] [nvarchar](50) NOT NULL,
	[reason] [nvarchar](50) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
INSERT [dbo].[email_audit] ([id], [user_id], [email], [created_dt], [body], [watchlist_name], [subject], [status], [reason]) VALUES (1, 1, N'someone@wmsi.com', CAST(N'2015-09-03 13:41:09.913' AS DateTime), NULL, NULL, N'SGX StockFacts Premium Alert', N'success', NULL)
