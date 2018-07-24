package uk.gov.dwp.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.format.DateTimeFormatter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DwpEncodedLoggerTest {

    private static final String TEST_APP_NAME = "Test Application Name";
    private static final String TEST_APP_VERSION = "0.0.0";

    private void runAsserts(String unencodedString, String encodedString) throws IOException {
        System.out.println(String.format("unencodedString:%s%n", unencodedString));
        System.out.println(String.format("encodedString:%s%n", encodedString));

        String lastFileEntry = getLastFileEntry();

        assertThat("should not contain the passed log", lastFileEntry, not(containsString(unencodedString)));
        assertThat("should be encoded", lastFileEntry, containsString(new ObjectMapper().writeValueAsString(encodedString)));
        assertNotNull("should produce valid ISO 8601 datetime", DateTimeFormatter.ISO_INSTANT.parse(
                new ObjectMapper().readTree(lastFileEntry).get("ts").asText()));
    }

    @Test
    public void testSixParameterObjectArrayInfoMessageIsOk() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        instance.info("{} {} {} {} {} {}", "l\n\n\n\tog", 1, true, Arrays.asList("o\t\nne", "t\0wo", "thr\tee"), "hell\n\no", false);

        runAsserts("l\n\n\n\tog 1 true o\t\nne t\0wo thr\tee hell\n\no falsee", "log 1 true [one, two, three] hello false");
    }

    @Test
    public void testThreeParameterObjectArrayInfoMessageIsOk() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        Object[] paramArray = new Object[]{"l\n\n\n\tog", 1, true};

        instance.info("{} {} {}", paramArray);
        runAsserts("l\n\n\n\tog 1 true", "log 1 true");
    }

    @Test
    public void testThreeParameterInfoMessageIsOk() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        instance.info("{} {} {}", "l\n\n\n\tog", 1, true);

        runAsserts("l\n\n\n\tog 1 true", "log 1 true");
    }

    @Test
    public void testThreeParameterNullInfoMessageIsOk() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        Object[] paramArray = new Object[]{"l\n\n\n\tog", null, true};

        instance.info("{} {} {}", paramArray);
        runAsserts("l\n\n\n\tog null true", "log null true");
    }

    @Test
    public void removeTabsAndNewlinesChars() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        String unEncodedString = ",></.,!\"$%^&*(\n\n\n\n\t):@~#';\\|?>{}-=`<$&^*&\n\n\n\n\t";
        String encodedString = ",></.,!\"$%^&*():@~#';\\|?>{}-=`<$&^*&";

        instance.info(unEncodedString);
        runAsserts(unEncodedString, encodedString);
    }

    @Test
    public void logPoundSymbol() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        String unEncodedString = "£\t";
        String encodedString = "£";
        instance.info(unEncodedString);
        runAsserts(unEncodedString, encodedString);
    }

    @Test
    public void testStackTraceIsPresent() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        try {
            instance.info("was {}, now {}", 1001, 1001 / Integer.valueOf("oops"));
            fail("should with number error");

        } catch (NumberFormatException e) {
            instance.error(e.getMessage(), e);
        }

        assertThat("should have stack trace", getLastFileEntry(), containsString("\"thread\":\"main\""));
    }

    @Test
    public void removeTabsAndNewlinesNumbers() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        String unencodedString = "12345\n\n\n\n\t67890\n\n\n\n\t";
        String encodedString = "1234567890";

        instance.info(unencodedString);
        runAsserts(unencodedString, encodedString);
    }

    @Test
    public void removeTabsAndNewlinesUpperC() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        String unencodedString = "ABCDE\n\n\n\n\tFGHIJ\n\n\n\n\t";
        String encodedString = "ABCDEFGHIJ";

        instance.info(unencodedString);
        runAsserts(unencodedString, encodedString);
    }

    @Test
    public void removeTabsAndNewlinesLowerC() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        String unencodedString = "abcde\n\n\n\n\tfghij\n\n\n\n\t";
        String encodedString = "abcdefghij";

        instance.info(unencodedString);
        runAsserts(unencodedString, encodedString);
    }

    @Test
    public void checkLevelsSetCorrectlyFromXML() {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        assertFalse("trace should be false", instance.isTraceEnabled());
        assertFalse("debug should be false", instance.isDebugEnabled());
        assertTrue("info should be true", instance.isInfoEnabled());
        assertTrue("warn should be true", instance.isWarnEnabled());
        assertTrue("error should be true", instance.isErrorEnabled());
    }

    @Test
    public void doubleLine() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        String unencodedString = URLDecoder.decode("twenty-one%0a%0a%20INFO:+User+logged+out%3dbadguy", "UTF-8");
        String encodedString = "twenty-one INFO: User logged out=badguy";

        instance.info(unencodedString);
        runAsserts(unencodedString, encodedString);
    }

    @Test
    public void logSomethingTestDefaultParameterPID() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        String somethingToLog = "ABCDEFGHIJ";

        instance.info(somethingToLog);
        assertThat("should have stack trace", getLastFileEntry(), containsString("\"pid\":\""));
    }

    @Test
    public void logSomethingTestDefaultParameterHostName() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        String somethingToLog = "ABCDEFGHIJ";

        instance.info(somethingToLog);
        assertThat("should have stack trace", getLastFileEntry(), containsString("\"hostname\":\""));
    }

    @Test
    public void logSomethingTestDefaultParameterAppVersion() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        String somethingToLog = "ABCDEFGHIJ";

        instance.info(somethingToLog);
        assertThat("should have stack trace", getLastFileEntry(), containsString(String.format("\"app_version\":\"%s\"", TEST_APP_VERSION)));
    }

    @Test
    public void logSomethingTestDefaultParameterAppName() throws IOException {
        Logger instance = LoggerFactory.getLogger(this.getClass());
        String somethhingToLog = "ABCDEFGHIJ";

        instance.info(somethhingToLog);
        assertThat("should have stack trace", getLastFileEntry(), containsString(String.format("\"app_name\":\"%s\"", TEST_APP_NAME)));
    }

    private String getLastFileEntry() throws IOException {
        File logFile = new File("src/test/resources/encoded_logging.log");
        String lastLine = null;
        if (logFile.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lastLine = line;
                }
            }
        }
        return lastLine;    }
}
