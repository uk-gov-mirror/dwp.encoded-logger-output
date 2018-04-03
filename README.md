# DwpEncodedLogger
[![Build Status](https://travis-ci.org/dwp/encoded-logger-output.svg?branch=master)](https://travis-ci.org/dwp/encoded-logger-output) [![Known Vulnerabilities](https://snyk.io/test/github/dwp/encoded-logger-output/badge.svg)](https://snyk.io/test/github/dwp/encoded-logger-output)

This logger wraps the `org.slf4j.Logger` to prevent log forging by removing all control characters from the input message before allowing it to be logged.

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
    
The type of logging framework that implements the `slf4j-api` needs to be included in the project along with the framework configuration files.  (eg. the `src/test` path implements `slf4j-log4j12`)

#### Example of use

    import uk.gov.dwp.logging.DwpEncodedLogger;

_declaration_

    private static final Logger LOGGER = DwpEncodedLogger.getLogger(<my-classname>.class.getName());

and then usage as normal for slf4j implementations (https://www.slf4j.org/api/org/slf4j/Logger.html)