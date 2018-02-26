package uk.gov.dwp.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class DwpEncodedLoggerFactory implements LoggerFactory {
    public Logger makeNewLoggerInstance(String name) {
        return new DwpEncodedLogger(name);
    }
}
