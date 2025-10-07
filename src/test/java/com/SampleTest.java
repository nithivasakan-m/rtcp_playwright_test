package com;

import static org.testng.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RecordVideoSize;
import com.microsoft.playwright.options.WaitForSelectorState;

public class SampleTest {

    String meetingURL;
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeSuite
    public void initializeLink() {
        Random random = new Random();
        int sixDigit = 100000 + random.nextInt(900000);
        meetingURL = "https://rtcplatform."
        		+ "zoho.com/zvpqameetings.do?id=" + sixDigit; 
        System.out.println("[Meeting URL] " + meetingURL);
    }

    @BeforeClass
    public void setup() 
    {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setArgs(Arrays.asList(
                        "--use-fake-ui-for-media-stream",
                        "--use-fake-device-for-media-stream",
                        "--auto-select-desktop-capture-source=Entire screen")));
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1490, 800).setRecordVideoDir(Paths.get("videos"))
                .setRecordVideoSize(new RecordVideoSize(1280, 720))
                .setPermissions(Arrays.asList("microphone", "camera")));
        page = context.newPage();
    }

    @Test(priority = 1)
    public void loadMeetingPage() 
    {
        page.navigate(meetingURL);
        System.out.println("[loadMeetingPage] Meeting page loaded successfully");
        waitForPageLoad();
    }

    @Test(priority = 2, dependsOnMethods = {"loadMeetingPage"})
    public void startMeeting() 
    {
    	takeScreenshot("Before Start Meeting");
        Assert.assertTrue(click("//*[@purpose='startFromPreviewRoom']"), "Failed to start meeting");
        System.out.println("[startMeeting] Joined meeting successfully");
        takeScreenshot("After Start Meeting");
    }

    @Test(priority = 3, dependsOnMethods = {"startMeeting"})
    public void muteVideo()
    {
    	takeScreenshot("Before Mute Video");
        hoverOnConferenceoptionset();
        assertTrue(click("//*[@purpose='muteVideo']"),"Failed to mute a video");
        System.out.println("[muteVideo] Video muted successfully");
        takeScreenshot("After Mute Video");
    }

    @Test(priority = 4, dependsOnMethods = {"startMeeting"})
    public void muteAudio() 
    {
    	takeScreenshot("Before Mute Audio");
        hoverOnConferenceoptionset();
        assertTrue(click("//*[@purpose='muteAudio']"),"Failed to mute a audio");
        System.out.println("[muteAudio] Audio muted successfully");
        takeScreenshot("After Mute Audio");
    }

    @Test(priority = 5, dependsOnMethods = {"startMeeting"})
    public void startScreenshare() 
    {
    	takeScreenshot("Before Start Screensahre");
    	String xpath = "//*[@purpose='showMoreOptions']";
    	
    	hoverOnConferenceoptionset();
    	if(click(xpath))
    	{
    		assertTrue(click("//*[@purpose='startScreenShare']"),"Failed to start screenshare");
    	}
    	else
    	{
    		System.out.println("[startScreenshare] Showmore option not clicked");
    	}
    	
    	waitFor(5000);
    	System.out.println("[startScreenshare] Screen sharing started successfully By User = "+executeJS("ZRSmartConferenceImpl.getCurrentSession()._screenShareUserId"));
    	takeScreenshot("After Start Screenshare");
    }

    @Test(priority = 6, dependsOnMethods = {"startMeeting"})
    public void endMeeting()
    {
        boolean clicked = click("//*[@purpose='endConference']");
        Assert.assertTrue(clicked, "Failed to end meeting");
        System.out.println("[endMeeting] Meeting ended successfully");
        takeScreenshot("After Meeting Ended");
    }

    // --- Utility Methods ---
    private void hoverOnConferenceoptionset()
    {
        hover("//*[@id='conferenceoptionset']");
        waitFor(1000);
    }

    private void waitFor(int millis) 
    {
        try 
        {
            Thread.sleep(millis);
        } 
        catch (InterruptedException e) 
        {
            Thread.currentThread().interrupt();
        }
    }

    public void waitForPageLoad() 
    {
        page.waitForLoadState();
        System.out.println("[Page Load] Complete");
    }

    private boolean click(String xpath) 
    {
        try 
        {
            Locator element = locateElement(xpath);
            element.click();
            return true;
        } 
        catch (Exception e) 
        {
            System.out.println("Failed to click: " + xpath + " - " + e.getMessage());
            return false;
        }
    }

    private boolean hover(String xpath) 
    {
        try 
        {
            Locator element = locateElement(xpath);
            element.hover();
            return true;
        } 
        catch (Exception e)
        {
            System.out.println("Failed to hover: " + xpath + " - " + e.getMessage());
            return false;
        }
    }
    
    private Locator locateElement(String xpath)
    {
    	Locator element = page.locator("xpath=" + xpath);
        element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
        return element;
    }
    private Object executeJS(String xpath)
    {
    	return page.evaluate("() =>"+xpath);
    }
    
    public void takeScreenshot(String fileName) {
        try 
        {
            page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("screenshots/" + fileName + ".png")).setFullPage(true));
            System.out.println("[Screenshot] Saved: " + fileName + ".png");
        } 
        catch (Exception e) 
        {
            System.out.println("[Screenshot] Failed: " + e.getMessage());
        }
    }

}
