package ge.tbc.testautomation.utils;


import io.visual_regression_tracker.sdk_java.TestRunStatus;
import java.util.logging.*;

public class ColorFormatter extends Formatter {

  private static final String RESET = "\u001B[0m";
  private static final String RED = "\u001B[31m";
  private static final String YELLOW = "\u001B[33m";
  private static final String GREEN = "\u001B[32m";
  private static final String CYAN = "\u001B[36m";
  private static final String GREY = "\u001B[90m";
  private static final String WHITE = "\u001B[97m";


  @Override
  public String format(LogRecord record) {
    String color = switch (record.getLevel().getName()) {
      case "SEVERE" -> RED;
      case "WARNING" -> YELLOW;
      default -> WHITE;
    };
    return color + record.getLevel() + ": " + formatMessage(record) + RESET
        + System.lineSeparator();
  }

  public static String statusColorized(TestRunStatus testRunStatus) {

    return switch (testRunStatus) {
      case OK, APPROVED, AUTO_APPROVED -> GREEN + testRunStatus;
      case UNRESOLVED, FAILED -> RED + testRunStatus;
      case NEW -> CYAN + testRunStatus;
    };
  }
}

