package uk.gov.dwp.logging.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.encoder.EncoderBase;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import uk.gov.dwp.logging.LogEntryBuilderFactory;

@SuppressWarnings("squid:S1168") // allow null to be returned from byte arrays
public class JsonEncoder extends EncoderBase<ILoggingEvent> {

  private String appName = "UNKNOWN";
  private String appVersion = "UNKNOWN";

  private final LogEntryBuilderFactory entryBuilderFactory;

  public JsonEncoder() {
    entryBuilderFactory = new LogEntryBuilderFactory(appName, appVersion);
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  @Override
  public byte[] headerBytes() {
    return null;
  }

  @Override
  public byte[] encode(ILoggingEvent event) {

    LogEntryBuilderFactory.LogEntryBuilder builder =
        entryBuilderFactory
            .getBuilder()
            .withAppName(appName)
            .withAppVersion(appVersion)
            .withLevel(event.getLevel().toString())
            .withMessage(event.getFormattedMessage())
            .withNameSpace(event.getLoggerName())
            .withTimeStamp(event.getTimeStamp())
            .withMeta("thread", event.getThreadName());

    Optional.ofNullable(getContext())
        .map(Context::getCopyOfPropertyMap)
        .ifPresent(map -> map.forEach(builder::withMeta));

    event.getMDCPropertyMap().forEach(builder::withMeta);

    Optional.ofNullable(event.getThrowableProxy())
        .map(ThrowableProxyUtil::asString)
        .ifPresent(p -> builder.withMeta("exception", p));

    return builder.build().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public byte[] footerBytes() {
    return null;
  }
}
