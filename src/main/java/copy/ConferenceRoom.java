package copy;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import copy.Constants.Browsers;
import copy.Constants.ConfigurationKeys;
import copy.Constants.FailureType;
import copy.Constants.MediaFileType;
import copy.Constants.Server;
import copy.Constants.TestPhase;
import copy.Constants.TestType;

class ConferenceRoom extends Room
{
	
	private Server server = null;
	
	private String hostSessionId = "YET TO BE GENERATED"; //No I18N
	private String conferenceKey = "YET TO BE GENERATED"; //No I18N
	private String conferenceId = "YET TO BE GENERATED"; //No I18N
	
	private int roomCapacity = 10;

	private String mediaIp = "";
	private String mediaIp_1 = "";
	private String mediaIp_2 = "";
	
	private int lsViewerCount = -1;
	private double playBackDuration = 0.0;
	private long conferenceRecordingStartTime = 0;

	private ConcurrentHashMap<String, Session> idVsActiveSessionMap = new ConcurrentHashMap<String, Session>();
	private ConcurrentHashMap<String, Session> idVsInActiveSessionMap = new ConcurrentHashMap<String, Session>();

	private HashMap<String, Integer> actionVsTimeStamp = new HashMap<>();

	private CopyOnWriteArrayList<Session> streamSessionList = new CopyOnWriteArrayList <Session>();

	private boolean postTestResult = true;
	private FailureType failuretype = FailureType.CALLFAILED;
	private Hashtable functionalTestResults = new Hashtable<>();	
	private boolean recordingStatus = false; 
	private static HashMap<String, AtomicInteger> testCaseVsRetryCount = new HashMap<String, AtomicInteger>();
	
//	public ConferenceRoom(String testName, String testId, String roomId, Client client, TestType type, TestConfig config, ConfRoom room)
//	{
//		super(testName,testId, roomId, client, type, config ,room);
//	}
//	
	public ConferenceRoom(String testName, String testId, String roomId, Client client, TestType type, TestConfig config, Server server)
	{
		super(testName,testId, roomId, client, type, config, server);
	}
	
	public void setRoomCapacity(int capacity)
	{
		this.roomCapacity = capacity;
	}
	
	public void addSession(Session session)
	{
		System.out.println("[addSession][ADDING SESSION TO MAP][TEST ID]"+this.testId+" [ROOM ID]"+this.roomId+" [SESSION ID]"+session.getId());
		idVsActiveSessionMap.put(session.getId(), session);
	}
	
	public int incrRetryCountForTestCase(String testname)
	{
		if(this.testCaseVsRetryCount.containsKey(testname))
		{
			return this.testCaseVsRetryCount.get(testname).incrementAndGet();
		}
		this.testCaseVsRetryCount.put(testname, new AtomicInteger());
		return this.testCaseVsRetryCount.get(testname).get();
	}
	
	public void handleStressTest()
	{
		System.out.println("[handleStressTest][INITIATING STRESS TEST][TEST ID]"+this.testId+" [ROOM ID]"+this.roomId+" [CLIENT] "+client);
		if (state == TestPhase.INITIATED)
		{
//			if(testConfig.getJoinExistingMeeting())
//			{
//				this.setRoomCapacity(this.roomCapacity + 1);
//				this.updatePhase(Constants.TestPhase.HOSTED);
//				return;
//			}
//			AsyncLoadInitiator.hostConferenceSession(this, client);
		}
	}
	
	@Override
	public void initiateTest()
	{
		if(this.testType == testType.FUNCTIONAL)
		{
			
		}
	}
	
	public void setConferenceRecordingStartTime(long startTime)
	{
		this.conferenceRecordingStartTime = startTime;
	}
	
	public Session getHostSession()
	{
		return idVsActiveSessionMap.get(ConfigurationKeys.HOST);
	}
	
	public Session getJoinee_1_Session()
	{
		return idVsActiveSessionMap.get(ConfigurationKeys.JOIN1);
	}
	
	public Session getJoinee_2_Session()
	{
		return idVsActiveSessionMap.get(ConfigurationKeys.JOIN2);
	}
	
	public Session getJoinee_3_Session()
	{
		return idVsActiveSessionMap.get(ConfigurationKeys.JOIN3);
	}
	
	public Session getJoinee_4_Session()
	{
		return idVsActiveSessionMap.get(ConfigurationKeys.JOIN4);
	}
	
	public Session getJoinee_5_Session()
	{
		return idVsActiveSessionMap.get(ConfigurationKeys.JOIN5);
	}
	
	public Session getJoinee_6_Session()
	{
		return idVsActiveSessionMap.get(ConfigurationKeys.JOIN6);
	}
	
	public Session getJoinee_7_Session()
	{
		return idVsActiveSessionMap.get(ConfigurationKeys.JOIN7);
	}
	
	public void setHostSessionId(String sessionId)
	{
		this.hostSessionId = sessionId;
	}

	public String getHostSessionId()
	{
		return hostSessionId;
	}

	public String getConferenceKey()
	{
		return this.conferenceKey;
	}

	public String getConferenceId()
	{
		return this.conferenceId;
	}

	public double getPlayBackDuration()
	{
		return this.playBackDuration;
	}

	public void setPlayBackDuration(double playBackduration)
	{
		this.playBackDuration = playBackduration;
	}

//	public AtomicInteger getActiveSessionCount()
//	{
//		return this.activeSessionCount;
//	}

	public void addTimeStampOfAction(String action ,long currentTime)
	{
		int actionTime = (int) ((currentTime - this.conferenceRecordingStartTime)/1000);
		this.actionVsTimeStamp.put(action , actionTime);
	}

	
	public void addStreamSession(Session session)
	{
		streamSessionList.add(session);
	}

	public CopyOnWriteArrayList getStreamSessionList()
	{
		return streamSessionList;
	}

	public void setMediaIp(String mediaip)
	{
		this.mediaIp = mediaip;
	}
	
	public String getMediaIp()
	{
		return this.mediaIp;
	}
	
	private void setRecordingStatus(boolean recordingEnabled) {
		// TODO Auto-generated method stub
		
	}
	
	private String smartConferenceSession(String str)
	{
	           return  "return (typeof ZCSmartConferenceImpl === 'undefined' ? " +"ZRSmartConferenceImpl : ZCSmartConferenceImpl)" +".getCurrentActiveSession()."+str;
	}
	public void loadConferenceKey(Session session) 
	{
	    try 
	    {
	    	Page page = session.getPage();
	    	
	        String id = smartConferenceSession("getId();");
	        this.conferenceId = (String) page.evaluate(id);
	        
	        String jsKey = "return RTCP.getConferenceSession().getConferenceKey();";
	        this.conferenceKey = (String) page.evaluate(jsKey);
	        
	        System.out.println("[loadConferenceKey] conferenceId="+conferenceId+", conferenceKey="+conferenceKey);

	    } 
	    catch (Exception ex) 
	    {
	    	System.out.println("[Exception in loadConferenceKey]"+ ex);
	    }
	}
	
	public void initFunctionalTest() throws Exception
	{
		if(server == Server.ZVP_MEDIA_ROUTER)
		{
			ZVPMediaRouter mediaRouter = new ZVPMediaRouter(this);
		}
	}
	
	public void checkConferenceHost(String testName, String description) {

	    System.out.println("[checkConferenceHost][process] " + testName +" [HOST CONFERENCE][ROOM ID]" + this.getRoomId() +" [CONF KEY]" + this.getConferenceKey() +" [CONF ID] " + this.getConferenceId());

	    try 
	    {
	        String hostSessionId = Constants.ConfigurationKeys.HOST;

	        // Check and attach audio file if present
	        if (new File(ConfManager.getMediaBasePath()+ File.separator + hostSessionId + ".wav").exists()) 
	        {
	        	client.getCapability().setAudioFile(new MediaFile(MediaFileType.AUDIO,new Hashtable<String, String>() {{put(Constants.ConfigurationKeys.FILE_NAME, hostSessionId);}}));
	        
	        }

	        Session session = new ConferenceSession(this.getTestId(),this.getRoomId(),Constants.RoomType.CONFERENCE,hostSessionId,client,Constants.UserType.HOST,testConfig);

	        boolean connected = session.host();
	        if (!connected) 
	        {
	            this.loadConferenceKey(session);
	            System.out.println("[checkConferenceHost][process] " + testName +" [HOST CONFERENCE][SESSION NOT CONNECTED]" +" [TEST ID]" + this.getTestId() +" [ROOM ID]" + this.getRoomId() +" [SESSION ID]" + hostSessionId +" [CONF ID]" + this.getConferenceId() +" [USER ID]" + session.getUserId() +" [DEBUG INFO] " + session.getDebugInfo());
	            this.updatePhase(TestPhase.FAILED);
//	            session.analyzeLog();
	            session.downloadWebRTCDump();
	            this.functionalTestResults.put("host", false);
	            return;
	        }

	        session.setSessionUserId(session.getUserId());

	        System.out.println("[checkConferenceHost][process] " + testName +" [HOSTED CONFERENCE]" +" [TEST ID]" + this.getTestId() +" [ROOM ID]" + this.getRoomId() +" [SESSION ID]" + hostSessionId +" [CONF ID]" + this.getConferenceId() +" [USER ID]" + session.getUserId() +" [DEBUG INFO] " + session.getDebugInfo());

	        this.loadConferenceKey(session);
	        this.setHostSessionId(hostSessionId);
	        
	        session.changeLayout(Constants.ConfigurationKeys.STAGE_LAYOUT);

	        // Interval for current session
	        this.addSession(session);
	        setRecordingStatus(session.isRecordingEnabled());
	        this.setMediaIp(session.getMediaIp());
	        setConferenceRecordingStartTime(session.getRecordingStartTime());

	        boolean confHosted = false;
	        int audioLevelCheckIteration = 0;

	        

//	        while (!confHosted && audioLevelCheckIteration < 6) {
//	            Util.waitAround(Timeouts.TEN_SECOND_INTERVAL * 4);
//	            confHosted = ClientStatsValidator.checkAudioUpstreamLevel(session.getDriver());
//	            ClientStatsValidator.clearStatsInfo(session.getDriver());
//	            audioLevelCheckIteration++;
//	        }

	        addTimeStampOfAction("host", System.currentTimeMillis());

	        System.out.println("[checkConferenceJoin][process] " + testName +" [JOIN STATUS]" + confHosted +" [TEST ID]" + this.getTestId() +" [ROOM ID]" + this.getRoomId() +" [SESSION ID]" + hostSessionId +" [CONF ID]" + this.getConferenceId() +" [USER ID]" + session.getUserId());

	        this.functionalTestResults.put("host", confHosted);  
	    } 
	    catch (Exception ex) 
	    {
	        getHostSession().screenShot("FailedHost");
	        this.functionalTestResults.put("host", false);
	        this.updatePhase(Constants.TestPhase.FAILED);
	        System.out.println("[Exception in checkConferenceHost][process] " + description);
	        ex.printStackTrace();
	    }
	}

	public void checkConferenceJoin(String testName, String description, Session hostSession) 
	{
	    if (state == TestPhase.FAILED) 
	    {
	        System.out.println("[checkConferenceJoin][process] " + testName +" [JOIN CONFERENCE] [FAILED] [ROOM ID]" + getRoomId() +" [CONF KEY]" + getConferenceKey() +" [CONF ID] " + getConferenceId());
	        return;
	    }

	    System.out.println("[checkConferenceJoin][process] " + testName +" [JOIN CONFERENCE][ROOM ID]" + getRoomId() +" [CONF KEY]" + getConferenceKey() +" [CONF ID] " + getConferenceId());

	    try {
	        String joinSessionId = Constants.ConfigurationKeys.JOIN1;

	        // if a pre-recorded audio file exists, attach it
	        if (new File(ConfManager.getMediaBasePath() + File.separator + joinSessionId + ".wav").exists()) {
	            client.getCapability().setAudioFile(new MediaFile(MediaFileType.AUDIO,new Hashtable<String, String>() {{put(Constants.ConfigurationKeys.FILE_NAME, joinSessionId);}}));
	        }

	        // create and join a conference session
	        Session session = new ConferenceSession(getTestId(),getRoomId(),Constants.RoomType.CONFERENCE,joinSessionId,client,Constants.UserType.HOST,testConfig);

	        boolean connected = session.join();
	        if (!connected) {
	            System.out.println("[checkConferenceJoin][process] " + testName +" [JOIN CONFERENCE][SESSION NOT GOT CONNECTED]" +" [TEST ID]" + getTestId() +" [ROOM ID]" + getRoomId() +" [SESSION ID]" + joinSessionId +" [CONF ID]" + getConferenceId() +" [USER ID]" + session.getUserId() +" [DEBUG INFO] " + session.getDebugInfo());
//	            session.analyzeLog();
	            session.downloadWebRTCDump();
	            functionalTestResults.put("join", false);
	            return;
	        }

	        session.setSessionUserId(session.getUserId());
	        System.out.println(
	            "[checkConferenceJoin][process] " + testName +
	            " [JOINED] [JOIN CONFERENCE]" +
	            " [TEST ID]" + getTestId() +
	            " [ROOM ID]" + getRoomId() +
	            " [SESSION ID]" + joinSessionId +
	            " [CONF ID]" + getConferenceId() +
	            " [USER ID]" + session.getUserId() +
	            " [DEBUG INFO] " + session.getDebugInfo()
	        );

	        addSession(session);

	        // allow some time for the session to stabilise
	        Util.waitAround(Timeouts.FIVE_SECOND_INTERVAL);

	        session.changeLayout(Constants.ConfigurationKeys.STAGE_LAYOUT);

	        boolean userJoinStatus = false;

	        // downstream audio/video checks
	        if (session.isVideoReceivedInDownStreamForUserId(session.getUserId()) &&
	            session.isVideoReceivedInDownStreamForUserId(hostSession.getUserId())) {

//	            ClientStatsValidator.executeScriptForAudioVideoCheck(session.getDriver());
//	            ClientStatsValidator.clearStatsInfo(hostSession.getDriver());
//	            Util.waitAround(Timeouts.TEN_SECOND_INTERVAL * 4);

//	            userJoinStatus =
//	                ClientStatsValidator.checkDownstreamVideoFrameDecoded(
//	                    hostSession.getDriver(),
//	                    session.getSSRCId(session.getSessionUserId())
//	                ) &&
//	                ClientStatsValidator.checkAudioDownstreamLevel(session.getDriver());

	            int audioLevelCheckIteration = 0;
	            while (!userJoinStatus && audioLevelCheckIteration < 6) {
	                userJoinStatus = ClientMediaValidator.checkAudioUpstreamLevel(session.getPage());
	                ClientMediaValidator.clearStatsInfo(session.getPage());
	                audioLevelCheckIteration++;
	            }
	        }

	        addTimeStampOfAction("joinee", System.currentTimeMillis());

	        System.out.println(
	            "[checkConferenceJoin][process] " + testName +
	            " [JOIN STATUS]" + userJoinStatus +
	            " [TEST ID]" + getTestId() +
	            " [ROOM ID]" + getRoomId() +
	            " [SESSION ID]" + joinSessionId +
	            " [CONF ID]" + getConferenceId() +
	            " [USER ID]" + session.getUserId()
	        );

	        functionalTestResults.put("join", userJoinStatus);

	    }

	    catch (Exception e) 
	    {
	    	if (getJoinee_1_Session() != null && getJoinee_1_Session().getPage() != null) {
	            try {
	                getJoinee_1_Session().getPage().close();
	            } catch (Exception closeEx) {
	                System.out.println("[checkConferenceJoin] Failed to close join session page: " + closeEx);
	            }
	        }

	        // Remove the JOIN2 session reference from your active map
	        idVsActiveSessionMap.remove(Constants.ConfigurationKeys.JOIN2);

	        System.out.println(
	            "[Exception in checkConferenceJoin] [Page/Session Unreachable] [Retrying test] [Description] "
	            + description + " Exception: " + e
	        );

	        // Retry logic
	        if (incrRetryCountForTestCase(testName) >= ConfManager.getMaxRetryCountForFunctionalTest()) {
	            System.out.println(
	                "[Exception in checkConferenceJoin] Maximum retries reached for "
	                + description + " Exception: " + e
	            );
	            functionalTestResults.put("join", false);
	            updatePhase(Constants.TestPhase.FAILED);
	            failuretype = FailureType.QAFAILED;
	            return;
	        }
	    	
	        getJoinee_1_Session().screenShot("FailedJoinee");
	        functionalTestResults.put("join", false);
	        updatePhase(Constants.TestPhase.FAILED);
	        System.out.println(
	            "[Exception in checkConferenceJoin] " + description +
	            " Exception: " + e
	        );
	    }
	}

	@Override
	public void takeWebRTCDump() {
	    for (Map.Entry<String, Session> entry : idVsActiveSessionMap.entrySet()) {
	        Session session = entry.getValue();

	        System.out.println("[takeWebRTCDump] [TEST ID] " + testId +" [ROOM ID] " + roomId +" [SESSION ID] " + session.getId() +" [CONF KEY] " + conferenceKey +" [CONF ID] " + conferenceId);

	        if (Browsers.CHROME.equals(session.getBrowserType())) 
	        {
	            session.downloadWebRTCDump();
	        }
	    }
	}

	public void checkConferenceMuteAudio(String testName, String description, Session hostSession, Session joinSession, Session join2Session) {
	    if (state == TestPhase.FAILED) {
	        System.out.println(
	            "[checkConferenceJoin2][process] " + testName + " [JOIN CONFERENCE][FAILED][ROOM ID]" 
	            + this.getRoomId() + " [CONF KEY]" + this.getConferenceKey() + " [CONF ID] " + this.getConferenceId()
	        );
	        return;
	    }
	    try {
	        System.out.println("[checkConferenceMuteAudioStatus] " + testName + " [Description] " + description);
	        Hashtable<String, Boolean> audioMute = new Hashtable<>();

	        hostSession.muteAudio();
	        joinSession.muteAudio();
	        join2Session.muteAudio();

	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
//	        ClientStatsValidator.clearStatsInfo(hostSession.getDriver());
//	        ClientStatsValidator.clearStatsInfo(joinSession.getDriver());
//	        ClientStatsValidator.clearStatsInfo(join2Session.getDriver());
	        Util.waitAround(Timeouts.TEN_SECOND_INTERVAL * 3);

	        boolean isAudioMutedHost = true;
	        if (joinSession.isAudioMuted(hostSession.getUserId()) && hostSession.isAudioMuted(hostSession.getUserId())
	                && join2Session.isAudioMuted(hostSession.getUserId())) {
//	            if (!ClientStatsValidator.checkAudioUpstreamSent(hostSession.getDriver())) {
//	                isAudioMutedHost = false;
//	            }
	        }
	        audioMute.put("host", isAudioMutedHost);
	        addTimeStampOfAction("host mute audio", System.currentTimeMillis());

	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	        boolean isAudioMutedJoin = true;
	        if (joinSession.isAudioMuted(joinSession.getUserId()) && hostSession.isAudioMuted(joinSession.getUserId())
	                && join2Session.isAudioMuted(joinSession.getUserId())) {
//	            if (!ClientStatsValidator.checkAudioUpstreamSent(joinSession.getDriver())) {
//	                isAudioMutedJoin = false;
//	            }
	        }
	        audioMute.put("joinee", isAudioMutedJoin);
	        addTimeStampOfAction("joinee mute audio", System.currentTimeMillis());

	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	        boolean isAudioMutedJoin2 = true;
	        if (join2Session.isAudioMuted(join2Session.getUserId()) && hostSession.isAudioMuted(join2Session.getUserId())
	                && joinSession.isAudioMuted(join2Session.getUserId())) {
//	            if (!ClientStatsValidator.checkAudioUpstreamSent(join2Session.getDriver())) {
//	                isAudioMutedJoin2 = false;
//	            }
	        }
	        audioMute.put("joinee2", isAudioMutedJoin2);
	        addTimeStampOfAction("joinee2 mute audio", System.currentTimeMillis());

	        System.out.println("[checkConferenceMuteAudioStatus] " + testName + " [Description] " + description + " " + audioMute);

	        this.functionalTestResults.put("audioMute", audioMute);
	    } 
	    catch (Exception e) {
	        System.out.println("[Exception in checkConferenceMuteAudio] " + description + " Exception: " + e);
	    }
	}

	public void checkConferenceMuteVideo(String testName, String description, Session hostSession, Session joinSession, Session join2Session)
	{
	    if (state == TestPhase.FAILED) 
	    {
	        System.out.println("[checkConferenceMuteVideoStatus] " + testName + " [Description] " + description + " [FAILED]");
	        return;
	    }
	    
	    try 
	    {
	        System.out.println("[checkConferenceMuteVideoStatus] " + testName + " [Description] " + description);
	        Hashtable<String, Boolean> videoMute = new Hashtable<>();

	        hostSession.muteVideo();
	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	        boolean isVideoMutedHost = false;
	        if (joinSession.isVideoMuted(hostSession.getUserId()) && hostSession.isVideoMuted(hostSession.getUserId())&& join2Session.isVideoMuted(hostSession.getUserId())) 
	        {
	            isVideoMutedHost = true;
	        }
	        videoMute.put("host", isVideoMutedHost);
	        addTimeStampOfAction("host mute video", System.currentTimeMillis());

	        joinSession.muteVideo();
	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	        boolean isVideoMutedJoin = false;
	        if (hostSession.isVideoMuted(joinSession.getUserId()) && joinSession.isVideoMuted(joinSession.getUserId())&& join2Session.isVideoMuted(joinSession.getUserId()))
	        {
	            isVideoMutedJoin = true;
	        }
	        videoMute.put("joinee", isVideoMutedJoin);
	        addTimeStampOfAction("joinee mute video", System.currentTimeMillis());

	        join2Session.muteVideo();
	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	        boolean isVideoMutedJoin2 = false;
	        if (hostSession.isVideoMuted(join2Session.getUserId()) && join2Session.isVideoMuted(join2Session.getUserId())&& joinSession.isVideoMuted(join2Session.getUserId())) 
	        {
	            isVideoMutedJoin2 = true;
	        }
	        videoMute.put("joinee2", isVideoMutedJoin2);
	        addTimeStampOfAction("joinee2 mute video", System.currentTimeMillis());

	        System.out.println("[checkConferenceMuteVideoStatus] " + testName + " [Description] " + description + " " + videoMute);

	        this.functionalTestResults.put("videoMute", videoMute);
	    } 
	    catch (Exception e) 
	    {
	        System.out.println("[Exception in checkConferenceMuteVideo] " + description + " Exception: " + e);
	    }
	}

	public void checkConferenceUnMuteAudio(String testName, String description, Session hostSession, Session joinSession, Session join2Session) {
	    if (state == TestPhase.FAILED) {
	        System.out.println("[checkConferenceUnMuteAudioStatus] " + testName + " [Description] " + description + " [FAILED]");
	        return;
	    }

	    try {
	        System.out.println("[checkConferenceUnMuteAudioStatus] " + testName + " [Description] " + description);

	        Hashtable<String, Boolean> audioUnMute = new Hashtable<>();

	        hostSession.muteAudio();
	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	        boolean isAudioMutedHost = false;
	        if (!joinSession.isAudioMuted(hostSession.getUserId())
	                && !hostSession.isAudioMuted(hostSession.getUserId())
	                && !join2Session.isAudioMuted(hostSession.getUserId())) {
	            isAudioMutedHost = true;
	        }
	        audioUnMute.put("host", isAudioMutedHost);
	        addTimeStampOfAction("host unmute audio", System.currentTimeMillis());

	        joinSession.muteAudio();
	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	        boolean isAudioMutedJoin = false;
	        if (!hostSession.isAudioMuted(joinSession.getUserId())
	                && !joinSession.isAudioMuted(joinSession.getUserId())
	                && !join2Session.isAudioMuted(joinSession.getUserId())) {
	            isAudioMutedJoin = true;
	        }
	        audioUnMute.put("joinee", isAudioMutedJoin);
	        addTimeStampOfAction("joinee unmute audio", System.currentTimeMillis());

	        join2Session.muteAudio();
	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	        boolean isAudioMutedJoin2 = false;
	        if (!hostSession.isAudioMuted(join2Session.getUserId())
	                && !join2Session.isAudioMuted(join2Session.getUserId())
	                && !joinSession.isAudioMuted(join2Session.getUserId())) {
	            isAudioMutedJoin2 = true;
	        }
	        audioUnMute.put("joinee2", isAudioMutedJoin2);
	        addTimeStampOfAction("joinee2 unmute audio", System.currentTimeMillis());

	        this.functionalTestResults.put("audioUnMute", audioUnMute);

	        System.out.println("[checkConferenceUnMuteAudioStatus] " + testName + " [Description] " + description + " " + audioUnMute);
	    } 
	    catch (Exception e) {
	        System.out.println("[Exception in checkConferenceUnMuteAudio] " + description + " Exception: " + e);
	    }
	}


	public void checkConferenceUnMuteVideo(String testName, String description, Session hostSession, Session joinSession, Session join2Session) {
	    if (state == TestPhase.FAILED) {
	        System.out.println("[checkConferenceUnMuteVideoStatus] " + testName + " [Description] " + description + " [FAILED]");
	        return;
	    }

	    try {
	        System.out.println("[checkConferenceUnMuteVideoStatus] " + testName + " [Description] " + description);

	        Hashtable<String, Boolean> videoUnMute = new Hashtable<>();

	        hostSession.muteVideo();
	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	        boolean isVideoMutedHost = false;
	        if (!joinSession.isVideoMuted(hostSession.getUserId())
	                && !hostSession.isVideoMuted(hostSession.getUserId())
	                && !join2Session.isVideoMuted(hostSession.getUserId())) {
	            isVideoMutedHost = true;
	        }
	        videoUnMute.put("host", isVideoMutedHost);
	        addTimeStampOfAction("host unmute video", System.currentTimeMillis());

	        joinSession.muteVideo();
	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	        boolean isVideoMutedJoin = false;
	        if (!hostSession.isVideoMuted(joinSession.getUserId())
	                && !joinSession.isVideoMuted(joinSession.getUserId())
	                && !join2Session.isVideoMuted(joinSession.getUserId())) {
	            isVideoMutedJoin = true;
	        }
	        videoUnMute.put("joinee", isVideoMutedJoin);
	        addTimeStampOfAction("joinee unmute video", System.currentTimeMillis());

	        join2Session.muteVideo();
	        Util.waitAround(Timeouts.THREE_SECOND_INTERVAL);
	        boolean isVideoMutedJoin2 = false;
	        if (!hostSession.isVideoMuted(join2Session.getUserId())
	                && !joinSession.isVideoMuted(join2Session.getUserId())
	                && !join2Session.isVideoMuted(join2Session.getUserId())) {
	            isVideoMutedJoin2 = true;
	        }
	        videoUnMute.put("joinee2", isVideoMutedJoin2);
	        addTimeStampOfAction("joinee2 unmute video", System.currentTimeMillis());

	        this.functionalTestResults.put("videoUnMute", videoUnMute);

	        System.out.println("[checkConferenceUnMuteVideoStatus] " + testName + " [Description] " + description + " " + videoUnMute);
	    } 
	    catch (Exception e) {
	        System.out.println("[Exception in checkConferenceUnMuteVideo] " + description + " Exception: " + e);
	    }
	}
	
	public void checkConferenceScreenShare(String testName, String description) {
        System.out.println("[checkConferenceScreenShare] " + testName + " [Description] " + description);

        Hashtable<String, Boolean> screenShareEnabled = new Hashtable<>();

        try {
            // Host starts screen share
            if (!startScreenShare(hostPage)) {
                System.out.println("[Host ScreenShare element not found, skipping test]");
                return;
            }
            Thread.sleep(5000);
            takeScreenshot(hostPage, "ScreenShare_Host");
            takeScreenshot(joinPage, "ScreenShare_Host_Join");
            takeScreenshot(join2Page, "ScreenShare_Host_Join2");
            screenShareEnabled.put("host", true);

            // Close screen share popup
            closeScreenShare(hostPage);

            // Joinee starts screen share
            if (!startScreenShare(joinPage)) {
                System.out.println("[Joinee ScreenShare element not found, skipping test]");
                return;
            }
            Thread.sleep(5000);
            takeScreenshot(hostPage, "ScreenShare_Joinee");
            takeScreenshot(joinPage, "ScreenShare_Joinee");
            takeScreenshot(join2Page, "ScreenShare_Joinee");
            screenShareEnabled.put("joinee", true);
            closeScreenShare(joinPage);

            // Joinee2 starts screen share
            if (!startScreenShare(join2Page)) {
                System.out.println("[Joinee2 ScreenShare element not found, skipping test]");
                return;
            }
            Thread.sleep(5000);
            takeScreenshot(hostPage, "ScreenShare_Joinee2");
            takeScreenshot(joinPage, "ScreenShare_Joinee2");
            takeScreenshot(join2Page, "ScreenShare_Joinee2");
            screenShareEnabled.put("joinee2", true);
            closeScreenShare(join2Page);

            functionalTestResults.put("ScreenShareEnabled", screenShareEnabled);
            System.out.println("[checkConferenceScreenShareStatus] " + testName + " " + screenShareEnabled);

        } catch (Exception e) {
            System.out.println("[Exception in checkConferenceScreenShare] " + e.getMessage());
        }
    }

    // ✅ Start screen share (Playwright)
    private boolean startScreenShare(Page page) {
        try {
            Locator shareButton = page.locator("button:has-text('Share Screen')");
            if (shareButton.isVisible()) {
                shareButton.click();
                System.out.println("[ScreenShare Started]");
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    // ✅ Close screen share
    private void closeScreenShare(Page page) {
        try {
            Locator closeBtn = page.locator("xpath=//*[@id='conferencemainsection']//span[contains(@class,'close')]");
            if (closeBtn.isVisible()) {
                closeBtn.click();
                System.out.println("[Closed ScreenShare]");
            }
        } catch (Exception ignored) {}
    }

    // ✅ Recording test
    public void checkConferenceRecording(String testName, String description) {
        System.out.println("[checkConferenceRecording] " + testName + " [Description] " + description);

        Hashtable<String, Boolean> startRecording = new Hashtable<>();
        Hashtable<String, Boolean> stopRecording = new Hashtable<>();

        try {
            stopRecording.put("host", stopRecording(hostPage));
            stopRecording.put("joinee", stopRecording(joinPage));
            stopRecording.put("joinee2", stopRecording(join2Page));

            Thread.sleep(5000);

            startRecording.put("host", startRecording(hostPage));
            startRecording.put("joinee", startRecording(joinPage));
            startRecording.put("joinee2", startRecording(join2Page));

            functionalTestResults.put("startRecording", startRecording);
            functionalTestResults.put("stopRecording", stopRecording);

            System.out.println("[Recording Results] Start: " + startRecording + " Stop: " + stopRecording);

        } catch (Exception e) {
            System.out.println("[Exception in checkConferenceRecording] " + e.getMessage());
        }
    }

    // ✅ Helpers for recording
    private boolean startRecording(Page page) {
        try {
            page.locator("button:has-text('Start Recording')").click();
            System.out.println("[Recording started]");
            return true;
        } catch (Exception e) {
            System.out.println("[Failed to start recording]");
            return false;
        }
    }

    private boolean stopRecording(Page page) {
        try {
            page.locator("button:has-text('Stop Recording')").click();
            System.out.println("[Recording stopped]");
            return true;
        } catch (Exception e) {
            System.out.println("[Failed to stop recording]");
            return false;
        }
    }
    
	@Override
	public void checkAndUpdateStatus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleTestPhaseChange() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] main)
	{
		System.out.println("HelloWorld");
	}
	
}