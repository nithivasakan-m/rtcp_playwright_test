package copy;

import java.io.File;
import java.util.Hashtable;

import com.microsoft.playwright.Page;

import copy.Constants.FailureType;
import copy.Constants.MediaFileType;
import copy.Constants.TestPhase;

public class ConferenceRoomCopy {
	
	public void checkConferenceJoin(String testName, String description, Session hostSession) {
		if (state == TestPhase.FAILED) {
	        System.out.println("[checkConferenceJoin][process] " + testName +
	            " [JOIN CONFERENCE] [FAILED] [ROOM ID]" + getRoomId() +
	            " [CONF KEY]" + getConferenceKey() + " [CONF ID] " + getConferenceId());
	        return;
	    }
	
	    System.out.println("[checkConferenceJoin][process] " + testName +
	        " [JOIN CONFERENCE][ROOM ID]" + getRoomId() +
	        " [CONF KEY]" + getConferenceKey() + " [CONF ID] " + getConferenceId());
	
	    try {
	        String joinSessionId = Constants.ConfigurationKeys.JOIN;
	
	        // attach audio file if it exists
	        if (new File(ConfManager.getMediaBasePath() + File.separator + joinSessionId + ".wav").exists()) {
	            client.getCapability().setAudioFile(
	                new MediaFile(MediaFileType.AUDIO,
	                    new Hashtable<String,String>() {{
	                        put(Constants.ConfigurationKeys.FILE_NAME, joinSessionId);
	                    }}
	                )
	            );
	        }

        // Launch Playwright browser and create a page
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        Page page = browser.newPage();

        // join the conference (implement Session.join() using Playwright navigation)
        Session session = new ConferenceSession(
            getTestId(), getRoomId(),
            Constants.RoomType.CONFERENCE,
            joinSessionId, client,
            Constants.UserType.HOST, testConfig);

        boolean connected = session.joinWithPlaywright(page);  // <-- implement joinWithPlaywright
        if (!connected) {
            System.out.println("[checkConferenceJoin][process] " + testName +
                " [JOIN CONFERENCE][SESSION NOT GOT CONNECTED][TEST ID]" + getTestId() +
                " [ROOM ID]" + getRoomId() + " [SESSION ID]" + joinSessionId +
                " [CONF ID]" + getConferenceId() + " [USER ID]" + session.getUserId() +
                " [DEBUG INFO] " + session.getDebugInfo());
            session.analyzeLog();
            session.downloadWebRTCDump();
            functionalTestResults.put("join", false);
            return;
        }

        session.setSessionUserId(session.getUserId());
        System.out.println("[checkConferenceJoin][process] " + testName +
            " [JOINED] [JOIN CONFERENCE][TEST ID]" + getTestId() +
            " [ROOM ID]" + getRoomId() + " [SESSION ID]" + joinSessionId +
            " [CONF ID]" + getConferenceId() + " [USER ID]" + session.getUserId() +
            " [DEBUG INFO] " + session.getDebugInfo());

        addSession(session);
        session.loadRecordingScript();
        Util.waitAround(Timeouts.FIVE_SECOND_INTERVAL);
        session.startAudioRecording();
        session.changeLayout(Constants.ConfigurationKeys.STAGE_LAYOUT);

        boolean userJoinStatus = false;
        if (session.isVideoReceivedInDownStreamForUserId(session.getUserId()) &&
            session.isVideoReceivedInDownStreamForUserId(hostSession.getUserId())) {

            ClientStatsValidator.executeScriptForAudioVideoCheck(page);
            ClientStatsValidator.clearStatsInfo(hostSession.getPage());
            Util.waitAround(Timeouts.TEN_SECOND_INTERVAL * 4);

            userJoinStatus =
                ClientStatsValidator.checkDownstreamVideoFrameDecoded(hostSession.getPage(),
                    session.getSSRCId(session.getSessionUserId())) &&
                ClientStatsValidator.checkAudioDownstreamLevel(page);

            int audioLevelCheckiteration = 0;
            while (!userJoinStatus && audioLevelCheckiteration < 6) {
                userJoinStatus = ClientStatsValidator.checkAudioUpstreamLevel(page);
                ClientStatsValidator.clearStatsInfo(page);
                audioLevelCheckiteration++;
            }
        }

        addTimeStampOfAction("joinee", System.currentTimeMillis());
        System.out.println("[checkConferenceJoin][process] " + testName +
            " [JOIN STATUS]" + userJoinStatus + "[TEST ID]" + getTestId() +
            " [ROOM ID]" + getRoomId() + " [SESSION ID]" + joinSessionId +
            " [CONF ID]" + getConferenceId() + " [USER ID]" + session.getUserId());

        functionalTestResults.put("join", userJoinStatus);

        browser.close();
        playwright.close();

    } catch (UnreachableBrowserException e) {
        getJoinSession().closePage();
        idVsActiveSessionMap.remove(Constants.ConfigurationKeys.JOIN2);
        System.out.println("[Exception in checkConferenceJoin] [UnreachableBrowserException] [Retrying test] [Description] " + description);
        if (incrRetryCountForTestCase(testName) >= ConfManager.getMaxRetryCountForFunctionalTest()) {
            System.out.println("[Exception in checkConferenceJoin] Maximum retries reached for " + description);
            functionalTestResults.put("join", false);
            updatePhase(Constants.TestPhase.FAILED);
            failuretype = FailureType.QAFAILED;
            return;
        }
        checkConferenceJoin("ZVT2", "join a conference", getHostSession());
    } catch (PlaywrightException ex) {
        failuretype = FailureType.QAFAILED;
        throw ex;
    } catch (Exception ex) {
        getJoinSession().screenShot("FailedJoinee");
        functionalTestResults.put("join", false);
        updatePhase(Constants.TestPhase.FAILED);
        System.out.println("[Exception in checkConferenceJoin] " + description + " : " + ex.getMessage());
        ex.printStackTrace();
    }
}
	public void checkConferenceMuteAudio(String testName , String description,
	        Session hostSession, Session joinSession, Session join2Session) {
	if (state == TestPhase.FAILED) {
	System.out.println("[checkConferenceJoin2][process] " + testName +
	" [JOIN CONFERENCE][FAILED][ROOM ID]" + getRoomId() +
	" [CONF KEY]" + getConferenceKey() + " [CONF ID] " + getConferenceId());
	return;
	}
	try {
	System.out.println("[checkConferenceMuteAudioStatus] " + testName + " [Description] " + description);
	Hashtable<String, Boolean> audioMute = new Hashtable<>();
	
	hostSession.muteAudio();
	joinSession.muteAudio();
	join2Session.muteAudio();
	
	Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	ClientStatsValidator.clearStatsInfo(hostSession.getDriver());
	ClientStatsValidator.clearStatsInfo(joinSession.getDriver());
	ClientStatsValidator.clearStatsInfo(join2Session.getDriver());
	Util.waitAround(Timeouts.TEN_SECOND_INTERVAL * 3);
	
	boolean isAudioMutedHost = joinSession.isAudioMuted(hostSession.getUserId()) &&
	      hostSession.isAudioMuted(hostSession.getUserId()) &&
	      join2Session.isAudioMuted(hostSession.getUserId()) &&
	      !ClientStatsValidator.checkAudioUpstreamSent(hostSession.getDriver());
	audioMute.put("host", isAudioMutedHost);
	addTimeStampOfAction("host mute audio", System.currentTimeMillis());
	
	Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	boolean isAudioMutedJoin = joinSession.isAudioMuted(joinSession.getUserId()) &&
	      hostSession.isAudioMuted(joinSession.getUserId()) &&
	      join2Session.isAudioMuted(joinSession.getUserId()) &&
	      !ClientStatsValidator.checkAudioUpstreamSent(joinSession.getDriver());
	audioMute.put("joinee", isAudioMutedJoin);
	addTimeStampOfAction("joinee mute audio", System.currentTimeMillis());
	
	Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	boolean isAudioMutedJoin2 = join2Session.isAudioMuted(join2Session.getUserId()) &&
	       hostSession.isAudioMuted(join2Session.getUserId()) &&
	       joinSession.isAudioMuted(join2Session.getUserId()) &&
	       !ClientStatsValidator.checkAudioUpstreamSent(join2Session.getDriver());
	audioMute.put("joinee2", isAudioMutedJoin2);
	addTimeStampOfAction("joinee2 mute audio", System.currentTimeMillis());
	
	System.out.println("[checkConferenceMuteAudioStatus] " + testName +
	" [Description] " + description + " " + audioMute);
	functionalTestResults.put("audioMute", audioMute);
	} catch (Exception e) {
	System.out.println("[Exception in checkConferenceMuteAudio] " + description + " : " + e.getMessage());
	e.printStackTrace();
	}
	}
	
	public void checkConferenceMuteVideo(String testName ,String description,
	        Session hostSession, Session joinSession, Session join2Session) {
	if (state == TestPhase.FAILED) {
	System.out.println("[checkConferenceMuteVideoStatus] " + testName +
	" [Description] " + description + " [FAILED]");
	return;
	}
	try {
	System.out.println("[checkConferenceMuteVideoStatus] " + testName + " [Description] " + description);
	Hashtable<String, Boolean> videoMute = new Hashtable<>();
	
	hostSession.muteVideo();
	Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	boolean isVideoMutedHost = joinSession.isVideoMuted(hostSession.getUserId()) &&
	      hostSession.isVideoMuted(hostSession.getUserId()) &&
	      join2Session.isVideoMuted(hostSession.getUserId());
	videoMute.put("host", isVideoMutedHost);
	addTimeStampOfAction("host mute video", System.currentTimeMillis());
	
	joinSession.muteVideo();
	Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	boolean isVideoMutedJoin = hostSession.isVideoMuted(joinSession.getUserId()) &&
	      joinSession.isVideoMuted(joinSession.getUserId()) &&
	      join2Session.isVideoMuted(joinSession.getUserId());
	videoMute.put("joinee", isVideoMutedJoin);
	addTimeStampOfAction("joinee mute video", System.currentTimeMillis());
	
	join2Session.muteVideo();
	Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	boolean isVideoMutedJoin2 = hostSession.isVideoMuted(join2Session.getUserId()) &&
	       join2Session.isVideoMuted(join2Session.getUserId()) &&
	       joinSession.isVideoMuted(join2Session.getUserId());
	videoMute.put("joinee2", isVideoMutedJoin2);
	addTimeStampOfAction("joinee2 mute video", System.currentTimeMillis());
	
	System.out.println("[checkConferenceMuteVideoStatus] " + testName +
	" [Description] " + description + " " + videoMute);
	functionalTestResults.put("videoMute", videoMute);
	} catch (Exception e) {
	System.out.println("[Exception in checkConferenceMuteVideo] " + description + " : " + e.getMessage());
	e.printStackTrace();
	}
	}
	
	public void checkConferenceUnMuteAudio(String testName , String description,
	          Session hostSession, Session joinSession, Session join2Session) {
	if (state == TestPhase.FAILED) {
	System.out.println("[checkConferenceUnMuteAudioStatus] " + testName +
	" [Description] " + description + " [FAILED]");
	return;
	}
	try {
	System.out.println("[checkConferenceUnMuteAudioStatus] " + testName + " [Description] " + description);
	Hashtable<String, Boolean> audioUnMute = new Hashtable<>();
	
	hostSession.muteAudio();
	Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	boolean isAudioMutedHost = !joinSession.isAudioMuted(hostSession.getUserId()) &&
	      !hostSession.isAudioMuted(hostSession.getUserId()) &&
	      !join2Session.isAudioMuted(hostSession.getUserId());
	audioUnMute.put("host", isAudioMutedHost);
	addTimeStampOfAction("host unmute audio", System.currentTimeMillis());
	
	joinSession.muteAudio();
	Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	boolean isAudioMutedJoin = !hostSession.isAudioMuted(joinSession.getUserId()) &&
	      !joinSession.isAudioMuted(joinSession.getUserId()) &&
	      !join2Session.isAudioMuted(joinSession.getUserId());
	audioUnMute.put("joinee", isAudioMutedJoin);
	addTimeStampOfAction("joinee unmute audio", System.currentTimeMillis());
	
	join2Session.muteAudio();
	Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	boolean isAudioMutedJoin2 = !hostSession.isAudioMuted(join2Session.getUserId()) &&
	       !join2Session.isAudioMuted(join2Session.getUserId()) &&
	       !joinSession.isAudioMuted(join2Session.getUserId());
	audioUnMute.put("joinee2", isAudioMutedJoin2);
	addTimeStampOfAction("joinee2 unmute audio", System.currentTimeMillis());
	
	functionalTestResults.put("audioUnMute", audioUnMute);
	System.out.println("[checkConferenceUnMuteAudioStatus] " + testName +
	" [Description] " + description + " " + audioUnMute);
	} catch (Exception e) {
	System.out.println("[Exception in checkConferenceUnMuteAudio] " + description + " : " + e.getMessage());
	e.printStackTrace();
	}
	}

public void checkConferenceUnMuteVideo(String testName , String description,
          Session hostSession, Session joinSession, Session join2Session) {
	if (state == TestPhase.FAILED) {
		System.out.println("[checkConferenceUnMuteVideoStatus] " + testName +
" [Description] " + description + " [FAILED]");
		return;
	}
	try {
System.out.println("[checkConferenceUnMuteVideoStatus] " + testName + " [Description] " + description);
Hashtable<String, Boolean> videoUnMute = new Hashtable<>();

hostSession.muteVideo();
Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
boolean isVideoMutedHost = !joinSession.isVideoMuted(hostSession.getUserId()) &&
      !hostSession.isVideoMuted(hostSession.getUserId()) &&
      !join2Session.isVideoMuted(hostSession.getUserId());
videoUnMute.put("host", isVideoMutedHost);
addTimeStampOfAction("host unmute video", System.currentTimeMillis());

joinSession.muteVideo();
Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
boolean isVideoMutedJoin = !hostSession.isVideoMuted(joinSession.getUserId()) &&
      !joinSession.isVideoMuted(joinSession.getUserId()) &&
      !join2Session.isVideoMuted(joinSession.getUserId());
videoUnMute.put("joinee", isVideoMutedJoin);
addTimeStampOfAction("joinee unmute video", System.currentTimeMillis());

join2Session.muteVideo();
Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
boolean isVideoMutedJoin2 = !hostSession.isVideoMuted(join2Session.getUserId()) &&
       !joinSession.isVideoMuted(join2Session.getUserId()) &&
       !join2Session.isVideoMuted(join2Session.getUserId());
videoUnMute.put("joinee2", isVideoMutedJoin2);
addTimeStampOfAction("joinee2 unmute video", System.currentTimeMillis());

functionalTestResults.put("videoUnMute", videoUnMute);
System.out.println("[checkConferenceUnMuteVideoStatus] " + testName +
" [Description] " + description + " " + videoUnMute);
} catch (Exception e) {
System.out.println("[Exception in checkConferenceUnMuteVideo] " + description + " : " + e.getMessage());
e.printStackTrace();
}
}
public void checkConferenceScreenShare(String testName, String description,
        Session hostSession, Session joinSession, Session join2Session) {
if (state == TestPhase.FAILED) {
LOGGER.info("[checkConferenceScreenShare] " + testName + " [Description] "
+ description + " [FAILED]");
return;
}
try {
Hashtable screenShareEnabled = new Hashtable<>();

// Host screenshare
if (!hostSession.startScreenShare(conferenceId)) {
LOGGER.info("[checkConferenceScreenShare] " + testName + " [Description] "
+ description + "[TESTID] " + getTestId()
+ "[HOST] [SCREENSHARE ELEMENT NOT FOUND] [RETURNING]");
return;
}
Util.waitAround(Timeouts.FIVE_SECOND_INTERVAL);
addTimeStampOfAction("host screenshare", System.currentTimeMillis());

hostSession.screenShot("functionaltest/ScreenShareHost");
joinSession.screenShot("functionaltest/ScreenShareHost");
join2Session.screenShot("functionaltest/ScreenShareHost");

boolean isScreenShareEnabledHost = false;
if (hostSession.isScreenSharingEnabled(hostSession.getUserId())
&& joinSession.isScreenSharingEnabled(hostSession.getUserId())
&& join2Session.isScreenSharingEnabled(hostSession.getUserId())) {
isScreenShareEnabledHost = joinSession.isScreenShareDownStreamConnected()
&& join2Session.isScreenShareDownStreamConnected();
}
screenShareEnabled.put("host", isScreenShareEnabledHost);
hostSession.closePIP();

WebDriverUtil.clickElement(hostSession.getDriver(), "click close screenshare",
By.xpath("//*[@id='conferencemainsection']/div[5]/div[6]/div[1]/span[2]/span"));
Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);

// Joinee screenshare
if (!joinSession.startScreenShare(conferenceId)) {
LOGGER.info("[checkConferenceScreenShare] " + testName + " [Description] "
+ description + "[TESTID] " + getTestId()
+ "[JOINEE] [SCREENSHARE ELEMENT NOT FOUND] [RETURNING]");
return;
}
addTimeStampOfAction("joinee screenshare", System.currentTimeMillis());
Util.waitAround(Timeouts.FIVE_SECOND_INTERVAL);

hostSession.screenShot("functionaltest/ScreenShareJoinee");
joinSession.screenShot("functionaltest/ScreenShareJoinee");
join2Session.screenShot("functionaltest/ScreenShareJoinee");

boolean isScreenShareEnabledJoinee = false;
if (hostSession.isScreenSharingEnabled(joinSession.getUserId())
&& joinSession.isScreenSharingEnabled(joinSession.getUserId())
&& join2Session.isScreenSharingEnabled(joinSession.getUserId())) {
isScreenShareEnabledJoinee = hostSession.isScreenShareDownStreamConnected()
&& join2Session.isScreenShareDownStreamConnected();
}
screenShareEnabled.put("joinee", isScreenShareEnabledJoinee);

joinSession.closePIP();
try {
WebDriverUtil.clickElement(joinSession.getDriver(), "click close screenshare",
By.xpath("//*[@id=\"conferencemainsection\"]/div[5]/div[6]/div[1]/span[2]/span"));
} catch (Exception e) {   // keep only where alternate Xpath is required
WebDriverUtil.clickElement(joinSession.getDriver(), "click close screenshare",
By.xpath("//*[@id=\"conferencemainsection\"]/div[6]/div[6]/div[1]/span[2]/span"));
}
Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);

// Joinee2 screenshare
if (!join2Session.startScreenShare(conferenceId)) {
LOGGER.info("[checkConferenceScreenShare] " + testName + " [Description] "
+ description + "[TESTID] " + getTestId()
+ "[JOINEE2] [SCREENSHARE ELEMENT NOT FOUND] [RETURNING]");
return;
}
addTimeStampOfAction("joinee2 screenshare", System.currentTimeMillis());
Util.waitAround(Timeouts.FIVE_SECOND_INTERVAL);

hostSession.screenShot("functionaltest/ScreenShareJoinee2");
joinSession.screenShot("functionaltest/ScreenShareJoinee2");
join2Session.screenShot("functionaltest/ScreenShareJoinee2");

boolean isScreenShareEnabledJoinee2 = false;
if (hostSession.isScreenSharingEnabled(join2Session.getUserId())
&& joinSession.isScreenSharingEnabled(join2Session.getUserId())
&& join2Session.isScreenSharingEnabled(join2Session.getUserId())) {
isScreenShareEnabledJoinee2 = hostSession.isScreenShareDownStreamConnected()
&& joinSession.isScreenShareDownStreamConnected();
}
screenShareEnabled.put("joinee2", isScreenShareEnabledJoinee2);

join2Session.closePIP();
try {
WebDriverUtil.clickElement(join2Session.getDriver(), "click close screenshare",
By.xpath("//*[@id=\"conferencemainsection\"]/div[5]/div[6]/div[1]/span[2]/span"));
} catch (Exception e) {
WebDriverUtil.clickElement(join2Session.getDriver(), "click close screenshare",
By.xpath("//*[@id=\"conferencemainsection\"]/div[6]/div[6]/div[1]/span[2]/span"));
}

addTimeStampOfAction("close screenshare", System.currentTimeMillis());
functionalTestResults.put("ScreenShareEnabled", screenShareEnabled);
LOGGER.info("[checkConferenceScreenShareStatus] " + testName + " [Description] "
+ description + " " + screenShareEnabled);

} catch (WebDriverException ex) {
String errMsg = "[Exception in checkConferenceEndSession] [TEST_ID] "
+ testId + " [ROOM_ID]" + roomId;
LOGGER.log(Level.SEVERE, errMsg, ex);
failuretype = FailureType.QAFAILED;
}
}
public void checkConferenceRecording(String testName, String description) throws Exception {
    if (state == TestPhase.FAILED) {
        System.out.println("[checkConferenceRecording][process] " + testName
                + " [CONFERENCE][FAILED][ROOM ID]" + this.getRoomId()
                + " [CONF KEY]" + this.getConferenceKey()
                + " [CONF ID] " + this.getConferenceId());
        return;
    }
    Hashtable<String, Boolean> startRecording = new Hashtable<>();
    Hashtable<String, Boolean> stopRecording = new Hashtable<>();
    try {
        getHostSession().stopRTCPRecording(conferenceId);
        Util.waitAround(Timeouts.FIVE_SECOND_INTERVAL);
        stopRecording.put("host", !getHostSession().isRecordingEnabled());
        stopRecording.put("joinee", !getJoinSession().isRecordingEnabled());
        stopRecording.put("joinee2", !getJoin2Session().isRecordingEnabled());

        getHostSession().startRTCPRecording(conferenceId);
        Util.waitAround(Timeouts.TEN_SECOND_INTERVAL);

        for (Session session : idVsActiveSessionMap.values()) {
            if (session.getId() != Constants.ConfigurationKeys.HOST) {
                if (WebDriverUtil.isVisible(session.getDriver(),
                        By.xpath("//*[@id=\"recording_alert\"]/div/div[2]/div[2]"))) {
                    WebDriverUtil.clickElement(session.getDriver(),
                            "click continue recording alert",
                            By.xpath("//*[@id=\"recording_alert\"]/div/div[2]/div[2]"));
                }
                Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
            }
        }
        Util.waitAround(Timeouts.FIVE_SECOND_INTERVAL);
        startRecording.put("host", getHostSession().isRecordingEnabled());
        startRecording.put("joinee", getJoinSession().isRecordingEnabled());
        startRecording.put("joinee2", getJoin2Session().isRecordingEnabled());

    } catch (WebDriverException ex) {
        System.out.println("[Exception in checkConferenceEndSession] [TEST_ID] "
                + testId + " [ROOM_ID]" + roomId);
        ex.printStackTrace();
        this.failuretype = FailureType.QAFAILED;
    }

    this.functionalTestResults.put("startRecording", startRecording);
    this.functionalTestResults.put("stopRecording", stopRecording);
    System.out.println("[checkConferenceRecordingStatus] " + testName
            + " [Description] " + description + " " + startRecording
            + " StopRecordingStatus " + stopRecording);
}

public void checkConferenceActiveUserSwap(String testName, String description,
                                          Session hostSession, Session joinSession, Session join2Session) {
    if (state == TestPhase.FAILED) {
        System.out.println("[checkConferenceActiveUserSwap][process] " + testName
                + " [CONFERENCE][FAILED][ROOM ID]" + this.getRoomId()
                + " [CONF KEY]" + this.getConferenceKey()
                + " [CONF ID] " + this.getConferenceId());
        return;
    }
    try {
        String joinSessionId = "join";
        if (new File(ConfManager.getMediaBasePath()
                + File.separator + "highvol" + ".wav").exists()) {
            client.getCapability().setAudioFile(
                    new MediaFile(MediaFileType.AUDIO,
                            new Hashtable<String, String>() {{
                                put(Constants.ConfigurationKeys.FILE_NAME, "HighVolume");
                            }}));
        }
        Session session = new ConferenceSession(
                this.getTestId(), this.getRoomId(),
                Constants.RoomType.CONFERENCE,
                hostSessionId, client,
                Constants.UserType.PARTICIPANT, testConfig);

        boolean connected = session.joinWithAudioMuted();
        if (!connected) {
            System.err.println("[checkConferenceActiveUserSwap][process] " + testName
                    + " [JOIN CONFERENCE][SESSION NOT GOT CONNECTED][TEST ID]"
                    + this.getTestId() + " [ROOM ID]" + this.getRoomId()
                    + " [SESSION ID]" + joinSessionId
                    + " [CONF ID]" + this.getConferenceId()
                    + " [USER ID]" + session.getUserId()
                    + " [DEBUG INFO] " + session.getDebugInfo());
            session.analyzeLog();
            session.downloadWebRTCDump();
            return;
        }
        System.out.println("[checkConferenceActiveUserSwap][process] " + testName
                + " [JOINED] [TEST ID]" + this.getTestId()
                + " [ROOM ID]" + this.getRoomId()
                + " [SESSION ID]" + joinSessionId
                + " [CONF ID]" + this.getConferenceId()
                + " [USER ID]" + session.getUserId()
                + " [DEBUG INFO] " + session.getDebugInfo());

        session.unMuteAudio();
        boolean isL1Swapped = false;
        boolean isL0Swapped = false;
        int l0SwapCheckIteration = 0;
        Hashtable<String, Boolean> l1Status = new Hashtable<>();
        Hashtable<String, Boolean> l0Status = new Hashtable<>();

        while (!isL0Swapped && l0SwapCheckIteration < 40) {
            if (session.getUserId() != null) {
                String activeUserKey = getHostSession()
                        .getStageLayoutStreamId(session.getUserId());
                if (activeUserKey != null) {
                    if (activeUserKey.contains("_0")) {
                        System.out.println("[checkConferenceActiveUserSwap][process] "
                                + testName + " [Checking user layout status] [USER IN L0]");
                        isL0Swapped = true;
                        l0Status.put("host", true);
                    } else {
                        System.out.println("[checkConferenceActiveUserSwap][process] "
                                + testName + " [Checking user layout status] [USER IN L1]");
                        isL1Swapped = true;
                        l1Status.put("host", true);
                    }
                }
                l0SwapCheckIteration++;
            }
        }
        functionalTestResults.put("L1SwapStatus", l1Status);
        functionalTestResults.put("L0SwapStatus", l0Status);
        session.leave();

    } catch (WebDriverException ex) {
        System.out.println("[Exception in checkConferenceEndSession] [TEST_ID] "
                + testId + " [ROOM_ID]" + roomId);
        ex.printStackTrace();
        this.failuretype = FailureType.QAFAILED;
    }
    public void checkConferenceLeaveSession(String testName, String description) {
        if (state == TestPhase.FAILED) {
            System.out.println("[checkConferenceLeaveSession] " + testName
                    + " [LEAVING THE CONFERENCE ROOM] " + description
                    + " [TEST_ID]" + testId + " [ROOM_ID] " + roomId + " [FAILED]");
            return;
        }
        try {
            updatePhase(TestPhase.ENDING);
            for (Session session : idVsActiveSessionMap.values()) {
                String id = session.getId();
                if (!id.equals(Constants.ConfigurationKeys.HOST)
                        && !id.equals(Constants.ConfigurationKeys.JOIN)
                        && !id.equals(Constants.ConfigurationKeys.JOIN2)) {
                    session.downloadWebRTCDump();
                    session.leave();
                }
            }
            getHostSession().stopAudioRecording();
            getJoinSession().stopAudioRecording();
            getJoin2Session().stopAudioRecording();
            Thread.sleep(Timeouts.ONE_SECOND_INTERVAL);

            Session join2Session = getJoin2Session();
            join2Session.downloadWebRTCDump();
            join2Session.leave();

            System.out.println("[checkConferenceLeaveSession] " + testName
                    + " [LEAVING THE CONFERENCE ROOM] " + description
                    + " [TEST_ID]" + testId + " [ROOM_ID]" + roomId
                    + " [SKIPPING JOIN SESSION]");

            addTimeStampOfAction("joinee2 leave", System.currentTimeMillis());

            if (!join2Session.isSessionActive()) {
                System.out.println("[checkConferenceLeaveSession] " + testName
                        + " [JOIN2SESSION LEFT] [TEST_ID]" + testId + " [ROOM_ID]" + roomId);
            } else {
                System.out.println("[checkConferenceLeaveSession] " + testName
                        + " [JOIN2SESSION STILL CONNECTED] " + join2Session.getUserId()
                        + " [TEST_ID]" + testId + " [ROOM_ID]" + roomId);
            }

        } catch (WebDriverException ex) {
            System.err.println("[Exception in checkConferenceLeaveSession] [TEST_ID] "
                    + testId + " [ROOM_ID]" + roomId);
            ex.printStackTrace();
            this.failuretype = FailureType.QAFAILED;
        }
    }
    public void checkConferenceEndSession(String testName, String description) {
        try {
            Session joinSession = getJoinSession();
            Session hostSession = getHostSession();

            if (joinSession != null && hostSession != null) {
                joinSession.downloadWebRTCDump();
                hostSession.downloadWebRTCDump();
            }

            try {
                updatePhase(TestPhase.ENDING);
                WebDriverUtil.clickElement(
                        hostSession.getDriver(), "Leave Meeting button",
                        By.className("smartconf-btn_end"));
                WebDriverUtil.clickElement(
                        hostSession.getDriver(), "Leave Meeting button",
                        By.xpath("//*[@id='" + conferenceId + "end_dropdowncnt']/div/div[1]"));
            } catch (Exception e) {
                System.out.println("[checkConferenceEndSession] " + testName
                        + " [CONFERENCE FAILED TO END HOST] [TEST_ID]"
                        + testId + " [ROOM_ID]" + roomId);
            }

            Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);

            System.out.println("[checkConferenceEndSession] " + testName
                    + " [CONFERENCE ENDED] [CHECKING IF JOINSESSION STILL CONNECTED]"
                    + " [TEST_ID]" + testId + " [ROOM_ID]" + roomId);

            if (joinSession != null && !joinSession.isSessionActive()) {
                joinSession.getDriver().quit();
                System.out.println("[checkConferenceEndSession] " + testName
                        + " [JOINSESSION LEFT] [TEST_ID]" + testId + " [ROOM_ID]" + roomId);
            } else if (joinSession != null) {
                System.out.println("[checkConferenceEndSession] " + testName
                        + " [JOINSESSION STILL CONNECTED] [TEST_ID]"
                        + testId + " [ROOM_ID]" + roomId);
                joinSession.leave();
            }

            try {
                checkRecordingPlayBack();
                getHostSession().userSignOut(testConfig.getDataCenter());
            } catch (Exception e) {
                System.out.println("[checkConferencePlayBack] [EXCEPTION] "
                        + "[Could not test playback] " + e);
            }

            if (hostSession != null) {
                hostSession.getDriver().quit();
                if (!hostSession.isSessionActive()) {
                    System.out.println("[checkConferenceEndSession] " + testName
                            + " [HOSTSESSION LEFT] [TEST_ID]" + testId + " [ROOM_ID]" + roomId);
                }
            }

        } catch (WebDriverException ex) {
            System.err.println("[Exception in checkConferenceEndSession] [TEST_ID] "
                    + testId + " [ROOM_ID]" + roomId);
            ex.printStackTrace();
            this.failuretype = FailureType.QAFAILED;
        } finally {
            if (state == TestPhase.ENDING) {
                TestRunner runner = TestManager.getRunnerForTestId(testId);
                runner.closeRoom(roomId);
            }
            checkAndPostResultsInCliq();
        }
    }
    
    
}
public void checkAndInitLiveStreamingLoad() {
    System.out.println(
        "[streamLoadTest][INITIATING STREAM LOAD TEST][TEST ID]" + this.testId +
        " [ROOM ID]" + this.roomId +
        " [CLIENT] " + client +
        " [NO OF VIEWERS] " + testConfig.getLsViewerCount()
    );

    lsViewerCount = testConfig.getLsViewerCount();
    int joinThreads = this.lsViewerCount / ConfManager.getMaxTupleSize();
    this.liveStreamJoinThreads += joinThreads;

    for (int index = 0; index < joinThreads; index++) {
        AsyncLiveStreamLoadInitiator.joinStreamSession(this, client, ConfManager.getMaxTupleSize());
    }

    // Handle any remaining viewers
    if (this.lsViewerCount % ConfManager.getMaxTupleSize() > 0) {
        liveStreamJoinThreads++;
        AsyncLiveStreamLoadInitiator.joinStreamSession(
            this,
            client,
            lsViewerCount % ConfManager.getMaxTupleSize()
        );
    }
}

public void checkAndEndLiveStreamingLoad() {
    int sessionCount = streamSessionList.size();
    liveStreamCloseThreads = sessionCount / ConfManager.getMaxTupleSize();

    for (int index = 0; index < liveStreamCloseThreads; index++) {
        int fromInd = index * ConfManager.getMaxTupleSize();
        int toInd = fromInd + ConfManager.getMaxTupleSize();
        AsyncLiveStreamLoadInitiator.leaveStreamSession(
            this,
            streamSessionList.subList(fromInd, toInd)
        );
    }

    
    // Close remaining sessions if any
    if (sessionCount % ConfManager.getMaxTupleSize() > 0) {
        int fromInd = liveStreamCloseThreads * ConfManager.getMaxTupleSize();
        int toInd = sessionCount;
        liveStreamCloseThreads++;
        AsyncLiveStreamLoadInitiator.leaveStreamSession(
            this,
            streamSessionList.subList(fromInd, toInd)
        );
    }
}

}


}
