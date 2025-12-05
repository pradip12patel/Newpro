package newproject.Nosotros;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import dev.failsafe.internal.util.Assert;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Listeners({io.qameta.allure.testng.AllureTestNg.class})
public class NosotrosTest extends BaseClass {

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

        final Logger logger = LogManager.getLogger(NosotrosTest.class);

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

        boolean isLoggedIn = false;

        try {
            isLoggedIn = pom.Profileview().isDisplayed();
        } catch (Exception e) {
            isLoggedIn = false;
        }

        if (expectedResult.equals("success")) {
            if (isLoggedIn) {
                System.out.println("Login successful for user: " + username);
                logger.info("Login successful for user: " + username);
                Assert.isTrue(true, "Login test passed");
            } else {
                System.out.println("Login failed for user: " + username);
                logger.error("Login failed for user: " + username);
                Assert.isTrue(false, "Login test failed");
            }
        } else if (expectedResult.equals("Fail")) {
            if (!isLoggedIn) {
                System.out.println("Login failed as expected for user: " + username);
                logger.info("Login failed as expected for user: " + username);
                Assert.isTrue(true, "Negative login test passed");
            } else {
                System.out.println("Login succeeded unexpectedly for user: " + username);
                logger.error("Login succeeded unexpectedly for user: " + username);
                Assert.isTrue(false, "Negative login test failed");
            }
        }

    }

}
