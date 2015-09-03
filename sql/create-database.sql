USE [sgx]
GO
/****** Object:  Table [dbo].[accounts]    Script Date: 9/3/2015 1:55:39 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[accounts](
	[id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[type] [nvarchar](50) NOT NULL,
	[active] [bit] NOT NULL,
	[always_active] [bit] NOT NULL,
	[contact_opt_in] [bit] NOT NULL,
	[start_dt] [datetime] NOT NULL,
	[expiration_dt] [datetime] NOT NULL,
	[created_by] [bigint] NOT NULL,
	[created_dt] [datetime] NOT NULL,
	[updated_by] [bigint] NOT NULL,
	[updated_dt] [datetime] NOT NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Assets]    Script Date: 9/3/2015 1:55:39 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Assets](
	[id] [bigint] NOT NULL,
	[ticker] [nvarchar](50) NULL,
	[isin] [nvarchar](50) NULL,
	[internal_id] [nvarchar](50) NULL,
	[currency] [nvarchar](50) NULL,
 CONSTRAINT [PK_Assets] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[authorities]    Script Date: 9/3/2015 1:55:39 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[authorities](
	[id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[authority] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_authorities] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[password_reset]    Script Date: 9/3/2015 1:55:39 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[password_reset](
	[id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[token] [nvarchar](128) NOT NULL,
	[redeemed] [bit] NOT NULL,
	[date] [datetime] NOT NULL,
 CONSTRAINT [PK_password_reset] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[trade_events]    Script Date: 9/3/2015 1:55:39 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[trade_events](
	[id] [bigint] NOT NULL,
	[market] [nvarchar](50) NULL,
	[ticker] [nvarchar](10) NOT NULL,
	[currency] [nvarchar](10) NOT NULL,
	[price] [numeric](18, 0) NULL,
	[volume] [numeric](18, 0) NULL,
	[last_trade_time] [datetime] NULL,
	[last_trade_price] [numeric](18, 0) NULL,
	[last_trade_volume] [numeric](18, 0) NULL,
	[bid] [numeric](18, 0) NULL,
	[ask] [numeric](18, 0) NULL,
	[high_price] [numeric](18, 0) NULL,
	[low_price] [numeric](18, 0) NULL,
	[open_price] [numeric](18, 0) NULL,
	[close_price] [numeric](18, 0) NULL,
	[trade_date] [datetime] NULL,
	[previous_close_date] [datetime] NULL,
 CONSTRAINT [PK_prices] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[user_login]    Script Date: 9/3/2015 1:55:39 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[user_login](
	[id] [bigint] NOT NULL,
	[username] [nvarchar](256) NOT NULL,
	[success] [bit] NOT NULL,
	[ipaddress] [nvarchar](128) NOT NULL,
	[date] [datetime] NOT NULL,
 CONSTRAINT [PK_user_login] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[user_verification]    Script Date: 9/3/2015 1:55:39 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[user_verification](
	[id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[token] [nvarchar](128) NOT NULL,
	[redeemed] [bit] NOT NULL,
	[date] [datetime] NOT NULL,
 CONSTRAINT [PK_user_verification] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[users]    Script Date: 9/3/2015 1:55:39 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[users](
	[id] [bigint] NOT NULL,
	[username] [nvarchar](256) NOT NULL,
	[password] [nvarchar](128) NOT NULL,
	[enabled] [bit] NOT NULL,
	[contact_opt_in] [bit] NOT NULL,
	[created_by] [bigint] NOT NULL,
	[created_dt] [datetime2](3) NOT NULL,
	[updated_by] [bigint] NOT NULL,
	[updated_dt] [datetime2](3) NOT NULL,
 CONSTRAINT [PK_users] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
INSERT [dbo].[accounts] ([id], [user_id], [type], [active], [always_active], [contact_opt_in], [start_dt], [expiration_dt], [created_by], [created_dt], [updated_by], [updated_dt]) VALUES (1, 1, N'TRIAL', 1, 1, 0, CAST(N'2015-09-03 13:42:45.300' AS DateTime), CAST(N'2015-09-17 13:42:45.307' AS DateTime), 1, CAST(N'2015-09-03 13:42:45.347' AS DateTime), 1, CAST(N'2015-09-03 13:42:45.347' AS DateTime))
INSERT [dbo].[authorities] ([id], [user_id], [authority]) VALUES (2, 1, N'ROLE_TRIAL')
INSERT [dbo].[authorities] ([id], [user_id], [authority]) VALUES (1, 1, N'ROLE_USER')
INSERT [dbo].[user_verification] ([id], [user_id], [token], [redeemed], [date]) VALUES (1, 1, N'1k4j58ochv51b96vl1pkj9dcqt', 1, CAST(N'2015-09-03 13:41:09.913' AS DateTime))
INSERT [dbo].[users] ([id], [username], [password], [enabled], [contact_opt_in], [created_by], [created_dt], [updated_by], [updated_dt]) VALUES (1, N'someone@wealthmsi.com', N'$2a$11$ptC7rrqd7zUmyJs0C41OZefq7PIrHAhHxAseUWcmzvZSSd9JbnVlC', 1, 0, 1, CAST(N'2015-09-03 13:41:09.8840000' AS DateTime2), 1, CAST(N'2015-09-03 13:42:45.3490000' AS DateTime2))
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_authorities_auth_username]    Script Date: 9/3/2015 1:55:39 PM ******/
ALTER TABLE [dbo].[authorities] ADD  CONSTRAINT [IX_authorities_auth_username] UNIQUE NONCLUSTERED 
(
	[user_id] ASC,
	[authority] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [UK_password_reset_token]    Script Date: 9/3/2015 1:55:39 PM ******/
ALTER TABLE [dbo].[password_reset] ADD  CONSTRAINT [UK_password_reset_token] UNIQUE NONCLUSTERED 
(
	[token] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [UK_user_verification_token]    Script Date: 9/3/2015 1:55:39 PM ******/
ALTER TABLE [dbo].[user_verification] ADD  CONSTRAINT [UK_user_verification_token] UNIQUE NONCLUSTERED 
(
	[token] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [UK_users]    Script Date: 9/3/2015 1:55:39 PM ******/
ALTER TABLE [dbo].[users] ADD  CONSTRAINT [UK_users] UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[accounts]  WITH CHECK ADD  CONSTRAINT [FK_accounts_users] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id])
GO
ALTER TABLE [dbo].[accounts] CHECK CONSTRAINT [FK_accounts_users]
GO
ALTER TABLE [dbo].[authorities]  WITH CHECK ADD  CONSTRAINT [FK_authorities_users] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id])
GO
ALTER TABLE [dbo].[authorities] CHECK CONSTRAINT [FK_authorities_users]
GO
ALTER TABLE [dbo].[password_reset]  WITH CHECK ADD  CONSTRAINT [FK_password_reset_users] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id])
GO
ALTER TABLE [dbo].[password_reset] CHECK CONSTRAINT [FK_password_reset_users]
GO
ALTER TABLE [dbo].[user_verification]  WITH CHECK ADD  CONSTRAINT [FK_user_verification_users] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id])
GO
ALTER TABLE [dbo].[user_verification] CHECK CONSTRAINT [FK_user_verification_users]
GO
