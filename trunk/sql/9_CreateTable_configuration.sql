USE [sgx]
GO
/****** Object:  Table [dbo].[configurations]    Script Date: 4/5/2016 2:30:03 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[configurations](
	[id] [nvarchar](50) NOT NULL,
	[Property] [nvarchar](50) NOT NULL,
	[Value] [varchar](50) NOT NULL,
	[ChangedBy] [nvarchar](50) NOT NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
INSERT [dbo].[configurations] ([id], [Property], [Value], [ChangedBy]) VALUES (N'1', N'dummy', N'dummy', N'dummy')
INSERT [dbo].[configurations] ([id], [Property], [Value], [ChangedBy]) VALUES (N'2', N'full.trial.duration', N'45', N'someone@wealthmsi.com')
INSERT [dbo].[configurations] ([id], [Property], [Value], [ChangedBy]) VALUES (N'3', N'halfway.trial.duration', N'23', N'someone@wealthmsi.com')
