INSERT INTO users (username, password)
VALUES ('admin', '$2a$10$w7XHj5a3y7sQEP0HysyA1uFMnTgfFuQfqOsZqRuM4gF0g3w6ViOyS')
ON CONFLICT (username) DO NOTHING;