ALTER TABLE watchlist_transaction
    ADD created_by bigint NOT NULL DEFAULT 1,
        created_dt datetime2(3) NOT NULL DEFAULT GETDATE(),
        updated_by bigint NOT NULL DEFAULT 1,
		updated_dt datetime2(3) NOT NULL DEFAULT GETDATE()

ALTER TABLE configurations
    ADD ChangedDt datetime2(3) NOT NULL DEFAULT GETDATE()
    
ALTER TABLE premium_verification
    ADD updated_dt datetime2(3) NOT NULL DEFAULT GETDATE()
    
ALTER TABLE user_verification
    ADD updated_dt datetime2(3) NOT NULL DEFAULT GETDATE()