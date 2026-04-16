package ge.tbc.testautomation;

import static ge.tbc.testautomation.data.Constants.FILE_NAME;
import static ge.tbc.testautomation.data.Messages.formatMissingPropertyMessage;
import static ge.tbc.testautomation.data.Messages.formatNotFoundMessage;
import static ge.tbc.testautomation.data.Messages.formatUnreadableMessage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

public class VRTeasyConfigProvider {

  private VRTeasyConfigProvider() {
  }

  public static Properties loadProperties(){
    Properties properties = new Properties();

    try (InputStream inputStream = Thread.currentThread()
        .getContextClassLoader()
        .getResourceAsStream(FILE_NAME)) {
      if (inputStream == null) {
        return properties;
      }

      properties.load(inputStream);
      return properties;
    } catch (IOException e) {
      throw new IllegalStateException(formatUnreadableMessage(), e);
    }
  }

  public static String getRequiredProperty(Properties properties, String key) {
    String value = properties.getProperty(key);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException(formatMissingPropertyMessage(key));
    }

    return value.trim();
  }

  public static Map<String, Object> readConfigFromJsonFile(File configFile){
    if (!configFile.exists()) {
      throw new IllegalArgumentException("File " + configFile + " doesn't exist");
    } else {
      String fileContent;
      try {
        fileContent = Files.readString(configFile.toPath(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new IllegalArgumentException("Can't read content of provided config file", e);
      }

      Type mapType = (new TypeToken<Map<String, Object>>() {
      }).getType();
      return (Map)(new Gson()).fromJson(fileContent, mapType);
    }
  }

}
