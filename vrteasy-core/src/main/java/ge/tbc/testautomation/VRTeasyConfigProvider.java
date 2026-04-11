package ge.tbc.testautomation;

import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class VRTeasyConfigProvider {

  private static final String NL = System.lineSeparator();
  private static final String FILE_NAME = "vrteasy.properties";
  private static final String API_URL = "API_URL";
  private static final String API_KEY = "API_KEY";
  private static final String PROJECT_NAME = "PROJECT_NAME";
  private static final String BRANCH_NAME = "BRANCH_NAME";
  private static final String REQUIRED_KEYS = API_URL + ", " + API_KEY + ", " + PROJECT_NAME + ", " + BRANCH_NAME;

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

  private static String formatNotFoundMessage() {
    return "[VRTeasy config error]" + NL
        + "Problem: file '" + FILE_NAME + "' was not found on the classpath." + NL
        + "What to do:" + NL
        + "  1) Copy 'vrteasy.properties.example' to '" + FILE_NAME + "'." + NL
        + "  2) Place it in 'src/test/resources' or 'src/main/resources'." + NL
        + "  3) Rebuild and rerun your tests.";
  }

  private static String formatUnreadableMessage() {
    return "[VRTeasy config error]" + NL
        + "Problem: failed to read file '" + FILE_NAME + "'." + NL
        + "What to do:" + NL
        + "  1) Ensure file format is valid Java properties (KEY=value)." + NL
        + "  2) Save the file as plain text." + NL
        + "  3) Verify the process has read access to this file.";
  }

  private static String formatMissingPropertyMessage(String key) {
    return "[VRTeasy config error]" + NL
        + "Problem: required property '" + key + "' is missing or empty in '" + FILE_NAME + "'." + NL
        + "Required keys: " + REQUIRED_KEYS + NL
        + "What to do:" + NL
        + "  1) Add a non-empty value for '" + key + "'." + NL
        + "  2) Make sure all required keys are present." + NL
        + "Example:" + NL
        + "  " + API_URL + "=http://localhost:4200/" + NL
        + "  " + API_KEY + "=<your-api-key>" + NL
        + "  " + PROJECT_NAME + "=<your-project>" + NL
        + "  " + BRANCH_NAME + "=<your-branch>";
  }
}
