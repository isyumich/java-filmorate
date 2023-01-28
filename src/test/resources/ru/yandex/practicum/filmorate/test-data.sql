MERGE INTO MPA (id, name, min_age_for_watching) VALUES (1, 'G', 0);
MERGE INTO MPA (id, name, min_age_for_watching) VALUES (2, 'PG', 0);
MERGE INTO MPA (id, name, min_age_for_watching) VALUES (3, 'PG-13', 13);
MERGE INTO MPA (id, name, min_age_for_watching) VALUES (4, 'R', 17);
MERGE INTO MPA (id, name, min_age_for_watching) VALUES (5, 'NC-17', 17);

MERGE INTO genres (id, name) VALUES (1, 'Комедия');
MERGE INTO genres (id, name) VALUES (2, 'Драма');
MERGE INTO genres (id, name) VALUES (3, 'Мультфильм');
MERGE INTO genres (id, name) VALUES (4, 'Триллер');
MERGE INTO genres (id, name) VALUES (5, 'Документальный');
MERGE INTO genres (id, name) VALUES (6, 'Боевик');

MERGE INTO event_types (id, name) VALUES (1, 'FRIEND');
MERGE INTO event_types (id, name) VALUES (2, 'LIKE');
MERGE INTO event_types (id, name) VALUES (3, 'REVIEW');

MERGE INTO event_operations_types (id, name) VALUES (1, 'ADD');
MERGE INTO event_operations_types (id, name) VALUES (2, 'REMOVE');
MERGE INTO event_operations_types (id, name) VALUES (3, 'UPDATE');