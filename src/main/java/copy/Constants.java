package copy;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	
	public enum Browsers {
		CHROME("chrome"),   //NO I18N
	    FIREFOX("firefox"),    //NO I18N
	    WEBKIT("safari"),     //NO I18N
	    OPERA("opera"),      //NO I18N
	    BRAVE("brave"),      //NO I18N
	    EDGE("edge"),
		ULAA("ulaa");//NO I18N
		
		private final String name;
		private static final Map<String, Browsers> NAME_BROWSER_MAP = new HashMap<String, Browsers>();

		static
		{
			for (Browsers browser : values())
			{
				NAME_BROWSER_MAP.put(browser.getName(), browser);
			}
		}

		private Browsers(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return this.name;
		}

		public static Browsers getBrowserbyName(String name)
		{
			return NAME_BROWSER_MAP.get(name);
		}
	}
	public enum Platform
	{
		MAC("MAC"), //NO I18N
		LINUX("LINUX"), //NO I18N
		WINDOWS("WINDOWS"); //NO I18N

		private final String name;
		private static final Map<String, Platform> NAME_PLATFORM_MAP = new HashMap<String, Platform>();

		static
		{
			for (Platform platform: values())
			{
				NAME_PLATFORM_MAP.put(platform.getName(), platform);
			}
		}

		private Platform(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return this.name;
		}

		public static Platform getPlatformbyName(String name)
		{
			return NAME_PLATFORM_MAP.get(name);
		}
	}
	public enum TestType
	{
		STRESS,
		FUNCTIONAL;
	}
	public enum TestStatus
	{
		STARTED,
		FAILED,
		COMPLETED;
	}
	public enum RoomType
	{
		ONE_TO_ONE,
		CONFERENCE;
	}

	public enum UserType
	{
		HOST,
		PARTICIPANT,
		SILENT_PARTICIPANT,
		VIEWER;
	}
	public enum Server
	{
		ZVP_RTCP,
		ZVP_MEDIA_ROUTER,
		ZVP_MEDIA,
		ZVP_MCU,
		ZVM_MSR,
		ZVP_TURN,
		ZVP_WSS,
		ZVP_RTMP;
	}
	public enum TestPhase
	{
		INITIATED,
		HOSTED,
		RAMPUP,
		RAMPUP_WAIT,
		ONGOING,
		LOADREACHED,
		FAILED, 
		COMPLETED,
		ENDING;
	}
	public enum FailureType
	{
		QAFAILED,
		CALLFAILED;
	}
	public enum DataCenter
	{
		US4("main-us"), //NO I18N
		LOCAL("main-local"), //NO I18N
		PRE_US4("pre-us"), //NO I18N
		PRE_LOCAL("pre-local"), //NO I18N
		MAIN_IN("main-in"), //NO I18N
		PRE_IN("pre-in"), //NO I18N
		MAIN_COM_AU("main-com-au"), //NO I18N
		MAIN_EU("main-eu"), //NO I18N
		MAIN_JP("main-jp"), //NO I18N
		MAIN_COM_CN("main-com-cn"); //NO I18N

		private final String dataCenter;
		private static final Map<String, DataCenter> DC_DATACENTER_MAP = new HashMap<String, DataCenter>();

		static
		{
			for(DataCenter dc : values())
			{
				DC_DATACENTER_MAP.put(dc.getType(), dc);
			}
		}

		private DataCenter(String dc)
		{
			this.dataCenter = dc;
		}

		public String getType()
		{
			return this.dataCenter;
		}

		public static String getDcbyDataCenter(String type)
		{
			return DC_DATACENTER_MAP.get(type).toString();
		}
	}
	public enum MediaFileType
	{
		VIDEO("video"),  //NO I18N        
		AUDIO("audio");  //NO I18N

		private final String mediaFileType;
		private static final Map<String, MediaFileType> TYPE_MEDIAFILETYPE_MAP = new HashMap<String, MediaFileType>();

		static 
		{
			for(MediaFileType type : values())
			{
				TYPE_MEDIAFILETYPE_MAP.put(type.getType(), type);
			}
		}

		private MediaFileType(String type)
		{
			this.mediaFileType = type;
		}

		public String getType()
		{
			return this.mediaFileType;
		}

		public MediaFileType getMediaFileTypeByType(String type)
		{
			return TYPE_MEDIAFILETYPE_MAP.get(type);
		}
	}
	public class Arguments
	{
		public static final String USE_FAKE_UI_FOR_MEDIA_STREAM = "use-fake-ui-for-media-stream"; //NO I18N
		public static final String USE_FAKE_DEVICE_FOR_MEDIA_STREAM = "use-fake-device-for-media-stream"; //NO I18N
		public static final String ENABLE_AUTOMATION = "enable-automation"; //NO I18N
		public static final String NO_SANDBOX = "no-sandbox"; //NO I18N
		public static final String DISABLE_INFOBARS = "disable-infobars"; //NO I18N
		public static final String DISABLE_DEV_SHM_USAGE = "disable-dev-shm-usage"; //NO I18N
		public static final String DISABLE_BROWSER_SIDE_NAVIGATION = "disable-browser-side-navigation"; //NO I18N
		public static final String DISABLE_GPU = "disable-gpu"; //NO I18N
		public static final String ALLOW_FILE_ACCESS_FROM_FILE = "allow-file-access-from-files"; //NO I18N
		public static final String USE_FILE_FOR_FAKE_VIDEO_CAPTURE = "use-file-for-fake-video-capture"; //NO I18N
		public static final String USE_FILE_FOR_FAKE_AUDIO_CAPTURE = "use-file-for-fake-audio-capture"; //NO I18N
		public static final String AUTO_SELECT_DESPKTOP_CAPTURE_SOURCE = "auto-select-desktop-capture-source"; //NO I18N
		public static final String GOOG_LOGGINGPREFS = "goog:loggingPrefs"; //NO I18N

		public static final String PREFS = "prefs"; //NO I18N
		public static final String HEADLESS = "headless"; //NO I18N
		public static final String WINDOW_SIZE = "window-size"; //NO I18N
		public static final String FAKEMEDIA = "fakeMedia"; //NO I18N                                                                                                                           
 

		public static final String MEDIA_NAVIGATOR_PERMISSION_DISABLED = "media.navigator.permission.disabled"; //NO I18N
		public static final String MEDIA_AUTOPLAY_ENABLED_USER_GESTURES_NEEDED = "media.autoplay.enabled.user-gestures-needed"; //NO I18N
		public static final String MEDIA_NAVIGATOR_STREAMS_FAKE = "media.navigator.streams.fake"; //NO I18N
		public static final String PROFILE_DEFAULT_CONSTENT_SETTINGS_POPUPS = "profile.default_content_settings.popups"; //NO I18N
		public static final String DOWNLOAD_EXTENSIONS_TO_OPEN = "download.extensions_to_open"; //NO I18N
		public static final String DOWNLOAD_DIRECTORY_UPGRADE = "download.directory_upgrade"; //NO I18N
		public static final String DOWNLOAD_PROMPT_FOR_DOWNLOAD = "download.prompt_for_download"; //NO I18N
		public static final String DOWNLOAD_DEFAULT_DIRECTORY = "download.default_directory"; //NO I18N
		public static final String SAVEFILE_DEFAULT_DIRECTORY = "savefile.default_directory"; //NO I18N
		public static final String SAFEBROWSING_ENABLE = "safebrowsing.enabled"; //NO I18N
		public static final String DOWNLOAD_BUBBLE_ENABLE = "download_bubble_enabled"; //NO I18N
		public static final String PROFILE_DEFAULT_CONTENT_SETTINGS_VALUES_AUTOMATIC_DOWNLOADS = "profile.default_content_setting_values.automatic_downloads"; //NO I18N
	
		public static final String START_MAXIMIZED = "start-maximized"; //NO I18N
		public static final String SAFEBROWSING_DISABLE_DOWNLOAD_PROTECTION = "--safebrowsing-disable-download-protection"; //NO I18N
		public static final String SAFEBROWSING_DISABLE_EXTENSION_BLACKLIST = "safebrowsing-disable-extension-blacklist"; //NO I18N
		public static final String WINDOW_SIZE_1920_1080 = "--window-size=1920,1080"; //NO I18N
		
		public static final String MICROPHONE = "microphone";
		public static final String CAMERA = "camera";
	}
	public class ConfigurationKeys
	{
		public static final String BROWSER_NAME = "browser_name";  //NO I18N
		public static final String BROWSER_VERSION = "browser_version";  //NO I18N
		public static final String PLATFORM = "platform"; //NO I18N
		public static final String BINARY_PATH = "binary_path"; //NO I18N
		public static final String CUSTOM_BINARY_PATH = "custom_binary_path"; //NO I18N
		public static final String HEADLESS = "headless"; //NO I18N
		public static final String FAKEMEDIA = "fakemedia"; //NO I18N
		public static final String VIDEO = "video"; //NO I18N
		public static final String AUDIO = "audio"; //NO I18N
		public static final String FILE_NAME = "file_name"; //NO I18N
		public static final String FILE_PATH = "file_path"; //NO I18N
		
		public static final String ROOM_TYPE = "roomtype";
		public static final String TEST_NAME = "testname";  //NO I18N
		public static final String DESCRIPTION = "description"; //NO I18N
		public static final String RAMP_UP_DELAY = "rampupdelay";
		public static final String MEETING_ID = "Meeting ID";
		public static final String TEST_TYPE = "testtype";  //NO I18N
		public static final String ROOM_SIZE = "roomsize"; //NO I18N
		public static final String ROOM_COUNT = "roomcount"; //NO I18N
		public static final String DATACENTER = "datacenter"; //NO I18N
		public static final String PRE_LOCAL = "pre-local"; //NO I18N
		public static final String LOCAL = "main-local"; //NO I18N
		public static final String PRE_US = "pre-us"; //NO I18N
		public static final String MAIN_US = "main-us"; //NO I18N
		public static final String MAIN_IN = "main-in"; //NO I18N
		public static final String PRE_IN = "pre-in"; //NO I18N
		public static final String MAIN_AR = "main-ar"; //NO I18N
		public static final String MAIN_COM_AU = "main-com-au"; //NO I18N
		public static final String MAIN_JP = "main-jp"; //NO I18N
		public static final String MAIN_COM_CN = "main-com-cn"; //NO I18N
		public static final String MAIN_EU = "main-eu"; //NO I18N
		public static final String SERVER = "server"; //NO I18N
		public static final String LOCATION = "location";//NO I18N
		public static final String LOCAL_DC = "local"; //NO I18N
		
		public static final String ACTIVE_SPEAKER_LAYOUT = "active-speaker-layout"; //NO I18N
		public static final String GRID_LAYOUT = "grid-layout";//NO I18N
		public static final String STAGE_LAYOUT = "stage-layout"; //NO I18N

		public static final String TRUE_SYMBOL = "✔️";//NO I18N
		public static final String FALSE_SYMBOL = "❌";//NO I18N
		public static final String JOINEXISTINGMEETING = "joinexistingmeeting";
		public static final String CLIENT = "client";
		public static final String HOST = "HOST";
		public static final String JOIN1 = "JOINEE 1";
		public static final String JOIN2 = "JOINEE 2";
		public static final String JOIN3 = "JOINEE 3";
		public static final String JOIN4 = "JOINEE 4";
		public static final String JOIN5 = "JOINEE 5";
		public static final String JOIN6 = "JOINEE 6";
		public static final String JOIN7 = "JOINEE 7";
		public static final String LS_VIEWER_COUNT = "lsviewercount"; //NO I18N
		public static final String STRESS = "stress"; //NO I18N
		public static final String FUNCTIONAL = "functional"; //NO I18N
		public static final String ONE_TO_ONE = "one_to_one";
		public static final String CONFERENCE = "conference";
		public static final boolean TRUE = true;
		public static final boolean FALSE = false;
		
	}
	
	public class ConferenceParameters
	{
		public static final String RECORDING = "recording"; //NO I18N
		public static final String PLAYBACK = "playback"; //NO I18N
		public static final String STREAMING = "streaming"; //NO I18N
		public static final String AND_SYMBOL = "&";//NO I18N
		public static final String EQUAL_SYMBOL = "=";//NO I18N
		public static final String PEER1 = "peer1";//NO I18N
		public static final String PEER2 = "peer2";//NO I18N
		public static final String MEETINGID = "meetingid";//NO I18N
	}

	public class Teams
	{
		public static final String ZVP_MEDIA = "ZVP-Media";
		public static final String MEDIA_ROTUER = "ZVP-MediaRouter";	
		public static final String MCUServer = "ZVP-MCU";
		public static final String MSRServer = "ZVP-MSR";
		public static final String TURNServer = "ZVP-Turn-c";
		public static final String RTMPServer = "ZVP-RTMP";
	}
}
