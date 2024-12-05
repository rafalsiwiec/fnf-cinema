CREATE TABLE IF NOT EXISTS movie (
    id UUID NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    title TEXT NOT NULL,
    imdb_id VARCHAR(10) NOT NULL
);
                     