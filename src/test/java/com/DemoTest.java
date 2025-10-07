package com;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import rtcplatform.automation.Constants;
import org.testng.annotations.*;
import java.util.Arrays;
import static org.testng.Assert.*;

public class DemoTest {

    private Playwright playwright;
    private Browser browser;
    private Page page;
    private BrowserContext context;

    private String videoMuted;
    private String audioMuted;

    private final String meetingLink = "https://rtcplatform.localzoho.com/rtcpdemo.do?usertype=joinee&callkey=e457ef41296451a373ee1362f06a25d7f023ff201e6f80925714d5e36e530699b686bade7c8832559f3a7b424f7088f3cfad82b54db98c7fc4ebea35dc94299a12c17c28a531c317fc14da7efaec2e44ad9e0dd02f4e1b7d83f9276ed4a3013dbb7c54acf947932d717b8c24482a396cd2df872ba5ddf381883314aa4bc1389196b7ff9935552d6a03e825e23247e228&conftype=video&iszohouser=true&isnewcss=true&isnewui=false&livetranscription=false";

    @BeforeSuite
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false));
        context = browser.newContext(new Browser.NewContextOptions()
        	    .setPermissions(Arrays.asList("camera", "microphone"))
        	    .setViewportSize(null));
        page = context.newPage();
    }

    @BeforeTest
    public void login() {
        page.navigate("https://rtcplatform.localzoho.com");
        page.locator("#" + Constants.LOGIN_ID_XPATH).fill("nithivasakan.m+t2@zohotest.com");
        page.locator("#" + Constants.NEXT_BUTTON_XPATH).click();
        page.waitForTimeout(1000);
        page.locator("#" + Constants.PASSWORD_XPATH).fill("Nithitest");
        page.locator("#" + Constants.NEXT_BUTTON_XPATH).click();
        page.waitForTimeout(2000);
    }

    @Test(priority = 1)
    public void startMeeting() {
        page.navigate(meetingLink);
        page.waitForTimeout(10000);

        Object video = page.evaluate("() => ZRSmartConferenceImpl._joiningSession.isVideoMuted()");
        Object audio = page.evaluate("() => ZRSmartConferenceImpl._joiningSession.isAudioMuted()");

        videoMuted = String.valueOf(video);
        audioMuted = String.valueOf(audio);

        assertTrue(clickButton(Constants.JOINMEETING_XPATH));
    }

    @Test(priority = 2, dependsOnMethods = {"startMeeting"})
    public void userJoinWithDefaultSetting() {
        String actualVideo = String.valueOf(page.evaluate("() => " + Constants.VIDEO_MUTED));
        String actualAudio = String.valueOf(page.evaluate("() => " + Constants.AUDIO_MUTED));

        assertEquals(videoMuted, actualVideo);
        assertEquals(audioMuted, actualAudio);
    }

    @Test(priority = 3, enabled = false)
    public void endMeeting() {
        assertTrue(clickButton(Constants.END_XPATH));
        System.out.println("Meeting Ended");
    }

    private boolean clickButton(String xpath) {
        try {
            Locator button = page.locator("xpath=" + xpath);
            button.click();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    @AfterSuite
//    public void tearDown() {
//        if (page != null) page.close();
//        if (context != null) context.close();
//        if (browser != null) browser.close();
//        if (playwright != null) playwright.close();
//    }
}
