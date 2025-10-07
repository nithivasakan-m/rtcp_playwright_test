package copy;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ZVPMediaRouter {
	
	String testName = null;
	String description  = null;
	ConferenceRoom confRoom ;

	public ZVPMediaRouter(ConferenceRoom confRoom)
	{
		testName = confRoom.testName;
		description = confRoom.description;
		this.confRoom = confRoom;
	}
	
	
	public void hostStartMeeting()
	{
		confRoom.checkConferenceHost(testName, description);
	}
	public void setSpotlightUser()
	{
		confRoom.
	}
	public void removeSpotlightUser()
	{
		
	}
}
