package Media;
import java.util.ArrayList;
import java.util.Hashtable;

import java.util.Random;

import com.ActionController;
import copy.Constants;
import com.PlaywrightFactory;
import com.PlaywrightUtils;
import com.microsoft.playwright.Page;

import copy.TestConfig;
import copy.Constants.ConferenceParameters;
import copy.Constants.ConfigurationKeys;
import copy.Constants.Teams;

import com.PlaywrightFactory;
public class Test {

	public static String url = "https://prertcplatform.zoho.com/zvpqameetings.do?id=";
	public static Hashtable<String,ActionController> users = new Hashtable<String, ActionController>();
	public static int getDigit()
	{
		Random random = new Random();
        int sixDigit = 100000 + random.nextInt(900000); // ensures 6 digits
        System.out.println("Random 6-digit number: " + sixDigit);
        return sixDigit;
	}
	public static void waitFor(int t)
	{
		try {Thread.sleep(t);}catch(Exception e) {}
	}
	public static void main(String[] args)
	{
		Hashtable config = new Hashtable();
		config.put(ConfigurationKeys.TEST_NAME,Teams.ZVP_MEDIA);
        config.put(ConfigurationKeys.DESCRIPTION, "Demo Testing");
        config.put(ConfigurationKeys.SERVER, Teams.ZVP_MEDIA);
        config.put(ConfigurationKeys.TEST_TYPE, ConfigurationKeys.FUNCTIONAL);
        config.put(ConfigurationKeys.ROOM_COUNT, 1);
        config.put(ConfigurationKeys.ROOM_SIZE, 7);
        config.put(ConfigurationKeys.DATACENTER,ConfigurationKeys.LOCAL);
        config.put(ConfigurationKeys.LOCATION, null);  // enum TestType
        config.put(ConfigurationKeys.ROOM_TYPE ,ConfigurationKeys.CONFERENCE);   // enum RoomType
        config.put(ConferenceParameters.RECORDING,ConfigurationKeys.TRUE);
        config.put(ConferenceParameters.STREAMING, ConfigurationKeys.TRUE);
        config.put(ConfigurationKeys.LS_VIEWER_COUNT, 2);
        config.put(ConfigurationKeys.HOST, ConfigurationKeys.FALSE);
        config.put(ConfigurationKeys.JOINEXISTINGMEETING,  ConfigurationKeys.FALSE);
        
        Hashtable clientInfo = new Hashtable();
        clientInfo.put(ConfigurationKeys.BROWSER_NAME, "chrome");
		clientInfo.put(ConfigurationKeys.BROWSER_VERSION, "123_0_6312_122");
		clientInfo.put(ConfigurationKeys.PLATFORM, "MAC");	
		clientInfo.put(ConfigurationKeys.BINARY_PATH, "/Users/nithi-22537/selenium_drivers/chromium_131_0_6778_85/chrome-mac-x64/Google_Chrome_For_Testing.app/Contents/MacOS/Google_Chrome_For_Testing");
		clientInfo.put(ConfigurationKeys.HEADLESS, false);
		clientInfo.put(ConfigurationKeys.FAKEMEDIA, true);
		config.put(ConfigurationKeys.CLIENT, new ArrayList<Hashtable>().add(clientInfo));
		
       TestConfig testConfig = new TestConfig(config);

	}
}
