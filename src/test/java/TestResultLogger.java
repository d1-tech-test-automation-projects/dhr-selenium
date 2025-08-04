import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TestResultLogger implements TestWatcher {

    private static final Logger logger = LogManager.getLogger(TestResultLogger.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Test başlangıç zamanlarını saklamak için
    private static final ConcurrentHashMap<String, LocalDateTime> testStartTimes = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Object> testInstances = new ConcurrentHashMap<>();

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        String testName = getTestName(context);
        String disableReason = reason.orElse("No reason provided");

        LogTest.warn("========================================");
        LogTest.warn("⚠ TEST DISABLED: " + testName);
        LogTest.warn("Disable reason: " + disableReason);
        LogTest.warn("Disabled at: " + LocalDateTime.now().format(formatter));
        LogTest.warn("========================================");

        logger.warn("[DISABLED] {} - Reason: {}", testName, disableReason);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        String testName = getTestName(context);
        Duration testDuration = calculateTestDuration(testName);

        LogTest.info("========================================");
        LogTest.info("✓ TEST PASSED: " + testName);
        LogTest.info("Completed at: " + LocalDateTime.now().format(formatter));
        if (testDuration != null) {
            LogTest.logExecutionTime(testName, testDuration.toMillis());
        }
        LogTest.info("========================================");

        logger.info("[PASSED] {} - Duration: {}ms", testName,
                testDuration != null ? testDuration.toMillis() : "Unknown");

        // Başarılı test sonrası screenshot (isteğe bağlı)
        takeSuccessScreenshot(context, testName);

        // Test verilerini temizle
        cleanupTestData(testName);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        String testName = getTestName(context);
        Duration testDuration = calculateTestDuration(testName);
        String abortReason = cause != null ? cause.getMessage() : "Unknown reason";

        LogTest.warn("========================================");
        LogTest.warn("⚠ TEST ABORTED: " + testName);
        LogTest.warn("Abort reason: " + abortReason);
        LogTest.warn("Aborted at: " + LocalDateTime.now().format(formatter));
        if (testDuration != null) {
            LogTest.logExecutionTime(testName + " (ABORTED)", testDuration.toMillis());
        }
        LogTest.warn("========================================");

        if (cause != null) {
            LogTest.logException("Test abortion", new Exception(cause));
        }

        logger.warn("[ABORTED] {} - Reason: {} - Duration: {}ms",
                testName, abortReason, testDuration != null ? testDuration.toMillis() : "Unknown");

        // Abort durumunda screenshot al
        takeFailureScreenshot(context, testName, "Test aborted: " + abortReason);

        cleanupTestData(testName);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testName = getTestName(context);
        Duration testDuration = calculateTestDuration(testName);
        String failureCause = cause != null ? cause.getMessage() : "Unknown cause";
        String exceptionType = cause != null ? cause.getClass().getSimpleName() : "Unknown";

        LogTest.error("========================================");
        LogTest.error("✗ TEST FAILED: " + testName);
        LogTest.error("Failure cause: " + failureCause);
        LogTest.error("Exception type: " + exceptionType);
        LogTest.error("Failed at: " + LocalDateTime.now().format(formatter));
        if (testDuration != null) {
            LogTest.logExecutionTime(testName + " (FAILED)", testDuration.toMillis());
        }
        LogTest.error("========================================");

        if (cause != null) {
            LogTest.logException("Test failure", new Exception(cause));

            // Stack trace'in önemli kısımlarını logla
            StackTraceElement[] stackTrace = cause.getStackTrace();
            if (stackTrace.length > 0) {
                LogTest.error("Failure location: " + stackTrace[0].toString());
            }
        }

        logger.error("[FAILED] {} - Cause: {} - Type: {} - Duration: {}ms",
                testName, failureCause, exceptionType,
                testDuration != null ? testDuration.toMillis() : "Unknown");

        // Failure screenshot al
        takeFailureScreenshot(context, testName, failureCause);

        cleanupTestData(testName);
    }

    /**
     * Test başladığında çağrılır (manuel olarak BaseTest'ten)
     */
    public static void recordTestStart(String testName, Object testInstance) {
        testStartTimes.put(testName, LocalDateTime.now());
        testInstances.put(testName, testInstance);

        LogTest.info("📋 TEST STARTED: " + testName);
        LogTest.info("Start time: " + LocalDateTime.now().format(formatter));
    }

    /**
     * Test adını context'ten çıkarır
     */
    private String getTestName(ExtensionContext context) {
        return context.getTestClass().map(Class::getSimpleName).orElse("Unknown") +
                "." + context.getDisplayName();
    }

    /**
     * Test süresini hesaplar
     */
    private Duration calculateTestDuration(String testName) {
        LocalDateTime startTime = testStartTimes.get(testName);
        if (startTime != null) {
            return Duration.between(startTime, LocalDateTime.now());
        }
        return null;
    }

    /**
     * Başarılı test sonrası screenshot alır
     */
    private void takeSuccessScreenshot(ExtensionContext context, String testName) {
        try {
            WebDriver driver = getDriverFromTestInstance(testName);
            if (driver != null) {
                TakeScreenShot.takeStepScreenshot(testName, "test_success", driver);
            }
        } catch (Exception e) {
            LogTest.debug("Could not take success screenshot: " + e.getMessage());
        }
    }

    /**
     * Başarısız test sonrası screenshot alır
     */
    private void takeFailureScreenshot(ExtensionContext context, String testName, String errorMessage) {
        try {
            WebDriver driver = getDriverFromTestInstance(testName);
            if (driver != null) {
                TakeScreenShot.takeFailureScreenshot(testName, driver, errorMessage);
            }
        } catch (Exception e) {
            LogTest.debug("Could not take failure screenshot: " + e.getMessage());
        }
    }

    /**
     * Test instance'ından driver'ı reflection ile alır
     */
    private WebDriver getDriverFromTestInstance(String testName) {
        Object testInstance = testInstances.get(testName);
        if (testInstance != null) {
            try {
                Field driverField = testInstance.getClass().getSuperclass().getDeclaredField("driver");
                driverField.setAccessible(true);
                return (WebDriver) driverField.get(testInstance);
            } catch (Exception e) {
                LogTest.debug("Could not access driver field: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Test verilerini temizler
     */
    private void cleanupTestData(String testName) {
        testStartTimes.remove(testName);
        testInstances.remove(testName);
    }

    /**
     * Test suite istatistiklerini döndürür
     */
    public static void logTestSuiteStatistics() {
        LogTest.info("========================================");
        LogTest.info("TEST SUITE COMPLETED");
        LogTest.info("Suite finished at: " + LocalDateTime.now().format(formatter));
        LogTest.info("========================================");
    }
}