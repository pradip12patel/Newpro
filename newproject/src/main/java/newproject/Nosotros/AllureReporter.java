package newproject.Nosotros;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.Status;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AllureReporter {

    // private AllureReporter() {
    // }

    public static void init() throws IOException {
        // Ensure allure-results directory exists under the current working directory's target folder
        Path results = Paths.get(System.getProperty("user.dir"), "target" + "\\allure-results\\" + "Test_Resports");
        if (!Files.exists(results)) {
            Files.createDirectories(results);
        }
    }

    public static void step(String name, Runnable code) {
        AllureLifecycle lifecycle = Allure.getLifecycle();
        StepResult step = new StepResult().setName(name);
        lifecycle.startStep(null, step);
        try {
            code.run();
            lifecycle.updateStep(stepResult -> stepResult.setStatus(Status.PASSED));
        } catch (Throwable t) {
            lifecycle.updateStep(stepResult -> stepResult.setStatus(Status.FAILED));
            throw t;
        } finally {
            lifecycle.stopStep();
        }
    }

    public static void attachText(String name, String text) {
        if (text == null) text = "";
        Allure.addAttachment(name, "text/plain", new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), ".txt");
    }

    public static void attachScreenshot(WebDriver driver, String name) {
        try {
            if (driver instanceof TakesScreenshot) {
                byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(name, new ByteArrayInputStream(bytes));
            }
        } catch (Exception ignored) {
        }
    }

    public static void attachFile(String name, Path filePath, String mimeType, String extension) {
        if (!Files.exists(filePath)) return;
        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
            Allure.addAttachment(name, mimeType, fis, extension);
        } catch (IOException ignored) {
        }
    }
}
