package ge.tbc.testautomation;

import static ge.tbc.testautomation.VRTeasyConfigProvider.readConfigFromJsonFile;
import static ge.tbc.testautomation.data.Messages.createSdkMismatchException;
import static ge.tbc.testautomation.data.Messages.formatInterruptedMessage;
import static ge.tbc.testautomation.data.Messages.formatIoFailureMessage;
import static ge.tbc.testautomation.utils.ColorFormatter.statusColorized;

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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;

public class VRTBase {

    private final VisualRegressionTracker vrt;
    private final Assertion assertion;
    private final Properties properties = new Properties();

    protected final VRTLogger logger = new VRTLogger();

    public VRTBase(VisualRegressionTrackerConfig vrtConfig) {
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

    public VRTBase() {
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

    public List<TestRunResponse> trackPDF(Path filePath, TestRunStatus expectedStatus) {
        AtomicInteger pageNum = new AtomicInteger(1);

        return FileHandler.streamPDFPagesAsImages(filePath)
                .map(pageImage -> {
                    int index = pageNum.getAndIncrement();
                    String fileName = filePath.getFileName().toString();
                    var imageIdentifier = fileName.substring(0, fileName.indexOf(".")) + "_page_" + index;
                    String base64Image = Base64.getEncoder().encodeToString(pageImage);

                    return trackImage(imageIdentifier, base64Image, expectedStatus);
                })
                .toList();
    }

    public TestRunResponse trackImage(String imageIdentifier, String base64Image,
                                         TestRunStatus expectedStatus) {
        TestRunResponse response;
        try {
            response = vrt.track(imageIdentifier, base64Image).getTestRunResponse();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (expectedStatus != null) {
            assertion.assertEquals(response.getStatus(), expectedStatus,
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
