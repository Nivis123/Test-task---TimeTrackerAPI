INSERT INTO users (username, password)
VALUES ('admin', '$2a$10$gt08Z4Kz/Pa.ehfvdP0nDeaP0o9SzsXvv06uw.SA3pKghnylSiR1C')
ON CONFLICT (username) DO NOTHING;