USE [sgx]
GO
ALTER TABLE [dbo].[accounts]
ADD currency nvarchar(8) NULL
Default ('SGD')  
GO 