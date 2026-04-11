package ge.tbc.testautomation.utils;

import ge.tbc.testautomation.data.Properties;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class FileHandler {

  public static Path waitForFileDownload(LocalTime startTime) {
    File dir = new File(Properties.downloadFolder);

    while (LocalTime.now().isBefore(startTime.plusSeconds(Properties.downloadTimeout))) {
      File[] dirContents = dir.listFiles();

      if (dirContents != null) {
        for (File file : dirContents) {
          long lastModified = file.lastModified();

          LocalTime fileTime = Instant.ofEpochMilli(lastModified)
              .atZone(ZoneId.systemDefault())
              .toLocalTime();

          if (fileTime.isAfter(startTime)) {
            String name = file.getName();
            if (!name.endsWith(".crdownload") && !name.endsWith(".part") && !name.endsWith(
                ".tmp")) {
              return file.toPath();
            }
          }
        }
      }

      try {
        Thread.sleep(Properties.downloadTick);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Download wait interrupted", e);
      }
    }

    throw new RuntimeException(
        "Download failed: Timeout reached after " + Properties.downloadTimeout + " seconds.");
  }

  public static Stream<byte[]> streamPdfPagesAsImages(Path filePath) {
    try {
      PDDocument document = Loader.loadPDF(filePath.toFile());
      PDFRenderer renderer = new PDFRenderer(document);

      return IntStream.range(0, document.getNumberOfPages())
          .mapToObj(pageIndex -> {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
              var bim = renderer.renderImageWithDPI(pageIndex, Properties.pdfImageDPI);
              ImageIO.write(bim, "png", baos);
              return baos.toByteArray();
            } catch (IOException e) {
              throw new RuntimeException("Failed to render page " + pageIndex, e);
            }
          })
          .onClose(() -> {
            try {
              document.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
    } catch (IOException e) {
      throw new RuntimeException("Failed to initialize PDF stream", e);
    }
  }
}
