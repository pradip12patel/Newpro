package newproject.Nosotros;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import io.github.bonigarcia.wdm.WebDriverManager;

@Listeners({io.qameta.allure.testng.AllureTestNg.class})
public class BaseClass extends AllureReporter{

	Properties properties = new Properties();
	WebDriver driver;

	public void driverintilizer() throws IOException {
		FileInputStream fis = new FileInputStream(
				Paths.get(System.getProperty("user.dir"), "src","main","java","newproject", "data.properties").toString());
		properties.load(fis);
        

		String key1 = properties.getProperty("browser");

		if (key1.equalsIgnoreCase("chrome")) {

			ChromeOptions option = new ChromeOptions();

			option.addArguments("remote-allow-origins=*");

			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(option);

		} else if (key1.equalsIgnoreCase("firefox")) {

			driver = new FirefoxDriver();
		} else {

			System.out.println("browser is not found");
		}

	}

	
    @BeforeMethod
	public void openurl() throws IOException {

		driverintilizer();

		String key2 = properties.getProperty("url");

		driver.get(key2);

	}

	@AfterMethod
	public void aftermethod()   {

       driver.manage().window().maximize();

	}

}
