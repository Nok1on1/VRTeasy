# VRTeasy

VRTeasy is a thin Java wrapper around [Visual Regression Tracker](https://github.com/Visual-Regression-Tracker/Visual-Regression-Tracker) for UI visual checks.

## Modules

- `ge.tbc.testautomation:vrteasy-core` - `VRTeasy`, `VRTeasyConfigProvider`, and `VRTClient`
- `ge.tbc.testautomation:vrteasy-playwright` - Playwright adapter (`PlaywrightVRTClient`)
- `ge.tbc.testautomation:vrteasy-selenium` - Selenium adapter (`SeleniumVRTClient`)

## Requirements

- Java 17+
- Maven 3.8+
- A running Visual Regression Tracker server

## Installation

```bash
mvn clean install
```

## Configuration

`VRTeasy` has two constructors:

```java
new VRTeasy(VRTClient client)
new VRTeasy(VRTClient client, VisualRegressionTrackerConfig vrtConfig)
```

- Use the second constructor when you want to supply a fully configured `VisualRegressionTrackerConfig`.
- Use the first constructor when you want VRTeasy to read `vrt.json` from the current working directory.
- If `enableSoftAssert` is `true`, assertion failures are collected and thrown at `stopVRT()`; otherwise they fail immediately.

Example `vrt.json`:

```json
{
  "enableSoftAssert": true
}
```

## Core API

Main class: `vrteasy-core/src/main/java/ge/tbc/testautomation/VRTeasy.java`

Interface: `vrteasy-core/src/main/java/ge/tbc/testautomation/client/VRTClient.java`

`VRTClient` only needs to provide screenshot bytes:

```java
public interface VRTClient {
  byte[] screenshot();
}
```

### Playwright adapter

`vrteasy-playwright/src/main/java/ge/tbc/testautomation/client/PlaywrightVRTClient.java`

```java
Playwright playwright = Playwright.create();
Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
Page page = browser.newPage();

VRTeasy vrt = new VRTeasy(new PlaywrightVRTClient(page));

page.navigate("https://example.com");
vrt.takeScreenshot("example-homepage", TestRunStatus.OK);
vrt.stopVRT();

page.close();
browser.close();
playwright.close();
```

### Selenium adapter

`vrteasy-selenium/src/main/java/ge/tbc/testautomation/client/SeleniumVRTClient.java`

```java
WebDriver driver = new ChromeDriver();
VRTeasy vrt = new VRTeasy(new SeleniumVRTClient(driver));

driver.get("https://example.com");
vrt.takeScreenshot("example-homepage", TestRunStatus.OK);
vrt.stopVRT();
driver.quit();
```

## Consumer dependency examples

VRTeasy keeps the major integrations on `provided` scope:

- Visual Regression Tracker SDK
- TestNG
- Playwright
- Selenium

That means your project must provide compatible runtime/compile-time dependencies.

Current artifact versions are based on the parent release `0.2`.

### Core only

```xml
<dependency>
  <groupId>ge.tbc.testautomation</groupId>
  <artifactId>vrteasy-core</artifactId>
  <version>0.2</version>
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

### Core + Playwright adapter

```xml
<dependency>
  <groupId>ge.tbc.testautomation</groupId>
  <artifactId>vrteasy-playwright</artifactId>
  <version>0.2</version>
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

### Core + Selenium adapter

```xml
<dependency>
  <groupId>ge.tbc.testautomation</groupId>
  <artifactId>vrteasy-selenium</artifactId>
  <version>0.2</version>
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

