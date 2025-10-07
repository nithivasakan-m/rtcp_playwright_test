package copy;

import java.util.Hashtable;

import copy.Constants.Server;
import copy.Constants.TestPhase;
import copy.Constants.TestType;

public abstract class Room
{
	protected String testName = null;
	protected String description = null;
	protected String testId = null;
	protected String roomId = null;
	protected Client client = null;
	protected Session session = null;
	protected TestType testType = null;
	protected Server server = null;

	protected TestPhase state = TestPhase.INITIATED;

	protected long rampUpDelay = 0l;
	protected int roomCount = 0;
	protected TestConfig testConfig = new TestConfig(new Hashtable());
	
	
	public Room(String testName, String testId, String id, Client client, TestType type, TestConfig testConfig, Server server)
	{
		this.testName = testName;
		this.testId = testId;
		this.roomId = id;
		this.client = client;
		this.testType = type;
		this.testConfig = testConfig;
		this.server = server;
	}
	public void setRampUpDelay(long delay)
	{
		this.rampUpDelay = delay;
	}

	public String getTestId()
	{
		return this.testId;
	}
	public String getRoomId()
	{
		return this.roomId;
	}

	public TestPhase getPhase()
	{
		return this.state;
	}

	public TestType getTestType()
        {
                return this.testType;
        }

	public void updatePhase(TestPhase phase)
	{
		System.out.println("[updatePhase][ROOM ID]" + this.roomId + " [PHASE] "+phase);
		this.state = phase;
		if (this.testType != TestType.FUNCTIONAL)
		{
			handleTestPhaseChange();
		}
	}

	public TestConfig getTestConfig() {
	
		return this.testConfig;
	}

	public abstract void takeWebRTCDump();

	public abstract void initiateTest();

	public abstract void checkAndUpdateStatus();

	public abstract void handleTestPhaseChange();
}
