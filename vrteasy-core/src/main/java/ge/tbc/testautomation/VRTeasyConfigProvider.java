package ge.tbc.testautomation;

import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class VRTeasyConfigProvider {

  private static final String FILE_NAME = "vrteasy.properties";
  private static final String API_URL = "API_URL";
  private static final String API_KEY = "API_KEY";
  private static final String PROJECT_NAME = "PROJECT_NAME";
  private static final String BRANCH_NAME = "BRANCH_NAME";

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
        throw new IllegalStateException(FILE_NAME + " not found in classpath");
      }

      properties.load(inputStream);
      return properties;
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read " + FILE_NAME, e);
    }
  }

  private static String getRequiredProperty(Properties properties, String key) {
    String value = properties.getProperty(key);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Missing required property: " + key);
    }

    return value.trim();
  }
}

