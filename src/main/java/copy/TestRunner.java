package copy;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.microsoft.playwright.Page;

import copy.Constants.RoomType;
import copy.Constants.Server;
import copy.Constants.TestStatus;
import copy.Constants.TestType;

public class TestRunner {
	
	private String testId = null;
	private String testName = null;
	private String description = null;
	private TestStatus testStatus = null;
	private ArrayList<Client> clientList = null;
	private long startTime = -1;

	private long rampUpDelay = 0l;
	private int roomCount = 1;
	private int roomCapacity = 1;
	private TestType testType = null;
	private String serverType = null;
	private Server server = null;
	private RoomType roomType = RoomType.CONFERENCE;
	private Boolean streaming = false;
	private Boolean recording = false;
	private TestConfig config = null;
	
	private ConcurrentHashMap<String, Room> idVsRoomMap = new ConcurrentHashMap<String, Room>();

	public TestRunner(String id, TestConfig testConfig)
	{
		this.testId = id;
		this.testName = testConfig.getTestName();
		this.description = testConfig.getDescription();
		this.startTime = System.currentTimeMillis();
		this.clientList = testConfig.getClients();
		this.testType = testConfig.getTestType();
		this.streaming = testConfig.getEnableStreaming();
		this.recording = testConfig.getEnableRecording();
		this.roomType = testConfig.getRoomType();
		this.serverType = testConfig.getServerType();
		this.server = testConfig.getServer();
		

		if(testConfig.getRoomSize() > 0)
		{
			this.roomCapacity = testConfig.getRoomSize();
		}
		if(testType == TestType.STRESS)
		{ 
			this.rampUpDelay = testConfig.getRampUpDelay();
		}
		this.testStatus = TestStatus.STARTED;
		this.config = testConfig;
	}
	
	public String initiateTest()
	{
		String roomId = ""+System.currentTimeMillis();
		Client client = clientList.get(0);
		try {
			if(roomType == RoomType.CONFERENCE)
			{
				Room room = new ConferenceRoom(testName, testId, roomId, client, testType, config, server);
				
				((ConferenceRoom)room).setRoomCapacity(roomCapacity);
				if(testType == testType.FUNCTIONAL)
				{
					idVsRoomMap.put(roomId, room);
					room.initiateTest();
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
