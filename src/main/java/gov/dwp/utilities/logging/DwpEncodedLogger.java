package gov.dwp.utilities.logging;

import org.apache.log4j.Logger;

public class DwpEncodedLogger extends Logger {
    private static DwpEncodedLoggerFactory dwpEncodedLoggerFactory = new DwpEncodedLoggerFactory();
    private static final String LOG_STANDARD_REGEX = "[\\u0000-\\u001f]";

    protected DwpEncodedLogger(String name) {
        super(name);
    }

    public static Logger getLogger(String name) {
        return Logger.getLogger(String.format("%s (dwp encoded)", name), dwpEncodedLoggerFactory);
    }

    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }

    @Override
    public void trace(Object message) {
        super.trace(message.toString().replaceAll(LOG_STANDARD_REGEX, ""));
    }

    @Override
    public void debug(Object message) {
        super.debug(message.toString().replaceAll(LOG_STANDARD_REGEX, ""));
    }

    @Override
    public void info(Object message) {
        super.info(message.toString().replaceAll(LOG_STANDARD_REGEX, ""));
    }

    @Override
    public void warn(Object message) {
        super.warn(message.toString().replaceAll(LOG_STANDARD_REGEX, ""));
    }

    @Override
    public void error(Object message) {
        super.error(message.toString().replaceAll(LOG_STANDARD_REGEX, ""));
    }

    @Override
    public void fatal(Object message) {
        super.fatal(message.toString().replaceAll(LOG_STANDARD_REGEX, ""));
    }
}