About

	XFDataDump is an exe file run on the XF_LOADER machine to export the data needed for SGX from the S&P ExpressFeed Database. It does the following

	1) It makes a request to the SGX website for a list of companies to include within the population.
	2) Creates a temporary table, with the SGX name overrides (from file above) as well as some info from the S&P database. See createTickerTable.sql in the XFDataDump project for more
	3) Dumps the following files
		a) CSV of the temporary table (described above created using exportTickerTable.sql)
		b) CSV of the unique currencies (created the exportUniqueCurrency.sql)
		c) CSV of companies in the SGX provided list that could not be matched with the S&P database
	4) Runs through a list of query files (.sql) in a directory configured with the exe config and dumps the data into CSV files using the same name (with CSV extension instead of SQL)
	5) Places the CSV files created from 3 & 4 above onto the FTP(S) server also configured with the exe config.
	
	The queries folder, contained in this project, is where you'll find the queries executed to perform the rest of the data dumps needed. As noted in #4 above, the process loops through these and creates CSV files with corresponding names.  
	Most, if not all, of these should contain a reference to ##sgxpop - the temporary table created in #2 above.   This allows you to constrain queries just to the SGX population of companies and not the entire universe.
	
	Because of the way we need to to build the company coverage list, most of the code in XFDataDump handles the parsing and importing of the SGX provided CSV.  This code, along with the queries to update/export base company and currency info relies upon a pretty rigid format. See sample_sgx_ticker_file.csv for exact format.

Configuration

	The output of the XFDataDump project is both an exe and conf file.  The conf file has the following parameters

	1) tmpDir - this is the temporary directory that all created files are written to
	2) sqlDir - this is the directory with the sql files to execute (#4 above)
	3) tickerURL - the SGX ticker file URL
	4) companiesFileName - this is the dump file created in #3a
	5) currenciesFileName - this is the dump file created in #3b
	6) notFoundFileName - this is the dump file created in #3c
	7) ftpURL - the url of the ftp site to put all files
	8) ftpUsername - the username for the ftp site
	9) ftpPassword - the password for the ftp site
	10) logLevel - for determining what happens in the log
	11) archiveFile - the file where current run will be saved
	12) xtraTickerURL - the additional tickers (not SGX listed) to pull
	13) sgxLog - dropped in the tmpDir (and eventually the archive zip) the log of events
	14) companiesTableName - the temp table created by the process that the SQL scripts will use to identify the SGX population
	
	
	In addition to these, the exe needs to be scheduled using task scheduler.  This should be done on the same machine as the loader database/program.  More info on scheduling can be found at http://www.howtogeek.com/school/using-windows-admin-tools-like-a-pro/lesson2/all/.