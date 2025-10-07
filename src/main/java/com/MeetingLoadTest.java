
package com;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.LoadState;
import rtcplatform.automation.Constants;

import java.nio.file.Paths;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MeetingLoadTest {

	static int num = 1;
	private static  String DOMAIN = "LOCAL";
	private static  String CALL_KEY;
    private static  String MEETING_URL;
    //private static final String LOGIN_URL = "https://accounts.localzoho.com";
    private static int THREAD_COUNT = 7;
    private static int USERS_PER_THREAD = 5;
    private static List<Page> pages = new ArrayList<Page>();
    private  List<Cookie> storedCookies;
    AtomicInteger totalUserCount = new AtomicInteger();
    private final List<Page> joinedPages = Collections.synchronizedList(new ArrayList<>());
    private final CountDownLatch loginLatch = new CountDownLatch(1);
    private final boolean isHeadless = true;
    
    private final HashMap<String, String> domains = new HashMap<String, String>();
    private final HashMap<String,String[]> credencials = new HashMap<String,String[]>();
    private final HashMap<String , String> loginUrl = new HashMap<String,String>();
    private final CyclicBarrier pauseBarrier = new CyclicBarrier(THREAD_COUNT, () -> {
        System.out.println("🛑 All threads reached 20 users. Waiting for 1 minute...");
        try {
            Thread.sleep(60000); // 1 minute pause
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    });
    {
    	domains.put("LOCAL", "https://rtcplatform.localzoho.com/rtcpdemo.do");
    	domains.put("PRE-LOCAL", "https://prertcplatform.localzoho.com/rtcpdemo.do");
    	domains.put("IDC","https://rtcplatform.zoho.com/rtcpdemo.do");
    	domains.put("PRE-IDC", "https://prertcplatform.zoho.com/rtcpdemo.do");
    	
    	credencials.put("LOCAL", new String[] {"nithivasakan.m+t1@zohotest.com","Nithitest"});
    	credencials.put("PRE-LOCAL", new String[] {"nithivasakan.m+t1@zohotest.com","Nithitest"});
    	credencials.put("PRE-IDC", new String[] {"mohammed.thanweer+2us@zohotest.com","RTCPlatform"});
    	credencials.put("IDC", new String[] {"mohammed.thanweer+2us@zohotest.com","RTCPlatform"});
    
    	loginUrl.put("LOCAL", "https://accounts.localzoho.com");
    	loginUrl.put("PRE-LOCAL", "https://accounts.localzoho.com");
    	loginUrl.put("IDC", "https://accounts.zoho.com");
    	loginUrl.put("PRE-IDC", "https://accounts.zoho.com");
    	
    }
    public static void main(String[] args) {
    	MeetingLoadTest test = new MeetingLoadTest();
  
    	test.setDOMAIN(null);
    	test.setCALL_KEY("085c05fcedbc84a5156f8643027f1722950852252525a7b45035c695879e02975e063d95f612b1db32fe1053c667d23f17ed9f70e1faf6bbbb8ca83967c676ae5954d447f5d591c162f1e4c412aa9089f041f8f7d3c011207ad4d606a2ce640fec2392446d1984775db91e163f85f861e2feb2ec0d420f5f0be9e7800051722f283b48fcf603c4ba6725e236737d9d1a");
    	System.out.println(test.domains.get(DOMAIN));
    	System.out.println(test.credencials.get(DOMAIN)[0]);
    	System.out.println(test.credencials.get(DOMAIN)[1]);
    	System.out.println(test.loginUrl.get(DOMAIN));
    	test.startLoadTest();
    }
    
    public void startLoadTest() {
    	getMeetingLink();
//    	MEETING_URL = "https://prertcplatform.zoho.com/zvpqameetings.do?id=12212111"+num;
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        executor.submit(() -> runUserFlow(USERS_PER_THREAD, true, "LoginThread"));
        
        
        for (int i = 1; i <= THREAD_COUNT; i++) {
            final String threadName = "UserThread-" + i;
            executor.submit(() -> runUserFlow(USERS_PER_THREAD, false, threadName));
        }
        waitFor(60000);
        for(Page page : pages)
        {
        	System.out.println(page.content()+" closed and page "+page);
        	page.context().close();
        	page.close();
        }
        executor.shutdown();
        
    }

     private  void runUserFlow(int users, boolean doLogin, String threadName) {
        try 
        {
        	Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(isHeadless)
                    .setArgs(Arrays.asList(
                            "--use-fake-ui-for-media-stream",
                            "--use-fake-device-for-media-stream",
//                            "--use-file-for-fake-video-capture=/Users/nithi-22537/Downloads/output.y4m",
                            "--allow-file-access-from-files",
                            "--no-sandbox",
                            "--start-fullscreen"
                    )));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setPermissions(Arrays.asList("microphone", "camera"))
                    );
            

            if (!doLogin){
                loginLatch.await(); 
                context.addCookies(storedCookies);
            }
            else{
            	
            	Page page = context.newPage();
                page.navigate(loginUrl.get(DOMAIN).toString());
                if (performLogin(page)) {
                    storedCookies = page.context().cookies();
                    loginLatch.countDown();
                    System.out.println("✅ Login successful. Released other threads.");
                } else {
                    System.err.println("❌ Login failed.");
                    return;
                }
                page = context.newPage();
                page.navigate(MEETING_URL);
                page.waitForLoadState(LoadState.LOAD);

                if (clickButton(page, Constants.JOINMEETING_XPATH,threadName)) {
					int currentCount = totalUserCount.incrementAndGet(); // or decrement if counting down
                    System.out.println("[" + threadName + "] User #" + currentCount + " joined.");
                }

            }
            int joinedCount = 0;

            for (int i = 1;i <= users ; i++) {
                Page userPage = context.newPage();
                userPage.navigate(MEETING_URL);
                userPage.waitForLoadState(LoadState.LOAD);

                boolean button = clickButton(userPage, "//*[@purpose='joinFromPreviewRoom']",threadName);
//                userPage.locator("xpath=//*[@purpose='joinFromPreviewRoom']").click();
                if (button) {
					int currentCount = totalUserCount.incrementAndGet(); // or decrement if counting down
                    System.out.println("[" + threadName + "] User #" + currentCount + " joined.");

                    if (joinedCount % 7 == 0) {
                    	pages.addAll(context.pages());
                        System.out.println("[" + threadName + "] Reached 10 users per browser. New browser context created...And Join count :"+joinedCount);
                        context = browser.newContext();
                        context.addCookies(storedCookies); 
                    }
                    joinedCount++;
                }
            }
           pages.addAll(context.pages());
           System.out.println("[" + threadName + "] Finished spawning " + joinedCount + " users.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean performLogin(Page page) {
        try {
        	String username = credencials.get(DOMAIN)[0];
        	String password = credencials.get(DOMAIN)[1];
            page.locator("#" + Constants.LOGIN_ID_XPATH).fill(username);
            page.locator("#" + Constants.NEXT_BUTTON_XPATH).click();
            waitFor(1000);
            page.locator("#" + Constants.PASSWORD_XPATH).fill(password);
            page.locator("#" + Constants.NEXT_BUTTON_XPATH).click();
            waitFor(1000);

            return true;
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            return false;
        }
    }

    private boolean clickButton(Page page, String xpath, String threadName) {
        try {
        	
        		 page.locator("xpath=" + xpath).click();
                 return true;
		   
        }catch (Exception e) {
            System.out.println("Thread Name: "+threadName+"  Click failed for: " + xpath);
            return false;
        }
    }
    private void waitFor(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }
    
    public void setDOMAIN(String domain)
    {
    	DOMAIN = domain;
    }
    public void setCALL_KEY(String callKey)
    {
    	CALL_KEY = callKey;
    }
    public void setTHREAD_COUNT(int count)
    {
    	THREAD_COUNT = count;
    }
    public void setUSER_PER_THREAD(int userCount)
    {
    	USERS_PER_THREAD = userCount;
    }
    public String getDOMAIN()
    {
    	return DOMAIN;
    }
    public String getCALL_KEY()
    {
    	return CALL_KEY;
    }
    public int getTHREAD_COUNT()
    {
    	return THREAD_COUNT;
    }
    public String getMeetingLink()
    {
    	return MEETING_URL = domains.get(DOMAIN)+"?usertype=joinee&callkey="+CALL_KEY+"&conftype=video&iszohouser=false&isnewcss=true&isnewui=false";
    }
    public String getLoginURL()
    {
    	return domains.get(DOMAIN);
    }
    public boolean login(Page page)
    {
    	return performLogin(page);
    }
}
