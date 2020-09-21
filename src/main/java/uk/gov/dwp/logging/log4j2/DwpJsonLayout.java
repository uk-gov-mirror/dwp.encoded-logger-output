package uk.gov.dwp.logging.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.util.Throwables;
import uk.gov.dwp.logging.LogEntryBuilderFactory;

import java.nio.charset.Charset;
import java.util.Optional;

@Plugin(name = "DwpJsonLayout", category = "Core", elementType = "layout", printObject = true)
public final class DwpJsonLayout extends AbstractStringLayout {

  private final LogEntryBuilderFactory logEntryBuilderFactory;

  public DwpJsonLayout(Charset charset, String appName, String version) {
    super(charset);
    logEntryBuilderFactory = new LogEntryBuilderFactory(appName, version);
  }

  @Override
  public String toSerializable(LogEvent le) {

    LogEntryBuilderFactory.LogEntryBuilder builder =
        logEntryBuilderFactory
            .getBuilder()
            .withLevel(le.getLevel().name())
            .withMessage(le.getMessage().getFormattedMessage())
            .withNameSpace(le.getLoggerName())
            .withTimeStamp(le.getNanoTime())
            .withMeta("thread", "" + le.getThreadId());

    Optional.ofNullable(le.getThrown())
        .ifPresent(
            thrown ->
                builder.withMeta(
                    "exception",
                    String.join("\n", Throwables.toStringList(thrown).toArray(new String[0]))));

    return builder.build();
  }

  @PluginFactory
  public static DwpJsonLayout createLayout(
      @PluginAttribute(value = "charset", defaultString = "UTF-8") Charset charset,
      @PluginAttribute(value = "app_name") String appName,
      @PluginAttribute(value = "app_version") String version) {
    return new DwpJsonLayout(charset, appName, version);
  }
}
