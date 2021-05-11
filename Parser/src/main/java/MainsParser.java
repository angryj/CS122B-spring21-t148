import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import helper.Movie;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MainsParser extends DefaultHandler{

	public Map<String, Movie> movies;
	public HashSet<String> genres;

	private Movie movie;
	private String tempVal;
	private String director;
	private int duplicates;
	private int titleErrors;
	private int dirErrors;
	private int yearErrors;
	private int nullYears;

    public int getDuplicates() {
        return duplicates;
    }

    public int getTitleErrors() {
        return titleErrors;
    }

    public int getDirErrors() {
        return dirErrors;
    }

    public int getYearErrors() {
        return yearErrors;
    }

    public int getNullYears() {
        return nullYears;
    }

    public MainsParser() {
		genres = new HashSet<String>();
		movies = new HashMap<String, Movie>();
		duplicates = 0;
		titleErrors = 0;
		dirErrors = 0;
		yearErrors = 0;
		nullYears = 0;
	}

	public static void main(String[] args) {
		MainsParser mh = new MainsParser();
		mh.runParser();
	}


	public void runParser() {
		parseDocument();
		printData();
	}

	private void parseDocument() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
	   		SAXParser sp = spf.newSAXParser();
		   	sp.parse("mains243.xml", this);
		   	System.out.println("Finished parsing movies and genres");
		} catch (SAXException se) {
		   	se.printStackTrace();
		} catch (ParserConfigurationException pce) {
		   	pce.printStackTrace();
		} catch (IOException ie) {
		   	ie.printStackTrace();
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempVal = "";
		if (qName.equalsIgnoreCase("film")) {
			movie = new Movie();
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		String result = tempVal.trim();
		if (qName.equalsIgnoreCase("film")) {
			if (!(movie.getId() == null)) {
				movie.setDirector(director);
				if (movie.isValid()) {
					if (movies.containsKey(movie.getId())){
						System.out.println("ERROR: Failed to parse  `" + movie.getId() + "` - `" + movie.getTitle() + "`. title already exists.");
					} else {
						movies.put(movie.getId(), movie);
					}
				}
			} else {
				System.out.println("ERROR: Missing Fid for `" + movie.getTitle() + "`.");
				duplicates += 1;
			}
		}
		else if (qName.equalsIgnoreCase("t")) {
			if (!(tempVal.isEmpty())) {
				movie.setTitle(tempVal);
			} else {
				System.out.println("ERROR: `" + movie.getTitle() + "` title.");
				movie.setTitle(null);
				titleErrors += 1;
			}

		} else if (qName.equalsIgnoreCase("fid")) {
			if (!(result.isEmpty())) {
				movie.setId(result);
			} else {
				System.out.println("ERROR: Missing Fid for `" + movie.getTitle() + "`.");
				movie.setId(null);
			}

		} else if (qName.equalsIgnoreCase("year")) {

			try{
				if (!(result.isEmpty())) {
					movie.setYear(Integer.parseInt(result));
				} else {
					System.out.println("ERROR: Failed to parse `" + movie.getTitle()+ "` due to a NULL value for the year field.");
					movie.setYear(-1);
					nullYears += 1;
				}
			} catch (Exception e) {
				System.out.println("ERROR: Failed to parse `" + movie.getTitle()+ "` due to a INVALID value for the year field.");
				movie.setYear(-1);
				yearErrors += 1;
			}

		} else if (qName.equalsIgnoreCase("dirname")) {

			if (!(result.isEmpty())) {
				director = result;
			} else {
				System.out.println("ERROR: Failed to parse `" + movie.getTitle()+ "` due to a NULL value for the director field.");
				director = null;
				dirErrors += 1;
			}

		} else if (qName.equalsIgnoreCase("cat")) {
			if (!(result.isEmpty())) {
				ArrayList<String> results = handleCat(result.toLowerCase());
				for (String s : results) {
					genres.add(s);
					movie.addGenre(s);
				}
			}
		}

	}

	private ArrayList<String> handleCat(String s) {
		ArrayList<String> cats = new ArrayList<>();

		if (s.matches(".*rom.*")) { cats.add("Romance"); }
		if (s.matches(".*comd.*") || s.matches("co.d") || s.matches("c.md")) { cats.add("Comedy"); }
		if (s.matches(".*myst.*")) { cats.add("Mystery"); }
		if (s.matches(".*sus.*")) { cats.add("Thriller"); }
		if (s.matches(".*dram.*")) { cats.add("Drama"); }
		if (s.matches("act.*") || s.matches("a.tn")|| s.matches("ac.n")) { cats.add("Action"); }
		if (s.matches(".*hor.*")) { cats.add("Horror"); }
		if (s.matches(".*hist.*")) { cats.add("History"); }
		if (s.matches(".*docu.*") || s.matches("d.c.")) { cats.add("Documentary"); }
		if (s.matches(".*muscl.*")||s.matches(".*musical.*")) { cats.add("Musical"); }
		if (s.matches(".*musc.*")) { cats.add("Music"); }
		if (s.matches(".*faml.*")) { cats.add("Family"); }
		if (s.matches(".*west.*")) { cats.add("Western"); }
		if (s.matches(".*advt.*")) { cats.add("Adventure"); }
		if (s.matches(".*crim.*")) { cats.add("Crime"); }
		if (s.matches(".*fant.*")) { cats.add("Fantasy"); }
		if (s.matches(".*sports.*")) { cats.add("Sport"); }
		if (s.matches("bio.*")) { cats.add("Biography"); }
		if (s.matches(".*kinky.*")||s.matches("por.*")) { cats.add("Adult"); }
		if (s.matches("s.*f.*")) { cats.add("Sci-Fi"); }
		//New genres
		if (s.matches(".*noir.*")) { cats.add("Noir"); }
		if (s.matches(".*sur.*")) { cats.add("Surrealist"); }
		if (s.matches(".*avant.*")) { cats.add("Avant-Garde"); }
		if (s.matches(".*epic.*")) { cats.add("Epic"); }
		if (s.matches(".*weird.*")) { cats.add("Weird"); }
		if (s.matches(".*expm.*")) { cats.add("Experimental"); }
		if (s.matches(".*psyc.*")) { cats.add("Psychological"); }
		if (s.matches(".*cnr.*") || s.matches("c.r") || s.matches(".nr")) { cats.add("Cops and Robbers"); }
		if (s.matches(".*tvm.*")) { cats.add("TV miniseries"); }
		else if (s.matches(".*tvs.*")) { cats.add("TV series"); }
		else if (s.matches(".*tv.*")) { cats.add("TV show"); }
		if (s.matches(".*homo.*")) { cats.add("Homoerotic"); }
		if (s.matches(".*cult.*")) { cats.add("Cult Classic"); }
		if (s.matches(".*sati.*")) { cats.add("Satire"); }
		if (s.matches(".*cart.*") || s.matches("ct.*")) { cats.add("Cartoon"); }

		if (cats.size() == 0) {
			cats.add(s.substring(0, 1).toUpperCase() + s.substring(1));
		}

		return cats;
	}


	private void printData() {
		/*
	    for (Movie m : movies.values()) {
			System.out.println(m.toString());
		}
		*/

		/*
	    Iterator<String> iter = genres.iterator();
	    while (iter.hasNext()) {
	        System.out.println(iter.next());
	    }
		*/

		System.out.println("\nStats for Nerds:");
		System.out.println("# of duplicates: " + duplicates);
		System.out.println("# of missing titles: " + titleErrors);
		System.out.println("# of missing directors: " + dirErrors);
		System.out.println("# of missing years: " + nullYears);
		System.out.println("# of invalid years: " + yearErrors);
	    System.out.println("# of movies " + movies.size());
	    System.out.println("# of genres '" + genres.size());

	}


}
