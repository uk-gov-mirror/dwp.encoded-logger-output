package uk.gov.dwp.logging;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SanitiserTest {

    @Test
    public void stripsBadCharacters() {
        Sanitiser sanitiser = new Sanitiser();

        assertThat(
                sanitiser.sanitise("l\n\n\n\tog 1 true"),
                is(equalTo("log 1 true"))
        );
        assertThat(
                sanitiser.sanitise("12345\n\n\n\n\t67890\n\n\n\n\t"),
                is(equalTo("1234567890"))
        );
    }

    @Test
    public void sanitiserDoesNotStripGoodCharacters() {
        Sanitiser sanitiser = new Sanitiser();
        
        assertThat(
                sanitiser.sanitise("abcdefghijklmnopqrstuvwxyz"),
                is(equalTo("abcdefghijklmnopqrstuvwxyz"))
        );
        assertThat(
                sanitiser.sanitise("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
                is(equalTo("ABCDEFGHIJKLMNOPQRSTUVWXYZ"))
        );
        assertThat(
                sanitiser.sanitise("!@£$%^&*()_+#<>?:|\\{}[]`~\"'"),
                is(equalTo("!@£$%^&*()_+#<>?:|\\{}[]`~\"'"))
        );

    }

}
