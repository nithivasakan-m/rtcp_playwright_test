package rooms;

import copy.Client;
import copy.TestConfig;
import copy.Constants.TestType;
import copy.Room;

public class ZVPRTCPConferenceRoom extends Room
{
	public ZVPRTCPConferenceRoom(String testName, String testId, String id, Client client, TestType type,TestConfig testConfig) 
	{
		super(testName, testId, id, client, type, testConfig);
	}

	@Override
	public void takeWebRTCDump() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initiateTest() {
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
