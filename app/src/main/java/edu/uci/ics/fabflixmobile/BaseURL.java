package edu.uci.ics.fabflixmobile;

public class BaseURL {
    private final String host = "10.0.0.158";
    private final String port = "8443";
    private final String domain = "blah";
    //public final String baseURL = "https://" + host + ":" + port + "/" + domain;
    //modify the baseURL to the EC2 Instance when we submit, modify this file for your own convenience
    //until then
    public final String baseURL = "https://" + host + ":" + port + "/" + domain;
}
