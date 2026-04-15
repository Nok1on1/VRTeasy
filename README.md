# VRTeasy

VRTeasy is a lightweight Java wrapper around [Visual Regression Tracker](https://github.com/Visual-Regression-Tracker/Visual-Regression-Tracker) for visual checks in UI tests.

see also: [vrt-sdk-java](https://github.com/Visual-Regression-Tracker/sdk-java)

## Modules

- `ge.tbc.testautomation:vrteasy-core` - `VRTeasy`, config loading, and base `VRTClient`
- `ge.tbc.testautomation:vrteasy-playwright` - Playwright adapter (`PlaywrightVRTClient`)
- `ge.tbc.testautomation:vrteasy-selenium` - Selenium adapter (`SeleniumVRTClient`)

## Requirements

- Java 17+
- Maven 3.8+
- A running Visual Regression Tracker server

## Build from source

```bash
mvn clean install
```

## Quick start

`VRTeasy` supports two startup styles:

```java
new VRTeasy(vrtClient);
new VRTeasy(vrtClient, visualRegressionTrackerConfig);
```

- Use `new VRTeasy(vrtClient)` to load configuration from `vrt.json` in the current working directory.
```
  {
    "apiUrl": "[http://162.243.161.172:4200](http://localhost:4200)",
    "project": "003f5fcf-6c5f-4f1f-a99f-82a697711382",
    "apiKey": "F5Z2H0H2SNMXZVHX0EA4YQM1MGDD",
    "branchName": "develop",
    "enableSoftAssert": false,
    "ciBuildId": "40bdba4"
    }
```
- Use `new VRTeasy(vrtClient, visualRegressionTrackerConfig)` when you want full runtime control from code.
```java
VisualRegressionTrackerConfig config = VisualRegressionTrackerConfig.builder()
                .apiUrl("http://localhost:4200")
                .apiKey("F5Z2H0H2SNMXZVHX0EA4YQM1MGDD")
                .project("003f5fcf-6c5f-4f1f-a99f-82a697711382")
                .enableSoftAssert(true)
                .branchName("develop")
                .build();
```
- If `enableSoftAssert` is `true`, assertion failures are aggregated and thrown on `stopVRT()`.

Example `vrt.json`:

```json
{
  "enableSoftAssert": true
}
```

## `vrteasy.properties` (download/PDF settings)

`VRTBase` initializes `new Properties()` once per JVM lifecycle (guarded by `hasInstance`).
`Properties` loads values from `vrteasy.properties` and stores them in static fields used by `FileHandler`.

Configuration keys in `vrteasy-core/src/main/java/ge/tbc/testautomation/data/Properties.java`:

| Key | Static field | Default | Used for |
|---|---|---|---|
| `download.folder` | `Properties.downloadFolder` | `target` | Folder scanned for newly downloaded files |
| `download.timeout` | `Properties.downloadTimeout` | `3` | Max wait time in seconds for download completion |
| `download.tick` | `Properties.downloadTick` | `300` | Polling interval in milliseconds while waiting |
| `pdf.image.dpi` | `Properties.pdfImageDPI` | `300` | DPI when rendering PDF pages to PNG for tracking |

Example `vrteasy.properties`:

```properties
download.folder=target
download.timeout=5
download.tick=250
pdf.image.dpi=300
```

Notes:

- Values are read only on the first `new Properties()` call in the process.
- Because fields are static, changing these values at runtime affects all tests using the same JVM.

## Core API

Main class: `vrteasy-core/src/main/java/ge/tbc/testautomation/VRTeasy.java`

`VRTBase` works with any web automation framework as long as you provide a `VRTClient` implementation when using `VRTeasy`.

Base client class: `vrteasy-core/src/main/java/ge/tbc/testautomation/client/VRTClient.java`

```java
public abstract class VRTClient {
  public abstract byte[] screenshot();
  public abstract Path downloadPDF(String xpath); // button that is pressed to download the PDF
}
```

Common `VRTBase` operations:

- `trackPDF(Path filePath, TestRunStatus expectedStatus)`
- `trackImage(String imageIdentifier, String base64Image, TestRunStatus expectedStatus)`
- `stopVRT()`


###  Use `VRTBase` directly for
#### 1) PDF Tracking

```java
VRTBase vrtBase = new VRTBase();
vrtBase.trackPDF(Path.of("build/reports/monthly-statement.pdf"), TestRunStatus.OK);
vrtBase.stopVRT();
```

### 2) Image Tracking

```java
VRTBase vrtBase = new VRTBase();
byte[] screenshot = null;//... get screenshot bytes from anywhere
var base64Image = Base64.getEncoder().encodeToString(screenshot);
var idintifier = "screenshot-1";
vrtBase.trackImage(idintifier,base64Image,TestRunStatus.OK); // expected status can be null. (no check will be performed on VRT side, but the image will be uploaded and visible in the dashboard)
vrtBase.stopVRT();
```

Common `VRTeasy` (extends `VRTBase`) operations:

- `takeScreenshotAndTrack(String screenshotIdentifier, TestRunStatus expectedStatus)`
- `downloadAndTrackPDF(String xpath, TestRunStatus expectedStatus)`

## Adapter examples

### Playwright

Implementation: `vrteasy-playwright/src/main/java/ge/tbc/testautomation/client/PlaywrightVRTClient.java`

```java
Playwright playwright = Playwright.create();
Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
Page page = browser.newPage();

VRTeasy vrt = new VRTeasy(new PlaywrightVRTClient(page));

page.navigate("https://example.com");
vrt.takeScreenshotAndTrack("example-homepage", TestRunStatus.OK);
vrt.downloadAndTrackPDF("//button[@id='download-statement']", TestRunStatus.OK); //xpath to the button that triggers PDF download
vrt.stopVRT();

page.close();
browser.close();
playwright.close();
```

### Selenium

Implementation: `vrteasy-selenium/src/main/java/ge/tbc/testautomation/client/SeleniumVRTClient.java`

```java
WebDriver driver = new ChromeDriver();
VRTeasy vrt = new VRTeasy(new SeleniumVRTClient(driver));

driver.get("https://example.com");
vrt.takeScreenshotAndTrack("example-homepage", TestRunStatus.OK);
vrt.downloadAndTrackPDF("//button[@id='download-statement']", TestRunStatus.OK); //xpath to the button that triggers PDF download
vrt.stopVRT();
driver.quit();
```

## Maven dependency examples

Project release version: `0.3.2`

VRTeasy modules declare key integrations as `provided`:

- `com.github.Visual-Regression-Tracker:sdk-java` (`5.3.3`)
- `org.testng:testng` (`7.12.0`)
- `com.microsoft.playwright:playwright` (`1.58.0`)
- `org.seleniumhq.selenium:selenium-java` (`4.41.0`)

Your project should include the dependencies it needs at compile/runtime.

### Core only

```xml
<dependency>
  <groupId>ge.tbc.testautomation</groupId>
  <artifactId>vrteasy-core</artifactId>
  <version>0.3.2</version>
</dependency>

<dependency>
  <groupId>com.github.Visual-Regression-Tracker</groupId>
  <artifactId>sdk-java</artifactId>
  <version>5.3.3</version>
</dependency>

<dependency>
  <groupId>org.testng</groupId>
  <artifactId>testng</artifactId>
  <version>7.12.0</version>
</dependency>
```

### Playwright adapter

```xml
<dependency>
  <groupId>ge.tbc.testautomation</groupId>
  <artifactId>vrteasy-playwright</artifactId>
  <version>0.3.2</version>
</dependency>

<dependency>
  <groupId>com.github.Visual-Regression-Tracker</groupId>
  <artifactId>sdk-java</artifactId>
  <version>5.3.3</version>
</dependency>

<dependency>
  <groupId>com.microsoft.playwright</groupId>
  <artifactId>playwright</artifactId>
  <version>1.58.0</version>
</dependency>

<dependency>
  <groupId>org.testng</groupId>
  <artifactId>testng</artifactId>
  <version>7.12.0</version>
</dependency>
```

### Selenium adapter

```xml
<dependency>
  <groupId>ge.tbc.testautomation</groupId>
  <artifactId>vrteasy-selenium</artifactId>
  <version>0.3.2</version>
</dependency>

<dependency>
  <groupId>com.github.Visual-Regression-Tracker</groupId>
  <artifactId>sdk-java</artifactId>
  <version>5.3.3</version>
</dependency>

<dependency>
  <groupId>org.seleniumhq.selenium</groupId>
  <artifactId>selenium-java</artifactId>
  <version>4.41.0</version>
</dependency>

<dependency>
  <groupId>org.testng</groupId>
  <artifactId>testng</artifactId>
  <version>7.12.0</version>
</dependency>
```
