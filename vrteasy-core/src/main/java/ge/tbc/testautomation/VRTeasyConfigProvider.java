package ge.tbc.testautomation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public final class VRTeasyConfigProvider {

  private VRTeasyConfigProvider() {
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
