import java.util.HashMap;
import java.util.Hashtable;

public class Config {
	
	static int LOAD_TEST_USER_COUNT = 10;
	
	static int userPerThread = 5;
	
	static String CALL_KEY;
	static String DOMAIN;
	
	private static int noOfParticipantsForFunctionalTest = 3;
	
	
    private final HashMap<String, String> domains = new HashMap<String, String>();
    private final HashMap<String,String[]> credencials = new HashMap<String,String[]>();
    private final HashMap<String , String> loginUrl = new HashMap<String,String>();
    {
    	domains.put("LOCAL", "https://rtcplatform.localzoho.com/zvpqameetings.do?id=");
    	domains.put("PRE-LOCAL", "https://prertcplatform.localzoho.com/zvpqameetings.do?id=");
    	domains.put("IDC","https://rtcplatform.zoho.com/zvpqameetings.do?id=");
    	domains.put("PRE-IDC", "https://prertcplatform.zoho.com/zvpqameetings.do?id=");
    	
    	credencials.put("LOCAL", new String[] {"nithivasakan.m+t1@zohotest.com","Nithitest"});
    	credencials.put("PRE-LOCAL", new String[] {"nithivasakan.m+t1@zohotest.com","Nithitest"});
    	credencials.put("PRE-IDC", new String[] {"mohammed.thanweer+2us@zohotest.com","RTCPlatform"});
    	credencials.put("IDC", new String[] {"mohammed.thanweer+2us@zohotest.com","RTCPlatform"});
    
    	loginUrl.put("LOCAL", "https://accounts.localzoho.com");
    	loginUrl.put("PRE-LOCAL", "https://accounts.localzoho.com");
    	loginUrl.put("IDC", "https://accounts.zoho.com");
    	loginUrl.put("PRE-IDC", "https://accounts.zoho.com");
    	
    }
	public Config(Hashtable config)
	{
		
	}
}
