CREATE TABLE IF NOT EXISTS movie (
    id UUID NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    title TEXT NOT NULL,
    imdb_id VARCHAR(10) NOT NULL
);

INSERT INTO movie (title, imdb_id) VALUES ('The Fast and the Furious', 'tt0232500');
INSERT INTO movie (title, imdb_id) VALUES ('2 Fast 2 Furious', 'tt0322259');
INSERT INTO movie (title, imdb_id) VALUES ('The Fast and the Furious: Tokyo Drift', 'tt0463985');
INSERT INTO movie (title, imdb_id) VALUES ('Fast & Furious', 'tt1013752');
INSERT INTO movie (title, imdb_id) VALUES ('Fast Five', 'tt1596343');
INSERT INTO movie (title, imdb_id) VALUES ('Fast & Furious 6', 'tt1905041');
INSERT INTO movie (title, imdb_id) VALUES ('Furious 7', 'tt2820852');
INSERT INTO movie (title, imdb_id) VALUES ('The Fate of the Furious', 'tt4630562');
INSERT INTO movie (title, imdb_id) VALUES ('F9: The Fast Saga', 'tt5433138');
                      
                     