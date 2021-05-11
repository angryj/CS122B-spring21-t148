package helper;

import java.util.ArrayList;

public class Movie {

	private String id;
	private String title;
	private String director;
	private int year;
	ArrayList<String> genres;
	
	
	public Movie() {
		genres = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDirector() {
		return director;
	}

	public int getYear() {
		return year;
	}

	public ArrayList<String> getGenres() {
		return genres;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void addGenre(String genre) {
		genres.add(genre);
	}


	public boolean isValid () {
		return getTitle() != null && getYear() != -1 && getDirector() != null;
	}
	@Override
	public String toString() {

		String s = "ID: " + getId() + ", Title: " + getTitle() + ", Director: " + getDirector() + ", Year: " + getYear();

		for (String i : genres) {
			s += ", " + i;
		}

		return s;
	}


}
