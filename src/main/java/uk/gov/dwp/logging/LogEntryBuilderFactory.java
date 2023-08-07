package uk.gov.dwp.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class LogEntryBuilderFactory {

  private static final String UNKNOWN = "<unknown>";
  private static final String APP_NAME = "app_name";
  private static final String APP_VERSION = "app_version";
  private static final String HOST_NAME = "hostname";
  private static final String PID = "pid";
  private static final String TIMESTAMP = "ts";
  private static final String MESSAGE = "message";
  private static final String LEVEL = "level";
  private static final String NAME_SPACE = "ns";
  private static final String META = "meta";

  private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;

  private final ObjectMapper mapper;
  private final Sanitiser sanitiser;

  private final String processId;
  private final String appName;
  private final String appVersion;
  private final String hostname;

  public LogEntryBuilderFactory(String appName, String appVersion) {
    this.processId = getPID();
    this.appName = appName;
    this.appVersion = appVersion;
    String localHostName;
    try {
      localHostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException ex) {
      localHostName = UNKNOWN;
    }
    this.hostname = localHostName;

    mapper = new ObjectMapper();

    sanitiser = new Sanitiser();
  }

  public LogEntryBuilder getBuilder() {
    return new LogEntryBuilder().withAppName(appName).withAppVersion(appVersion).withPid(processId);
  }

  private static String getPID() {
    String vmName = ManagementFactory.getRuntimeMXBean().getName();
    int p = vmName.indexOf('@');
    return vmName.substring(0, p);
  }

  public class LogEntryBuilder {

    private final ObjectNode eventNode;
    private final ObjectNode metaNode;

    public LogEntryBuilder() {
      this.eventNode = mapper.createObjectNode();
      this.eventNode.put(APP_NAME, UNKNOWN);
      this.eventNode.put(APP_VERSION, UNKNOWN);
      this.eventNode.put(HOST_NAME, hostname);
      this.eventNode.put(PID, processId);
      this.eventNode.put(TIMESTAMP, dateTimeFormatter.format(Instant.now()));
      this.eventNode.put(MESSAGE, UNKNOWN);
      this.eventNode.put(LEVEL, UNKNOWN);
      this.eventNode.put(NAME_SPACE, UNKNOWN);

      this.metaNode = eventNode.putObject(META);
    }

    private void addToNode(String key, String val) {
      eventNode.put(key.toLowerCase(), val);
    }

    private void addToMeta(String key, String val) {
      metaNode.put(key.toLowerCase(), val);
    }

    public LogEntryBuilder withAppName(String appName) {
      addToNode(APP_NAME, appName);
      return this;
    }

    public LogEntryBuilder withAppVersion(String appVersion) {
      addToNode(APP_VERSION, appVersion);
      return this;
    }

    public LogEntryBuilder withPid(String pid) {
      addToNode(PID, pid);
      return this;
    }

    public LogEntryBuilder withMessage(String message) {
      addToNode(MESSAGE, sanitiser.sanitise(message));
      return this;
    }

    public LogEntryBuilder withLevel(String level) {
      addToNode(LEVEL, level);
      return this;
    }

    public LogEntryBuilder withNameSpace(String namespace) {
      addToNode(NAME_SPACE, namespace);
      return this;
    }

    public LogEntryBuilder withTimeStamp(long timestamp) {
      addToNode(TIMESTAMP, dateTimeFormatter.format(Instant.ofEpochMilli(timestamp)));
      return this;
    }

    public LogEntryBuilder withTimeStamp(Instant timestamp) {
      addToNode(TIMESTAMP, dateTimeFormatter.format(timestamp));
      return this;
    }

    public LogEntryBuilder withMeta(String key, String value) {
      addToMeta(key, sanitiser.sanitise(value));
      return this;
    }

    public String build() {
      try {
        return mapper.writeValueAsString(eventNode) + "\n";
      } catch (JsonProcessingException ex) {
        return ex.getMessage();
      }
    }
  }
}
