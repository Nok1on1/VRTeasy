# VRTeasy

VRTeasy is a lightweight Java wrapper around [Visual Regression Tracker](https://github.com/Visual-Regression-Tracker/Visual-Regression-Tracker) for visual checks in UI tests.

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
- Use `new VRTeasy(vrtClient, visualRegressionTrackerConfig)` when you want full runtime control from code.
- If `enableSoftAssert` is `true`, assertion failures are aggregated and thrown on `stopVRT()`.

Example `vrt.json`:

```json
{
  "enableSoftAssert": true
}
```

## Core API

Main class: `vrteasy-core/src/main/java/ge/tbc/testautomation/VRTeasy.java`

Base client class: `vrteasy-core/src/main/java/ge/tbc/testautomation/client/VRTClient.java`

```java
public abstract class VRTClient {
  public abstract byte[] screenshot();
  public abstract Path downloadPDF(String xpath);
}
```

Common `VRTeasy` operations:

- `takeScreenshotAndTrack(String screenshotIdentifier, TestRunStatus expectedStatus)`
- `downloadAndTrackPDF(String xpath, TestRunStatus expectedStatus)`
- `trackPDF(Path filePath, TestRunStatus expectedStatus)`
- `stopVRT()`

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
vrt.stopVRT();
driver.quit();
```

## Maven dependency examples

Project release version: `0.3.1`

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
  <version>0.3.1</version>
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
  <version>0.3.1</version>
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
  <version>0.3.1</version>
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

