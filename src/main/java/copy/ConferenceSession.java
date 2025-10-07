package copy;

import java.nio.file.Paths;


import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import copy.Constants.ConferenceParameters;
import copy.Constants.RoomType;
import copy.Constants.UserType;

public class ConferenceSession extends Session{
	
	public ConferenceSession(String testId, String roomId, RoomType roomType, String sessionId, Client client, UserType type, TestConfig config) throws Exception
	{
		super(testId, roomId, roomType, sessionId, client, type, config);
	}
    // Host a conference
    public boolean host() {
        try {
            String roomUrl;
            if (testConfig.getHostLoginEnabled()) {
                roomUrl = ConfManager.getConferenceHostBaseUrl(testConfig.getDataCenter())
                    + this.roomId
                    + ConferenceParameters.AND_SYMBOL + ConferenceParameters.RECORDING + ConferenceParameters.EQUAL_SYMBOL + testConfig.getEnableRecording()
                    + ConferenceParameters.AND_SYMBOL + ConferenceParameters.STREAMING + ConferenceParameters.EQUAL_SYMBOL + testConfig.getEnableStreaming();

                boolean isLoggedIn = userLogin(testConfig.getDataCenter());
                if (!isLoggedIn) {
                    page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("login_failed.png")));
                    System.out.println("[host][LOGIN FAILED] continuing without login");
                    roomUrl = ConfManager.getRTCPDomainForDC(testConfig.getDataCenter())
                        + this.roomId
                        + ConferenceParameters.AND_SYMBOL + ConferenceParameters.RECORDING + ConferenceParameters.EQUAL_SYMBOL + testConfig.getEnableRecording()
                        + ConferenceParameters.AND_SYMBOL + ConferenceParameters.STREAMING + ConferenceParameters.EQUAL_SYMBOL + testConfig.getEnableStreaming();
                }
            } else {
                roomUrl = ConfManager.getRTCPDomainForDC(testConfig.getDataCenter())
                    + this.roomId
                    + ConferenceParameters.AND_SYMBOL + ConferenceParameters.RECORDING + ConferenceParameters.EQUAL_SYMBOL + testConfig.getEnableRecording()
                    + ConferenceParameters.AND_SYMBOL + ConferenceParameters.STREAMING + ConferenceParameters.EQUAL_SYMBOL + testConfig.getEnableStreaming();
            }

            page.navigate(roomUrl);
            page.waitForTimeout(3000);

            if (roomType == RoomType.CONFERENCE) {
                Locator hostBtn = page.locator("//*[@id=\"preview_room_window\"]/section/div[3]/div[5]/div/div[2]");
//                page.waitForTimeout(ConfManager.getDurationOnPreviewPage());
                hostBtn.click();
                page.waitForTimeout(3000);
                page.locator("//*[@id=\"conferenceoptionset\"]").waitFor();
                System.out.println("[host][WAITING TO GET CONNECTED]");
                page.waitForTimeout(3000);
                return waitUntilConnected();
            }
        } catch (Exception ex) {
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("exception_in_host.png")));
            System.out.println("[Exception in host] " + ex.getMessage());
            return false;
        }
        return false;
    }

    public boolean join() 
    {
        try 
        {
        	System.out.println("[join] starting...");

            String roomUrl = ConfManager.getRTCPDomainForDC(testConfig.getDataCenter()) + this.roomId;
            if (testConfig.getJoinExistingMeeting()) 
            {
                roomUrl = ConfManager.getRTCPDomainForDC(testConfig.getDataCenter()) + testConfig.getMeetingId();
                System.out.println("[join][EXISTING MEETING]");
            }

            page.navigate(roomUrl);
            page.waitForTimeout(3000);
            page.locator("//*[@id=\"preview_room_window\"]").waitFor();

            if (roomType == RoomType.CONFERENCE) {
                Locator joinBtn = page.locator("//*[@id=\"preview_room_window\"]/section/div[3]/div[5]/div/div[2]");
                if (this.isRecordingEnabledOnPreviewPage()) {
                    joinBtn = page.locator("//*[@id='preview_room_window']/section/div[3]/div[6]/div/div[2]");
                }

                try {
                    page.waitForTimeout(ConfManager.getDurationOnPreviewPage());
                    joinBtn.waitFor();
                } catch (Exception e) {
                    page.reload();
                    page.waitForTimeout(ConfManager.getDurationOnPreviewPage());
                    joinBtn.waitFor();
                }

                joinBtn.click();
                page.waitForTimeout(1000);
                System.out.println("[join][WAITING TO GET CONNECTED]");
                if (waitUntilConnected()) {
                    try {
                        page.locator("//*[@id=\"smartconf_main_container\"]").waitFor();
                        System.out.println("[join][CONNECTED]");
                        return true;
                    } catch (Exception e) {
                        System.out.println("[join][FAILED TO FIND CONFERENCE OPTION ELEMENT]");
                        return false;
                    }
                }
                return false;
            }
        } catch (Exception ex) {
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("exception_in_join.png")));
            System.out.println("[Exception in join] " + ex.getMessage());
        }
        return false;
    }

    // Join with audio muted
    public boolean joinWithAudioMuted() {
        try {
            String roomUrl = ConfManager.getRTCPDomainForDC(testConfig.getDataCenter()) + this.roomId;
            page.navigate(roomUrl);
            page.waitForTimeout(3000);
            page.locator("//*[@id=\"preview_room_window\"]").waitFor();

            if (roomType == RoomType.CONFERENCE) {
                Locator joinBtn = page.locator("//*[@id=\"preview_room_window\"]/section/div[3]/div[5]/div/div[2]");
                if (this.isRecordingEnabledOnPreviewPage()) {
                    joinBtn = page.locator("//*[@id='preview_room_window']/section/div[3]/div[6]/div/div[2]");
                }

                try {
//                    page.waitForTimeout(ConfManager.getDurationOnPreviewPage());
                    joinBtn.waitFor();
                } catch (Exception e) {
                    page.reload();
//                    page.waitForTimeout(ConfManager.getDurationOnPreviewPage());
                    joinBtn.waitFor();
                }

                // Mute audio before joining
                page.locator("//*[@id=\"preview_room_window\"]/section/div[3]/div[4]/div[2]/div/div[2]/div[1]/label/span").click();
                joinBtn.click();

                page.waitForTimeout(1000);
                System.out.println("[joinWithAudioMuted][WAITING TO GET CONNECTED]");

                if (waitUntilConnected()) {
                    try {
                        page.locator("//*[@id=\"smartconf_main_container\"]").waitFor();
                        System.out.println("[joinWithAudioMuted][CONNECTED]");
                        return true;
                    } catch (Exception e) {
                        System.out.println("[joinWithAudioMuted][FAILED TO FIND CONFERENCE OPTION ELEMENT]");
                        return false;
                    }
                }
                return false;
            }
        } catch (Exception ex) {
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("exception_in_joinmuted.png")));
            System.out.println("[Exception in joinWithAudioMuted] " + ex.getMessage());
        }
        return false;
    }

    // Leave meeting
    public boolean leave() {
        try {
            page.locator(".smartconf-btn_end").click();
            return true;
        } catch (Exception ex) {
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("exception_in_leave.png")));
            System.out.println("[Exception in leave] " + ex.getMessage());
        } finally {
            try {
                page.waitForTimeout(3000);
                page.context().browser().close();
            } catch (Exception e) {
                System.out.println("[Exception in leave] Tried Action after browser closed");
            }
        }
        return false;
    }
 // Rename the host
    public void hostRename(String hostName) {
        try {
            Locator input = page.locator("#joineename");
            input.fill("");            // clear existing text
            input.fill(hostName);      // set new host name
        } catch (Exception ex) {
            System.out.println("[Exception in Host Rename] " + ex.getMessage());
        }
    }

    // End a meeting
    public boolean end(String conferenceId) {
        try {
            Locator leaveBtn = page.locator(".smartconf-btn_end");
            Locator endBtn = page.locator("//*[@id='" + conferenceId + "end_dropdowncnt']/div/div[1]");

            leaveBtn.click();
            try {
                endBtn.click();
            } catch (Exception e) {
                System.out.println("[End Button Not Found] [Leaving session]");
            }
            return true;
        } catch (Exception ex) {
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("exception_on_end.png")));
            System.out.println("[Exception in end] " + ex.getMessage());
        } finally {
            try {
                page.waitForTimeout(3000);
                page.context().browser().close();
            } catch (Exception e) {
                System.out.println("[Exception closing browser in end()]");
            }
        }
        return false;
    }

    // Get User ID via JS
    public String getUserId() {
        try {
            if (page == null) {
                System.out.println("[getUserId][PAGE IS NULL][RETURNING NULL]");
                return null;
            }

            String jsScript = "if(RTCP.getConferenceSession()) { return RTCP.getConferenceSession()._userId; }";
            Object userIdStr = page.evaluate(jsScript);

            if (userIdStr != null) {
                return userIdStr.toString();
            }
        } catch (Exception e) {
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("exception_in_getUserId.png")));
            System.out.println("[Exception in getUserId] " + e.getMessage());
        }
        return null;
    }
    // Check if connected via JS
    public boolean isConnected() {
        try {
            if (page == null) {
                System.out.println("[isConnected][PAGE IS NULL][RETURN FALSE]");
                return false;
            }

            String jsScript = 
                "return (typeof ZCSmartConferenceImpl === 'undefined' ? " +
                "ZRSmartConferenceImpl : ZCSmartConferenceImpl)" +
                ".getCurrentActiveSession().isInitialSessionConnected();";

            Object result = page.evaluate(jsScript);

            if (result != null && result instanceof Boolean && (Boolean) result) {
                return true;
            }
        } catch (Exception e) {
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("exception_in_isConnected.png")));
            System.out.println("[Exception in isConnected] " + e.getMessage());
        }
        return false;
    }
    
    public boolean waitUntilConnected() {
        try {
            int extendedTimeoutMs = Timeouts.EXTENDED_TIMEOUT;
            int oneSecondMs = Timeouts.ONE_SECOND_INTERVAL;

            for (int elapsed = 0; elapsed < extendedTimeoutMs; elapsed += oneSecondMs) {
                // Small delay between checks
                Thread.sleep(oneSecondMs / 2);

                String jsScriptForConnectionState =
                    "return (typeof ZCSmartConferenceImpl === \"undefined\" ? " +
                    "ZRSmartConferenceImpl : ZCSmartConferenceImpl)" +
                    ".getCurrentActiveSession().isInitialSessionConnected();";

                Boolean isConnected = (Boolean) page.evaluate(jsScriptForConnectionState);

                if (Boolean.TRUE.equals(isConnected)) {
                    return true;
                }

                Thread.sleep(oneSecondMs);
            }

            // Max retry reached
            System.out.println(
                "[waitUntilConnected][MAX RETRY REACHED][TEST_ID]" + testId +
                " [ROOM ID]" + roomId +
                " [SESSION ID]" + sessionId
            );

            // Take a screenshot for debugging
            page.screenshot(new Page.ScreenshotOptions()
                                .setPath(Paths.get("session_not_connected_" + sessionId + ".png")));

            return false;

        } catch (Exception e) {
            System.out.println(
                "[waitUntilConnected][EXCEPTION] [TEST_ID]" + testId +
                " [ROOM ID]" + roomId +
                " [SESSION ID]" + sessionId +
                " Exception: " + e
            );
            return false;
        }
    }

    }
