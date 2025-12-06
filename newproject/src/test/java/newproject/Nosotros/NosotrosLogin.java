package newproject.Nosotros;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Listeners({io.qameta.allure.testng.AllureTestNg.class})
public class NosotrosLogin extends BaseClass {

    static ConstantMethod cm = new ConstantMethod();

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {

        return new Object[][]{
            {cm.username1, cm.password1, "success"},
            {cm.username2, cm.password2, "Fail"},
            {cm.username3, cm.password3, "Fail"},
            {cm.username4, cm.password4, "Fail"},};
    }

    @Epic("User Module")
    @Feature("Login Feature")
    @Story("Verify Login with multiple users")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Login test using multiple username & password combinations")
    @Test(dataProvider = "loginData")
    public void testLoginMethod(String username, String password, String expectedResult) {

        final Logger logger = LogManager.getLogger(NosotrosLogin.class);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(40));
        final PageObjectModel pom = new PageObjectModel(driver);

        logger.info("------------------Testing Started-------------");
        // Login flow
        pom.Clicklogin().click();
        Allure.step("Login button clicked");

        pom.Username().sendKeys(username);
        pom.Password().sendKeys(password);
        logger.info("--------------Entered Data-------------");

        pom.Login().click();

        SoftAssert softAssert = new SoftAssert();
        boolean isLoggedIn = false;

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.visibilityOf(pom.Profileview()));
            isLoggedIn = true;
        } catch (Exception e) {
            isLoggedIn = false;
        }

        if (expectedResult.equalsIgnoreCase("success")) {

            if (isLoggedIn) {
                logger.info("Login successful for user: " + username);
                softAssert.assertTrue(isLoggedIn, "Login should succeed for user: " + username);
            } else {
                logger.error("Login failed for user: " + username);
                softAssert.assertTrue(false, "Login test failed");
            }

        } else if (expectedResult.equalsIgnoreCase("fail")) {

            if (!isLoggedIn) {
                logger.info("Login failed as expected for user: " + username);
                softAssert.assertFalse(isLoggedIn, "Login should fail for user: " + username);
            } else {
                logger.error("Login succeeded unexpectedly for user: " + username);
                softAssert.assertTrue(false, "Negative login test failed");
            }
        }

        softAssert.assertAll();

    }

}
