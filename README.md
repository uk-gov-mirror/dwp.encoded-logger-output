# DwpEncodedLogger

This logger wraps the `org.apache.log4j.Logger` to prevent log forging by removing all control characters from the input message before allowing it to be logged.

Usage is the same as the original Logger

`private static final Logger LOGGER = Logger.getLogger(<class>);`

with the new implementation being 

`private static final Logger LOGGER = DwpEncodedLogger.getLogger(<class>);`

This was created to mitigate the **Heap_Inspection** vulnerability :-

_`The application writes audit logs upon security-sensitive actions. Since the audit log includes user input that is neither checked for data type validity nor subsequently sanitized, the input could contain false information made to look like legitimate audit log data`_

#### Project inclusion

properties entry in pom

    <properties>
        <dwp.encoded_logger>x.x</dwp.encoded_logger>
    </properties>

dependency reference

    <dependency>
        <groupId>uk.gov.dwp.logging</groupId>
        <artifactId>encoded-logger-output</artifactId>
        <version>${dwp.encoded_logger}</version>
    </dependency>
    
The type of logging (log4j.xml) file is not part of this package and should be set by the service using this utility.

#### Example of use

    import uk.gov.dwp.logging.DwpEncodedLogger;

_declaration_

    private static final Logger LOGGER = DwpEncodedLogger.getLogger(SecureStrings.class.getName());

and then usage as normal

    LOGGER.trace("test");
    LOGGER.debug("test");
    LOGGER.info("test");
    LOGGER.warn("test");
    LOGGER.error("test");
    LOGGER.fatal("test");