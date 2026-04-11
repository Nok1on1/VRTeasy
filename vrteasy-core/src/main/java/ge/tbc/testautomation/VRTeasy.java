package ge.tbc.testautomation;

import static ge.tbc.testautomation.data.messages.createSdkMismatchException;
import static ge.tbc.testautomation.data.messages.formatInterruptedMessage;
import static ge.tbc.testautomation.data.messages.formatIoFailureMessage;

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
}

