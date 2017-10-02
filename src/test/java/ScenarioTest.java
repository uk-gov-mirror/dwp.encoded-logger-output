import gov.dwp.utilities.logging.DwpEncodedLogger;
import org.apache.log4j.Logger;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ScenarioTest {
    private static final Logger instance = DwpEncodedLogger.getLogger(ScenarioTest.class);

    private void runAsserts(String unencodedString, String encodedString) throws IOException {
        String lastFileEntry = getLastFileEntry();

        assertThat("should not contain the passed log", lastFileEntry, not(endsWith(unencodedString)));
        assertThat("should be encoded", lastFileEntry, endsWith(encodedString));
    }

    @Test
    public void removeTabsAndNewlines_Chars() throws IOException {
        String unencodedString = ",></.,!\"£$%^&*(\n\n\n\n\t):@~#';\\|?>{}-=`<$&^*£&\n\n\n\n\t";
        String encodedString = ",></.,!\"£$%^&*():@~#';\\|?>{}-=`<$&^*£&";

        instance.info(unencodedString);
        runAsserts(unencodedString, encodedString);
    }

    @Test
    public void removeTabsAndNewlines_Numbers() throws IOException {
        String unencodedString = "12345\n\n\n\n\t67890\n\n\n\n\t";
        String encodedString = "1234567890";

        instance.info(unencodedString);
        runAsserts(unencodedString, encodedString);
    }

    @Test
    public void removeTabsAndNewlines_UpperC() throws IOException {
        String unencodedString = "ABCDE\n\n\n\n\tFGHIJ\n\n\n\n\t";
        String encodedString = "ABCDEFGHIJ";

        instance.info(unencodedString);
        runAsserts(unencodedString, encodedString);
    }

    @Test
    public void removeTabsAndNewlines_LowerC() throws IOException {
        String unecodedString = "abcde\n\n\n\n\tfghij\n\n\n\n\t";
        String encodedString = "abcdefghij";

        instance.info(unecodedString);
        runAsserts(unecodedString, encodedString);
    }

    @Test
    public void doubleLine() throws IOException {
        String unecodedString = URLDecoder.decode("twenty-one%0a%0a%20INFO:+User+logged+out%3dbadguy", "UTF-8");
        String encodedString = "twenty-one INFO: User logged out=badguy";

        instance.info(unecodedString);
        runAsserts(unecodedString, encodedString);
    }

    private String getLastFileEntry() throws IOException {
        File logFile = new File("src/test/resources/encoded_logging.log");
        String lastLine = null;

        if (logFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
                String line;

                while ((line = br.readLine()) != null) {
                    lastLine = line;
                }

            }
        }

        return lastLine;
    }
}
