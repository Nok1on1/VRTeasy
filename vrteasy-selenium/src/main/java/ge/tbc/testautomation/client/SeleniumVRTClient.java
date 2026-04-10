package ge.tbc.testautomation.client;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class SeleniumVRTClient implements VRTClient {

  private final WebDriver driver;

  public SeleniumVRTClient(WebDriver driver) {
    this.driver = driver;
  }

  @Override
  public byte[] screenshot() {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
  }
}

