package copy;

import java.util.ArrayList;
import java.util.Hashtable;

import copy.Constants.ConferenceParameters;
import copy.Constants.ConfigurationKeys;
import copy.Constants.RoomType;
import copy.Constants.Server;
import copy.Constants.TestType;

public class TestConfig {

    private String testName;
    private String description;
    private String serverType;
    private Server server;
    private int roomCount;
    private int roomSize;
    private long rampUpDelay;
    private String dataCenter;
    private String location;
    private String meetingId;
    private TestType testType;
    private RoomType roomType;
    private boolean enableRecording;
    private boolean enableStreaming;
    private int lsViewerCount;
    private boolean hostLoginEnabled;
    private boolean joinExistingMeeting;
    private ArrayList<Client> clients;

    // ---------- Constructor ----------
    public TestConfig(Hashtable<String, Object> data) {
        if (data != null && !data.isEmpty()) {
            constructTestConfig(data);
        } else {
            clients = new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private void constructTestConfig(Hashtable<String, Object> data) {
        System.out.println("constructTestConfig " + data);

        setTestName((String) data.getOrDefault(ConfigurationKeys.TEST_NAME, "Unnamed Test"));
        setDescription((String) data.getOrDefault(ConfigurationKeys.DESCRIPTION, "Manual QA"));

        setTestType(String.valueOf(data.getOrDefault(ConfigurationKeys.TEST_TYPE, ConfigurationKeys.FUNCTIONAL)));
        setRoomType(String.valueOf(data.getOrDefault(ConfigurationKeys.ROOM_TYPE, ConfigurationKeys.CONFERENCE)));

        setServer((Server)data.get(ConfigurationKeys.SERVER ));

        if (data.containsKey(ConfigurationKeys.ROOM_SIZE)) {
            setRoomSize((Integer)data.get(ConfigurationKeys.ROOM_SIZE));
        }
        if (data.containsKey(ConfigurationKeys.ROOM_COUNT)) {
            setRoomCount((Integer)(data.get(ConfigurationKeys.ROOM_COUNT)));
        }
        if(data.containsKey(ConfigurationKeys.RAMP_UP_DELAY))
		{
			setRampUpDelay(Long.parseLong(""+data.get(ConfigurationKeys.RAMP_UP_DELAY)));
		}
        setDataCenter((String) data.getOrDefault(ConfigurationKeys.DATACENTER, ""));
        setLocation((String) data.getOrDefault(ConfigurationKeys.LOCATION, ""));

        setEnableRecording(Boolean.parseBoolean(String.valueOf(data.getOrDefault(ConferenceParameters.RECORDING, false))));
        setEnableStreaming(Boolean.parseBoolean(String.valueOf(data.getOrDefault(ConferenceParameters.STREAMING, false))));
        setLsViewerCount(Integer.parseInt(String.valueOf(data.getOrDefault(ConfigurationKeys.LS_VIEWER_COUNT, 0))));
        setHostLoginEnabled(Boolean.parseBoolean(String.valueOf(data.getOrDefault(ConfigurationKeys.HOST, false))));
        setJoinExistingMeeting(Boolean.parseBoolean(String.valueOf(data.getOrDefault(ConfigurationKeys.JOINEXISTINGMEETING, false))));
        setMeetingId((String) data.getOrDefault(ConferenceParameters.MEETINGID, ""));

        // Create clients if present
       
       
            createClients((ArrayList<Hashtable>)data.get(ConfigurationKeys.CLIENT));
        
    }

    // ---------- Client creation ----------
    private void createClients(ArrayList<Hashtable> clientInfo)
	{
		if(clientInfo.size() > 0)
		{
			clients = new ArrayList<Client>(clientInfo.size());
		}

		for(Hashtable clientData : clientInfo)
		{
			this.clients.add(new Client(clientData));
		}
	}

    public ArrayList<Client> getClients() {
        return this.clients;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType; 
    }
    public String getServerType() {
        return serverType;
    }
    public void setServer(Server server)
    {
    	this.server = server;
    }
    public Server getServer()
    {
    	return server;
    }
    public Boolean getHostLoginEnabled() {
        return hostLoginEnabled;
    }
    public void setHostLoginEnabled(Boolean hostloginEnabled) {
        this.hostLoginEnabled = hostloginEnabled;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
    public String getTestName() {
        return testName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setTestType(String testType) {
        if (ConfigurationKeys.STRESS.equalsIgnoreCase(testType)) {
            this.testType = TestType.STRESS;
        } else {
            this.testType = TestType.FUNCTIONAL;
        }
    }
    public TestType getTestType() {
        return testType;
    }

    public void setRoomType(String roomType) {
        if (ConfigurationKeys.ONE_TO_ONE.equalsIgnoreCase(roomType)) {
            this.roomType = RoomType.ONE_TO_ONE;
        } else {
            this.roomType = RoomType.CONFERENCE;
        }
    }
    public RoomType getRoomType() {
        return roomType;
    }
    public void setRampUpDelay(long rampUpDelay)
    {
    	this.rampUpDelay = rampUpDelay;
    }
    public long getRampUpDelay()
    {
    	return this.rampUpDelay;
    }
    public void setRoomSize(int roomSize) {
        this.roomSize = roomSize;
    }
    public int getRoomSize() {
        return roomSize;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }
    public int getRoomCount() {
        return roomCount;
    }

    public void setDataCenter(String dataCenter) {
        this.dataCenter = dataCenter;
    }
    public String getDataCenter() {
        return dataCenter;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getLocation() {
        return location;
    }

    public void setEnableRecording(Boolean recording) {
        this.enableRecording = recording;
    }
    public Boolean getEnableRecording() {
        return enableRecording;
    }

    public void setEnableStreaming(Boolean streaming) {
        this.enableStreaming = streaming;
    }
    public Boolean getEnableStreaming() {
        return enableStreaming;
    }

    public void setLsViewerCount(int lsViewerCount) {
        this.lsViewerCount = lsViewerCount;
    }
    public int getLsViewerCount() {
        return lsViewerCount;
    }

    public Boolean getJoinExistingMeeting() {
        return joinExistingMeeting;
    }
    public void setJoinExistingMeeting(Boolean joinMeeting) {
        this.joinExistingMeeting = joinMeeting;
    }

    public String getMeetingId() {
        return meetingId;
    }
    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }
}
