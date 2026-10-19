-- Data integrity, enforced by the database whatever the caller (V1 stays untouched once applied).
-- NUMERIC(15, 2): balances below 10^13, where the legacy double still holds every cent exactly (A-5).
-- An update that would cross it fails and rolls back instead of storing a wrong cent.

ALTER TABLE timeDeposits
    ALTER COLUMN balance TYPE NUMERIC(15, 2),
    ADD CONSTRAINT time_deposits_balance_not_negative CHECK (balance >= 0),
    ADD CONSTRAINT time_deposits_days_not_negative CHECK (days >= 0);

-- A-2: a withdrawal is money taken out, so its amount is positive.
ALTER TABLE withdrawals
    ALTER COLUMN amount TYPE NUMERIC(15, 2),
    ADD CONSTRAINT withdrawals_amount_positive CHECK (amount > 0);
