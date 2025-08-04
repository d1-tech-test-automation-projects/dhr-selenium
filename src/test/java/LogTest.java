import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ExtendWith(TestResultLogger.class)
public class LogTest {

    private static final Logger logger = LogManager.getLogger(LogTest.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Test başlangıç/bitiş metodları
    public static void startLog(String testClassName) {
        logger.info("========================================");
        logger.info("{} Test Started at: {}", testClassName, LocalDateTime.now().format(formatter));
        logger.info("========================================");
    }

    public static void endLog(String testClassName) {
        logger.info("========================================");
        logger.info("{} Test Ended at: {}", testClassName, LocalDateTime.now().format(formatter));
        logger.info("========================================");
    }

    // Temel log metodları
    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void fatal(String message) {
        logger.fatal(message);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    // Selenium-specific log metodları
    public static void stepInfo(String stepDescription) {
        logger.info("STEP: {}", stepDescription);
    }

    public static void actionInfo(String action, String element) {
        logger.info("ACTION: {} on element: {}", action, element);
    }

    public static void verificationInfo(String verification, String expected, String actual) {
        logger.info("VERIFICATION: {} | Expected: {} | Actual: {}", verification, expected, actual);
    }

    public static void screenshotInfo(String screenshotPath) {
        logger.info("SCREENSHOT: Saved at {}", screenshotPath);
    }

    // Test sonuç metodları
    public static void testPassed(String testName) {
        logger.info("✓ TEST PASSED: {}", testName);
    }

    public static void testFailed(String testName, String reason) {
        logger.error("✗ TEST FAILED: {} | Reason: {}", testName, reason);
    }

    public static void testSkipped(String testName, String reason) {
        logger.warn("⚠ TEST SKIPPED: {} | Reason: {}", testName, reason);
    }

    // Exception handling
    public static void logException(String context, Exception e) {
        logger.error("EXCEPTION in {}: {} - {}", context, e.getClass().getSimpleName(), e.getMessage());
        logger.debug("Stack trace: ", e);
    }

    // Performance logging
    public static void logExecutionTime(String operation, long timeInMs) {
        logger.info("PERFORMANCE: {} completed in {} ms", operation, timeInMs);
    }

    // Browser/Driver related logs
    public static void browserAction(String action, String browser) {
        logger.info("BROWSER: {} action performed on {}", action, browser);
    }

    public static void pageNavigation(String url) {
        logger.info("NAVIGATION: Navigated to {}", url);
    }

    public static void elementInteraction(String action, String elementDescription) {
        logger.info("ELEMENT: {} performed on {}", action, elementDescription);
    }

    // Data-driven test logs
    public static void testDataInfo(String dataSet, Object... values) {
        logger.info("TEST DATA: {} | Values: {}", dataSet, java.util.Arrays.toString(values));
    }

    // Utility method for formatted messages
    public static void logWithFormat(String level, String message, Object... params) {
        String formattedMessage = String.format(message, params);
        switch (level.toUpperCase()) {
            case "INFO":
                logger.info(formattedMessage);
                break;
            case "WARN":
                logger.warn(formattedMessage);
                break;
            case "ERROR":
                logger.error(formattedMessage);
                break;
            case "DEBUG":
                logger.debug(formattedMessage);
                break;
            default:
                logger.info(formattedMessage);
        }
    }
}




