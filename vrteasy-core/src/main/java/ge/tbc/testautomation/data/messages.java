package ge.tbc.testautomation.data;

import static ge.tbc.testautomation.data.constants.*;

public class messages {

  private static final String NL = System.lineSeparator();

  public static IllegalStateException createSdkMismatchException(String action, NoSuchMethodError error) {
    return new IllegalStateException("[VRTeasy runtime compatibility error]" + NL
        + "Problem: incompatible Visual Regression Tracker SDK detected while trying to '" + action + "'." + NL
        + "What to do:" + NL
        + "  1) Use the same sdk-java version that VRTeasy was built with." + NL
        + "  2) Remove duplicate/older sdk-java versions from your dependency tree." + NL
        + "  3) Run 'mvn dependency:tree -Dincludes=com.github.Visual-Regression-Tracker:sdk-java'" + NL
        + "     and keep only one version.", error);
  }

  public static String formatInterruptedMessage(String action) {
    return "[VRTeasy runtime error]" + NL
        + "Problem: thread was interrupted during VRT '" + action + "' call." + NL
        + "What to do:" + NL
        + "  1) Treat this test as aborted." + NL
        + "  2) Check timeout/cancellation logic in your test framework.";
  }

  public static String formatIoFailureMessage(String action) {
    return "[VRTeasy runtime error]" + NL
        + "Problem: I/O failure while calling VRT '" + action + "'." + NL
        + "What to do:" + NL
        + "  1) Verify API_URL/API_KEY in vrteasy.properties." + NL
        + "  2) Ensure VRT server is running and reachable." + NL
        + "  3) Retry once and inspect nested cause for HTTP details.";
  }


  public static String formatNotFoundMessage() {
    return "[VRTeasy config error]" + NL
        + "Problem: file '" + FILE_NAME + "' was not found on the classpath." + NL
        + "What to do:" + NL
        + "  1) Copy 'vrteasy.properties.example' to '" + FILE_NAME + "'." + NL
        + "  2) Place it in 'src/test/resources' or 'src/main/resources'." + NL
        + "  3) Rebuild and rerun your tests.";
  }

  public static String formatUnreadableMessage() {
    return "[VRTeasy config error]" + NL
        + "Problem: failed to read file '" + FILE_NAME + "'." + NL
        + "What to do:" + NL
        + "  1) Ensure file format is valid Java properties (KEY=value)." + NL
        + "  2) Save the file as plain text." + NL
        + "  3) Verify the process has read access to this file.";
  }

  public static String formatMissingPropertyMessage(String key) {
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
