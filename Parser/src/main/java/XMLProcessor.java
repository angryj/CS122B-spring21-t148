import helper.Movie;
import helper.Star;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class XMLProcessor {

    private String loginUser;
    private String loginPasswd;
    private String loginUrl;
    private HashSet<Star> newStars;
    private Map<String, Movie> movies;
    private HashSet<String> genres;
    private Map<String, HashSet<String>> starsMovies;

    private int starDuplicates;
    private int starNameErrors;

    private int nameErrors;
    private int saCount;

    private int movieDuplicates;
    private int titleErrors;
    private int dirErrors;
    private int yearErrors;
    private int nullYears;

    private int insertMovieDuplicates;
    private int insertMovies;

    private int insertStars;
    private int insertStarsDuplicates;
    private int nullMovies;

    public XMLProcessor() {
        loginUser = "mytestuser";
        loginPasswd = "My6$Password";
        loginUrl = "jdbc:mysql://localhost:3306/moviedb?allowMultiQueries=true";
        insertMovieDuplicates = 0;
        insertMovies = 0;
        insertStars = 0;
        insertStarsDuplicates = 0;
        nullMovies = 0;

    }

    public void parseAll() {
        System.out.println("Parsing actors63.xml");
        ActorsParser mh = new ActorsParser();
        mh.runParser();
        newStars = mh.stars;
        starDuplicates = mh.getDuplicates();
        starNameErrors = mh.getNameErrors();

        System.out.println("Parsing mains243.xml");
        MainsParser mh2 = new MainsParser();
        mh2.runParser();
        movies = mh2.movies;
        genres = mh2.genres;
        movieDuplicates = mh2.getDuplicates();
        titleErrors = mh2.getTitleErrors();
        dirErrors = mh2.getDirErrors();
        yearErrors = mh2.getYearErrors();
        nullYears = mh2.getNullYears();

        System.out.println("Parsing casts124.xml");
        CastsParser mh3 = new CastsParser();
        mh3.runParser();
        starsMovies = mh3.starsMovies;
        nameErrors = mh3.getNameErrors();
        saCount = mh3.getSaCount();
    }

    public void logger() {
        try {
            FileWriter myWriter = new FileWriter("stats4nerds.log");
            myWriter.write("--Errors while parsing `actors63.xml`--\n");
            myWriter.write("# of invalid names: " + starNameErrors + "\n");
            myWriter.write("# of duplicates: " + starDuplicates + "\n");
            myWriter.write("\n--Errors while parsing `mains243.xml`--\n");
            myWriter.write("# of duplicates: " + movieDuplicates + "\n");
            myWriter.write("# of missing titles: " + titleErrors + "\n");
            myWriter.write("# of missing directors: " + dirErrors + "\n");
            myWriter.write("# of missing years: " + yearErrors + "\n");
            myWriter.write("# of invalid years: " + nullYears + "\n");
            myWriter.write("\n--Errors while parsing `casts124.xml`--\n");
            myWriter.write("# of NULL names: " + nameErrors + "\n");
            myWriter.write("# of unknown actors: " + saCount + "\n");

            myWriter.write("\n--Errors while inserting--\n");
            myWriter.write("# of duplicate movies already in database: " + insertMovieDuplicates + "\n");
            myWriter.write("# of duplicate stars already in database: " + insertStarsDuplicates + "\n");
            myWriter.write("# of NULL movie ids: " + insertStarsDuplicates + "\n");

            myWriter.write("\n--Other Data--\n");
            myWriter.write("# of movies added: " + insertMovies + "\n");
            myWriter.write("# of stars: " + insertStars + "\n");
            myWriter.write("# of genres found in the xml files: " + genres.size() + "\n");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void insertAll() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Statement statement = conn.createStatement();

            String existingGenresQuery = "SELECT name, id FROM genres";
            PreparedStatement existingGenresStatement = conn.prepareStatement(existingGenresQuery);
            ResultSet rs = existingGenresStatement.executeQuery();
            HashMap<String, Integer> existingGenres = new HashMap<String, Integer>();
            while (rs.next()) {
                existingGenres.put(rs.getString(1), rs.getInt(2));
            }

            String existingStarsQuery = "SELECT name, birthYear, id FROM stars";
            String existingMoviesQuery = "SELECT title, year FROM movies";
            PreparedStatement existingStarsStatement = conn.prepareStatement(existingStarsQuery);
            PreparedStatement existingMoviesStatement = conn.prepareStatement(existingMoviesQuery);
            HashSet<Integer> starsHash = new HashSet<>();
            HashSet<Integer> moviesHash = new HashSet<>();
            HashMap<String, String> existingStars = new HashMap<>();
            ResultSet existingMoviesRS = existingMoviesStatement.executeQuery();
            ResultSet existingStarsRS = existingStarsStatement.executeQuery();

            while (existingMoviesRS.next()) {
                moviesHash.add(existingMoviesRS.getString(1).hashCode() + existingMoviesRS.getInt(2));
            }

            while (existingMoviesRS.next()) {
                starsHash.add(existingStarsRS.getString(1).hashCode() + existingStarsRS.getInt(2));
                existingStars.put(existingStarsRS.getString(1), existingStarsRS.getString(3));
            }

            String maxMovieIdQuery = "SELECT max(id) FROM movies";
            PreparedStatement maxMovieIdStatement = conn.prepareStatement(maxMovieIdQuery);
            ResultSet maxMovieIdRS = maxMovieIdStatement.executeQuery();
            String maxMovieId = "";
            while (maxMovieIdRS.next()) {
                System.out.println(maxMovieId);
                maxMovieId = maxMovieIdRS.getString(1);
            }
            int maxMovieIdInt = Integer.parseInt(maxMovieId.substring(2)) + 1;

            String maxStarIdQuery = "SELECT max(id) FROM stars";
            PreparedStatement maxStarIdStatement = conn.prepareStatement(maxStarIdQuery);
            ResultSet maxSTARIdRS = maxStarIdStatement.executeQuery();
            String maxStarId = "";
            while (maxSTARIdRS.next()) {
                maxStarId = maxSTARIdRS.getString(1);
            }
            int maxStarIdInt = Integer.parseInt(maxStarId.substring(2)) + 1;

            String maxGenreIdQuery = "SELECT max(id) FROM genres";
            PreparedStatement maxGenreIdStatement = conn.prepareStatement(maxGenreIdQuery);
            ResultSet maxGenreIdRS = maxGenreIdStatement.executeQuery();
            int maxGenreIdInt = 0;
            while (maxGenreIdRS.next()) {
                maxGenreIdInt = maxGenreIdRS.getInt(1) + 1;
            }
            String movieInsert = "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?);";
            PreparedStatement movieInsertStatement = conn.prepareStatement(movieInsert);
            String genresInsert = "INSERT INTO genres (id, name) VALUES (?, ?);";
            PreparedStatement genresInsertStatement = conn.prepareStatement(genresInsert);
            String genresInMoviesInsert = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?);";
            PreparedStatement genresInMoviesStatement = conn.prepareStatement(genresInMoviesInsert);
            HashMap<String, String> fidToMovieId = new HashMap<>();

            for (Map.Entry<String, Movie> e : movies.entrySet()) {
                String key = e.getKey();
                Movie m = e.getValue();
                int mHash = m.getTitle().hashCode() + m.getYear();
                if (moviesHash.contains(mHash)) {
                    System.out.println("Error: duplicate movie `" + m.getTitle() + "` detected");
                    insertMovieDuplicates += 1;
                } else {
                    moviesHash.add(hashCode());
                    int zeroes = 0;
                    if (Integer.toString(maxMovieIdInt).length() < 7) {
                        zeroes = 7 - Integer.toString(maxMovieIdInt).length();
                    }
                    String id = "tt" + zeroes*0 + maxMovieIdInt;
                    movieInsertStatement.setString(1, id);
                    movieInsertStatement.setString(2, m.getTitle());
                    movieInsertStatement.setInt(3, m.getYear());
                    movieInsertStatement.setString(4, m.getDirector());
                    fidToMovieId.put(key, id);
                    System.out.println(key);
                    System.out.println(id);

                    //genres & genres-in-movies
                    for (String g: m.getGenres()) {
                        if (existingGenres.keySet().contains(g)) {
                            genresInMoviesStatement.setInt(1, existingGenres.get(g));
                            genresInMoviesStatement.setString(2, id);
                            genresInMoviesStatement.addBatch();
                        } else {
                            existingGenres.put(g, maxGenreIdInt);
                            genresInsertStatement.setInt(1, maxGenreIdInt);
                            genresInsertStatement.setString(2, g);
                            genresInsertStatement.addBatch();

                            genresInMoviesStatement.setInt(1, maxGenreIdInt);
                            genresInMoviesStatement.setString(2, id);
                            genresInMoviesStatement.addBatch();
                            maxGenreIdInt += 1;
                        }
                    }

                    movieInsertStatement.addBatch();
                    maxMovieIdInt += 1;
                    insertMovies += 1;
                }
            }
            genresInsertStatement.executeBatch();
            movieInsertStatement.executeBatch();
            genresInMoviesStatement.executeBatch();
            System.out.println("Finished batch insert for tables Movies and Genres_In_Movies");
            String starsInsert = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?);";
            PreparedStatement starsInsertStatement = conn.prepareStatement(starsInsert);
            HashMap<String, String> starNameToId = new HashMap<>();
            for (Star x: newStars) {
                int sHash = x.getName().hashCode() + x.getBirthYear();
                if (starsHash.contains(sHash)) {
                    System.out.println("Error: duplicate star `" + x.getName() + "` detected");
                    insertStarsDuplicates += 1;
                } else {
                    starsHash.add(sHash);
                    String id = "nm" + maxStarIdInt;
                    starsInsertStatement.setString(1, id);
                    starsInsertStatement.setString(2, x.getName());
                    starNameToId.put(x.getName(), id);
                    if (x.getBirthYear() != -1) {
                        starsInsertStatement.setInt(3, x.getBirthYear());
                    } else {
                        starsInsertStatement.setNull(3, java.sql.Types.INTEGER);
                    }
                    starsInsertStatement.addBatch();
                    maxStarIdInt += 1;
                    insertStars += 1;
                }

            }
            starsInsertStatement.executeBatch();
            System.out.println("Finished batch insert for table Stars");

            String starsInMoviesInsert = "INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?);";
            PreparedStatement starsInMoviesInsertStatement = conn.prepareStatement(starsInMoviesInsert);
            for (Map.Entry<String, HashSet<String>> e : starsMovies.entrySet()) {
                String id = fidToMovieId.get(e.getKey());
                if (id == null) {
                    System.out.println("Error: Abnormal state; NULL movieId, skipping");
                    nullMovies += 1;
                }
                else {
                    for (String s : e.getValue()) {
                        if (starNameToId.containsKey(s)) {
                            starsInMoviesInsertStatement.setString(1, starNameToId.get(s));
                            starsInMoviesInsertStatement.setString(2, id);
                            starsInMoviesInsertStatement.addBatch();
                        }
                        else if (existingStars.containsKey(s)) {
                            starsInMoviesInsertStatement.setString(1, existingStars.get(s));
                            starsInMoviesInsertStatement.setString(2, id);
                            starsInMoviesInsertStatement.addBatch();
                        }
                        else {
                            System.out.println("Error: star `" + s + "` does not exist in the database");
                        }
                    }
                }
            }
            starsInMoviesInsertStatement.executeBatch();
            System.out.println("Finished batch insert for table stars_in_movies");
            logger();
        } catch (Exception e) {
            System.out.println(e);
        }
    }



    public static void main(String[] args){
        /*
        String loginUser = "root";
        String loginPasswd = "";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        */
        XMLProcessor a = new XMLProcessor();
        a.parseAll();
        a.insertAll();





    }
}
