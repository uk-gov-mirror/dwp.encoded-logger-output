package uk.gov.dwp.logging;


public class Sanitiser {

    private static final String LOG_STANDARD_REGEX = "[\\u0000-\\u001f]";
    
    public String sanitise(String original) {
        return original.replaceAll(LOG_STANDARD_REGEX, "");
    }

}
