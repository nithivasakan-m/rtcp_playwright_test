package Media;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import copy.Client;
import copy.Constants;
import copy.Room;
import copy.Session;
import copy.TestConfig;
import copy.Constants.FailureType;
import copy.Constants.Server;
import copy.Constants.TestPhase;
import copy.Constants.TestType;

public class ZVPMediaRouterConferenceRoom extends Room
{
	private String hostSessionId = "YET TO BE GENERATED"; //No I18N
	private String conferenceKey = "YET TO BE GENERATED"; //No I18N
	private String conferenceId = "YET TO BE GENERATED"; //No I18N
	private int roomCapacity = 10;

	private String mediaIp = "";
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
	
	public ZVPMediaRouterConferenceRoom(String testName, String testId, String roomId, Client client, TestType type, TestConfig config)
	{
		super(testName,testId, roomId, client, type, config);
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
			if(this.server == Server.ZVP_MEDIA_ROUTER)
			{
				
			}
		}
	}
	@Override
	public void takeWebRTCDump() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void checkAndUpdateStatus() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handleTestPhaseChange() {
		// TODO Auto-generated method stub
		
	}
}
