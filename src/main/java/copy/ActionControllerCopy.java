package copy;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.Constants;
import com.PlaywrightUtils;
// Assuming these exist in your project (same as your previous code)
import com.Constants.PARTICIPANTS;
import com.Constants.SCREENSHARE;
import com.Constants.SESSION;

public class ActionControllerCopy {

    private final Page page;
    private final PlaywrightUtils utils;
    private final String URL;

    // ---- Timers (ms)
    public static final int ONE_SECOND     = 1000;
    public static final int FIVE_SECONDS   = ONE_SECOND * 5;
    public static final int TEN_SECONDS    = FIVE_SECONDS * 2;
    public static final int TWENTY_SECONDS = TEN_SECONDS * 2;
    public static final int THIRTY_SECONDS = TEN_SECONDS * 3;
    public static final int ONE_MINUTE     = THIRTY_SECONDS * 2;

    // Optional knobs
    private static final int SPOTLIGHT_USER_COUNT = 7;

    // ---------- Constructors ----------
    public ActionControllerCopy(Page page, String url) {
        this(page, new PlaywrightUtils(), url);
    }

    public ActionControllerCopy(Page page) {
        this(page, new PlaywrightUtils(), "");
    }

    private ActionControllerCopy(Page page, PlaywrightUtils utils, String url) {
        this.page = page;
        this.utils = utils;
        this.URL = url;
        System.out.println("ℹ ActionControllerCopy initialized with URL: " + (url == null || url.isEmpty() ? "[Not Set]" : url));
    }

    // ---------- Wait Utilities ----------
    private void waitFor(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("⚠ Wait interrupted: " + e.getMessage());
        }
    }
    public void waitForSeconds(int seconds) { waitFor(seconds * ONE_SECOND); }
    public void waitForMinutes(int minutes) { waitFor(minutes * ONE_MINUTE); }
    public void waitForOneSecond()     { waitFor(ONE_SECOND); }
    public void waitForFiveSeconds()   { waitFor(FIVE_SECONDS); }
    public void waitForTenSeconds()    { waitFor(TEN_SECONDS); }
    public void waitForTwentySeconds() { waitFor(TWENTY_SECONDS); }
    public void waitForThirtySeconds() { waitFor(THIRTY_SECONDS); }
    public void waitForOneMinute()     { waitFor(ONE_MINUTE); }

    // ---------- Navigation ----------
    public void loadPage(String url) {
        try {
            System.out.println("🌐 Navigating to: " + url);
            page.navigate(url);
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            waitForFiveSeconds();
            System.out.println("✅ Page content loaded: " + url);
        } catch (Exception e) {
            System.err.println("❌ Failed to load page: " + e.getMessage());
        }
    }

    public void loadPage() {
        if (URL == null || URL.isEmpty()) {
            System.err.println("⚠ No default URL set. Use loadPage(String url).");
            return;
        }
        loadPage(URL);
    }

    // ---------- Preview actions ----------
    public void cancelPreviewRoom() {
        page.waitForSelector("//*[@purpose='closePreviewRoom']", new Page.WaitForSelectorOptions()
            .setState(WaitForSelectorState.VISIBLE).setTimeout(FIVE_SECONDS));
        page.locator("//*[@purpose='closePreviewRoom']").click();
        System.out.println("❌ Preview cancelled");
    }

    public void startMeetingFromPreview() { joinMeeting("startFromPreviewRoom"); }
    public void joinFromPreviewPage()     { joinMeeting("joinFromPreviewRoom"); }

    private void joinMeeting(String purpose) {
        String xpath = "//*[@purpose='" + purpose + "']";
        page.waitForSelector(xpath, new Page.WaitForSelectorOptions()
            .setState(WaitForSelectorState.VISIBLE).setTimeout(ONE_MINUTE));
        page.locator(xpath).click();
        System.out.println("✅ Meeting started from preview");
    }

    // ---------- End / Leave ----------
    public void endConference() {
        if (isHost()) {
            Locator endButton = page.locator("//*[@purpose='endConference']");
            endButton.click();
            try {
                page.waitForSelector("//*[@purpose='endConferenceAfterConfirmation']",
                    new Page.WaitForSelectorOptions().setTimeout(2000));
                page.locator("//*[@purpose='endConferenceAfterConfirmation']").click();
                System.out.println("🛑 Meeting ended with confirmation.");
            } catch (PlaywrightException e) {
                System.out.println("🛑 Meeting ended instantly (only host in room).");
            }
        } else {
            System.out.println("⚠️ Only a Host can end the meeting.");
        }
    }

    public void leaveMeeting() {
        Locator leaveButton = page.locator("//*[@purpose='endConference']");
        if (leaveButton.isVisible()) {
            leaveButton.click();
            System.out.println("🚪 Clicked Leave Meeting button");
        } else {
            System.out.println("⚠️ Leave Meeting button not visible");
        }
    }

    private boolean isHost() {
        Boolean isHost = (Boolean) page.evaluate("() => " + SESSION.IS_CURRENT_USER_HOST);
        return Boolean.TRUE.equals(isHost);
    }

    private boolean isHostOrCoHost() {
        Boolean isHost = isHost();
        Boolean isCoHost = (Boolean) page.evaluate("() => " + SESSION.IS_CURRENT_USER_CO_HOST);
        return Boolean.TRUE.equals(isHost) || Boolean.TRUE.equals(isCoHost);
    }

    // ---------- Name ----------
    public void changeDisplayName(String newName) {
        Locator nameInput = page.locator("//input[@purpose='setDName']");
        nameInput.fill("");
        nameInput.fill(newName);
        System.out.println("🪪 Display name changed to: " + newName);
    }

    // ---------- Audio ----------
    public void muteAudio() {
        System.out.println("[Action] Muting audio in meeting...");
        toggleMic(false);
        System.out.println("[Result] Audio muted.");
    }

    public void unmuteAudio() {
        System.out.println("[Action] Unmuting audio in meeting...");
        toggleMic(true);
        System.out.println("[Result] Audio unmuted.");
    }

    public void muteAudioInPreview() {
        System.out.println("[Action] Muting audio in preview...");
        togglePreview(page, "toggleAudioInPreview", true, "Microphone");
        System.out.println("[Result] Audio muted (preview).");
    }

    public void unmuteAudioInPreview() {
        System.out.println("[Action] Unmuting audio in preview...");
        togglePreview(page, "toggleAudioInPreview", false, "Microphone");
        System.out.println("[Result] Audio unmuted (preview).");
    }

    public void switchAudioInPreview() {
        System.out.println("[Action] Switching microphone in preview...");
        selectNextMicrophoneFromPreview();
        System.out.println("[Result] Switched microphone (preview).");
    }

    private void toggleMic(boolean enable) {
        String micXpath = "//*[@class='smartconf-btns_mic' or @purpose='muteAudio' or @purpose='unmuteAudio']";
        String currentPurpose = utils.getAttributeByXpath(page, micXpath, "purpose");
        if (enable && "unmuteAudio".equalsIgnoreCase(currentPurpose)) {
            utils.clickByXpath(page, micXpath);
            System.out.println("🎙 Mic turned ON");
        } else if (!enable && "muteAudio".equalsIgnoreCase(currentPurpose)) {
            utils.clickByXpath(page, micXpath);
            System.out.println("🔇 Mic turned OFF");
        } else {
            System.out.println("ℹ Mic already " + (enable ? "ON" : "OFF"));
        }
    }

    // ---------- Video ----------
    public void muteVideo() {
        System.out.println("[Action] Muting camera...");
        toggleCamera(false);
        System.out.println("[Result] Camera muted.");
    }

    public void unmuteVideo() {
        System.out.println("[Action] Unmuting camera...");
        toggleCamera(true);
        System.out.println("[Result] Camera unmuted.");
    }

    public void muteVideoInPreview() {
        System.out.println("[Action] Muting camera in preview...");
        togglePreview(page, "toggleVideoInPreview", true, "Camera");
        System.out.println("[Result] Camera muted (preview).");
    }

    public void unmuteVideoInPreview() {
        System.out.println("[Action] Unmuting camera in preview...");
        togglePreview(page, "toggleVideoInPreview", false, "Camera");
        System.out.println("[Result] Camera unmuted (preview).");
    }

    public void switchVideoInPreview() {
        System.out.println("[Action] Switching camera in preview...");
        selectNextCameraFromPreview();
        System.out.println("[Result] Switched camera (preview).");
    }

    private void toggleCamera(boolean enable) {
        String camXpath = "//*[@class='smartconf-btns_cam' or @purpose='muteVideo' or @purpose='unmuteVideo']";
        String currentPurpose = utils.getAttributeByXpath(page, camXpath, "purpose");
        if (enable && "unmuteVideo".equalsIgnoreCase(currentPurpose)) {
            utils.clickByXpath(page, camXpath);
            System.out.println("📷 Camera turned ON");
        } else if (!enable && "muteVideo".equalsIgnoreCase(currentPurpose)) {
            utils.clickByXpath(page, camXpath);
            System.out.println("📷 Camera turned OFF");
        } else {
            System.out.println("ℹ Camera already " + (enable ? "ON" : "OFF"));
        }
    }
    
    // ---------- Preview toggles ----------
    public void togglePreview(Page page, String purpose, boolean enable, String label) {
        Locator input = page.locator("input[purpose='" + purpose + "']");
        if (!input.isVisible()) {
            input.locator("..").click(); // click wrapper
            System.out.println(label + " toggled by clicking wrapper");
            return;
        }
        if (enable) {
            if (!input.isChecked()) {
                input.check(new Locator.CheckOptions().setForce(true));
                System.out.println(label + " enabled");
            } else {
                System.out.println(label + " already enabled");
            }
        } else {
            if (input.isChecked()) {
                input.uncheck(new Locator.UncheckOptions().setForce(true));
                System.out.println(label + " disabled");
            } else {
                System.out.println(label + " already disabled");
            }
        }
    }

    // ---------- Device pickers (preview) ----------
    private void selectNextCameraFromPreview() {
        String dropdownButtonXpath = "//*[@id='videoinputdropdowncnt']//*[@purpose='openDevicesDropDown']";
        String selectedDeviceXpath = "//*[@id='videoinputdropdowncnt']";
        String deviceListXpath    = "//*[@id='videoinputdropdown']//div[@class='zcl-menu-item']";

        String currentDeviceId = page.locator(selectedDeviceXpath).getAttribute("selecteddeviceid");
        page.locator(dropdownButtonXpath).click();
        page.waitForSelector(deviceListXpath);

        List<ElementHandle> devices = page.locator(deviceListXpath).elementHandles();
        List<String> deviceIds = new ArrayList<>();
        for (ElementHandle device : devices) deviceIds.add(device.getAttribute("deviceid"));

        int currentIndex = deviceIds.indexOf(currentDeviceId);
        int nextIndex = (currentIndex + 1) % deviceIds.size();
        devices.get(nextIndex).click();
        System.out.println("🎥 Camera switched to: " + deviceIds.get(nextIndex));
    }

    private void selectNextMicrophoneFromPreview() {
        Locator dropdown = page.locator("//div[@id='audioinputdropdowncnt']//div[@purpose='openDevicesDropDown']");
        dropdown.click();

        String currentDeviceId = page.locator("//div[@id='audioinputdropdowncnt']").getAttribute("selecteddeviceid");
        Locator devices = page.locator("//div[@id='audioinputdropdown']//div[contains(@class,'zcl-menu-item')]");
        int count = devices.count();

        for (int i = 0; i < count; i++) {
            String deviceId = devices.nth(i).getAttribute("deviceid");
            if (!deviceId.equals(currentDeviceId)) {
                String label = devices.nth(i).innerText();
                devices.nth(i).click();
                System.out.println("🎙 Microphone switched to: " + label);
                break;
            }
        }
    }

    // ---------- More options ----------
    public void showMoreOptions() {
        String xpath = "//*[@purpose='showMoreOptions']";
        String classAttr = (String) utils.getAttributeByXpath(page, xpath, "class");
        if (classAttr == null || !classAttr.contains("active")) {
            utils.clickByXpath(page, xpath);
            System.out.println("☰ More Options opened.");
        } else {
            System.out.println("ℹ More Options already open.");
        }
    }

    private void openMoreOptionsIfNeeded() {
        Locator dropdownContent = page.locator("//*[contains(@id,'dropdowncnt')]");
        if (!dropdownContent.isVisible()) {
            page.locator("//*[@purpose='showMoreOptions']").click();
            dropdownContent.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        }
    }

    // ---------- Screenshare ----------
    public void startScreenShare() {
        if (Boolean.TRUE.equals((Boolean) utils.executeJS(page, SESSION.IS_CURRENT_USER_HOST))
                || Boolean.TRUE.equals((Boolean) utils.executeJS(page, SESSION.IS_CURRENT_USER_CO_HOST))
                || Boolean.FALSE.equals((Boolean) utils.executeJS(page, SCREENSHARE.SCREEN_DOWNSTREAM_CONNECTED))) {

            showMoreOptions();
            utils.clickByXpath(page, "//*[@purpose='startScreenShare']");

            Boolean isUpstreamConnected = (Boolean) utils.executeJS(page, SCREENSHARE.SCREEN_UPSTREAM_CONNECTED);
            if (Boolean.TRUE.equals(isUpstreamConnected)) {
                String currentUserName = (String) utils.executeJS(page, SCREENSHARE.SCREENSHARE_CURRENT_USER_NAME);
                System.out.println("✅ Screenshare started by: " + currentUserName);
            }
        } else {
            String existingUserName = (String) utils.executeJS(page, SCREENSHARE.SCREENSHARE_CURRENT_USER_NAME);
            System.out.println("ℹ Already sharing: " + existingUserName);
        }
    }

    public void stopScreenShare() {
        Boolean isScreenshareUser = (Boolean) utils.executeJS(page, SCREENSHARE.IS_SCREENSHARE_USER);
        if (Boolean.TRUE.equals(isScreenshareUser)) {
            showMoreOptions();
            utils.clickByXpath(page, "//*[@purpose='stopScreenShare']");
            String currentUserId = (String) utils.executeJS(page, SESSION.ZUID);
            System.out.println("🛑 Screenshare stopped by: " + currentUserId);
        } else {
            String actualUserName = (String) utils.executeJS(page, SCREENSHARE.SCREENSHARE_CURRENT_USER_NAME);
            String actualUserId = (String) utils.executeJS(page, SCREENSHARE.SCREENSHARE_CURRENT_USER_ID);
            System.out.println("ℹ Active screenshare user: " + actualUserName + " (" + actualUserId + ")");
        }
    }

    // ---------- Participants RHS ----------
    public void openParticipantsTab() {
        try {
            Locator btns = page.locator("//*[@purpose='openParticipantsInRHS']");
            int count = btns.count();
            if (count == 0) {
                System.err.println("❌ No participants tab button found.");
                return;
            }
            Locator button = btns.nth(0);
            button.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE).setTimeout(TEN_SECONDS));
            button.click();
            System.out.println("✅ Participants tab opened.");
        } catch (Exception e) {
            System.err.println("❌ Failed to open participants tab: " + e.getMessage());
        }
    }

    private void openUserActionsMenu(String userId) {
        String userRow   = PARTICIPANTS.getUserRowByUserId(userId);
        String actionsBtn = PARTICIPANTS.getShowActionsOnMemberByUserId(userId);
        page.locator(userRow).hover();
        page.locator(actionsBtn).click();
    }

    // ---------- User actions ----------
    public void setAsCoHost(String userId) {
        openParticipantsTab();
        openUserActionsMenu(userId);
        page.locator(PARTICIPANTS.SET_AS_CO_HOST).click();
    }

    public void setInSpotlight(String userId) {
        openParticipantsTab();
        openUserActionsMenu(userId);
        page.locator(PARTICIPANTS.SET_IN_SPOTLIGHT).click();
    }

    public void removeSpotlight(String userId) {
        try {
            String userRow = PARTICIPANTS.getUserRowByUserId(userId);
            Locator userContainer = page.locator(userRow);
            if (userContainer.count() == 0) {
                System.out.println("⚠ User " + userId + " not found in participants list.");
                return;
            }
            Locator indicator = userContainer.locator(".smartconf-sqr-spotlight-ind.zcf-spotlight");
            if (indicator.isVisible()) {
                System.out.println("✅ User " + userId + " is spotlighted. Removing...");
                openUserActionsMenu(userId);
                Locator removeBtn = page.locator("//*[@purpose='removeSpotlightUser']");
                removeBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                removeBtn.click();
                System.out.println("✅ Spotlight removed for user " + userId);
            } else {
                System.out.println("ℹ User " + userId + " is not spotlighted.");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to remove spotlight for " + userId + ": " + e.getMessage());
        }
    }

    public void restrictAudioUnmuteForUser(String userId) {
        openParticipantsTab();
        openUserActionsMenu(userId);
        page.locator(PARTICIPANTS.RESTRICT_AUDIO_UNMUTE_FOR_USER).click();
    }

    public void showUpdateUserRole(String userId) {
        openParticipantsTab();
        openUserActionsMenu(userId);
        page.locator(PARTICIPANTS.SHOW_UPDATE_USER_ROLE).click();
    }

    public void removeParticipant(String userId) {
        openParticipantsTab();
        openUserActionsMenu(userId);
        page.locator(PARTICIPANTS.REMOVE_PARTICIPANT).click();
    }

    public void pinUserVideo(String userId) {
        openParticipantsTab();
        openUserActionsMenu(userId);
        page.locator(PARTICIPANTS.PIN_USER_VIDEO).click();
    }

    // ---------- Assign & Leave ----------
    public void assignAndLeave() {
        showMoreOptions();
        if (!isEndConferenceDropdownOpen()) {
            utils.clickByXpath(page, "//*[@purpose='endConference']");
        }
        utils.clickByXpath(page, "//*[@purpose='showHostLeaveConfirmation']");
        utils.clickByXpath(page, "//*[@purpose='showResultsDom']");
        utils.clickByXpath(page, "(//*[@selected-input]//following::div[contains(@class,'dropdown-item')])[1]");
        utils.clickByXpath(page, "//*[@purpose='leaveMeetingAfterAssigningHost']");
    }

    private boolean isEndConferenceDropdownOpen() {
        String style = utils.getAttributeByXpath(page, "//*[contains(@id,'end_dropdowncnt')]", "style");
        return style != null && style.contains("display: block");
    }

    // ---------- Active Speaker Spotlight ----------
    public void allowActiveSpeakerInSpotlight() {
        if (!isHostOrCoHost()) {
            System.out.println("⚠ Not Host/Co-Host. Cannot allow Active Speaker Spotlight.");
            return;
        }
        toggleActiveSpeakerSpotlight(true);
    }

    public void disableActiveSpeakerInSpotlight() {
        if (!isHostOrCoHost()) {
            System.out.println("⚠ Not Host/Co-Host. Cannot disable Active Speaker Spotlight.");
            return;
        }
        toggleActiveSpeakerSpotlight(false);
    }

    private void toggleActiveSpeakerSpotlight(boolean enable) {
        String verb = enable ? "Allow" : "Disable";
        try {
            Locator spotlightedUsers = page.locator("//div[contains(@class,'smartconf-spotlight-user-list')]");
            int count = spotlightedUsers.count();
            System.out.println("ℹ Spotlighted users count: " + count);

            if (count == 0) {
                if (enable) {
                    String userId = getUserId();
                    System.out.println("ℹ No spotlighted users. Setting current user in Spotlight: " + userId);
                    setInSpotlight(userId);
                    System.out.println("✅ User " + userId + " set in Spotlight.");
                } else {
                    System.out.println("ℹ No spotlighted users. Skip disabling.");
                    return;
                }
            }

            openParticipantsTab();
            Locator spotOptionsBtn = page.locator("//*[@purpose='showSpotLightOptions']");
            spotOptionsBtn.click();
            System.out.println("✅ Spotlight menu opened.");

            String optionText = enable ? "Allow Active Speaker Spotlight" : "Disable Active Speaker Spotlight";
            Locator option = page.locator("//div[@id='spotlightdropdowncnt']//div[text()='" + optionText + "']");
            option.waitFor(new Locator.WaitForOptions().setTimeout(FIVE_SECONDS));
            option.click();
            System.out.println("✅ '" + optionText + "' executed.");
        } catch (Exception e) {
            System.err.println("❌ Failed to " + verb + " Active Speaker Spotlight: " + e.getMessage());
        }
    }

    // ---------- Global actions ----------
    public void muteAll() {
        clickIfHostOrCoHost("//*[@purpose='muteAllParticipantsAudio']", "✅ All participants muted", "⚠️ 'Mute All' button not visible");
    }

    public void unmuteAll() {
        clickIfHostOrCoHost("//*[@purpose='unmuteAllParticipantsAudio']", "✅ All participants unmuted", "⚠️ 'Unmute All' button not visible");
    }

    private void clickIfHostOrCoHost(String xpath, String successMsg, String failMsg) {
        if (!isHostOrCoHost()) {
            System.out.println("⚠ You must be Host or Co-Host.");
            return;
        }
        Locator button = page.locator(xpath);
        if (button.isVisible()) {
            button.click();
            System.out.println(successMsg);
        } else {
            System.out.println(failMsg);
        }
    }

    // ---------- Recording / Streaming / Settings ----------
    public void startRecording() {
        openMoreOptionsIfNeeded();
        page.locator("//*[@purpose='startRecording']").click();
    }

    public void stopRecording() {
        openMoreOptionsIfNeeded();
        page.locator("//*[@purpose='stopRecording']").click();
    }

    public void startStreaming() {
        openMoreOptionsIfNeeded();
        page.locator("//*[@purpose='startBroadcast']").click();
    }

    public void stopStreaming() {
        openMoreOptionsIfNeeded();
        page.locator("//*[@purpose='stopBroadcast']").click();
    }

    public void openSettings() {
        openMoreOptionsIfNeeded();
        page.locator("//*[@purpose='openSettings']").click();
    }

    // ---------- Views ----------
    public void stageView() { switchView(page, "switchToStageLayout"); }
    public void gridView()  { switchView(page, "switchToVideoGridLayout"); }
    public void mixedView() { switchView(page, "switchToMixedVideoLayout"); }

    public void switchView(Page page, String viewPurpose) {
        String xpath = "//*[@purpose='" + viewPurpose + "']";
        Locator viewButton = page.locator(xpath);
        viewButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        boolean isActive = Boolean.parseBoolean(viewButton.evaluate("el => el.classList.contains('active')").toString());
        if (!isActive) {
            viewButton.click();
            System.out.println("🖼 Switched to view: " + viewPurpose);
        } else {
            System.out.println("ℹ Already in view: " + viewPurpose);
        }
    }

    // ---------- Misc UI ----------
    public void showActionsOnMember() {
        utils.hoverAndClick(page, "//*[@purpose='showActionsOnMember']");
    }

    public void clickParticipantIcon() {
        utils.click(page, "//*[@purpose='participantIcon']");
    }

    public void clickSettings() {
        utils.click(page, "//*[@purpose='settingsIcon']");
    }

    public void changeAudioDevice() {
        utils.click(page, "//*[@purpose='changeAudioDevice']");
    }

    public void changeVideoDevice() {
        utils.click(page, "//*[@purpose='changeVideoDevice']");
    }

    public void selectUserFromList(String userId) {
        Locator user = page.locator("//*[@purpose='userListItem'][@data-userid='" + userId + "']");
        boolean isSelected = (boolean) user.evaluate("el => el.classList.contains('sel')");
        if (!isSelected) user.click();
    }

    // ---------- Getters / Screenshot ----------
    public String getUserId() {
        return String.valueOf(page.evaluate("() => RTCP._rtcpuserid"));
    }

    public String getConferenceId() {
        return String.valueOf(page.evaluate("() => RTCP._conferenceId"));
    }

    public Page getPage() {
        return this.page;
    }
    public void takeScreenshot(String filePrefix) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = filePrefix + "_" + timestamp + ".png";
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("screenshots", fileName))
                .setFullPage(true));
            System.out.println("📸 Screenshot saved: screenshots/" + fileName);
        } catch (Exception e) {
            System.err.println("❌ Failed to take screenshot: " + e.getMessage());
        }
    }
}

