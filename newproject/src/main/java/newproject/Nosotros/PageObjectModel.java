package newproject.Nosotros;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObjectModel {

    WebDriver driver;
    By clicklogin = By.xpath("//div[@class='ant-row deathDeskBtn css-198drv2']//button[@type='button'][normalize-space()='Login']");
    By username = By.xpath("//input[@id='studentUsername']");
    By password = By.xpath("//input[@id='password']");
    By login = By.xpath("//div[@class='ant-col ant-col-24 webLoginBtn css-198drv2']");
    By successlogin = By.xpath("//h1[1]");
    By profile = By.xpath("//div[@class='desk usrDetailCstmLft']");


    PageObjectModel(WebDriver driver2) {

        this.driver = driver2;
    }

    WebElement Clicklogin() {

        return driver.findElement(clicklogin);
    }

    WebElement Username() {

        return driver.findElement(username);
    }

    WebElement Password() {

        return driver.findElement(password);
    }

     WebElement Login() {

        return driver.findElement(login);
    }

    WebElement SuccessLogin() {

        return driver.findElement(successlogin);
    }

    WebElement Profileview() {

        return driver.findElement(profile);
    }
}
