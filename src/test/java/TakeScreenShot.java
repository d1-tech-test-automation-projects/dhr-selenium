import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TakeScreenShot extends LogTest {

    // Dinamik dosya yolu - proje klasörü içinde
    private static final String BASE_SCREENSHOT_PATH = System.getProperty("user.dir") +
            File.separator + "src" + File.separator + "test" + File.separator + "screenshots";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

    /**
     * Tam sayfa screenshot alır
     * @param testName Test adı
     * @param driver WebDriver instance
     * @return Screenshot dosya yolu
     */
    public static String takeFullPageScreenshot(String testName, WebDriver driver) {
        stepInfo("Taking full page screenshot for: " + testName);

        try {
            // Screenshot klasörünü oluştur
            createScreenshotDirectory();

            // Screenshot al
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            // Dosya adı oluştur
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String fileName = testName + "_fullpage_" + timestamp + ".png";
            String filePath = BASE_SCREENSHOT_PATH + File.separator + fileName;

            // Dosyayı kaydet
            File destFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destFile);

            screenshotInfo(filePath);
            info("Screenshot saved successfully: " + fileName);

            return filePath;

        } catch (IOException e) {
            logException("Taking full page screenshot", e);
            return null;
        }
    }

    /**
     * Belirli bir elementin screenshot'ını alır
     * @param element Screenshot alınacak element
     * @param elementName Element açıklaması
     * @param testName Test adı
     * @param driver WebDriver instance
     * @return Screenshot dosya yolu
     */
    public static String takeElementScreenshot(WebElement element, String elementName,
                                               String testName, WebDriver driver) {
        stepInfo("Taking element screenshot: " + elementName);

        try {
            createScreenshotDirectory();

            // Element screenshot al
            File sourceFile = element.getScreenshotAs(OutputType.FILE);

            // Dosya adı oluştur
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String fileName = testName + "_" + elementName.replaceAll("[^a-zA-Z0-9]", "_")
                    + "_" + timestamp + ".png";
            String filePath = BASE_SCREENSHOT_PATH + File.separator + fileName;

            // Dosyayı kaydet
            File destFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destFile);

            screenshotInfo(filePath);
            info("Element screenshot saved: " + fileName);

            return filePath;

        } catch (IOException e) {
            logException("Taking element screenshot", e);
            return null;
        }
    }

    /**
     * Test failed durumunda screenshot alır
     * @param testName Test adı
     * @param driver WebDriver instance
     * @param errorMessage Hata mesajı
     * @return Screenshot dosya yolu
     */
    public static String takeFailureScreenshot(String testName, WebDriver driver, String errorMessage) {
        error("Test failed, taking screenshot: " + testName);

        try {
            createScreenshotDirectory();

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String fileName = "FAILED_" + testName + "_" + timestamp + ".png";
            String filePath = BASE_SCREENSHOT_PATH + File.separator + "failures"
                    + File.separator + fileName;

            // Failure klasörünü oluştur
            createDirectory(BASE_SCREENSHOT_PATH + File.separator + "failures");

            File destFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destFile);

            error("Failure screenshot saved: " + filePath);
            error("Error details: " + errorMessage);

            return filePath;

        } catch (IOException e) {
            logException("Taking failure screenshot", e);
            return null;
        }
    }

    /**
     * Belirli CSS selector ile element screenshot alır
     * @param cssSelector CSS selector
     * @param testName Test adı
     * @param driver WebDriver instance
     * @return Screenshot dosya yolu
     */
    public static String takeScreenshotBySelector(String cssSelector, String testName, WebDriver driver) {
        try {
            WebElement element = driver.findElement(By.cssSelector(cssSelector));
            return takeElementScreenshot(element, "css_element", testName, driver);
        } catch (Exception e) {
            logException("Taking screenshot by CSS selector", e);
            return null;
        }
    }

    /**
     * Test adımları için screenshot serisi alır
     * @param testName Test adı
     * @param stepName Adım adı
     * @param driver WebDriver instance
     * @return Screenshot dosya yolu
     */
    public static String takeStepScreenshot(String testName, String stepName, WebDriver driver) {
        stepInfo("Taking step screenshot: " + stepName);

        try {
            createScreenshotDirectory();

            // Steps klasörü oluştur
            String stepsPath = BASE_SCREENSHOT_PATH + File.separator + "steps" + File.separator + testName;
            createDirectory(stepsPath);

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String fileName = stepName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
            String filePath = stepsPath + File.separator + fileName;

            File destFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destFile);

            screenshotInfo(filePath);

            return filePath;

        } catch (IOException e) {
            logException("Taking step screenshot", e);
            return null;
        }
    }

    /**
     * Screenshot klasörünü oluşturur
     */
    private static void createScreenshotDirectory() {
        createDirectory(BASE_SCREENSHOT_PATH);
    }

    /**
     * Belirtilen klasörü oluşturur
     * @param directoryPath Klasör yolu
     */
    private static void createDirectory(String directoryPath) {
        try {
            Files.createDirectories(Paths.get(directoryPath));
        } catch (IOException e) {
            logException("Creating directory: " + directoryPath, e);
        }
    }

    /**
     * Screenshot klasörünü temizler (eski dosyaları siler)
     * @param daysOld Kaç gün önceki dosyalar silinecek
     */
    public static void cleanupOldScreenshots(int daysOld) {
        info("Cleaning up screenshots older than " + daysOld + " days");

        File screenshotDir = new File(BASE_SCREENSHOT_PATH);
        if (screenshotDir.exists() && screenshotDir.isDirectory()) {
            File[] files = screenshotDir.listFiles();
            if (files != null) {
                long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000);

                for (File file : files) {
                    if (file.isFile() && file.lastModified() < cutoffTime) {
                        if (file.delete()) {
                            debug("Deleted old screenshot: " + file.getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Screenshot base path'ini döndürür
     * @return Base screenshot path
     */
    public static String getScreenshotBasePath() {
        return BASE_SCREENSHOT_PATH;
    }
}