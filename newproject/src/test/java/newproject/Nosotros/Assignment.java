package newproject.Nosotros;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Listeners({io.qameta.allure.testng.AllureTestNg.class, 
            newproject.Nosotros.AllureFailureListener.class})
public class Assignment extends BaseClass {

    static ConstantMethod cm = new ConstantMethod();

    @Epic("Teacher'assignment Module")
    @Feature("Assignment create feature")
    @Story("Verify Assignment create with single user")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Assignment create using single username & password combination")
    @Test
    public void testAssignment() {

        final Logger logger = LogManager.getLogger(Assignment.class);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(40));
        final PageObjectModel pom = new PageObjectModel(driver);

        logger.info("------------------Testing Started-------------");
        // Login flow
        pom.Clicklogin().click();
        Allure.step("Login button clicked");

        pom.Username().sendKeys(cm.username1);
        pom.Password().sendKeys(cm.password1);
        logger.info("--------------Entered Data-------------");

        pom.Login().click();

        List<WebElement> classes = pom.classview();

        for (int i = 0; i < classes.size(); i++) {

            if (i == 3) {
                String className = classes.get(3).getText();
                classes.get(3).click();
                logger.info("Clicked on class: " + className);
                Allure.step("Class Name: " + className);
                break;
            }
        }

        List<WebElement> books = pom.bookview();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        for (int j = 0; j < books.size(); j++) {

            if (j == 1) {

                String bookName = books.get(1).getText();
                WebElement book = books.get(1);

                JavascriptExecutor js = (JavascriptExecutor) driver;

                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", book);

                wait.until(ExpectedConditions.elementToBeClickable(book));
                js.executeScript("arguments[0].click();", book);

                logger.info("Clicked on Book: " + bookName);
                Allure.step("Book Name: " + bookName);
                break;
            }
        }

    }

}
