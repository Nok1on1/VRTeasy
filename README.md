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

Use the example file from the core module and create your local config:

```bash
cp vrteasy-core/src/main/resources/vrteasy.properties.example vrteasy-core/src/main/resources/vrteasy.properties
```

Then edit `vrteasy-core/src/main/resources/vrteasy.properties`:

```properties
API_URL=http://localhost:4200
API_KEY=YOUR_VRT_API_KEY
PROJECT_NAME=Default project
BRANCH_NAME=master
```

`VRTeasy` loads these keys through `VRTeasyConfigProvider` at startup.

## Core API

Main class: `vrteasy-core/src/main/java/ge/tbc/testautomation/VRTeasy.java`

Constructor:

```java
new VRTeasy(VRTClient client, Boolean softAssert)
```

- `client`: testing Framework (`PlaywrightVRTClient` or `SeleniumVRTClient`)
- `softAssert`: `true` collects assertion failures and throws at `stopVRT()`; `false` fails immediately

## Usage Example (Playwright)

```java
Playwright playwright = Playwright.create();
Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
Page page = browser.newPage();

VRTeasy vrt = new VRTeasy(new PlaywrightVRTClient(page), false);

page.navigate("https://example.com");
vrt.takeScreenshot("example-homepage", TestRunStatus.OK);
vrt.stopVRT();

page.close();
browser.close();
playwright.close();
```

## Usage Example (Selenium)

```java
WebDriver driver = new ChromeDriver();
VRTeasy vrt = new VRTeasy(new SeleniumVRTClient(driver), false);

driver.get("https://example.com");
vrt.takeScreenshot("example-homepage", TestRunStatus.OK);
vrt.stopVRT();
driver.quit();
```

## Consumer Dependency Examples

Note on dependency scopes:
- VRTeasy modules declare major integrations (Visual-Regression-Tracker, Playwright, Selenium, TestNG) as `provided`.
- This keeps VRTeasy artifacts lightweight.
- Your project must provide compatible versions of the libraries you use at compile/runtime.
- If multiple or incompatible versions are present, runtime issues like `NoSuchMethodError` may occur.

Core only:

```xml
<dependency>
  <groupId>ge.tbc.testautomation</groupId>
  <artifactId>vrteasy-core</artifactId>
  <version>0.1</version>
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

Core + Playwright adapter:

```xml
<dependency>
  <groupId>ge.tbc.testautomation</groupId>
  <artifactId>vrteasy-playwright</artifactId>
  <version>0.1</version>
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

Core + Selenium adapter:

```xml
<dependency>
  <groupId>ge.tbc.testautomation</groupId>
  <artifactId>vrteasy-selenium</artifactId>
  <version>0.1</version>
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

