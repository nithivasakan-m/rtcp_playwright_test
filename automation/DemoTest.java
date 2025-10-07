package rtcplatform.automation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;


import java.io.File;
import java.util.concurrent.TimeUnit;

public class DemoTest {

	private WebDriver driver;
    private WebDriverWait wait;
    private String videoMuted;
    private String audioMuted;
    
    private String meetingLink = "https://rtcplatform.localzoho.com/rtcpdemo.do?usertype=joinee&callkey=6ded7e0d22f1107051b1d729213286fbec7454dd4aaff5c1d13426291b05556da955f8b7cd2b5ce3848cd9a18a38acafbcd25d0c449de1fb243be5438f5dfd3429bbf653e58b32052917f3c3999e2f48505dee5f725cbdb8837260e7f4ec82e677a9d5b5c1e4f2452c2afee536451cd453b594fb53b38aed74b406b8755689c33d96bd8ef400be33416a385ca550fb2e5dfd71f8d4be2c3aafff8764513611df&conftype=video&iszohouser=false&isnewcss=true&isnewui=false";

    @BeforeSuite
    public void createDriver() {
        System.setProperty("webdriver.chrome.driver", "/Users/nithi-22537/Selenium/chromedriver");
		ChromeOptions option = new ChromeOptions();
		option.addArguments("--use-fake-ui-for-media-stream");
		driver = new ChromeDriver(option);
		driver.manage().window().maximize();
		wait = new WebDriverWait(driver, 30);
	
    }
    public boolean isCallAlive()
    {
    	try
    	{
    		if(executeScript(driver, wait,Constants.CURRENT_SESSION) == null)
    			return false;
    		return true;
    	}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    	
    }
    
    @BeforeTest
    private void login() {
    	driver.get("https://rtcplatform.localzoho.com");
    	String username = "nithivasakan.m+t1@zohotest.com";
    	String password = "Nithitest";
    	
    	WebElement loginId = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(Constants.LOGIN_ID_XPATH)));
        loginId.sendKeys(username);
        WebElement nextButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(Constants.NEXT_BUTTON_XPATH)));
        nextButton.click();
        sleep(1000);
        //System.out.println(driver.findElement(By.xpath(Constants.USERNAME_ERROR_XPATH)).isDisplayed());

        WebElement passwordId = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(Constants.PASSWORD_XPATH)));
        passwordId.sendKeys(password);
        //System.out.println(driver.findElement(By.xpath(Constants.PASSWORD_ERROR_XPATH)).isDisplayed());
        nextButton.click();
        sleep(2000);
    }
    
    @Test(priority = 1)
    public void startMeeting() {
    	
    	driver.get(meetingLink);
    	sleep(10000);
    	videoMuted = executeScript(driver, wait,"ZRSmartConferenceImpl._joiningSession.isVideoMuted()").toString();
    	audioMuted = executeScript(driver, wait, "ZRSmartConferenceImpl._joiningSession.isVideoMuted()").toString();
    	
//    	System.out.println(executeScript(driver, wait, "RTCP._conferenceId"));
//    	System.out.println("video muted -->"+videoMuted);
//    	System.out.println("audio muted -->"+audioMuted); 
    	Assert.assertEquals(clickButton(driver,wait, Constants.JOINMEETING_XPATH), true);
    	
    }
    @Test(priority = 2 , dependsOnMethods = "startMeeting")
    public void userJoinWithDefaultSetting()
    {
    	System.out.println("userJoinWithDefaultSetting");
    	
    	Assert.assertEquals(this.videoMuted,  executeScript(driver, wait, Constants.VIDEO_MUTED).toString());
    	Assert.assertEquals(this.audioMuted, executeScript(driver, wait, Constants.AUDIO_MUTED).toString());
//    	System.out.println(executeScript(driver, wait, Constants.VIDEO_MUTED));
//    	sleep(5000);
//    	System.out.println(executeScript(driver, wait, Constants.AUDIO_MUTED));
//    	System.out.println("video = "+video);
//    	System.out.println("audio = "+audio);
//    	Assert.assertEquals(videoMuted, video);
//    	Assert.assertEquals(audioMuted, audio);
    }
    @Test(priority = 3,enabled = false)
    public void endMeeting() {
        Assert.assertEquals(clickButton(driver, wait, Constants.END_XPATH), true);
        System.out.println("Meeting Ended");
    }
    private boolean hoverAndClick(WebDriver driver , WebDriverWait wait, String xpath)
    {
    	try
    	{
    		 Actions actions = new Actions(driver);
             WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
             actions.moveToElement(element);
             actions.perform();
             actions.click(); 
             return true;
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    }
    private boolean clickButton(WebDriver driver,WebDriverWait wait,String xpath) {
    	try
    	{
	        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
	        button.click();
	        return true;
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    }
    private Object executeScript(WebDriver driver , WebDriverWait wait,String script)
    {
    	
    	try {
    		JavascriptExecutor js = (JavascriptExecutor)driver;
    		return  js.executeScript("return "+script);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
    @AfterTest
    public void quiteBrowser()
    {
//    	driver.quit();
    }

}
