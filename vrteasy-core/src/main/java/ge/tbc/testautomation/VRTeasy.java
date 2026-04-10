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
    } catch (IOException e) {
      throw new RuntimeException(e);
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
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void stopVRT() {
    try {
      vrt.stop();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (assertion instanceof SoftAssert softAssert) {
      softAssert.assertAll();
    }
  }
}

