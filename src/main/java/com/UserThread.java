package com;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import rtcplatform.automation.Constants;

public class UserThread {
    static int TOTAL_USERS = 20; // change as needed
    private static final Object browserLock = new Object();
    private static String url = null;
    public static void waitFor(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    static CyclicBarrier barrier = new CyclicBarrier(TOTAL_USERS, () ->
    System.out.println("✅ All users ready, starting join now...")
);

    public static void main(String[] args) {
        Random random = new Random();
        int sixDigit = 100000 + random.nextInt(900000);
        System.out.println("Random 6-digit meeting ID: " + sixDigit);

        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setArgs(Arrays.asList(
                    "--use-fake-ui-for-media-stream",
                    "--use-fake-device-for-media-stream",
                    "--use-file-for-fake-video-capture=/Users/nithi-22537/Downloads/output.y4m",
                    "--allow-file-access-from-files",
                    "--no-sandbox",
                    "--start-fullscreen",
                    "--use-fake-video-capture-with-impl=looping"
                ))
        );

        url = "https://prertcplatform.zoho.com/zvpqameetings.do?id=" + sixDigit;

        // This barrier ensures all threads wait until everyone is at preview screen
        

        BrowserContext context =  browser.newContext();
        Page user = context.newPage();
        user.navigate(url);
        waitFor(10000);
        user.locator("xpath=//*[@purpose='startFromPreviewRoom']").click();
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for(int i = 0; i < TOTAL_USERS ; i++)
        {
        	 executor.submit(() -> runUserFlow(1, true, "LoginThread"));
        	 System.out.println("User : "+i);
        }
        
    }
    private static void runUserFlow(int users, boolean doLogin, String threadName) {
        try 
        {
        	Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
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
            Page page = context.newPage();
            page.navigate(url);
            
            barrier.await();
            page.locator("xpath=//*[@purpose='joinFromPreviewRoom']").click();
            System.out.println("Thread Name :"+threadName);
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
    }

}
