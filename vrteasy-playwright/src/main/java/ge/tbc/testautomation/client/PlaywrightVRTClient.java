package ge.tbc.testautomation.client;

import com.microsoft.playwright.Page;

public class PlaywrightVRTClient implements VRTClient {

  private final Page page;

  public PlaywrightVRTClient(Page page) {
    this.page = page;
  }

  @Override
  public byte[] screenshot() {
    return page.screenshot();
  }
}

