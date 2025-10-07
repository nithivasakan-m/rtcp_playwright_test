package com;
import com.microsoft.playwright.*;
import com.microsoft.playwright.Page.WaitForCloseOptions;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.LoadState;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import rtcplatform.automation.Constants;

public class Example {
	static String meetingLink = "https://rtcplatform.localzoho.com/rtcpdemo.do?usertype=joinee&callkey=77a50e16a033d72712e9a62cf1c95bbbf5a050498023f0e32ab5f7615754abcb74ecb9d7c2d83225ed47dfb6b4e7773995b4823fe0101947d5873d6ce405abf56aaa7c4f71825d826e133921a6d42314bcbdfade419699c61271eaac679ba6f571a36f6fc97b80168c6b29b47d7ae04ed1887f98dcf1c0a7a23b1430ca80870321e7cea198e69206be4ff585691287c0&conftype=video&iszohouser=false&isnewcss=true&isnewui=false&e2eenable=false&livetranscription=false";
	 int n = 1;
	 List<Cookie> cookies;
	 AtomicInteger totalUserCount = new AtomicInteger();
	 boolean flag = true;
	 static boolean loginSuccess = false;
	 int waitCount = 5;
	 List<Page> pages = new ArrayList<Page>();
	public static void waitTenSec(long waitTime)
	{
		try {Thread.sleep(waitTime);}catch(Exception e) {e.printStackTrace();}
	}
	public void runThread() {
	   
		
		Map<Integer, Thread> threadsMap = new HashMap<Integer, Thread>();
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		executor.submit(() -> runPlaywrightTask(n, flag, "Thread " + 1));
		waitTenSec(10000);
		if(loginSuccess) {
			System.out.println("Thread Loops");
			for (int taskId = 2; taskId <= 5; taskId++) {
			    int finalTaskId = taskId; // Optional if needed in inner class, not required in lambda
			    executor.submit(() -> runPlaywrightTask(n, flag, "Thread " + finalTaskId));
			}
	}
		waitTenSec(60000);
		
//		for(Page page : pages)
//			leave(page);
	    
	}
	private void runPlaywrightTask(int n, boolean flag, String threadName) 
	{
	    try {
	    	Playwright playwright = Playwright.create();
	        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
	                .setHeadless(flag)
	                .setArgs(Arrays.asList(
	                        "--use-fake-ui-for-media-stream",
	                        "--use-fake-device-for-media-stream",
	                        "--use-file-for-fake-video-capture=/Users/nithi-22537/Downloads/output.y4m")));

	        BrowserContext context = browser.newContext(new Browser.NewContextOptions().setRecordVideoDir(Paths.get("video/"))
	                .setPermissions(Arrays.asList("microphone", "camera")));

	        Page page = context.newPage();
	        page.navigate(meetingLink);
	        if(!loginSuccess) {
	        	loginSuccess = login(page);
	        }
	        else {
	        	context.addCookies(cookies);
	        	page.reload();
	        }
	        pages.add(page);
	        
	        page.waitForLoadState(LoadState.LOAD);
	        clickButton(page, Constants.JOINMEETING_XPATH);

	        int count = 0;
	        int count2 =5;
	        for (int i = 0; i < n; i++) {
	            Page page2 = context.newPage();
	            page2.navigate(meetingLink);
	            page2.waitForLoadState(LoadState.LOAD);
	            if(clickButton(page2, Constants.JOINMEETING_XPATH))
	            {
	            	int ig = totalUserCount.getAndDecrement();
	            	System.out.println(ig);
	            	if(ig==20)
	            	{
	            		waitTenSec(60000);
	            		System.out.print("Thread name ="+threadName+" totalUserCount = "+totalUserCount+" wait for 1 min");
	            	}
	            	
	            }
	            count++;
//	            page2.waitForLoadState(LoadState.LOAD);
	            if(count == count2) {
	            	context = browser.newContext();
	            	context.addCookies(cookies);
	            	count2+=2;
	            	System.out.println("ThreadName = "+threadName+"Count = "+count+" new Browser Context created");
	            }
	        }
	        System.out.println(threadName + " completed with count = " + count);
	        waitTenSec(10000);
	        context.close();
	        browser.close();
	        playwright.close();
	        System.out.println("playwright closed");

	    }catch (Exception e) {
	        e.printStackTrace();
	    }
}
	public synchronized void leave(Page page)
	{
		try
		{
			clickButton(page, Constants.END_MEETING_XPATH);
			System.out.println("Leaving conference ="+page);
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
	}
    private boolean login(Page page)
    {
    	page.navigate("https://rtcplatform.localzoho.com");
        String username = "nithivasakan.m+t1@zohotest.com";
        
        //username = "mohammed.thanweer+4us@zohotest.com";
        String password = "Nithitest";
        //password = "RTCPlatform";

        page.locator("#" + Constants.LOGIN_ID_XPATH).fill(username);
        page.locator("#" + Constants.NEXT_BUTTON_XPATH).click();
        page.waitForTimeout(1000);

        page.locator("#" + Constants.PASSWORD_XPATH).fill(password);
        page.locator("#" + Constants.NEXT_BUTTON_XPATH).click();
        page.waitForTimeout(1000);
        cookies = page.context().cookies();
        System.out.println("Login successfull and Cookies stored = "+cookies);
        return true;
    }
    private  boolean clickButton(Page page, String xpath) {
        try {
        	page.locator("xpath=" + xpath).click();
        	return true;
        } catch (Exception e) {
            System.out.println("Click failed for: " + xpath);
            return false;
        }
        
    }
    public static void main(String[]args) {
		new Example().runThread();
		
	}
}
