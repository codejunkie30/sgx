USE [sgx]
GO
/****** Object:  Table [dbo].[accounts]    Script Date: 8/13/2015 4:06:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[accounts](
	[id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[type] [nvarchar](50) NOT NULL,
	[active] [bit] NOT NULL,
	[start_dt] [datetime] NOT NULL,
	[expiration_dt] [datetime] NOT NULL,
	[created_by] [bigint] NOT NULL,
	[created_dt] [datetime] NOT NULL,
	[updated_by] [bigint] NOT NULL,
	[updated_dt] [datetime] NOT NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Assets]    Script Date: 8/13/2015 4:06:32 PM ******/
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
/****** Object:  Table [dbo].[authorities]    Script Date: 8/13/2015 4:06:32 PM ******/
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [IX_authorities_auth_username] UNIQUE NONCLUSTERED 
(
	[user_id] ASC,
	[authority] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[password_reset]    Script Date: 8/13/2015 4:06:32 PM ******/
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_password_reset_token] UNIQUE NONCLUSTERED 
(
	[token] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[trade_events]    Script Date: 8/13/2015 4:06:32 PM ******/
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
/****** Object:  Table [dbo].[user_login]    Script Date: 8/13/2015 4:06:32 PM ******/
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
/****** Object:  Table [dbo].[user_verification]    Script Date: 8/13/2015 4:06:32 PM ******/
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_user_verification_token] UNIQUE NONCLUSTERED 
(
	[token] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[users]    Script Date: 8/13/2015 4:06:32 PM ******/
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
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_users] UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

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
