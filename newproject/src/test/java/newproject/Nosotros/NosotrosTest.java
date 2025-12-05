package newproject.Nosotros;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

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

        return new Object[][] {
            {cm.username1, cm.password1},
            {cm.username2, cm.password2},
            {cm.username3, cm.password3},          
            {cm.username4, cm.password4},    
        };
    }
    @Epic("User Module")
    @Feature("Login Feature")
    @Story("Verify Login with multiple users")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Login test using multiple username & password combinations")
    @Test(dataProvider = "loginData")
  public void testLoginMethod(String username, String password) {

       final Logger logger = LogManager.getLogger(NosotrosTest.class);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(40));
        final PageObjectModel pom = new PageObjectModel(driver);
           
        logger.info("------------------Testing Started-------------");
        // Login flow
        pom.Clicklogin().click();
        Allure.step("Login button clicked");
		pom.Username().sendKeys(username);
		pom.Password().sendKeys(password);
        pom.Login().click();
        Allure.step("Successfully Login");

        logger.info("--------------Entered Data-------------");
        // Validation
        System.out.println("Login result for " + username );

    }
}
