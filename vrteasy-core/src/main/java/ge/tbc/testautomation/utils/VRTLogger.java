package ge.tbc.testautomation.utils;

import java.util.logging.Logger;

public class VRTLogger {
  private static Logger logger;

  public VRTLogger() {
    if(logger == null){
      logger = Logger.getLogger(VRTLogger.class.getName());
      logger.setUseParentHandlers(false);
      var consoleHandler = new java.util.logging.ConsoleHandler();
      consoleHandler.setFormatter(new ColorFormatter());
      logger.addHandler(consoleHandler);
    }
  }

  public Logger getLogger() {
    return logger;
  }
}
