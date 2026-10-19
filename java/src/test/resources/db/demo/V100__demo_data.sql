-- A-8: demo data for `./mvnw spring-boot:test-run` only (profile "demo"); tests use their own fixtures.
INSERT INTO timeDeposits (id, planType, days, balance) VALUES
    (1, 'basic',   45,  1234567.00),
    (2, 'student', 200, 1200.00),
    (3, 'student', 400, 1200.00),
    (4, 'premium', 46,  5000.50),
    (5, 'premium', 40,  5000.50),
    (6, 'basic',   31,  18.00);

INSERT INTO withdrawals (id, timeDepositId, amount, date) VALUES
    (1, 4, 100.25, DATE '2026-09-15'),
    (2, 4, 300.00, DATE '2026-10-01'),
    (3, 2, 50.00,  DATE '2026-10-05');
