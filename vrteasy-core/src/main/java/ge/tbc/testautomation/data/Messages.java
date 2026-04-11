package ge.tbc.testautomation.data;

public class Messages {

  private static final String NL = System.lineSeparator();

  public static IllegalStateException createSdkMismatchException(String action,
      NoSuchMethodError error) {
    return new IllegalStateException("[VRTeasy runtime compatibility error]" + NL
        + "Problem: incompatible Visual Regression Tracker SDK detected while trying to '" + action
        + "'." + NL
        + "What to do:" + NL
        + "  1) Use the same Visual Regression Tracker version that VRTeasy was built with." + NL
        + "  2) Remove duplicate/older Visual Regression Tracker versions from your dependency tree."
        + NL, error);
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
        + "  1) Verify apiUrl/apiKey in vrt.json (if using one)" + NL
        + "  2) Ensure VRT server is running and reachable." + NL
        + "  3) Retry once and inspect nested cause for HTTP details.";
  }

  public static String formatNotFoundMessage() {
    return "";
  }

  public static String formatUnreadableMessage() {

    return "";
  }

  public static String formatMissingPropertyMessage(String key) {
   return "";
  }
}
