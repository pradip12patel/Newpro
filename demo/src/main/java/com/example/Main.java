package com.example;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {

        logger.info("----------Application started------------");

        // Timestamp and file location
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(dtf);

        String projectPath = System.getProperty("user.dir");
        String filepath = projectPath + "\\Download_data\\";
        String filename = "Speaker_List_" + timestamp + ".xlsx";

        // Create directory if not exist
        Files.createDirectories(Paths.get(filepath));
        logger.info("Ensured download directory exists: {}", filepath);

        // Setup ChromeDriver path
        String driverPath = Paths.get(projectPath, "drivers", "chromedriver-win64", "chromedriver.exe").toString();
        System.setProperty("webdriver.chrome.driver", driverPath);
        logger.info("Configured webdriver.chrome.driver = {}", driverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // Only set binary if you have manual chrome.exe
        // options.setBinary(projectPath + "\\drivers\\chrome-win64\\chrome.exe");

        WebDriverManager.chromedriver().setup();
        logger.info("WebDriverManager set up chromedriver");
        WebDriver driver = new ChromeDriver(options);
        logger.info("Launched ChromeDriver");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.manage().window().maximize();

        // Initialize Allure reporter (safe to fail)
        try {
            AllureReporter.init();
            logger.info("Allure reporter initialized");
        } catch (Exception e) {
            logger.warn("Failed to initialize Allure reporter", e);
        }

        // Navigate to page inside an Allure step
        AllureReporter.step("Navigate to speakers page", () -> {
            driver.get("https://thebabyshows.com/toronto-fall-baby-show/#speakers");
            logger.info("Navigated to speakers page");
        });

        // Close popup if exists (inside an Allure step)
        AllureReporter.step("Close popup if present", () -> {
            try {
                WebElement closeBtn = driver.findElement(By.xpath("(//button[@class='ub-emb-close'])[1]"));
                closeBtn.click();
                logger.info("Closed popup dialog if present");
            } catch (Exception e) {
                logger.info("No popup displayed.");
            }
        });

        // Collect main elements
        List<WebElement> listNames = driver.findElements(By.xpath("//h2[@class='ab-profile-name']"));
        List<WebElement> listTitles = driver.findElements(By.xpath("//p[@class='ab-profile-title']"));
        List<WebElement> listButtons = driver.findElements(By.xpath("//div[@class='wp-block-button']//a[contains(text(),'FULL BIO')]"));
        List<WebElement> listImages = driver.findElements(By.xpath("//figure[@class='ab-profile-image-square']//img"));

        logger.info("Found elements - names: {}, titles: {}, buttons: {}, images: {}",
            listNames.size(), listTitles.size(), listButtons.size(), listImages.size());

        // Workbook and Excel sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Speakers");

        // Header styling
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setWrapText(true);

        // Write header row
        XSSFRow headerRow = sheet.createRow(0);
        String[] headers = {"Speaker_Name", "Speaker_Title", "Speaker_SocialHandle", "Speaker_Image", "Speaker_Profile"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Loop through speakers
        for (int i = 0; i < listNames.size(); i++) {
            logger.info("Processing speaker index {}", i);

            try {
                final int idx = i;
                final List<WebElement> namesSnap = listNames;
                final List<WebElement> titlesSnap = listTitles;
                final List<WebElement> buttonsSnap = listButtons;
                final List<WebElement> imagesSnap = listImages;

                AllureReporter.step("Process speaker index " + i, () -> {
                    try {
                        String SpeakerName = namesSnap.get(idx).getText();
                        String SpeakerTitle = titlesSnap.get(idx).getText();
                        String ImageURL = imagesSnap.get(idx).getAttribute("src");

                        JavascriptExecutor js = (JavascriptExecutor) driver;
                        js.executeScript("arguments[0].click()", buttonsSnap.get(idx));

                        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

                        String SpeakerProfile = driver.findElement(By.xpath("//div[@class='ab-profile-text']")).getText();

                        // Collect social links
                        List<WebElement> socialHandles = driver.findElements(By.xpath("//ul[@class='ab-social-links']//li//a"));
                        List<String> socialList = new ArrayList<>();
                        for (WebElement handle : socialHandles) {
                            socialList.add(handle.getAttribute("href"));
                        }
                        String allSocialHandles = String.join(", ", socialList);

                        // Log and attach fetched data
                        logger.info("Fetched speaker data - Name: {}", SpeakerName);
                        AllureReporter.attachText("Name", SpeakerName);
                        logger.info("Title: {}", SpeakerTitle);
                        AllureReporter.attachText("Title", SpeakerTitle);
                        logger.info("Social Handles: {}", allSocialHandles);
                        AllureReporter.attachText("Social Handles", allSocialHandles);
                        logger.info("Image URL: {}", ImageURL);
                        AllureReporter.attachText("Image URL", ImageURL);
                        logger.info("Profile: {}", SpeakerProfile);
                        AllureReporter.attachText("Profile", SpeakerProfile);
                        AllureReporter.attachScreenshot(driver, "Speaker - " + SpeakerName);

                        // Create row
                        XSSFRow row = sheet.createRow(idx + 1);
                        row.createCell(0).setCellValue(SpeakerName);
                        row.createCell(1).setCellValue(SpeakerTitle);
                        row.createCell(2).setCellValue(allSocialHandles);
                        row.createCell(3).setCellValue(ImageURL);
                        row.createCell(4).setCellValue(SpeakerProfile);

                        driver.navigate().back();

                    } catch (Exception ex) {
                        logger.error("Error inside Allure step processing speaker at index {}", idx, ex);
                        throw ex;
                    }
                });

                // Refresh elements after navigating back
                listNames = driver.findElements(By.xpath("//h2[@class='ab-profile-name']"));
                listTitles = driver.findElements(By.xpath("//p[@class='ab-profile-title']"));
                listButtons = driver.findElements(By.xpath("//div[@class='wp-block-button']//a[contains(text(),'FULL BIO')]"));
                listImages = driver.findElements(By.xpath("//figure[@class='ab-profile-image-square']//img"));

            } catch (Exception e) {
                logger.error("Error processing speaker at index {}", i, e);
                try { AllureReporter.attachText("Processing error at index " + i, e.toString()); } catch (Exception ignored) {}
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write file
        try (FileOutputStream fos = new FileOutputStream(filepath + filename)) {
            workbook.write(fos);
            logger.info("Wrote Excel workbook to {}", filepath + filename);
            try {
                AllureReporter.attachFile("Speakers Excel", Paths.get(filepath, filename), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");
                logger.info("Attached Excel workbook to Allure results");
            } catch (Exception e) {
                logger.warn("Failed to attach Excel to Allure results", e);
            }
        } catch (IOException e) {
            logger.error("Failed to write Excel file", e);
        } finally {
            workbook.close();
            driver.quit();
            logger.info("Closed workbook and quit driver");
        }
    }
}
