CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO books (id, title, author, publication_year) VALUES
(gen_random_uuid(), 'Clean Code', 'Robert C. Martin', 2008),
(gen_random_uuid(), 'Effective Java', 'Joshua Bloch', 2018),
(gen_random_uuid(), 'Design Patterns', 'Erich Gamma', 1994),
(gen_random_uuid(), 'Refactoring', 'Martin Fowler', 2018),
(gen_random_uuid(), 'Spring in Action', 'Craig Walls', 2018);