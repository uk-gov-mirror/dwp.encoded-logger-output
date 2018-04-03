package uk.gov.dwp.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.lang.reflect.Proxy;
import java.util.stream.Stream;

public class DwpEncodedLogger {
    private static final String LOG_STANDARD_REGEX = "[\\u0000-\\u001f]";

    private DwpEncodedLogger() {
        // private on purpose
    }

    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String name) {
        return wrap(LoggerFactory.getLogger(String.format("%s (dwp encoded)", name)));
    }

    private static Object sanitise(Object obj) {
        if ((!(obj instanceof Marker)) && (!(obj instanceof Throwable))) {
            return obj.toString().replaceAll(LOG_STANDARD_REGEX, "");
        } else {
            return obj;
        }
    }

    private static Object[] sanitise(Object[] obj) {
        return Stream.of(obj).map(DwpEncodedLogger::sanitise).toArray(Object[]::new);
    }

    private static Logger wrap(Logger logger) {
        return (Logger) Proxy.newProxyInstance(
                DwpEncodedLogger.class.getClassLoader(),
                new Class[]{Logger.class},
                (proxy, method, methodArgs) -> {
                    switch (method.getName()) {
                        case "trace":
                        case "debug":
                        case "info":
                        case "warn":
                        case "error":
                            return method.invoke(logger, sanitise(methodArgs));
                        default:
                            return method.invoke(logger, methodArgs);

                    }
                }
        );
    }
}
