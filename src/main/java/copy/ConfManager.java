package copy;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Properties;

public class ConfManager {
	
	private static String serverHome = null;
	
	static
	{
		serverHome = System.getProperty("server.home");
	}
	
	private static int maxRetryCountForFunctionalTest = 2;
	private static int durationOnPreviewPage = 10*1000; //10 seconds
	
	private static Properties usernameConf;
	private static Properties passwordConf;
	
	private static String usernameConfFile = null;
	
	private static String userName = "";
	private static String password = "";
	
	private static String mediaBasePath = null;
	private static String httpsProtocol = "https://";//No I18N
	private static String conferenceHostBaseUrl = "/hostzvpqameetings.do?id=";
	private static String zvpqameetingUri = "/zvpqameetings.do?id=";//No I18N
	
	private static String datacenter = Constants.ConfigurationKeys.LOCAL;
	
	private static Hashtable<String, String> dcVSRtcpDomainsMap = new Hashtable<String, String>();
	private static Hashtable<String, String> dcVSZohoSubDomainMap = new Hashtable<String, String>();
	
	public static boolean initialize()
	{
		usernameConfFile = serverHome+File.separator+"conf"+File.separator+"username.properties";//No I18N
		
		usernameConf = getProperties(usernameConfFile);
		
		dcVSRtcpDomainsMap.put(Constants.ConfigurationKeys.LOCAL, "rtcplatform.localzoho.com");
		dcVSRtcpDomainsMap.put(Constants.ConfigurationKeys.PRE_LOCAL, "prertcplatform.localzoho.com");
		dcVSRtcpDomainsMap.put(Constants.ConfigurationKeys.PRE_US, "prertcplatform.zoho.com");
		dcVSRtcpDomainsMap.put(Constants.ConfigurationKeys.MAIN_IN, "rtcplatform.zoho.in");
		dcVSRtcpDomainsMap.put(Constants.ConfigurationKeys.MAIN_US, "rtcplatform.zoho.com");
		dcVSRtcpDomainsMap.put(Constants.ConfigurationKeys.PRE_IN, "prertcplatform.zoho.in");
		dcVSRtcpDomainsMap.put(Constants.ConfigurationKeys.MAIN_AR, "rtcplatform.arattai.in");
		
		dcVSZohoSubDomainMap.put(Constants.ConfigurationKeys.LOCAL, "localzoho.com");
		dcVSZohoSubDomainMap.put(Constants.ConfigurationKeys.PRE_US, "zoho.com");
		dcVSZohoSubDomainMap.put(Constants.ConfigurationKeys.MAIN_IN, "zoho.in");
		dcVSZohoSubDomainMap.put(Constants.ConfigurationKeys.MAIN_US, "zoho.com");
		dcVSZohoSubDomainMap.put(Constants.ConfigurationKeys.PRE_IN, "zoho.in");
		dcVSZohoSubDomainMap.put(Constants.ConfigurationKeys.MAIN_COM_AU, "zoho.com.au");
		dcVSZohoSubDomainMap.put(Constants.ConfigurationKeys.MAIN_JP, "zoho.jp");
		dcVSZohoSubDomainMap.put(Constants.ConfigurationKeys.MAIN_EU, "zoho.eu");
		dcVSZohoSubDomainMap.put(Constants.ConfigurationKeys.MAIN_COM_CN, "zoho.com.cn");
		dcVSZohoSubDomainMap.put(Constants.ConfigurationKeys.MAIN_AR, "arattai.in");
		
		return true;
	}
	
	public static String getRTCPDomainForDC(String dataCenter)
	{
		if(!Util.isNull(dataCenter))
		{
			return httpsProtocol + dcVSRtcpDomainsMap.get(dataCenter) + zvpqameetingUri;
		}
		return httpsProtocol + dcVSRtcpDomainsMap.get(datacenter) + zvpqameetingUri;
	}
	
	public static String getConferenceHostBaseUrl(String dataCenter)
	{
		if(!Util.isNull(dataCenter))
		{
			return httpsProtocol + dcVSRtcpDomainsMap.get(dataCenter) + conferenceHostBaseUrl;
		}
		return httpsProtocol + dcVSRtcpDomainsMap.get(datacenter) + conferenceHostBaseUrl;
	}
	
	public static String getUserName(String dataCenter)
	{
		return usernameConf.getProperty(dataCenter, userName);
	}
	
	public static String getPassword(String dataCenter)
	{
		return passwordConf.getProperty(dataCenter, password);
	}
	
	public static String getAccountsLoginUrl(String dataCenter)
	{
		return httpsProtocol + "accounts." + dcVSZohoSubDomainMap.get(dataCenter);//No I18N
	}
	
	public static String getMediaBasePath()
	{
		return mediaBasePath;
	}

	public static int getDurationOnPreviewPage()
	{
		return durationOnPreviewPage;
	}
	
	public static int getMaxRetryCountForFunctionalTest()
	{
		return maxRetryCountForFunctionalTest;
	}
	
	public static Properties getProperties(String propsFile)
	{
		try
		{
			System.out.println("Loading props "+propsFile);//No I18N
			Properties props = new Properties();
			props.load(new FileInputStream(propsFile));
			return props;
		}
		catch(Exception exp)
		{
			System.out.println("Unable to load conf file : "+propsFile+" "+exp);//No I8n
			return null;
		}
	}
	
}
