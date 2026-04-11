package ge.tbc.testautomation;

import ge.tbc.testautomation.client.VRTClient;
import io.visual_regression_tracker.sdk_java.TestRunStatus;
import io.visual_regression_tracker.sdk_java.VisualRegressionTracker;
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import io.visual_regression_tracker.sdk_java.response.TestRunResponse;
import java.io.IOException;
import java.util.Base64;
import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;

public class VRTeasy {

  private static final String NL = System.lineSeparator();

  private final VisualRegressionTracker vrt;
  private final Assertion assertion;
  private final VRTClient vrtClient;

  public VRTeasy(VRTClient vrtClient, Boolean softAssert) {
    this.vrtClient = vrtClient;

    if (softAssert) {
      assertion = new SoftAssert();
    } else {
      assertion = new Assertion();
    }

    VisualRegressionTrackerConfig vrtConfig = VRTeasyConfigProvider.loadConfig(softAssert);
    vrt = new VisualRegressionTracker(vrtConfig);

    try {
      vrt.start();
    } catch (NoSuchMethodError e) {
      throw createSdkMismatchException("start", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(formatInterruptedMessage("start"), e);
    } catch (IOException e) {
      throw new IllegalStateException(formatIoFailureMessage("start"), e);
    }
  }

  public Assertion getAssertion() {
    return assertion;
  }

  public TestRunResponse takeScreenshot(String screenshotIdentifier, TestRunStatus testRunStatus) {
    byte[] screenshot = vrtClient.screenshot();

    try {
      String base64Screenshot = Base64.getEncoder().encodeToString(screenshot);
      TestRunResponse response = vrt.track(screenshotIdentifier, base64Screenshot).getTestRunResponse();

      if (testRunStatus != null) {
        assertion.assertEquals(response.getStatus(), testRunStatus,
            "VRT test run status mismatch for screenshot: " + screenshotIdentifier);
      }

      return response;
    } catch (NoSuchMethodError e) {
      throw createSdkMismatchException("track", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(formatInterruptedMessage("track"), e);
    } catch (IOException e) {
      throw new IllegalStateException(formatIoFailureMessage("track"), e);
    }
  }

  public void stopVRT() {
    try {
      vrt.stop();
    } catch (NoSuchMethodError e) {
      throw createSdkMismatchException("stop", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(formatInterruptedMessage("stop"), e);
    } catch (IOException e) {
      throw new IllegalStateException(formatIoFailureMessage("stop"), e);
    }

    if (assertion instanceof SoftAssert softAssert) {
      softAssert.assertAll();
    }
  }

  private static IllegalStateException createSdkMismatchException(String action, NoSuchMethodError error) {
    return new IllegalStateException("[VRTeasy runtime compatibility error]" + NL
        + "Problem: incompatible Visual Regression Tracker SDK detected while trying to '" + action + "'." + NL
        + "What to do:" + NL
        + "  1) Use the same sdk-java version that VRTeasy was built with." + NL
        + "  2) Remove duplicate/older sdk-java versions from your dependency tree." + NL
        + "  3) Run 'mvn dependency:tree -Dincludes=com.github.Visual-Regression-Tracker:sdk-java'" + NL
        + "     and keep only one version.", error);
  }

  private static String formatInterruptedMessage(String action) {
    return "[VRTeasy runtime error]" + NL
        + "Problem: thread was interrupted during VRT '" + action + "' call." + NL
        + "What to do:" + NL
        + "  1) Treat this test as aborted." + NL
        + "  2) Check timeout/cancellation logic in your test framework.";
  }

  private static String formatIoFailureMessage(String action) {
    return "[VRTeasy runtime error]" + NL
        + "Problem: I/O failure while calling VRT '" + action + "'." + NL
        + "What to do:" + NL
        + "  1) Verify API_URL/API_KEY in vrteasy.properties." + NL
        + "  2) Ensure VRT server is running and reachable." + NL
        + "  3) Retry once and inspect nested cause for HTTP details.";
  }
}

