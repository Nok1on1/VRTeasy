package ge.tbc.testautomation;

import static ge.tbc.testautomation.data.Messages.createSdkMismatchException;

import ge.tbc.testautomation.client.VRTClient;
import io.visual_regression_tracker.sdk_java.TestRunStatus;
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import io.visual_regression_tracker.sdk_java.response.TestRunResponse;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

public class VRTeasy extends VRTBase {

  private final VRTClient vrtClient;

  public VRTeasy(VRTClient vrtClient) {
    super();
    this.vrtClient = vrtClient;
  }

  public VRTeasy(VRTClient vrtClient,
      VisualRegressionTrackerConfig vrtConfig) {
    super(vrtConfig);
    this.vrtClient = vrtClient;
  }

  public TestRunResponse takeScreenshotAndTrack(String screenshotIdentifier,
      TestRunStatus expectedStatus) {
    byte[] screenshot = vrtClient.screenshot();

    try {
      String base64Screenshot = Base64.getEncoder().encodeToString(screenshot);

      return trackImage(screenshotIdentifier, base64Screenshot, expectedStatus);
    } catch (NoSuchMethodError e) {
      throw createSdkMismatchException("track", e);
    }
  }

  public List<TestRunResponse> downloadAndTrackPDF(String xpath, TestRunStatus expectedStatus) {
    Path filePath = vrtClient.downloadPDF(xpath);
    return trackPDF(filePath, expectedStatus);
  }
}
