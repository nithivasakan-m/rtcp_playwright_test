package copy;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import copy.Constants.Browsers;
import copy.Constants.RoomType;
import copy.Constants.UserType;


public abstract class Session {

    // ---------------- Fields ----------------
    protected String testId;
    protected String roomId;
    protected String sessionId;
    protected RoomType roomType;
    protected UserType userType;
    protected String userId;
    protected boolean isDownstreamDisabled;

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    private String currentUrl;
    protected String downloadFilepath;
    private Browsers browserType;
    protected TestConfig testConfig;

    private static AtomicInteger ssIndex = new AtomicInteger();

    protected Page sessionPage;
    protected Page webrtcPage;
    
    public Session(String testId) 
    {
        this.testId = testId;
    }

    public Session(String testId, String roomId, RoomType roomType, String sessionId, Client client, UserType type, TestConfig config) throws Exception 
    {
        BrowserSpecs specs = client.getBrowserSpecs();
        this.browserType = specs.getBrowser();
        this.testId = testId;
        this.roomId = roomId;
        this.roomType = roomType;
        this.sessionId = sessionId;
        this.userType = type;
        this.isDownstreamDisabled = client.getCapability().isNullVideoDecoderEnabled();
        this.testConfig = config;

        this.sessionPage = this.page;

        if (browserType.equals(Browsers.CHROME))
        {
            loadWebRTCInternalPage();
        }
    }

    public Page getPage()
    {
    	return sessionPage;
    }
    // ---------------- Getters / Setters ----------------
    public String getId() 
    {
    	return this.sessionId; 
    }
    
    public void setSessionUserId(String userId)
    {
    	this.userId = userId; 
    }
    
    public String getSessionUserId() 
    {
    	return this.userId;
    }
    
    public String getDownloadPath() 
    { 
    	return this.downloadFilepath;
    }
    
    public Browsers getBrowserType()
    {
    	return this.browserType;
    }

    // ---------------- WebRTC Internals ----------------
    public void loadWebRTCInternalPage() 
    {
        try 
        {
            webrtcPage = context.newPage();
            webrtcPage.navigate("chrome://webrtc-internals/");
            Locator summaryButton = webrtcPage.locator("summary");
            if (summaryButton.isVisible()) summaryButton.click();
            System.out.println("[loadWebRTCInternalPage] WebRTC Internals page loaded successfully.");
        } 
        catch (Exception e)
        {
            System.out.println("[Exception in loadWebRTCInternalPage] " + e.getMessage());
        }
    }

    public boolean downloadWebRTCDump() {
        try {
            if(webrtcPage == null) throw new IllegalStateException("WebRTC Internals page not loaded.");
            Download download = webrtcPage.waitForDownload(() -> 
            {
                webrtcPage.locator("xpath=/html/body/p/details/div/div[1]/a/button").click();
            });
            download.saveAs(Paths.get(downloadFilepath, "webrtc_internals_dump.txt"));
            System.out.println("[downloadWebRTCDump] File downloaded: " + download.path());
            return true;
        } 
        catch (Exception e)
        {
            System.out.println("[Exception in downloadWebRTCDump] " + e.getMessage());
            return false;
        } 
        finally
        {
            try 
            { 
            	if(sessionPage != null) 
            	{
            		sessionPage.bringToFront();
            	} 
            }
            catch (Exception ignored) 
            {
            	
            }
        }
    }

    // ---------------- Page Helpers ----------------
    public void click(String selector)
    {
    	page.locator(selector).click(); 
    }
    
    public void click(String selector, boolean force) 
    {
    	page.locator(selector).click(new Locator.ClickOptions().setForce(force)); 
    }

    public void waitUntilAvailabilityOf(String selector, int timeoutInSeconds) 
    {
        page.locator(selector).waitFor(new Locator.WaitForOptions().setTimeout(timeoutInSeconds * 1000));
    }

    public void waitUntilVisibilityOf(String selector, int timeoutInSeconds) 
    {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutInSeconds * 1000));
    }

    public void reload() 
    {
    	page.reload();
    }

    public void loadPage(String url)
    {
    	loadPage(url, 30000); 
    }
    
    public void loadPage(String url, int timeoutInSeconds)
    {
        page.navigate(url, new Page.NavigateOptions().setTimeout(timeoutInSeconds * 1000));
    }

    // ---------------- JS Evaluation Helpers ----------------
    public String getMediaIp() 
    {
        try 
        {
            if (page == null) return null;
            String jsScript = "return (typeof ZCSmartConferenceImpl==='undefined'?ZRSmartConferenceImpl:ZCSmartConferenceImpl)"
                    + ".getCurrentActiveSession().getMediaServerIp();";
            Object mediaIp = page.evaluate(jsScript);
            return mediaIp != null ? mediaIp.toString() : null;
        }
        catch (Exception e) 
        {
            screenShot("getMediaIp_error");
            return null;
        }
    }

    public boolean waitUntilConnected(int maxTimeoutSeconds) 
    {
        try 
        {
            for (int elapsed = 0; elapsed < maxTimeoutSeconds; elapsed++) 
            {
                String jsScript = "return (typeof ZCSmartConferenceImpl==='undefined'?ZRSmartConferenceImpl:ZCSmartConferenceImpl)"
                        + ".getCurrentActiveSession().isInitialSessionConnected();";
                Object isConnected = page.evaluate(jsScript);
                if (isConnected != null && (boolean) isConnected) return true;
                page.waitForTimeout(1000);
            }
            screenShot("session_not_connected_" + sessionId);
            return false;
        } 
        catch (Exception e) 
        {
            screenShot("waitUntilConnected_error_" + sessionId);
            return false;
        }
    }

    void screenShot(String msg) 
    {
        try {
            String fileName = "screenshot_" + msg + "_" + ssIndex.incrementAndGet() + ".png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(downloadFilepath != null ? downloadFilepath : ".", fileName)));
        } 
        catch (Exception ignored) 
        {
        	
        }
    }

    public long getRecordingStartTime() 
    {
        try 
        {
            if (page == null) return 0;
            String jsScript = "return (typeof ZCSmartConferenceImpl==='undefined'?ZRSmartConferenceImpl:ZCSmartConferenceImpl)"
                    + ".getCurrentActiveSession().getStartTime()";
            Object startTimeStr = page.evaluate(jsScript);
            return startTimeStr != null ? Long.parseLong(startTimeStr.toString()) : 0;
        } 
        catch (Exception e) 
        { 
        	return 0;
        }
    }

    public boolean isSessionActive()
    {
        try 
        {
        	if (page == null) return false;
            String jsScript = "return (typeof ZCSmartConferenceImpl==='undefined'?ZRSmartConferenceImpl:ZCSmartConferenceImpl)"
                    + ".getCurrentActiveSession();";
            Object isSessionActive = page.evaluate(jsScript);
            return isSessionActive != null;
        } 
        catch (Exception e) 
        { 
        	return false;
        }
    }

    // ---------------- Media Controls ----------------
    public boolean isAudioMuted(String zuid) 
    {
    	return evaluateBooleanMemberStatus(zuid, "isAudioMuted"); 
    }
    
    public boolean isVideoMuted(String zuid) 
    {
    	return evaluateBooleanMemberStatus(zuid, "isVideoMuted"); 
    }

    private boolean evaluateBooleanMemberStatus(String zuid, String method)
    {
        try 
        {
            if (page == null) 
            	return false;
            String js = "return (typeof ZCSmartConferenceImpl==='undefined'?ZRSmartConferenceImpl:ZCSmartConferenceImpl)"
                    + ".getCurrentSession().getMember('" + zuid + "')." + method + "();";
            Object result = page.evaluate(js);
            return result != null && (boolean) result;
        }
        catch (Exception e) 
        {
        	return false; 
        }
    }

    public boolean muteAudio() 
    {
    	return toggleOption("//*[@id='conferenceoptionset']/div[1]", "MuteAudio_error"); 
    }
    
    public boolean unMuteAudio() 
    { 
    	return toggleOption("//*[@id='conferenceoptionset']/div[1]", "UnMuteAudio_error");
    }
    
    public boolean muteVideo() 
    {
    	return toggleOption("//*[@id='conferenceoptionset']/div[2]", "MuteVideo_error");
    }
    
    public boolean unMuteVideo()
    {
    	return toggleOption("//*[@id='conferenceoptionset']/div[2]", "UnMuteVideo_error");
    }

    private boolean toggleOption(String selector, String screenshotName)
    {
        try 
        {
        	page.locator(selector).click();
        	return true; 
        }
        catch(Exception e)
        {
        	page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotName + ".png")));
        	return false; 
        }
    }

    // ---------------- Layout & Spotlight ----------------
    public boolean changeLayout(String layout)
    {
        try 
        {
            if ("ACTIVE_SPEAKER_LAYOUT".equals(layout)) page.locator("//*[@id='layout_options']/div[1]").click();
            else if ("STAGE_LAYOUT".equals(layout)) page.locator("//*[@id='layout_options']/div[2]").click();
            else if ("GRID_LAYOUT".equals(layout)) page.locator("//*[@id='layout_options']/div[3]").click();
            return true;
        } 
        catch (Exception e) 
        { 
        	return false; 
        }
    }

    public boolean closePIP() 
    {
        try 
        {
            if(page.locator("//*[@id='conferenceoptionset']/div[8]").isVisible()) 
            {
                page.locator("//*[@id='conferenceoptionset']/div[8]").click();
                page.locator("//*[@id='smartconference_container']").hover();
                page.locator("//*[@id='conferencemainsection']/div[2]/span").click();
            }
            return true;
        } 
        catch (Exception e)
        { 
        	return false;
        }
    }

    // Spotlight / Active Speaker
 // ---------------- Spotlight & Active Speaker ----------------
    public boolean enableSpotLightForUser(Page page, String userId, String conferenceId) 
    {
        try {
            if (page == null) 
            {
                System.out.println("[enableSpotLightForUser][PAGE IS NULL][RETURN FALSE]");
                return false;
            }
            page.locator("#smartconferenceuserslist [item-id='" + userId + "']").hover();
            page.locator("//*[@id=\"" + conferenceId + userId + "useractiondropdownopt\"]").click();

            Locator option1 = page.locator("//*[@id=\"" + conferenceId + userId + "useractiondropdowncnt\"]/div[1]");
            Locator option2 = page.locator("//*[@id=\"" + conferenceId + userId + "useractiondropdowncnt\"]/div[2]");

            if(option1.isVisible() && option1.innerText().equals("Spotlight")) 
            {
                option1.click();
            } 
            else if (option2.isVisible() && option2.innerText().equals("Spotlight"))
            {
                option2.click();
            }

            boolean status = isSpotlightEnabled(page, userId);
            System.out.println("[enableSpotLightForUser] [STATUS] " + status);
            return status;
        } 
        catch (Exception e) 
        {
            System.out.println("[Exception in enableSpotLightForUser] " + e.getMessage());
            return false;
        }
    }

    public boolean disableSpotLightForUser(Page page, String userId, String conferenceId)
    {
        try {
            if (page == null) 
            {
                System.out.println("[disableSpotLightForUser][PAGE IS NULL][RETURN FALSE]");
                return false;
            }
            page.locator("#smartconferenceuserslist [item-id='" + userId + "']").hover();
            page.locator("//*[@id=\"" + conferenceId + userId + "useractiondropdownopt\"]").click();

            page.waitForTimeout(1000);

            Locator option1 = page.locator("//*[@id=\"" + conferenceId + userId + "useractiondropdowncnt\"]/div[1]");
            Locator option2 = page.locator("//*[@id=\"" + conferenceId + userId + "useractiondropdowncnt\"]/div[2]");

            if (option1.isVisible() && option1.innerText().equals("Remove spotlight"))
            {
                option1.click();
            } else if (option2.isVisible() && option2.innerText().equals("Remove spotlight")) {
                option2.click();
            }

            boolean status = isSpotlightEnabled(page, userId);
            System.out.println("[disableSpotLightForUser] [STATUS] " + status);
            return status;
        } 
        catch (Exception e) 
        {
            System.out.println("[Exception in disableSpotLightForUser] " + e.getMessage());
            return false;
        }
    }

    public boolean isSpotlightEnabled(Page page, String userId) 
    {
        try
        {
            if (page == null) 
            {
                System.out.println("[isSpotlightEnabled][PAGE IS NULL][RETURN FALSE]");
                return false;
            }

            String js =
                "var smartConfSession = ZRSmartConferenceImpl.getCurrentSession();" +
                "if(!smartConfSession){return false;}" +
                "return smartConfSession.isUserInSpotLight('" + userId + "');";

            Object spotlight = page.evaluate(js);
            return spotlight != null && (boolean) spotlight;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean enableActiveSpeakerInSpotlight(Page page) 
    {
        try
        {
            if (page == null) 
            {
                System.out.println("[enableActiveSpeakerInSpotlight][PAGE IS NULL][RETURN FALSE]");
                return false;
            }
            page.locator("//*[@id=\"v2_header_opts\"]/div[2]/div[1]/span[2]").click();
            page.locator("//*[@id=\"spotlightdropdowncnt\"]/div/div[2]/span[2]/label/span").click();
            page.locator("//*[@id=\"v2_header_opts\"]/div[2]/div[1]/span[2]").click();

            boolean status = isActiveSpeakerEnabledInSpotlight(page);
            System.out.println("[enableActiveSpeakerInSpotlight] [STATUS] " + status);
            return status;
        }
        catch (Exception e)
        {
            System.out.println("[Exception in enableActiveSpeakerInSpotlight] " + e.getMessage());
            return false;
        }
    }

    public boolean isActiveSpeakerEnabledInSpotlight(Page page)
    {
        try {
            if(page == null) 
            {
                System.out.println("[isActiveSpeakerEnabledInSpotlight][PAGE IS NULL][RETURN FALSE]");
                return false;
            }

            String js ="return (typeof ZCSmartConferenceImpl === \"undefined\" ? ZRSmartConferenceImpl : ZCSmartConferenceImpl)" +
                ".getCurrentSession().isSpeakersVideoAllowedInSpotLight();";

            Object result = page.evaluate(js);
            return result != null && (Boolean) result;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- Recording & Screen Share ----------------
    public boolean startScreenShare(String conferenceId) {
        try {
            if(page.locator("/html/body/div[2]/div[3]/div/section/div[5]/div[5]/div[6]").isVisible()) 
            {
                page.locator("/html/body/div[2]/div[3]/div/section/div[5]/div[5]/div[6]").click();
                page.locator("/html/body/div[2]/div[3]/div/section/div[5]/div[5]/div[6]/div/div/div[1]").click();
                return true;
            } 
            else if(page.locator("//*[@id='smartconfmoreopt']").isVisible()) 
            {
                page.locator("//*[@id='smartconfmoreopt']").click();
                page.locator("//*[@id='" + conferenceId + "dropdowncnt']/div/div[1]").click();
                return true;
            }
            return false;
        }
        catch (Exception e)
        {
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("startScreenShare_error.png")));
            System.out.println("[Exception in startScreenShare] " + e.getMessage());
            return false;
        }
    }

    public boolean startRTCPRecording(Page page, String conferenceId) {
        try 
        {
            if(page.locator("/html/body/div[2]/div[3]/div/section/div[5]/div[5]/div[6]").isVisible()) 
            {
                page.locator("/html/body/div[2]/div[3]/div/section/div[5]/div[5]/div[6]").click();
            }
            page.waitForTimeout(1000);
            page.locator("/html/body/div[2]/div[3]/div/section/div[5]/div[5]/div[6]/div/div/div[2]").click();
            return true;
        } catch (Exception e) 
        {
            System.out.println("[Exception in startRTCPRecording] " + e.getMessage());
            return false;
        }
    }

    public boolean stopRTCPRecording(Page page, String conferenceId) 
    {
        try 
        {
        	if(page.locator("/html/body/div[2]/div[3]/div/section/div[5]/div[5]/div[6]").isVisible())
        	{
                page.locator("/html/body/div[2]/div[3]/div/section/div[5]/div[5]/div[6]").click();
            }
            page.waitForTimeout(3000);
            page.locator("/html/body/div[2]/div[3]/div/section/div[5]/div[5]/div[6]/div/div/div[2]").click();
            return true;
        } catch (Exception e) 
        {
            System.out.println("[Exception in stopRTCPRecording] " + e.getMessage());
            return false;
        }
    }
    
    boolean userLogin(String dataCenter) 
    {
        try{
            String username = ConfManager.getUserName(dataCenter);
            String password = ConfManager.getPassword(dataCenter);

            if (Util.isNull(username) || Util.isNull(password)) 
            {
                System.out.println("[userLogin] [NO USERNAME PASSWORD FOUND] [USERNAME] " + username + " [PASSWORD] " + password);
                return false;
            }

            String accountsLoginUrl = ConfManager.getAccountsLoginUrl(dataCenter);
            page.navigate(accountsLoginUrl);

            page.waitForSelector("#login_id");
            page.fill("#login_id", username);
            page.click("#nextbtn");

            page.waitForSelector("#password");
            page.fill("#password", password);
            page.click("#nextbtn");

            if (page.locator("#continue_button").isVisible()) 
            {
                page.click("#continue_button");
            }

            System.out.println("[userLogin] [USER LOGGED IN SUCCESSFULLY]");
            
            return true;
        } catch (Exception ex) 
        {
        	System.out.println("[Exceptions in userLogin] "+ ex);
            return false;
        }
    }

    // ---------- User Sign-Out ----------
    static boolean userSignOut(Page page, String dataCenter)
    {
        try 
        {
            String username = ConfManager.getUserName(dataCenter);
            String password = ConfManager.getPassword(dataCenter);
            
            System.out.println("[userSignOut] [Signing out] [USERNAME] " + username + " [PASSWORD] " + password);

            String accountsLoginUrl = ConfManager.getAccountsLoginUrl(dataCenter);
            page.navigate(accountsLoginUrl);

            page.waitForSelector("#headder_thumb_pic");
            page.click("#headder_thumb_pic");

            page.waitForSelector("xpath=/html/body/div[6]/div[2]/div[6]");
            page.click("xpath=/html/body/div[6]/div[2]/div[6]");

            System.out.println("[userSignOut] [Account Signed off successfully]");
            return true;
        } 
        catch (Exception ex) 
        {
        	System.out.println("[Exceptions in userSignOut]" + ex);
        	return false;
        }
    }

    public boolean isRecordingEnabled() {
        try {
            if (page == null) {
                System.out.println(
                    "[isRecordingEnabled][PAGE IS NULL][RETURN FALSE]" +
                    "[TESTID]" + testId +
                    " [ROOMID] " + roomId +
                    " [SESSION ID]" + sessionId
                );
                return false;
            }

            // JS snippet to check the recording status inside the web app
            String jsScriptForRecordingStatus =
                "var smartConfSession = ZRSmartConferenceImpl.getCurrentActiveSession();"
              + "if(!smartConfSession){return false;}"
              + "return smartConfSession.isRecordingEnabled();";

            Boolean isRecordingEnabled = (Boolean) page.evaluate(jsScriptForRecordingStatus);

            System.out.println(
                "[isRecordingEnabled] [STATUS] " + isRecordingEnabled
            );

            return Boolean.TRUE.equals(isRecordingEnabled);
        } catch (Exception e) {
            // capture screenshot if needed
            if (page != null) {
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("isRecordingEnabled-error.png")));
            }
            System.out.println(
                "[Exception in isRecordingEnabled] [TESTID]" + testId +
                " [ROOMID] " + roomId +
                " [SESSION ID]" + sessionId +
                " Exception: " + e
            );
        }
        return false;
    }
    public boolean isRecordingEnabledOnPreviewPage() {
        try {
            if (page == null) {
                System.out.println(
                    "[isRecordingEnabledOnPreviewPage][PAGE IS NULL][RETURN FALSE][TESTID]"
                    + testId + " [ROOMID]" + roomId + " [SESSION ID]" + sessionId
                );
                return false;
            }

            String jsScriptForRecordingStatus =
                "var smartConfSession = ZRSmartConferenceImpl.getJoiningSession();" +
                "if(!smartConfSession){return;} return !!smartConfSession.isRecordingEnabled();";

            Boolean isRecordingEnabled = (Boolean) page.evaluate(jsScriptForRecordingStatus);

            System.out.println(
                toString() + "[isRecordingEnabledOnPreviewPage] [STATUS] " + isRecordingEnabled
            );

            if (isRecordingEnabled != null && isRecordingEnabled) {
                return true;
            }
        } catch (Exception e) {
//            screenShot(); // capture screenshot with your own method
            System.out.println(
                "[Exception in isRecordingEnabledOnPreviewPage] [TESTID]"
                + testId + " [ROOMID]" + roomId + " [SESSION ID]" + sessionId
                + " Exception: " + e
            );
        }
        return false;
    }
    
 // Checks if a given user (by ZUID) is sharing their screen
    public boolean isScreenSharingEnabled(String zuid) {
        try {
            if (page == null) {
                System.out.println(
                    "[isScreenSharingEnabled][PAGE IS NULL][RETURN FALSE][TESTID]"
                    + testId + " [ROOMID] " + roomId + " [SESSION ID] " + sessionId
                );
                return false;
            }

            String js =
                "return (typeof ZCSmartConferenceImpl === 'undefined' ? "
              + "ZRSmartConferenceImpl : ZCSmartConferenceImpl)"
              + ".getCurrentSession().isUserScreenSharer('" + zuid + "');";

            Object result = page.evaluate(js);
            boolean enabled = result instanceof Boolean && (Boolean) result;

            System.out.println(
                toString() + " [isScreenSharingEnabled] [ZUID] "
                + zuid + " [STATUS] " + enabled
            );

            return enabled;
        } catch (Exception e) {
            screenShot(); // your screenshot method
            System.out.println(
                "[Exception in isScreenSharingEnabled] [TESTID] " + testId
                + " [ROOMID] " + roomId + " [SESSION ID] " + sessionId
                + " [ZUID] " + zuid + " Exception: " + e
            );
            return false;
        }
    }

    // Checks if the downstream screen-share connection exists
    public boolean isScreenShareDownStreamConnected() {
        try {
            if (page == null) {
                System.out.println(
                    "[isScreenShareDownStreamConnected][PAGE IS NULL][RETURN FALSE][TESTID]"
                    + testId + " [ROOMID] " + roomId + " [SESSION ID] " + sessionId
                );
                return false;
            }

            String js =
                "return (typeof ZCSmartConferenceImpl === 'undefined' ? "
              + "ZRSmartConferenceImpl : ZCSmartConferenceImpl)"
              + ".getCurrentSession().hasScreenDownStreamConnection();";

            Object result = page.evaluate(js);
            boolean connected = result instanceof Boolean && (Boolean) result;

            System.out.println(
                toString() + " [isScreenShareDownStreamConnected] [STATUS] " + connected
            );

            return connected;
        } catch (Exception e) {
            screenShot();
            System.out.println(
                "[Exception in isScreenShareDownStreamConnected] [TESTID] " + testId
                + " [ROOMID] " + roomId + " [SESSION ID] " + sessionId
                + " Exception: " + e
            );
            return false;
        }
    }
    
    public boolean isVideoReceivedInDownStreamForUserId(String zuid) {
        try {
            if (page == null) {
                System.out.println(
                    "[isVideoReceivedInDownStreamForUserId][PAGE IS NULL][RETURN FALSE]"
                    + "[TESTID]" + testId + " [ROOMID]" + roomId + " [SESSION ID]" + sessionId
                );
                return false;
            }

            // JavaScript to verify downstream video for a given user
            String jsScript =
                "if (ZRSmartConferenceImpl.getCurrentActiveSession()) {" +
                "   var session = ZRSmartConferenceImpl.getCurrentActiveSession();" +
                "   if (!session) return null;" +
                "   var stream = session.getVideoStreamWithUserId('" + zuid + "');" +
                "   if (!stream) return null;" +
                "   return true;" +
                "}";

            Boolean isVideoIncoming = (Boolean) page.evaluate(jsScript);

            System.out.println(
                toString() + "[isVideoReceivedInDownStreamForUserId] [STATUS OF] "
                + zuid + " [STATUS] " + isVideoIncoming
            );

            return isVideoIncoming != null && isVideoIncoming;

        } catch (Exception e) {
            // Optional: capture a screenshot with your own screenshot method
            // screenShot("video_downstream_error_" + zuid);
            System.out.println(
                "[Exception in isVideoReceivedInDownStreamForUserId] [TESTID]"
                + testId + " [ROOMID]" + roomId + " [SESSION ID]" + sessionId
                + " [ZUID] " + zuid + " Exception: " + e
            );
        }
        return false;
    }

    
    // ---------------- DenableActiveSpeakerInSpotlightbug Info ----------------
    public String getSSRCId(String zuid) 
    {
    	return null; 
    }
    
    public String getDebugInfo() 
    {
    	return null;
    }
    
    public void screenShot()
    {
    	screenShot("Host");
    }
    // ---------------- Abstract session methods ----------------
    public abstract boolean host();
    public abstract boolean join();
    public abstract boolean leave();
    public abstract boolean end(String conferenceId);
    public abstract boolean joinWithAudioMuted();
    public abstract boolean isConnected();
    public abstract String getUserId();
}
