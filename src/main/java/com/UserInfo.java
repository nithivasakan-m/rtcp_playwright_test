package com;

public class UserInfo {
	
    private String userName;
    private String userId;
    private String callId;
    private String callKey;
//    private String 
    // --- Constructors ---
    public UserInfo() {
    }

    public UserInfo(String userName, String userId, String callId, String callKey) {
        this.userName = userName;
        this.userId = userId;
        this.callId = callId;
        this.callKey = callKey;
    }

    // --- Getters & Setters ---
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCallKey() {
        return callKey;
    }

    public void setCallKey(String callKey) {
        this.callKey = callKey;
    }

    // --- toString (for easy printing) ---
    @Override
    public String toString() {
        return "UserInfo{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", callId='" + callId + '\'' +
                ", callKey='" + callKey + '\'' +
                '}';
    }
}
