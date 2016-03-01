USE [sgx]
GO
/****** Object:  Table [dbo].[enets]    Script Date: 3/1/2016 12:52:03 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[enets](
	[trans_Id] [nvarchar](50) NOT NULL,
	[active] [bit] NOT NULL,
	[user_id] [bigint] NOT NULL,
	[trans_dt] [datetime] NOT NULL,
	[id] [bigint] NOT NULL
) ON [PRIMARY]

GO
INSERT [dbo].[enets] ([trans_Id], [active], [user_id], [trans_dt], [id]) VALUES (N'123', 1, 1, CAST(N'2016-03-01 00:00:00.000' AS DateTime), 1)

GO
DECLARE @maxVal bigint;
DECLARE @seqSQL nvarchar(max);

SET @maxVal = (SELECT max(id)+1 FROM enets);
SET @seqSQL = N'CREATE SEQUENCE enets_seq AS bigint START WITH ' + cast(@maxVal as nvarchar(20)) + 'INCREMENT BY 1 NO CYCLE;';
EXEC SP_EXECUTESQL @seqSQL;

GO