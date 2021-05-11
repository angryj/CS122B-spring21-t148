import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CastsParser extends DefaultHandler {
    public Map<String, HashSet<String>> starsMovies;

    private String name;
    private String fid;
    private String tempVal;
    private int nameErrors;
    private int saCount;

    public int getNameErrors() {
        return nameErrors;
    }

    public int getSaCount() {
        return saCount;
    }

    public CastsParser() {
        starsMovies = new HashMap<String, HashSet<String>>();
        nameErrors = 0;
        saCount = 0;
    }

    public static void main(String[] args) {
        CastsParser mh = new CastsParser();
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
            sp.parse("casts124.xml", this);
            System.out.println("Finished parsing stars in movies");
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
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        String result = tempVal.trim();

        if (qName.equalsIgnoreCase("m") && name != null) {
            if (starsMovies.containsKey(fid)) {
                starsMovies.get(fid).add(name);
            } else {
                HashSet<String> names = new HashSet<String>();
                names.add(name);
                starsMovies.put(fid, names);
            }
        } else if (qName.equalsIgnoreCase("f")) {
            fid = result;
        } else if (qName.equalsIgnoreCase("a")) {
            if (!(tempVal.isEmpty())) {
                if (tempVal.equals("s a")) {
                    name = null;
                    saCount += 1;
                } else {
                    name = tempVal;
                }
            } else {
                name = null;
                System.out.println("ERROR: NULL Star name.");
                nameErrors += 1;
            }
        }
    }

    private void printData() {
        /*
        for (Map.Entry<String, HashSet<String>> entry : starsMovies.entrySet()) {
            System.out.println("<<< Movie Id: " + entry.getKey() + " >>>");
            for (String s : entry.getValue()) {
                System.out.println(s);
            }
        }
        */
        System.out.println("\nStats for Nerds:");
        System.out.println("# of invalid names: " + nameErrors);
        System.out.println("# of unknown actors: " + saCount);
        System.out.println("# of casts " + starsMovies.size());
    }


}
