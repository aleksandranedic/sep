-- PASSWORD = 'cascaded'
INSERT INTO ADMIN(ID, EMAIL, EMAIL_VERIFIED, FIRST_NAME, LAST_NAME, PASSWORD, ROLE, PHONE_NUMBER, CITY, PASSWORD_SET, LOGIN_ATTEMPTS, LAST_LOGIN_ATTEMPT, LOCKED_UNTIL, LOCK_REASON, TWO_FACTOR_KEY, DELETED)
VALUES ('e3661c31-d1a4-47ab-94b6-1c6500dccf24', 'admin@authservice.com', TRUE, 'Super', 'Admin',
        '$2a$10$Qg.gpYTtZiVMJ6Fs9QbQA.BtCx4106oSj92X.A/Gv7iAEKQXAg.gy', 'ROLE_ADMIN', '+48123456789', 'Warszawa', true, 0, '-1000000000-01-01T00:00:00Z', TIMESTAMP '-1000000000-01-01T00:00:00Z', null, 'F3OPURVECFTYHZXAM62YME7PVESQZXP7', false);