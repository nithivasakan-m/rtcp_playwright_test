package rtcplatform.automation;
import java.io.FileInputStream;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.adventnet.wms.common.HttpConnection;

import com.adventnet.wms.common.HttpDataWraper;

public class ConferenceKey 
{
    private static final String FALSE = "false";
    private static final String OPR = "opr";
    private static final String CREATEKEY = "createkey";
    private static final String RECORDINGENABLED = "recordingenabled";
    private static final String STREAMINGENABLED = "streamingenabled";
    private static final String OVERSPILL = "overspill";
    private static final String BROLLENABLED = "brollenabled";
    private static final String BROADCASTINGENABLED = "broadcastenabled";
    private static final String TITLE = "title";
    private static final String WAITINGROOMENABLED = "waitingroomenabled";
    private static final String CONFTYEP = "conftype";
    private static final String VIDEO = "video";
    private static final String ISMIXEDVIEW = "ismixedview";
    private static final String SINGLEVIDEOCONFENABELD = "singlevideoconfenabeld";
    private static final String COOKIE = "Cookie";
    private static Properties properties;

    public static Logger LOGGER = Logger.getLogger(ConferenceKey.class.getName());
    
    public static Properties loadProperty(String filePath) {
        properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
        	properties.load(fileInputStream);
            LOGGER.log(Level.INFO, "Properties loaded successfully from " + filePath);
            return  ConferenceKey.properties = properties;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load properties file", e);
            return null;
        }
    }

    private static String generateConferenceKey(String COOKIE,String TITLE ,String DOMAIN ,String WAITINGROOMENABLED ,String ISMIXEDVIEW ,String RECORDINGENABLED ,String STREAMINGENABLED)
    {
        Hashtable<String , String> domains = new Hashtable<String , String>();
        Hashtable<String, String> params = new Hashtable<String, String>();
        domains.put("local-main","rtcplatform.localzoho");
        domains.put("pre-local","prertcplatform.localzoho");
        domains.put("pre-idc","prertcplatform.zoho");
        domains.put("idc","rtcplatform.zoho");

        if(TITLE == null || TITLE.isEmpty())
            TITLE = "DEMO TESTING";
        params.put(ConferenceKey.TITLE,TITLE);
        params.put(ConferenceKey.OPR,CREATEKEY);
        params.put(ConferenceKey.WAITINGROOMENABLED,WAITINGROOMENABLED+"");
        params.put(ConferenceKey.RECORDINGENABLED,RECORDINGENABLED+"");
        params.put(ConferenceKey.ISMIXEDVIEW,ISMIXEDVIEW+"");
        params.put(ConferenceKey.STREAMINGENABLED,STREAMINGENABLED+"");
        params.put(ConferenceKey.OVERSPILL,ConferenceKey.FALSE);
        params.put(ConferenceKey.BROLLENABLED,ConferenceKey.FALSE);
        params.put(ConferenceKey.BROADCASTINGENABLED,ConferenceKey.FALSE);
        params.put(ConferenceKey.CONFTYEP,ConferenceKey.VIDEO);
        params.put(ConferenceKey.SINGLEVIDEOCONFENABELD,ConferenceKey.FALSE);

        HttpConnection connection = null;
        try{
            connection = new HttpConnection("https://"+domains.get(DOMAIN)+".com/rtcpdemoaction.do");
            Hashtable<String , String> header = new Hashtable<String , String>();
            header.put(ConferenceKey.COOKIE,COOKIE);
            List<?> response = (ArrayList<?>)connection.doGet(params,header);
            Hashtable<?, ?> confKey = (Hashtable<?, ?>)HttpDataWraper.getObject(((Hashtable<?, ?>)response.get(0)).get("objString").toString());
            return confKey.get("confkey").toString();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
			
		}
        
    }
    public static String getConferenceKey(String filePath)
    {
        loadProperty(filePath);
        String cookies = properties.getProperty("cookies");
        String waitingRoom = properties.getProperty("waitingRoom");
        String streaming = properties.getProperty("streaming");
        String recording = properties.getProperty("recording");
        String title = properties.getProperty("title");
        String domain = properties.getProperty("domain");
        String mixedView = properties.getProperty("mixedview");
        return generateConferenceKey(cookies,title,domain,waitingRoom,mixedView,recording,streaming);
    }
    public static Properties getProperty()
    {
        return properties;
    }
}