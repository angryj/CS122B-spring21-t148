USE moviedb;
DROP PROCEDURE IF EXISTS add_movie;
DELIMITER $$
CREATE PROCEDURE add_movie
(
	IN
	movie_title varchar(100),
    movie_year integer,
    movie_director varchar(100),
    star varchar(100),
    genre varchar(32)
)
BEGIN
	DECLARE max_movie varchar(10) DEFAULT 0;
	DECLARE new_movie_id INT DEFAULT 0;
    DECLARE new_movie varchar(10) DEFAULT 0;
    
    DECLARE max_star varchar(10) DEFAULT 0;
    DECLARE new_star_id INT DEFAULT 0;
    DECLARE new_star varchar(10) DEFAULT 0;
    
    DECLARE max_genre INT DEFAULT 0;
    DECLARE new_genre_id INT DEFAULT 0;
    
	SET max_movie = (SELECT max(id) from movies);
    SET new_movie_id = (CONVERT(SUBSTRING(max_movie, 3), unsigned INTEGER));
    SET new_movie_id = new_movie_id + 1;
    SET new_movie = CONCAT("tt", new_movie_id);
    
    SET max_star = (SELECT max(id) from stars);
    SET new_star_id = (CONVERT(SUBSTRING(max_star, 3), unsigned INTEGER));
	SET new_star_id = new_star_id + 1;
    SET new_star = CONCAT("nm",new_star_id);
    
    
	IF (select count(*) from stars where  stars.name = star) = 0 THEN
        INSERT INTO stars VALUES (new_star, star,NULL);
    ELSE
		SET new_star = (select id from stars where stars.name =star limit 1);
    END IF;
    
    IF (select count(*) from genres where genres.name = genre) =  0 THEN
		INSERT INTO genres(`name`) VALUE (genre);
    ELSE
		SET new_genre_id = (select id from genres where genres.name = genre limit 1);

	END IF;
    
    INSERT INTO movies VALUES (new_movie, movie_title, movie_year, movie_director);
    INSERT INTO stars_in_movies VALUES (new_star, new_movie);
    INSERT INTO  genres_in_movies VALUES (new_genre_id, new_movie);
    INSERT INTO ratings VALUES(new_movie,10.0,0);
	
END$$
DELIMITER ;
