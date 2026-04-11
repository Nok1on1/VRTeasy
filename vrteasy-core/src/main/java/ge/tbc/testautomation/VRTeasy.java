package ge.tbc.testautomation;

import static ge.tbc.testautomation.VRTeasyConfigProvider.readConfigFromJsonFile;
import static ge.tbc.testautomation.data.Messages.createSdkMismatchException;
import static ge.tbc.testautomation.data.Messages.formatInterruptedMessage;
import static ge.tbc.testautomation.data.Messages.formatIoFailureMessage;
import static ge.tbc.testautomation.utils.ColorFormatter.statusColorized;

import ge.tbc.testautomation.client.VRTClient;
import ge.tbc.testautomation.data.Properties;
import ge.tbc.testautomation.utils.FileHandler;
import ge.tbc.testautomation.utils.VRTLogger;
import io.visual_regression_tracker.sdk_java.TestRunStatus;
import io.visual_regression_tracker.sdk_java.VisualRegressionTracker;
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig;
import io.visual_regression_tracker.sdk_java.response.TestRunResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;

public class VRTeasy {

  private final VisualRegressionTracker vrt;
  private final Assertion assertion;
  private final VRTClient vrtClient;
  private Properties properties = new Properties();

  protected final VRTLogger logger = new VRTLogger();

  public VRTeasy(VRTClient vrtClient, VisualRegressionTrackerConfig vrtConfig) {
    this.vrtClient = vrtClient;
    Boolean softAssert = vrtConfig.getEnableSoftAssert();

    if (softAssert) {
      assertion = new SoftAssert();
    } else {
      assertion = new Assertion();
    }

    vrt = new VisualRegressionTracker(vrtConfig);

    try {
      vrt.start();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public VRTeasy(VRTClient vrtClient) {
    this.vrtClient = vrtClient;
    File file = new File("vrt.json");

    boolean softAssert = (boolean) readConfigFromJsonFile(file).get("enableSoftAssert");

    if (softAssert) {
      assertion = new SoftAssert();
    } else {
      assertion = new Assertion();
    }

    vrt = new VisualRegressionTracker();

    try {
      vrt.start();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }


  public Assertion getAssertion() {
    return assertion;
  }

  public VisualRegressionTracker getVRT() {
    return vrt;
  }

  public TestRunResponse takeScreenshot(String screenshotIdentifier, TestRunStatus testRunStatus) {
    byte[] screenshot = vrtClient.screenshot();

    try {
      String base64Screenshot = Base64.getEncoder().encodeToString(screenshot);

      return trackImage(screenshotIdentifier, base64Screenshot, testRunStatus);
    } catch (NoSuchMethodError e) {
      throw createSdkMismatchException("track", e);
    }
  }

  public Stream<TestRunResponse> downloadAndComparePdf(String xpath, TestRunStatus testRunStatus) {
    Path filePath = vrtClient.downloadPdf(xpath);
    return comparePdf(filePath, testRunStatus);
  }

  public Stream<TestRunResponse> comparePdf(Path filePath, TestRunStatus testRunStatus) {
    AtomicInteger pageNum = new AtomicInteger(1);

    return FileHandler.streamPdfPagesAsImages(filePath).map(pageImage -> {
      int index = pageNum.getAndIncrement();
      var imageIdentifier = filePath.getFileName().toString() + "_page_" + index;
      String base64Image = Base64.getEncoder().encodeToString(pageImage);

      return trackImage(imageIdentifier, base64Image, testRunStatus);
    });
  }

  protected TestRunResponse trackImage(String imageIdentifier, String base64Image,
      TestRunStatus testRunStatus) {
    TestRunResponse response;
    try {
      response = vrt.track(imageIdentifier, base64Image).getTestRunResponse();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }

    if (testRunStatus != null) {
      assertion.assertEquals(response.getStatus(), testRunStatus,
          "VRT test run status mismatch for Image: " + imageIdentifier);
    }

    logger.getLogger().info(
        "image: " + imageIdentifier + " returned status: " + statusColorized(response.getStatus()));
    return response;
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

