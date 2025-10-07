package com;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.Constants.*;
import com.Constants.PARTICIPANTS;
import com.Constants.SCREENSHARE;
import com.Constants.SESSION;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
public class ActionController {
	
    private final Page page;
    private final PlaywrightUtils utils;
    private final String URL;
    
 // ✅ Time interval constants (in ms)
    public static final int ONE_SECOND     = 1000;
    public static final int FIVE_SECONDS   = ONE_SECOND * 5;
    public static final int TEN_SECONDS    = FIVE_SECONDS * 2;
    public static final int TWENTY_SECONDS = TEN_SECONDS * 2;
    public static final int THIRTY_SECONDS = TEN_SECONDS * 3;
    public static final int ONE_MINUTE     = THIRTY_SECONDS * 2;
    
    int SPOTLIGHT_USER_COUNT = 7;
    
    public ActionController(Page page, String url) {
        this(page, new PlaywrightUtils(), url);
    }

    public ActionController(Page page) {
        this(page,new PlaywrightUtils(),"");
    }

    // Private main constructor
    private ActionController(Page page, PlaywrightUtils utils, String url) {
        this.page = page;
        this.utils = utils;
        this.URL = url;
        System.out.println( ActionController initialized with URL: " + (url.isEmpty() ? "[Not Set]" : url));
    }

    // Helper wait method
    private void waitFor(long milliSec) {
        try {
            Thread.sleep(milliSec);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("⚠ Wait interrupted: " + e.getMessage());
        }
    }
    public void waitForOneSecond()   { waitFor(ONE_SECOND); }
    public void waitForFiveSeconds() { waitFor(FIVE_SECONDS); }
    public void waitForTenSeconds()  { waitFor(TEN_SECONDS); }
    public void waitForTwentySeconds() { waitFor(TWENTY_SECONDS); }
    public void waitForThirtySeconds() { waitFor(THIRTY_SECONDS); }
    public void waitForOneMinute()   { waitFor(ONE_MINUTE); }
    // Load a given page
    public void loadPage(String url) {
        try {
            System.out.println("🌐 Navigating to: " + url);
            page.navigate(url);
            page.waitForLoadState(LoadState.DOMCONTENTLOADED); // HTML parsed
            waitForFiveSeconds(); // small buffer
            System.out.println("✅ Page content loaded successfully: " + url);
        } catch (Exception e) {
            System.err.println("❌ Failed to load page: " + url + " | Reason: " + e.getMessage());
        }
    }

    // Load default URL (if provided during init)
    public void loadPage() {
        if (URL == null || URL.isEmpty()) {
            System.err.println("⚠ No default URL set. Use loadPage(String url) instead.");
            return;
        }
        loadPage(URL);
    }

    // start a meeting 
    public void cancelPreviewRoom() {
        // Wait until Cancel button is attached and visible
        page.waitForSelector("//*[@purpose='closePreviewRoom']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(FIVE_SECONDS));

        Locator cancelButton = page.locator("//*[@purpose='closePreviewRoom']");
        cancelButton.click();
        System.out.println("❌ Preview cancelled");
    }

    public void startMeetingFromPreview() 
    {
    	joinMeeting("startFromPreviewRoom");
    }
    public void joinFromPreviewPage() {
    	joinMeeting("joinFromPreviewRoom");
    }

    private void joinMeeting(String xpath) {
        // Wait until Start Meeting button is attached and visible
    	xpath = "//*[@purpose='"+xpath+"']";
        page.waitForSelector(xpath, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(ONE_MINUTE));

        Locator startButton = page.locator(xpath);
        startButton.click();
        System.out.println("✅ Meeting started from preview");
    }
    
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
    
    // -- changing display name for a user --
    public void changeDisplayName(String newName) {
        Locator nameInput = page.locator("//input[@purpose='setDName']");
        nameInput.fill("");      
        nameInput.fill(newName); 
        System.out.println("Display name changed to: " + newName);
    }
    
    // -- mute audio --
 // ===================== AUDIO CONTROLS =====================

	 // -- mute audio in meeting --
	 public void muteAudio() {
	     System.out.println("[Action] Muting audio in meeting...");
	     toggleMic(false);
	     System.out.println("[Result] Audio muted in meeting.");
	 }
	
	 // -- mute audio in preview page --
	 public void muteAudioInPreview() {
	     System.out.println("[Action] Muting audio in preview page...");
	     togglePreview(page, "toggleAudioInPreview", true, "Microphone");
	     System.out.println("[Result] Audio muted in preview page.");
	 }
	
	 // -- unmute audio in meeting --
	 public void unmuteAudio() {
	     System.out.println("[Action] Unmuting audio in meeting...");
	     toggleMic(true);
	     System.out.println("[Result] Audio unmuted in meeting.");
	 }
	
	 // -- unmute audio in preview page --
	 public void unmuteAudioInPreview() {
	     System.out.println("[Action] Unmuting audio in preview page...");
	     togglePreview(page, "toggleAudioInPreview", false, "Microphone");
	     System.out.println("[Result] Audio unmuted in preview page.");
	 }
	
	 // -- switch audio from preview page --
	 public void switchAudioInPreview() {
	     System.out.println("[Action] Switching microphone in preview page...");
	     selectNextMicrophoneFromPreview();
	     System.out.println("[Result] Switched microphone in preview page.");
	 }
	
	
	 // ===================== VIDEO CONTROLS =====================
	
	 // -- mute camera in meeting --
	 public void muteVideo() {
	     System.out.println("[Action] Muting camera in meeting...");
	     toggleCamera(false);
	     System.out.println("[Result] Camera muted in meeting.");
	 }
	
	 // -- mute camera in preview page --
	 public void muteVideoInPreview() {
	     System.out.println("[Action] Muting camera in preview page...");
	     togglePreview(page, "toggleVideoInPreview", true, "Camera");
	     System.out.println("[Result] Camera muted in preview page.");
	 }
	
	 // -- unmute camera in meeting --
	 public void unmuteVideo() {
	     System.out.println("[Action] Unmuting camera in meeting...");
	     toggleCamera(true);
	     System.out.println("[Result] Camera unmuted in meeting.");
	 }
	
	 // -- unmute camera in preview page --
	 public void unmuteVideoInPreview() {
	     System.out.println("[Action] Unmuting camera in preview page...");
	     togglePreview(page, "toggleVideoInPreview", false, "Camera");
	     System.out.println("[Result] Camera unmuted in preview page.");
	 }
	
	 // -- switch video from preview page --
	 public void switchVideoInPreview() {
	     System.out.println("[Action] Switching camera in preview page...");
	     selectNextCameraFromPreview();
	     System.out.println("[Result] Switched camera in preview page.");
	 }

    private void toggleCamera(boolean enable) {
        String camXpath = "//*[@class='smartconf-btns_cam' or @purpose='muteVideo' or @purpose='unmuteVideo']";
        String currentPurpose = utils.getAttributeByXpath(page, camXpath, "purpose");

        if (enable && "unmuteVideo".equalsIgnoreCase(currentPurpose)) {
            utils.clickByXpath(page, camXpath);
            System.out.println("Camera turned ON");
        } else if (!enable && "muteVideo".equalsIgnoreCase(currentPurpose)) {
            utils.clickByXpath(page, camXpath);
            System.out.println("Camera turned OFF");
        } else {
            System.out.println("Camera already " + (enable ? "ON" : "OFF"));
        }
    }
    
    private void toggleMic(boolean enable) {
        String micXpath = "//*[@class='smartconf-btns_mic' or @purpose='muteAudio' or @purpose='unmuteAudio']";
        String currentPurpose = utils.getAttributeByXpath(page, micXpath, "purpose");

        if (enable && "unmuteAudio".equalsIgnoreCase(currentPurpose)) {
            utils.clickByXpath(page, micXpath);
            System.out.println("Mic turned ON");
        } else if (!enable && "muteAudio".equalsIgnoreCase(currentPurpose)) {
            utils.clickByXpath(page, micXpath);
            System.out.println("Mic turned OFF");
        } else {
            System.out.println("Mic already " + (enable ? "ON" : "OFF"));
        }
    }

    public void togglePreview(Page page, String purpose, boolean enable, String label) {
        Locator input = page.locator("input[purpose='" + purpose + "']");
        
        // If the checkbox is hidden, click the label or parent instead
        if (!input.isVisible()) {
            Locator wrapper = input.locator(".."); // get parent element
            wrapper.click();
            System.out.println(label + " toggled by clicking wrapper");
            return;
        }

        if (enable) {
            if (!input.isChecked()) {
                input.check(new Locator.CheckOptions().setForce(true)); // force allows hidden inputs
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

   
    private void selectNextCameraFromPreview() {
        String dropdownButtonXpath = "//*[@id='videoinputdropdowncnt']//*[@purpose='openDevicesDropDown']";
        String selectedDeviceXpath = "//*[@id='videoinputdropdowncnt']";
        String deviceListXpath = "//*[@id='videoinputdropdown']//div[@class='zcl-menu-item']";

        // 1. Get current selected device ID
        String currentDeviceId = page.locator(selectedDeviceXpath).getAttribute("selecteddeviceid");

        // 2. Open the dropdown
        page.locator(dropdownButtonXpath).click();

        // 3. Wait for device list to appear
        page.waitForSelector(deviceListXpath);

        // 4. Get all device IDs
        List<ElementHandle> devices = page.locator(deviceListXpath).elementHandles();
        List<String> deviceIds = new ArrayList<>();
        for (ElementHandle device : devices) {
            deviceIds.add(device.getAttribute("deviceid"));
        }

        // 5. Find next index
        int currentIndex = deviceIds.indexOf(currentDeviceId);
        int nextIndex = (currentIndex + 1) % deviceIds.size();

        // 6. Click the next device
        devices.get(nextIndex).click();
        System.out.println("Camera switched to: " + deviceIds.get(nextIndex));
    }
    
    private void selectNextMicrophoneFromPreview() {
        Locator dropdown = page.locator("//div[@id='audioinputdropdowncnt']//div[@purpose='openDevicesDropDown']");
        dropdown.click();

        // Get currently selected device
        String currentDeviceId = page.locator("//div[@id='audioinputdropdowncnt']").getAttribute("selecteddeviceid");

        // Find all devices
        Locator devices = page.locator("//div[@id='audioinputdropdown']//div[contains(@class,'zcl-menu-item')]");
        int count = devices.count();

        for (int i = 0; i < count; i++) {
            String deviceId = devices.nth(i).getAttribute("deviceid");
            if (!deviceId.equals(currentDeviceId)) {
                devices.nth(i).click();
                System.out.println("Microphone switched to: " + devices.nth(i).innerText());
                break;
            }
        }
    }

    // operations

    public void showMoreOptions() {
        String xpath = "//*[@purpose='showMoreOptions']";
        
        String classAttr = (String) utils.getAttributeByXpath(page, xpath, "class");
        
        if (classAttr == null || !classAttr.contains("active")) {
            utils.clickByXpath(page, xpath);
            System.out.println("More Options menu opened.");
        } else {
            System.out.println("More Options menu is already open.");
        }
    }

    public void startScreenShare() {
        if (Boolean.TRUE.equals((Boolean) utils.executeJS(page, SESSION.IS_CURRENT_USER_HOST)) || Boolean.TRUE.equals((Boolean) utils.executeJS(page, SESSION.IS_CURRENT_USER_CO_HOST)) || Boolean.FALSE.equals((Boolean) utils.executeJS(page, SCREENSHARE.SCREEN_DOWNSTREAM_CONNECTED))) {
            showMoreOptions();
            utils.clickByXpath(page, "//*[@purpose='startScreenShare']");

            Boolean isUpstreamConnected = (Boolean) utils.executeJS(page, SCREENSHARE.SCREEN_UPSTREAM_CONNECTED);
            if (Boolean.TRUE.equals(isUpstreamConnected)) {
                String currentUserName = (String) utils.executeJS(page, SCREENSHARE.SCREENSHARE_CURRENT_USER_NAME);
                System.out.println("Screenshare started successfully by: " + currentUserName);
            }
        } else {
            String existingUserName = (String) utils.executeJS(page, SCREENSHARE.SCREENSHARE_CURRENT_USER_NAME);
            System.out.println("Already user " + existingUserName + " is sharing their screen");
        }
    }
    
    public void stopScreenShare() {
        Boolean isScreenshareUser = (Boolean) utils.executeJS(page, SCREENSHARE.IS_SCREENSHARE_USER);

        if (Boolean.TRUE.equals(isScreenshareUser)) {
            showMoreOptions();
            utils.clickByXpath(page, "//*[@purpose='stopScreenShare']");

            String currentUserId = (String) utils.executeJS(page, SESSION.ZUID);
            System.out.println("Screenshare stopped by: " + currentUserId);
        } else {
            String actualUserName = (String) utils.executeJS(page, SCREENSHARE.SCREENSHARE_CURRENT_USER_NAME);
            String actualUserId = (String) utils.executeJS(page, SCREENSHARE.SCREENSHARE_CURRENT_USER_ID);
            System.out.println("Actual screenshare user is: " + actualUserName + " (User ID: " + actualUserId + ")");
        }
    }

    // Step 1: Open participants tab
    public void openParticipantsTab() {
        try {
            Locator participantsBtn = page.locator("//*[@purpose='openParticipantsInRHS']");
            int count = participantsBtn.count();

            if (count == 0) {
                System.err.println("❌ No participants tab button found.");
                return;
            }
            Locator buttonToClick = participantsBtn.nth(0);
            buttonToClick.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(TEN_SECONDS));

            buttonToClick.click();
            System.out.println("✅ Participants tab opened successfully.");

        } catch (Exception e) {
            System.err.println("❌ Failed to open participants tab: " + e.getMessage());
        }
    }

    // Step 2: Click "show actions" for a specific user
    private void openUserActionsMenu(String userId) {
        String userRow = PARTICIPANTS.getUserRowByUserId(userId);
        String actionsBtn = PARTICIPANTS.getShowActionsOnMemberByUserId(userId);
        page.locator(userRow).hover();
        page.locator(actionsBtn).click();
    }

    // Step 3: Actions on user
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
            // User row XPath from your PARTICIPANTS utility
            String userRow = PARTICIPANTS.getUserRowByUserId(userId);
            Locator userContainer = page.locator(userRow);

            if (userContainer.count() == 0) {
                System.out.println("⚠ User with ID " + userId + " not found in participants list.");
                return;
            }

            // Spotlight indicator inside that row
            Locator spotlightIndicator = userContainer.locator(".smartconf-sqr-spotlight-ind.zcf-spotlight");

            // Check if indicator is visible
            if (spotlightIndicator.isVisible()) {
                System.out.println("✅ User " + userId + " is spotlighted. Removing spotlight...");

                // Open user actions menu using your helper
                openUserActionsMenu(userId);

                // Click "Remove Spotlight"
                Locator removeSpotlightBtn = page.locator("//*[@purpose='removeSpotlightUser']");
                removeSpotlightBtn.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(5000));
                removeSpotlightBtn.click();

                System.out.println("✅ Spotlight removed for user " + userId);
            } else {
                System.out.println("ℹ User " + userId + " is not spotlighted. No action taken.");
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to remove spotlight for user " + userId + ": " + e.getMessage());
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

    //  --- Assign and leave
    
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
    private boolean isEndConferenceDropdownOpen() 
    {
        String display = utils.getAttributeByXpath(page,"//*[contains(@id,'end_dropdowncnt')]","style");
        return display != null && display.contains("display: block");
    }
    
    public void allowActiveSpeakerInSpotlight() {
        if (isHostOrCoHost()) {
            System.out.println("🔹 Request received: Enable Active Speaker Spotlight.");
            toggleActiveSpeakerSpotlight(true);
        } else {
            System.out.println("⚠ Current user is not Host/Co-Host. Cannot allow Active Speaker Spotlight.");
        }
    }

    public void disableActiveSpeakerInSpotlight() {
        if (isHostOrCoHost()) {
            System.out.println("🔹 Request received: Disable Active Speaker Spotlight.");
            toggleActiveSpeakerSpotlight(false);
        } else {
            System.out.println("⚠ Current user is not Host/Co-Host. Cannot disable Active Speaker Spotlight.");
        }
    }

    private void toggleActiveSpeakerSpotlight(boolean enable) {
        String action = enable ? "Enable" : "Disable";
        try {
            System.out.println("▶ Starting process to " + action + " Active Speaker Spotlight...");

            // Step 1: Ensure at least one user is spotlighted
            Locator spotlightedUsers = page.locator("//div[contains(@class,'smartconf-spotlight-user-list')]");
            int spotlightCount = spotlightedUsers.count();
            System.out.println("ℹ Currently spotlighted users count: " + spotlightCount);

            if (spotlightCount == 0) {
                if (enable) {
                    String userId = getUserId();
                    System.out.println("ℹ No spotlighted users found. Adding user " + userId + " to Spotlight...");
                    setInSpotlight(userId); // ensure at least one spotlight
                    System.out.println("✅ User " + userId + " successfully set in Spotlight.");
                } else {
                    System.out.println("ℹ No users in Spotlight. Skipping Disable operation.");
                }
                return;
            }

            // Step 2: Open Spotlight options menu
            System.out.println("▶ Opening Spotlight options menu...");
            openParticipantsTab();
            Locator spotlightOptionsBtn = page.locator("//*[@purpose='showSpotLightOptions']");
            spotlightOptionsBtn.click();
            System.out.println("✅ Spotlight options menu opened.");

            // Step 3: Select the correct option
            String optionText = enable ? "Allow Active Speaker Spotlight" : "Disable Active Speaker Spotlight";
            System.out.println("▶ Looking for option: " + optionText);
            Locator option = page.locator("//div[@id='spotlightdropdowncnt']//div[text()='" + optionText + "']");
            option.waitFor(new Locator.WaitForOptions().setTimeout(FIVE_SECONDS));
            option.click();

            System.out.println("✅ '" + optionText + "' executed successfully.");
            System.out.println("🎯 Active Speaker Spotlight is now " + (enable ? "ENABLED" : "DISABLED") + ".");
        } catch (Exception e) {
            System.err.println("❌ Failed to " + action + " Active Speaker Spotlight: " + e.getMessage());
        }
    }



    

    public void showActionsOnMember() {
        utils.hoverAndClick(page, "//*[@purpose='showActionsOnMember']");
//        utils.click(page, "//*[@purpose='showActionsOnMember']");
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
        if (!(boolean)user.evaluate("el => el.classList.contains('sel')")) {
            user.click();
        }
    }
    
    public void muteAll() {
        clickIfHostOrCoHost("//*[@purpose='muteAllParticipantsAudio']","✅ All participants muted","⚠️ 'Mute All' button not visible");
    }

    public void unmuteAll() {
        clickIfHostOrCoHost("//*[@purpose='unmuteAllParticipantsAudio']","✅ All participants unmuted","⚠️ 'Unmute All' button not visible"
        );
    }

    private void clickIfHostOrCoHost(String xpath, String successMsg, String failMsg) {
        if (!isHostOrCoHost()) {
            System.out.println("You should be a Host or Co-Host");
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

    private boolean isHostOrCoHost() {
        Boolean isHost = (Boolean) page.evaluate("() => " + SESSION.IS_CURRENT_USER_HOST);
        Boolean isCoHost = (Boolean) page.evaluate("() => " + SESSION.IS_CURRENT_USER_CO_HOST);
        return Boolean.TRUE.equals(isHost) || Boolean.TRUE.equals(isCoHost);
    }
    
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
    public void stageView()
    {
    	switchView(page, "switchToStageLayout"); 
    }
    public void gridView()
    {
    	switchView(page, "switchToVideoGridLayout"); 
    }
    public void mixedView()
    {
    	switchView(page, "switchToMixedVideoLayout");
    }
    public void switchView(Page page, String viewType) {
        String xpath = "//*[@purpose='"+viewType+"']";
        Locator viewButton = page.locator(xpath);
        viewButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        if (!viewButton.evaluate("el => el.classList.contains('active')").toString().equals("true")) {
            viewButton.click();
            System.out.println("Switched to view: " + viewType);
        } else {
            System.out.println("Already in view: " + viewType);
        }
    }

    private void openMoreOptionsIfNeeded() {
        Locator dropdownContent = page.locator("//*[contains(@id,'dropdowncnt')]");
        if (!dropdownContent.isVisible()) {
            page.locator("//*[@purpose='showMoreOptions']").click();
            dropdownContent.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
        }
    }
    
    public String getUserId()
    {
    	return page.evaluate("() => RTCP._rtcpuserid").toString();
    }
    public String getConferenceId()
    {
    	return page.evaluate("() => RTCP._conferenceId").toString(); 
    }
    public Page getPage()
    {
    	return this.page;
    }
    public void takeScreenshot(String filePrefix) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = filePrefix + "_" + timestamp + ".png";
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get("screenshots", fileName))
                    .setFullPage(true));
            System.out.println("Screenshot saved: screenshots/" + fileName);
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }
}
