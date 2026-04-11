package ge.tbc.testautomation;

import static ge.tbc.testautomation.data.constants.*;
import static ge.tbc.testautomation.data.messages.formatMissingPropertyMessage;
import static ge.tbc.testautomation.data.messages.formatNotFoundMessage;
import static ge.tbc.testautomation.data.messages.formatUnreadableMessage;

import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class VRTeasyConfigProvider {

  private VRTeasyConfigProvider() {
  }

  public static VisualRegressionTrackerConfig loadConfig(boolean softAssert) {
    Properties properties = loadProperties();

    return VisualRegressionTrackerConfig.builder()
        .apiUrl(getRequiredProperty(properties, API_URL))
        .apiKey(getRequiredProperty(properties, API_KEY))
        .project(getRequiredProperty(properties, PROJECT_NAME))
        .branchName(getRequiredProperty(properties, BRANCH_NAME))
        .enableSoftAssert(softAssert)
        .build();
  }

  private static Properties loadProperties() {
    Properties properties = new Properties();

    try (InputStream inputStream = Thread.currentThread()
        .getContextClassLoader()
        .getResourceAsStream(FILE_NAME)) {
      if (inputStream == null) {
        throw new IllegalStateException(formatNotFoundMessage());
      }

      properties.load(inputStream);
      return properties;
    } catch (IOException e) {
      throw new IllegalStateException(formatUnreadableMessage(), e);
    }
  }

  private static String getRequiredProperty(Properties properties, String key) {
    String value = properties.getProperty(key);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException(formatMissingPropertyMessage(key));
    }

    return value.trim();
  }

}
