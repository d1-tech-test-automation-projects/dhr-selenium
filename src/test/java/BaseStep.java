import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BaseStep extends LogTest {


        public static WebDriver driver;
        private static WebDriverWait wait;
        private static final String DEFAULT_URL = "https://dhrtest.d1-tech.com.tr/login";
        private static final int DEFAULT_TIMEOUT = 10;

        /**
         * Chrome driver'ı başlatır ve konfigüre eder
         */
        public static void openChromeDriver() {
            try {
                stepInfo("Initializing Chrome WebDriver");

                // Chrome options
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--ignore-certificate-errors");
                options.addArguments("--ignore-ssl-errors");
                options.addArguments("--allow-running-insecure-content");

                // Driver oluştur
                driver = new ChromeDriver(options);
                wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));

                // Temel konfigürasyonlar
                driver.manage().window().maximize();
                driver.manage().deleteAllCookies();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(DEFAULT_TIMEOUT));

                browserAction("Chrome browser initialized", "Chrome");

                // Default URL'e git
                navigateToUrl(DEFAULT_URL);

            } catch (Exception e) {
                logException("Opening Chrome driver", e);
                throw new RuntimeException("Failed to initialize Chrome driver", e);
            }
        }

        //wairseconds method
        public static void waitSeconds(int seconds) {
                try {
                    Thread.sleep(seconds * 1000L); // ✅ Mükemmel!
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Wait interrupted: " + e.getMessage());
                }
            }

        
        /**
         * Driver'ı kapatır
         */
        public static void driverQuit() {
            try {
                if (driver != null) {
                    stepInfo("Closing browser");
                    driver.quit();
                    driver = null;
                    wait = null;
                    browserAction("Browser closed", "Chrome");
                }
            } catch (Exception e) {
                logException("Closing driver", e);
            }
        }

        /**
         * Timeout ayarlarını değiştirir
         */
        public static void setImplicitWait(int seconds) {
            try {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
                info("Implicit wait set to: " + seconds + " seconds");
            } catch (Exception e) {
                logException("Setting implicit wait", e);
            }
        }

        /**
         * Element temizleyip text girer
         */
        public static void clearAndType(WebElement element, String text, String elementDescription) {
            try {
                // Null kontrolü
                if (element == null) {
                    throw new IllegalArgumentException("Element cannot be null");
                }
                if (text == null) {
                    text = "";
                }

                actionInfo("Clear and type: " + text, elementDescription);

                // Element durumu kontrolü
                if (!element.isDisplayed() || !element.isEnabled()) {
                    throw new RuntimeException("Element is not interactable: " + elementDescription);
                }

                // Bekleme ekle
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.elementToBeClickable(element));

                // Clear işlemi
                element.clear();

                // Clear başarılı oldu mu kontrol et
                String currentValue = element.getAttribute("value");
                if (currentValue != null && !currentValue.isEmpty()) {
                    element.sendKeys(Keys.CONTROL + "a");
                    element.sendKeys(Keys.DELETE);
                }

                // Metin girişi
                element.sendKeys(text);

                stepInfo("Text entered successfully: " + text);

            } catch (Exception e) {
                logException("Clear and type operation", e);
                TakeScreenShot.takeFailureScreenshot("clearAndType", driver, "Failed to enter text: " + text);
                throw new RuntimeException("Failed to enter text: " + text + " for element: " + elementDescription, e);
            }
        }

        /**
         * Element click işlemi
         */
        public static void clickElement(WebElement element, String elementDescription) {
            try {
                actionInfo("Click", elementDescription);
                element.click();
                stepInfo("Element clicked successfully: " + elementDescription);
            } catch (Exception e) {
                logException("Click operation", e);
                TakeScreenShot.takeFailureScreenshot("clickElement", driver, "Failed to click: " + elementDescription);
                throw new RuntimeException("Failed to click element: " + elementDescription, e);
            }
        }

        /**
         * Text assertion
         Updated güncellendi
         public static void assertEqualsGetText(String xpath, String expectedText, String description) {
         WebElement element = null;
         String actualText = null;

         try {
         element = findElementXpathWithWait(xpath, DEFAULT_TIMEOUT);
         actualText = element.getText();

         verificationInfo(description, expectedText, actualText);
         assertEquals(expectedText, actualText);
         testPassed("Text assertion: " + description);

         } catch (AssertionError e) {
         // Assertion hatası için özel işlem
         logException("Text assertion failed", e);
         TakeScreenShot.takeFailureScreenshot("assertEqualsGetText", driver, "Text assertion failed: " + description);
         testFailed("Text assertion: " + description, "Expected: " + expectedText + ", Actual: " + actualText);
         throw e;

         } catch (Exception e) {
         // Diğer hatalar için genel işlem
         logException("Text assertion error", e);
         TakeScreenShot.takeFailureScreenshot("assertEqualsGetText", driver, "Text assertion error: " + description);
         testFailed("Text assertion: " + description, "Expected: " + expectedText + ", Error: " + e.getMessage());
         throw e;
         }
         }
         */


        /**
         * Title assertion

         public static void assertEqualsGetTitle(String expectedTitle, String description) {
         try {
         String actualTitle = driver.getTitle();

         verificationInfo(description, expectedTitle, actualTitle);
         assertEquals(expectedTitle, actualTitle);
         testPassed("Title assertion: " + description);

         } catch (Exception e) {
         logException("Title assertion", e);
         TakeScreenShot.takeFailureScreenshot("assertEqualsGetTitle", driver, "Title assertion failed: " + description);
         testFailed("Title assertion: " + description, "Expected: " + expectedTitle);
         throw e;
         }
         }
         */


        // ============ FIND ELEMENT METHODS ============

        /**
         * XPath ile element bulur (wait olmadan)
         */
        public static WebElement findElementXpath(String xpath) {
            try {
                WebElement element = driver.findElement(By.xpath(xpath));
                debug("Element found by xpath: " + xpath);
                return element;
            } catch (Exception e) {
                error("Element not found by xpath: " + xpath);
                TakeScreenShot.takeFailureScreenshot("findElementXpath", driver, "Element not found: " + xpath);
                logException("Finding element by xpath", e);
                return null;
            }
        }

        /**
         * XPath ile element bulur (wait ile)
         */
        public static WebElement findElementXpathWithWait(String xpath, int timeoutSeconds) {
            if (driver == null) {
                throw new IllegalStateException("WebDriver is not initialized");
            }
            if (xpath == null || xpath.trim().isEmpty()) {
                throw new IllegalArgumentException("XPath cannot be null or empty");
            }
            if (timeoutSeconds <= 0) {
                throw new IllegalArgumentException("Timeout must be positive");
            }

            try {
                debug("Searching for element with xpath: " + xpath);

                WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                WebElement element = customWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

                debug("Element found by xpath: " + xpath);
                return element;

            } catch (TimeoutException e) {
                error("Element not found within " + timeoutSeconds + " seconds: " + xpath);
                TakeScreenShot.takeFailureScreenshot("findElementXpathWithWait", driver, "Element not found: " + xpath);
                logException("Finding element by xpath with wait", e);
                throw new NoSuchElementException("Element not found after " + timeoutSeconds + " seconds: " + xpath, e);

            } catch (Exception e) {
                error("Unexpected error while finding element: " + xpath);
                TakeScreenShot.takeFailureScreenshot("findElementXpathWithWait", driver, "Error finding element: " + xpath);
                logException("Finding element by xpath with wait", e);
                throw new RuntimeException("Error finding element: " + xpath, e);
            }


        }

        /**
         * ID ile element bulur (wait ile)
         */
        public static WebElement findElementIdWithWait(String id, int timeoutSeconds) {
            try {
                WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                WebElement element = customWait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
                debug("Element found by id with wait: " + id);
                return element;
            } catch (Exception e) {
                error("Element not found by id with wait: " + id);
                logException("Finding element by id with wait", e);
                return null;
            }
        }

        /**
         * Class name ile element bulur (wait ile)
         */
        public static WebElement findElementClassNameWithWait(String className, int timeoutSeconds) {
            try {
                WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                WebElement element = customWait.until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
                debug("Element found by class name with wait: " + className);
                return element;
            } catch (Exception e) {
                error("Element not found by class name with wait: " + className);
                logException("Finding element by class name with wait", e);
                return null;
            }
        }

        /**
         * CSS Selector ile element bulur (wait ile)
         */
        public static WebElement findElementCssSelectorWithWait(String cssSelector, int timeoutSeconds) {
            try {
                WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                WebElement element = customWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
                debug("Element found by CSS selector with wait: " + cssSelector);
                return element;
            } catch (Exception e) {
                error("Element not found by CSS selector with wait: " + cssSelector);
                logException("Finding element by CSS selector with wait", e);
                return null;
            }
        }

        /**
         * Name ile element bulur (wait ile)
         */
        public static WebElement findElementNameWithWait(String name, int timeoutSeconds) {
            try {
                WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                WebElement element = customWait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
                debug("Element found by name with wait: " + name);
                return element;
            } catch (Exception e) {
                error("Element not found by name with wait: " + name);
                logException("Finding element by name with wait", e);
                return null;
            }
        }

        /**
         * Link text ile element bulur (wait ile)
         */
        public static WebElement findElementLinkTextWithWait(String linkText, int timeoutSeconds) {
            try {
                WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                WebElement element = customWait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(linkText)));
                debug("Element found by link text with wait: " + linkText);
                return element;
            } catch (Exception e) {
                error("Element not found by link text with wait: " + linkText);
                logException("Finding element by link text with wait", e);
                return null;
            }
        }

        /**
         * Tag name ile element bulur (wait ile)
         */
        public static WebElement findElementTagNameWithWait(String tagName, int timeoutSeconds) {
            try {
                WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                WebElement element = customWait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(tagName)));
                debug("Element found by tag name with wait: " + tagName);
                return element;
            } catch (Exception e) {
                error("Element not found by tag name with wait: " + tagName);
                logException("Finding element by tag name with wait", e);
                return null;
            }
        }

        // ============ ADDITIONAL METHODS ============

        /**
         * Dropdown'dan element seçer
         */
        public static void selectElementFromDropdown(WebElement dropdown, String optionText, String dropdownDescription) {
            try {
                actionInfo("Select from dropdown: " + optionText, dropdownDescription);
                Select select = new Select(dropdown);
                select.selectByVisibleText(optionText);
                stepInfo("Option selected from dropdown: " + optionText);
            } catch (Exception e) {
                logException("Selecting from dropdown", e);
                TakeScreenShot.takeFailureScreenshot("selectElementFromDropdown", driver, "Failed to select: " + optionText);
                throw new RuntimeException("Failed to select from dropdown: " + optionText, e);
            }
        }

        /**
         * Dosya upload işlemi
         * güncellendi
         */
        public static void fileUpload(String xPath, File filePath, String description) {
            try {
                // Null ve dosya varlık kontrolü
                if (filePath == null || !filePath.exists()) {
                    throw new IllegalArgumentException("File does not exist: " + filePath);
                }

                actionInfo("File upload", description);
                WebElement fileInput = driver.findElement(By.xpath(xPath)); // xPath parametresini kullan
                fileInput.sendKeys(filePath.getAbsolutePath());
                stepInfo("File uploaded successfully: " + filePath.getName());
            } catch (Exception e) {
                logException("File upload", e);
                TakeScreenShot.takeFailureScreenshot("fileUpload", driver, "Failed to upload file: " + filePath.getName());
                throw new RuntimeException("Failed to upload file: " + filePath.getName(), e);
            }
        }
        /**
         * Geri navigasyon
         */
        public static void navigateBack() {
            try {
                stepInfo("Navigating back");
                driver.navigate().back();
                pageNavigation("Back");
            } catch (Exception e) {
                logException("Navigate back", e);
                throw new RuntimeException("Failed to navigate back", e);
            }
        }

        /**
         * İleri navigasyon
         */
        public static void navigateForward() {
            try {
                stepInfo("Navigating forward");
                driver.navigate().forward();
                pageNavigation("Forward");
            } catch (Exception e) {
                logException("Navigate forward", e);
                throw new RuntimeException("Failed to navigate forward", e);
            }
        }

        /**
         * URL'e navigasyon
         */
        public static void navigateToUrl(String url) {
            try {
                stepInfo("Navigating to URL: " + url);
                driver.get(url);
                pageNavigation(url);

                // Sayfanın yüklenmesini bekle
                wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));

            } catch (Exception e) {
                logException("Navigate to URL", e);
                throw new RuntimeException("Failed to navigate to URL: " + url, e);
            }
        }

        /**
         * Element görünür olana kadar bekler
         */
        public static WebElement waitForElementVisible(By locator, int timeoutSeconds) {
            try {
                WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                return customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            } catch (Exception e) {
                logException("Waiting for element visibility", e);
                return null;
            }
        }

        /**
         * Element clickable olana kadar bekler
         */
        public static WebElement waitForElementClickable(By locator, int timeoutSeconds) {
            try {
                WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                return customWait.until(ExpectedConditions.elementToBeClickable(locator));
            } catch (Exception e) {
                logException("Waiting for element clickable", e);
                return null;
            }
        }

        /**
         * Sayfanın tamamen yüklenmesini bekler
         */
        public static void waitForPageLoad() {
            try {
                wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
                stepInfo("Page loaded completely");
            } catch (Exception e) {
                logException("Waiting for page load", e);
            }
        }

        /**
         * Element var mı kontrol eder
         */
        public static boolean isElementPresent(By locator) {
            try {
                driver.findElement(locator);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Multiple element bulur
         */
        public static List<WebElement> findElements(By locator) {
            try {
                List<WebElement> elements = driver.findElements(locator);
                debug("Found " + elements.size() + " elements");
                return elements;
            } catch (Exception e) {
                logException("Finding multiple elements", e);
                return null;
            }
        }

        public void legitimateChecks() {
            // 1. Optional elements için
            WebElement optionalBanner = BaseStep.findElementIdWithWait("promo-banner", TimeOut.SHORT.value);
            if (optionalBanner != null) {
                BaseStep.clickElement(optionalBanner, "Promo banner");
            }
        /*
        // 2. Business logic için
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.contains("login")) {
            performLogin();
        } else if (currentUrl.contains("dashboard")) {
            performDashboardOperations();
        }

        // 3. Dynamic content için
        java.util.List<WebElement> notifications = BaseStep.findElements(By.className("notification"));
        if (notifications != null && !notifications.isEmpty()) {
            info("Found " + notifications.size() + " notifications");
        }*/
        }


    }



