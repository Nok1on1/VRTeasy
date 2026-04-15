package ge.tbc.testautomation.data;

import ge.tbc.testautomation.VRTeasyConfigProvider;

public class Properties {

  private static boolean hasInstance;
  public static String downloadFolder;
  public static int downloadTimeout;
  public static int downloadTick;
  public static int pdfImageDPI;

  public Properties() {
    if (hasInstance) {
      return;
    }

    var properties = VRTeasyConfigProvider.loadProperties();
    downloadFolder = properties.getProperty("download.folder", "target");
    downloadTimeout = Integer.parseInt(properties.getProperty("download.timeout", "3"));
    downloadTick = Integer.parseInt(properties.getProperty("download.tick", "300"));
    pdfImageDPI = Integer.parseInt(properties.getProperty("pdf.image.dpi", "300"));
    hasInstance = true;
  }
}
