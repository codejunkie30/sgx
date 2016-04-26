USE [sgx]
GO
/****** Object:  Table [dbo].[premium_verification]    Script Date: 10/20/2015 1:13:25 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[premium_verification](
	[id] [bigint] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[token] [nvarchar](128) NOT NULL,
	[redeemed] [bit] NOT NULL,
	[date] [datetime] NOT NULL,
 CONSTRAINT [PK_premium_verification] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
