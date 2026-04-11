package ge.tbc.testautomation.client;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import ge.tbc.testautomation.data.Properties;
import ge.tbc.testautomation.utils.VRTLogger;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlaywrightVRTClient extends VRTClient {

  private final Page page;

  public PlaywrightVRTClient(Page page) {
    this.page = page;
  }

  @Override
  public byte[] screenshot() {
    return page.screenshot();
  }

  @Override
  public Path downloadPdf(String xpath) {
    Download download = page.waitForDownload(() -> {
      page.click(xpath);
    });

    var path = Paths.get(Properties.downloadFolder, download.suggestedFilename());
    download.saveAs(path);

    logger.getLogger().info("Downloaded file: " + download.suggestedFilename());

    return path;
  }
}

