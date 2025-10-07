package com;

import java.util.Map;

import rtcplatform.automation.Automation;

import java.util.HashMap;

public class Constants
{
	public static final String HOST = "HOST";
	public static final String CO_HOST = "CO HOST";
	public static final String PARTICIPANT = "PARTICIPANT";
	public static final String SILENT_PARTICIPANT = "SILENT PARTICIPANT";
	
	public static final String MEETING_URL = "https://prertcplatform.zoho.com/zvpqameetings.do?id=";
	public static final String WEBRTC_INTERNAL = "chrome://webrtc-internals";
	public static class LOGIN
	{
		public static final String LOGIN_ID_XPATH = "login_id";  // -- Login Page --
	    public static final String NEXT_BUTTON_XPATH = "nextbtn";
	    public static final String USERNAME_ERROR_XPATH = "//*[@id='getusername']/span/div[4]";
	    public static final String PASSWORD_XPATH = "password";
	    public static final String PASSWORD_ERROR_XPATH = "//*[@id='password_container']/div[2]/div[1]";
	}
	
	public static class PREVIEWPAGE
	{
		public static final String STARTMEETING_XPATH = "//*[@id=\"preview_room_window\"]/section/div[3]/div[5]/div/div[2]";
		public static final String JOINMEETING_XPATH = "//*[@id='preview_room_window']/section/div[3]/div[5]/div/div[2]";
		public static final String PREVIEW_CAMERA = "//*[@id='preview_room_window']/section/div[3]/div[4]/div[2]/div/div[1]/div[1]/label/span";
	    public static final String PREVIEW_AUDIO = "//*[@id='preview_room_window']/section/div[3]/div[4]/div[2]/div/div[2]/div[1]/label/span";
	}
	
	// -- meeting --
	public static class MEETING
	{
		
	}
    public static final String GENERATE_KEY = "//*[@id='conferencestartbutton']";
    public static final String CALL_KEY = "RTCPDemo.UIEvents._encConfereneKey";
    
    public static final String PARTICIPANTS_TAB_XPATH = "//*[@id='rhsparticipantsoption']/em";  // -- Common Xpaths --
    
    public static final String DOWN_ARROW_XPATH = "//*[@id='conference-assign-host-selection-result-cnt']/span[1]/span";
    public static final String NAME_SEARCH_BOX_XPATH = "//*[@id='conference-assign-host-selection-search-field']";
    public static final String NAME_CONTAINER_XPATH = "//*[@id='custom']/div/div[2]/div[1]";
    public static final String ASSIGN_AND_LEAVE_XPATH = "//*[@id='host_leave_confirm_cnt']/div/div[3]/div[2]";
    public static final String REACTION_XPATH = "//*[@id='smartconfreactionopt']";
    public static final String REQUEST_SPEAK_XPATH = "//*[@id='conferenceoptionset']/div[3]";
    public static final String RIGHT_HAND_XPATH = "//*[@id='conferencemainsection']/div[5]/div[4]/div";
    public static final String APPROVE_ALL_XPATH_1 = "//*[@id='notification-body']/div[2]/div[2]";
    public static final String APPROVE_ALL_XPATH_2 = "//*[@id='waiting_room_section']/div[1]/div/span[2]";
    public static final String REJECT_ALL_XPATH_1 = "//*[@id='notification-body']/div[2]/div[3]";
    public static final String REJECT_ALL_XPATH_2 = "//*[@id='waiting_room_section']/div[1]/div/span[1]";
    public static final String DISPLAY_NAME = "//*[@id='joineename']";   // -- Preview Page Xpath --
    public static final String MORE_OPTION = "//div[@purpose='showMoreOptions']";  // -- Screenshare --
    
    // -- view --
    public static class VIEW
    {
    	 public static final String MCU_VIEW = "//*[@id='layout_options']/div[1]";  // -- Views --
    	 public static final String STAGE_VIEW = "//*[@id='layout_options']/div[2]";      
    	 public static final String GRID_VIEW = "//*[@id='layout_options']/div[3]";
    	 public static final String ACTIVE_SPEAKER_VIEW = "";
    }
   
    public static final String CURRENT_SESSION = "ZRSmartConferenceImpl.getCurrentSession()";

    // -- screenshare --
    public static class SCREENSHARE
    {
    	public static final String SCREENSHARE_START = "//span[text()='Screen Share']";
        public static final String SCREENSHARE_CURRENT_USER_ID = "ZRSmartConferenceImpl.getCurrentActiveSession()._screenShareUserId";
        public static final String SCREENSHARE_CURRENT_USER_NAME = "ZRSmartConferenceImpl.getCurrentSession().getScreenSharer()._name";
        public static final String SCREENSHARE_STOP = "//span[text()='Stop screen share']";
        public static final String IS_SCREENSHARE_USER = "ZRSmartConferenceImpl.getCurrentSession().isScreenSharer()";
        public static final String SCREEN_DOWN_STREAM = "ZRSmartConferenceImpl.getCurrentActiveSession()._screeownStreamConnected";  // -- Check All Streams --
        public static final String SCREEN_UP_STREAM = "ZRSmartConferenceImpl.getCurrentActiveSession()._screenUpStreamConnected";
        public static final String SREENSHARER = "ZRSmartConferenceImpl.getCurrentSession().getScreenSharer()";
        public static final String SCREEN_DOWNSTREAM_CONNECTION = "ZRSmartConferenceImpl.getCurrentSession()._screenDownStreamConnection";
        public static final String SCREEN_DOWNSTREAM_RECONNECTING = "ZRSmartConferenceImpl.getCurrentSession()._screenDownStreamReconnecting";
        public static final String SCREEN_UPSTREAM_CONNECTED = "ZRSmartConferenceImpl.getCurrentSession()._screenUpStreamConnected";
        public static final String SCREEN_UPSTREAM_CONNECTION = "ZRSmartConferenceImpl.getCurrentSession()._screenUpStreamConnection";
        public static final String SCREEN_UPSTREAM_RECONNECTING = "ZRSmartConferenceImpl.getCurrentSession()._screenUpStreamReconnecting";
        public static final String SCREEN_DOWNSTREAM_CONNECTED = "ZRSmartConferenceImpl.getCurrentSession()._screenDownStreamConnected";
    }
    
    // -- recordings
    public static class RECORDING
    {
    	public static final String RECORDING_START = "//span[text()='Start Recording']";
    	public static final String RECORDING_STOP = "//span[text()='Stop Recording']";
    	
    }
    
    public static class PARTICIPANTS
    {
    	public static final String SHOW_ACTION_ON_MEMBER = "//div[@purpose='showActionsOnMember']";
    	public static final String ASK_TO_UNMUTE_AUDIO = "//div[@purpose='askUserToUnmuteAudio']";
    	public static final String ALLOW_USER_TO_UNMUTE_AUDIO = "//[@purpose='allowUserToUnmuteAudio']";
    	public static final String MUTE_USER_AUDIO = "//div[@purpose='muteUserAudio']";
    	public static final String OPEN_PARTICIPANTS_IN_RHS = "//span[@purpose='openParticipantsInRHS']";
    	
        public static final String SET_AS_CO_HOST = "//div[@purpose='setAsCohost']";
        public static final String SET_IN_SPOTLIGHT = "//div[@purpose='setInSpotLight']";
        public static final String RESTRICT_AUDIO_UNMUTE_FOR_USER = "//div[@purpose='restrictAudioUnmuteForUser']";
        public static final String SHOW_UPDATE_USER_ROLE = "//div[@purpose='showUpdateUserRole']";
        public static final String REMOVE_PARTICIPANT = "//div[@purpose='removeParticipant']";
        public static final String PIN_USER_VIDEO = "//div[@purpose='pinUserVideo']";

        // Dynamic locators
        public static String getUserRowByUserId(String userId) {
            return "//div[@category-id='Participant']//span[@elemtype='user' and @uid='" + userId + "']/ancestor::div[@category-id='Participant']";
        }

        public static String getShowActionsOnMemberByUserId(String userId) {
            return getUserRowByUserId(userId) + "//div[@purpose='showActionsOnMember']";
        }

    }
    // -- params --
    public static class PARAMS
    {
    	public static final String HOST = "host";
        public static final String JOINEE= "joinee";
        public static final String SILENTJOINEE = "silentjoinee";
        public static final String VIEWER = "viewer";
        public static final String PLAYBACK = "vodplayback";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String NODEURL = "nodeurl";

    }
    
    // -- session --
    public static class SESSION
    {
    	public static final String CONFERENCE_ID = "RTCP._conferenceId";
        public static final String ZUID = "RTCP._rtcpuserid";
        public static final String CONFERENCE_KEY = "RTCP.conferenceSession._conferencekey";
        public static final String SPOTLIGHT_USERS = "ZRSmartConferenceImpl.getCurrentActiveSession().getDataChannelData(false).syncData.spotlightusers";
        public static final String USER_VIEW = "ZRSmartConferenceImpl.getCurrentActiveSession().getCurrentLayout()";
        public static final String IP_PORT = "ZRSmartConferenceImpl._mediaIpList";
        public static final String IS_CURRENT_USER_HOST = "ZRSmartConferenceImpl.isCurrentUserHost()";
        public static final String IS_CURRENT_USER_CO_HOST = "ZRSmartConferenceImpl.isCurrentUserCohost()";
        public static final String MEDIA_SERVER_IP = "ZRSmartConferenceImpl._currentSession._mediaServerIp";
        public static final String GUEST_MEMBER = "ZRSmartConferenceImpl._currentSession._guestMembers";
        public static final String GRID_VIDEO_STREAM_IDS =  "ZRSmartConferenceImpl._currentSession._videoGridStreamIds";
        public static final String STAGE_VIDEO_STREAM_IDS = "ZRSmartConferenceImpl._currentSession._stageVideoStreamIds";
        public static final String SESSION_ID = "ZRSmartConferenceImpl._currentSession._sessionId";
        public static final String HOST_NAME = "ZRSmartConferenceImpl.getCurrentSession()._hostName";
    }
    
   
    // -- Cookies --
    public static class IAMCOOKIES
    {
	    public static final String _IAMBDT = "_iambdt";
	    public static final String _ZCSR_TMP = "_zcsr_tmp";
	    public static final String COM_CHAT_OWNER = "com_chat_owner";
	    public static final String CONCSR = "concsr";
	    public static final String DIGITALMARKETING__ZLDP = "digitalmarketing-_zldp";
	    public static final String JSESSIONID = "JSESSIONID";
	    public static final String RTCPLATFORM_CSRF_TOKEN = "RTCPLATFORM_CSRF_TOKEN";
	    public static final String WMS_TKP_TOKEN = "wms-tkp-token";
	    public static final String X_CLIENT_ACCESS_TOKEN = "x-client-access-token";
	    public static final String ZAB_USER_ID = "zabUserId";
	    public static final String ZALB_2787B01083 = "zalb_2787b01083";
	    public static final String ZALB_EE60B7108B = "zalb_ee60b7108b";
	    public static final String ZOHOCARES_UUID = "zohocares-_uuid";
	    public static final String ZOHOCARES_ZLDP = "zohocares-_zldp";
	    public static final String ZOHOCLIQSUPPORT_ZLDP = "zohocliqsupport-_zldp";
	    public static final String ZOHOCLIQSUPPORT_ZLDT = "zohocliqsupport-_zldt";
	    public static final String ZPS_TGR_DTS = "zps-tgr-dts";
	    public static final String ZUSERLANG = "zuserlang";
    }

    //											-- Video Streams --
    // -- video --
    public static class VIDEO
    {
    	public static final String VIDEO_UP_STREAM_CONNECTED = "ZRSmartConferenceImpl.getCurrentActiveSession()._videoUpStreamConnected";
	    public static final String VIDEO_DOWN_STREAM_CONNECTED = "ZRSmartConferenceImpl.getCurrentActiveSession()._videoDownStreamConnected";
	    public static final String CURRENT_SESSION_VIDEO_MUTED = "ZRSmartConferenceImpl.getCurrentActiveSession().isVideoMuted()";
	    public static final String VIDEO_MUTED = "ZRSmartConferenceImpl._joiningSession.isVideoMuted()";
	    public static final String VIDEO_UPSTREAM_CONNECTION = "ZRSmartConferenceImpl.getCurrentSession()._videoUpStreamConnection";
	    public static final String VIDEO_DOWNSTREAM_CONNECTION = "ZRSmartConferenceImpl.getCurrentSession()._videoDownStreamConnection";
	    public static final String VIDEO_DOWNSTREAM_RECONNECTING = "ZRSmartConferenceImpl.getCurrentSession()._videoDownStreamReconnecting";
	    public static final String VIDEO_UPSTREAM_CONNECTED = "ZRSmartConferenceImpl.getCurrentSession()._videoUpStreamConnected";
	    public static final String VIDEO_DOWNSTREAM_CONNECTED = "ZRSmartConferenceImpl.getCurrentSession()._videoDownStreamConnected";
	    public static final String VIDEO_UP_STREAM_BLOCKED = "ZRSmartConferenceImpl.getCurrentSession().isVideoUpStreamBlocked()";
	    public static final String VIDEO_UPSTREAM_RECONNECTING = "ZRSmartConferenceImpl.getCurrentSession()._videoUpStreamReconnecting";
	    
	    public static final String LOW_VIDEO_RESOLUTION = "AVQuality.Video.setLDVideoResolution()";    // -- Video Quality --
	    public static final String HIGH_VIDEO_RESOLUTION = "AVQuality.Video.setHDVideoResolution()";
	    public static final String FULL_VIDEO_RESOLUTION = "AVQuality.Video.setFHDVideoResolution()";

    
    }
   
    // -- audio --
    
    public static class AUDIO{
    	
	    public static final String AUDIO_UP_STREAM = "ZRSmartConferenceImpl.getCurrentActiveSession()._audioUpStreamConnected";
	    public static final String AUDIO_DOWN_STREAM = "ZRSmartConferenceImpl.getCurrentActiveSession()._audioDownStreamConnected";
	    public static final String AUDIO_MUTED = "ZRSmartConferenceImpl.getCurrentActiveSession().isAudioMuted()";
	    public static final String AUDIO_MUTED_PREVIEW = "ZRSmartConferenceImpl._joiningSession.isAudioMuted()";
	    public static final String WAITING_USERS_LIST = "ZRSmartConferenceImpl.getCurrentActiveSession()._waitingUsersList";  //  -- Participants List --
	    public static final String HOST_NAME = "ZRSmartConferenceImpl.getCurrentSession()._hostName";
	    public static final String AUDIO_DOWNSTREAM_CONNECTION = "ZRSmartConferenceImpl.getCurrentSession()._audioDownStreamConnection";
	    public static final String AUDIO_DOWNSTREAM_RECONNECTING = "ZRSmartConferenceImpl.getCurrentSession()._audioDownStreamReconnecting";
	    public static final String AUDIO_UPSTREAM_CONNECTED = "ZRSmartConferenceImpl.getCurrentSession()._audioUpStreamConnected";
	    public static final String AUDIO_DOWNSTREAM_CONNECTED = "ZRSmartConferenceImpl.getCurrentSession()._audioDownStreamConnected";
	    public static final String AUDIO_UPSTREAM_RECONNECTING = "ZRSmartConferenceImpl.getCurrentSession()._audioUpStreamReconnecting";
	    public static final String AUDIO_UPSTREAM_CONNECTION = "ZRSmartConferenceImpl.getCurrentSession()._audioUpStreamConnection";
	    public static final String AUDIO_UP_STREAM_BLOCKED = "ZRSmartConferenceImpl.getCurrentSession().isAudioUpStreamBlocked()";
	    public static final String AUDIO_MODE = "ZRSmartConferenceImpl.getCurrentSession().getAudioMode()";
	    public static final String AUDIO_BLOCKED = "ZRSmartConferenceImpl.getCurrentSession()._audioUpStreamBlocked";
    }
    
    
    
    public static final String VIDEO_BLOCKED = "";
    
   
    public static final String DEFAULT_AUDIO_UP_STREAM = null;
    public static final String RAW_MONO_AUDIO_UP_STREAM = null;
    public static final String RAW_STEREO_AUDIO_UP_STREAM = null;
    public static final String SCREEN_UP_STREAM_DUPLICATE = null;
   
    // Stream Status Flags

    public static class DATACHANNEL
    {
    	public static final String DC_RECONNECTING = "ZRSmartConferenceImpl.getCurrentSession()._dcReconnecting";
    	public static final String IS_DATA_CHANNEL_CONNECTED = "ZRSmartConferenceImpl.getCurrentSession()._isDataChannelConnected";
    	
    }
    
    

}