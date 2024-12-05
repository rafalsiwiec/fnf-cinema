CREATE TABLE show
(
    id         UUID           NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    movie_id   UUID           NOT NULL REFERENCES movie (id) ON DELETE CASCADE,
    amount     numeric(17, 2) NOT NULL,
    currency   VARCHAR(4)     NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX idx_show_start_time ON show (start_time);
CREATE INDEX idx_show_movie_id_start_time ON show (movie_id, start_time);

