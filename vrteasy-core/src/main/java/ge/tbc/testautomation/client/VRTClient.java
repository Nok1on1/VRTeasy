package ge.tbc.testautomation.client;

import ge.tbc.testautomation.utils.VRTLogger;
import java.nio.file.Path;

public abstract class VRTClient {

  protected final VRTLogger logger = new VRTLogger();

  public abstract byte[] screenshot();

  public abstract Path downloadPDF(String fileName, String xpath);
}

