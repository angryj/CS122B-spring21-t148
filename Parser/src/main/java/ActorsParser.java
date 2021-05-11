import java.io.IOException;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import helper.Star;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ActorsParser extends DefaultHandler {

    public HashSet<Star> stars;
    private Star star;
    private String tempVal;
    private int duplicates;
    private int nameErrors;

    public int getDuplicates() {
        return duplicates;
    }

    public int getNameErrors() {
        return nameErrors;
    }

    public ActorsParser() {
        stars = new HashSet<Star>();
        duplicates = 0;
        nameErrors = 0;
    }


    public static void main(String[] args) {
        ActorsParser mh = new ActorsParser();
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
            sp.parse("actors63.xml", this);
            System.out.println("Finished parsing stars");
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
        if (qName.equalsIgnoreCase("actor")) {
            star = new Star();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        String result = tempVal.trim();
        if (qName.equalsIgnoreCase("actor")) {
            if (star.isValid()) {
                if (!stars.contains(star)) {
                    stars.add(star);
                } else {
                    System.out.println("ERROR: Failed to parse " + star.getName() + ". Star already exists");
                    duplicates += 1;
                }

            }

        } else if (qName.equalsIgnoreCase("stagename")) {
            if (!(tempVal.isEmpty())) {
                star.setName(tempVal);
            } else {
                System.out.println("ERROR: Failed to parse Star due to a NULL value for the name field");
                star.setName(null);
                nameErrors += 1;
            }
        } else if (qName.equalsIgnoreCase("dob")) {
            try {
                if (!(result.isEmpty())) {
                    star.setBirthYear(Integer.parseInt(result));
                } else {
                    star.setBirthYear(-1);
                }
            } catch (Exception e) {
                star.setBirthYear(-1);
            }
        }
    }

    private void printData() {
        /*
        for (Star i : stars) {
            System.out.println(i);
        }
         */
        System.out.println("\nStats for Nerds:");
        System.out.println("# of duplicates: " + duplicates);
        System.out.println("# of invalid names: " + nameErrors);
        System.out.println("# of stars: " + stars.size() + ".");

    }


}