package ge.tbc.testautomation.client;

import static ge.tbc.testautomation.utils.FileHandler.waitForFileDownload;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.ZoneId;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class SeleniumVRTClient extends VRTClient {

  private final WebDriver driver;

  public SeleniumVRTClient(WebDriver driver) {
    this.driver = driver;
  }

  @Override
  public byte[] screenshot() {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
  }

  @Override
  public Path downloadPdf(String xpath) {
    driver.findElement(By.xpath(xpath)).click();

    Path filePath = waitForFileDownload(LocalTime.now(ZoneId.systemDefault()));

    logger.getLogger().info("Downloaded file: " + filePath.getFileName());

    return filePath;
  }
}

