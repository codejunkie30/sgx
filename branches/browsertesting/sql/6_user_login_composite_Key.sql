USE [sgx]
GO

ALTER TABLE [sgx].[dbo].[user_login] 
DROP CONSTRAINT [PK_user_login]
GO

ALTER TABLE [sgx].[dbo].[user_login]
ADD CONSTRAINT [PK_user_login] PRIMARY KEY (id,username,date)