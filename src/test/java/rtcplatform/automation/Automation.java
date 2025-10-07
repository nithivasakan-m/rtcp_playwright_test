package rtcplatform.automation;
import java.util.Hashtable;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Automation{
    private static String conferenceKey;
    private static String conferenceId;
    private static String hostMeetingLink;
    private static String participantMeetingLink;
    private static String silentParticipantMeetingLink;
    private static String viewerLink;
    private static String playbackLink;
    private static String hostId;
    private static Hashtable<String , String> participantsList;
    private static Properties properties;
    private static final String domain = "";

    public Automation(String confKey, Properties props) {
        conferenceKey = confKey;
        properties = props;
    }

    private static boolean clickButton(WebDriverWait wait, String xpath, String buttonName) {
        try {
            WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            button.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean hover(WebDriverWait wait, String xpath, String hoverName) {
        try {
            Actions actions = new Actions((WebDriver)wait);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            actions.moveToElement(element);
            actions.perform();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String generateConferenceLink(String userType, boolean iszohouser) {
        return "https://" + domain + ".com/rtcpdemo.do?usertype=" + userType + "&callkey=" + conferenceKey + "&title=RTCP&iszohouser=" + iszohouser + "true&isnewcss=true&isnewui=false&isgeofencingenabled=false";
    }

    public static boolean login(WebDriverWait wait, String userName, String password) {
        try {
            WebElement loginId = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(Constants.LOGIN_ID_XPATH)));
            loginId.sendKeys(userName);
            WebElement nextButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(Constants.NEXT_BUTTON_XPATH)));
            nextButton.click();

            if (((WebDriver)wait).findElement(By.xpath(Constants.USERNAME_ERROR_XPATH)).isDisplayed()) {
                return false;
            }

            WebElement passwordId = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(Constants.PASSWORD_XPATH)));
            passwordId.sendKeys(password);
            if (((WebDriver)wait).findElement(By.xpath(Constants.PASSWORD_ERROR_XPATH)).isDisplayed()) {
                return false;
            }

            nextButton.click();
            Thread.sleep(2000);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static void initializeMeetingLinks() {
        hostMeetingLink = Automation.generateConferenceLink(Constants.HOST, true);
        participantMeetingLink = Automation.generateConferenceLink(Constants.JOINEE, false);
        silentParticipantMeetingLink = Automation.generateConferenceLink(Constants.SILENTJOINEE, false);
        viewerLink = Automation.generateConferenceLink(Constants.VIEWER, false);
        playbackLink = Automation.generateConferenceLink(Constants.PLAYBACK, false);
    }

    public static boolean startMeeting(WebDriver driver, WebDriverWait wait, String meetingLink) {
        try {
            String userName = properties.getProperty(Constants.USERNAME);
            String password = properties.getProperty(Constants.PASSWORD);
            driver.get(meetingLink);
            Thread.sleep(2000);
            if (login( wait, userName, password) && clickButton(wait, Constants.JOINMEETING_XPATH, "Host Start")) {
                conferenceId = (String) executeJS(driver, Constants.CONFERENCE_ID);
                hostId = (String) executeJS(driver, Constants.ZUID);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean endMeeting(WebDriver driver, WebDriverWait wait, boolean isHost) {
        try {
            clickButton(wait, Constants.END_XPATH, "End Button");
            if (isHost) {
                clickButton( wait, Constants.END_MEETING_XPATH, "End Meeting");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void joinMeeting(WebDriver driver, WebDriverWait wait) {
        clickButton(wait, Constants.JOINMEETING_XPATH, "Join Button");
        // Add participant logic here
    }

    public boolean openReaction(WebDriver driver, WebDriverWait wait) {
        return clickButton(wait, Constants.REACTION_XPATH, "Open Reaction");
    }

    public boolean closeReaction(WebDriver driver, WebDriverWait wait) {
        return openReaction(driver, wait);
    }

    public boolean raiseRequestToSpeak(WebDriver driver, WebDriverWait wait) {
        return clickButton(wait, Constants.REQUEST_SPEAK_XPATH, "Request to Speak");
    }

    public boolean cancelRequestToSpeak(WebDriver driver, WebDriverWait wait) {
        return raiseRequestToSpeak(driver, wait);
    }

    public boolean clickParticipantsTab(WebDriver driver, WebDriverWait wait) {
        return clickButton(wait, Constants.PARTICIPANTS_TAB_XPATH, "Participants Tab");
    }

    public boolean closeParticipantsTab(WebDriver driver, WebDriverWait wait) {
        return clickParticipantsTab(driver, wait);
    }

    public boolean approveAll(WebDriver driver, WebDriverWait wait) {
        return clickButton( wait, Constants.APPROVE_ALL_XPATH_1, "Approve All 1") &&
               clickButton(wait, Constants.APPROVE_ALL_XPATH_2, "Approve All 2");
    }

    public boolean rejectAll(WebDriver driver, WebDriverWait wait) {
        return clickButton(wait, Constants.REJECT_ALL_XPATH_1, "Reject All 1") &&
               clickButton(wait, Constants.REJECT_ALL_XPATH_2, "Reject All 2");
    }

    public boolean clickRightHandButton(WebDriver driver, WebDriverWait wait) {
        return clickButton(wait, Constants.RIGHT_HAND_XPATH, "Right Hand");
    }

    public boolean setLDVideoResolution(WebDriver driver) {
        return executeJS(driver,Constants.LOW_VIDEO_RESOLUTION) != null;
    }

    public boolean setHDVideoResolution(WebDriver driver) {
        return executeJS(driver,Constants.HIGH_VIDEO_RESOLUTION) != null;
    }

    public boolean setFHDVideoResolution(WebDriver driver) {
        return executeJS(driver,Constants.FULL_VIDEO_RESOLUTION) != null;
    }

    public boolean switchView(WebDriver driver, WebDriverWait wait, String view) {
        switch(view) {
            case "stage":
                return clickButton(wait, Constants.STAGE_VIEW, "Stage View");
            case "grid":
                return clickButton(wait, Constants.GRID_VIEW, "Grid View");
            case "mcu":
                return clickButton(wait, Constants.MCU_VIEW, "MCU View");
            case "activespeaker":
                return clickButton(wait, Constants.ACTIVE_SPEAKER_VIEW, "Active Speaker View");
            default:
                return clickButton(wait, Constants.STAGE_VIEW, "Default View");
        }
    }

    public boolean switchStageView(WebDriver driver, WebDriverWait wait) {
        if ("stage".equals(executeJS(driver, Constants.USER_VIEW))) {
            return true;
        }
        return switchView(driver, wait, "stage");
    }

    public boolean switchGridView(WebDriver driver, WebDriverWait wait) {
        if ("grid".equals(executeJS(driver, Constants.USER_VIEW))) {
            return true;
        }
        return switchView(driver, wait, "grid");
    }

    public boolean switchMCUView(WebDriver driver, WebDriverWait wait) {
        if ("mixed".equals(executeJS(driver, Constants.USER_VIEW))) {
            return true;
        }
        return switchView(driver, wait, "mcu");
    }

    public boolean switchActiveSpeakerView(WebDriver driver, WebDriverWait wait) {
        if ("stage".equals(executeJS(driver, Constants.USER_VIEW))) {
            return true;
        }
        return switchView(driver, wait, "activespeaker");
    }

    private static Object executeJS(WebDriver driver, String script) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return js.executeScript("return " + script);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean startRecording(WebDriver driver, WebDriverWait wait) {
        return clickButton(wait, Constants.RECORDING_START, "Start Recording");
    }

    public static boolean stopRecording(WebDriver driver, WebDriverWait wait) {
        return clickButton(wait, Constants.RECORDING_STOP, "Stop Recording");
    }

    public static boolean startScreenshare(WebDriver driver, WebDriverWait wait) {
        return clickButton(wait, Constants.SCREENSHARE_START, "Start Screenshare");
    }

    public static String getConferenceId() {
        return conferenceId;
    }

    public static String getConferenceKey() {
        return conferenceKey;
    }

    public static String getHostMeetingLink() {
        return hostMeetingLink;
    }

    public static String getParticipantMeetingLink() {
        return participantMeetingLink;
    }

    public static String getSilentParticipantLink() {
        return silentParticipantMeetingLink;
    }

    public static String getStreamingLink() {
        return viewerLink;
    }

    public static String getRecordingLink() {
        return playbackLink;
    }
    public static void main(String[] args)
    {
    	System.out.println("Main Method");
    }
}
z