USE SGX;

DELETE FROM accounts
WHERE user_id != '1'

DELETE FROM authorities
WHERE user_id != '1'

DELETE FROM password_reset

DELETE FROM user_verification

DELETE FROM premium_verification

DELETE FROM user_login

DELETE FROM users
WHERE id != '1'

DELETE FROM watchlist

DELETE FROM watchlist_company

DELETE FROM watchlist_option

DELETE FROM enets 
Where user_id != '1'

DELETE FROM email_audit
Where user_id != '1'

DELETE FROM watchlist_transaction
Where id ! = '1';