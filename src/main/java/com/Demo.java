package com;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

public class Demo {

    public static Hashtable<String, ActionController> users = new Hashtable<>();
    public static Hashtable<String, String> usersVsUserId = new Hashtable<>();
    public static String USER = "User ";

    public static void waitFor(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        Random random = new Random();
        int sixDigit = 100000 + random.nextInt(900000); // ensures 6 digits
        System.out.println("Random 6-digit number: " + sixDigit);

        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setArgs(Arrays.asList(
                        "--use-fake-ui-for-media-stream",
                        "--use-fake-device-for-media-stream",
                        "--use-file-for-fake-video-capture=/Users/nithi-22537/Downloads/output.y4m",
                        "--allow-file-access-from-files",
                        "--no-sandbox",
                        "--start-fullscreen",
                        "--use-fake-video-capture-with-impl=looping"
                )));

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setPermissions(Arrays.asList("microphone", "camera")));

        String url = "https://prertcplatform.zoho.com/zvpqameetings.do?id=" + sixDigit;
        ActionController host = new ActionController(context.newPage());

        host.loadPage(url);
        host.changeDisplayName(Constants.HOST);
        host.startMeetingFromPreview();

        int totalUsers = 15; // 👈 total participants
        CyclicBarrier barrier = new CyclicBarrier(totalUsers, () -> {
            System.out.println("🚀 All participants reached preview page, now joining together!");
        });

        // Launch users in parallel threads
        for (int i = 1; i <= totalUsers; i++) {
            final int userIndex = i;
            new Thread(() -> {
                ActionController user = new ActionController(browser.newContext().newPage());
                user.loadPage(url);
                String name = Constants.PARTICIPANT + " " + userIndex;
                user.changeDisplayName(name);

                try {
                    // Wait until all users reach here
                    barrier.await();
                    // Now all click "Join" at the same time
                    user.joinFromPreviewPage();
                    users.put(name, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // 👇 optional: wait a while before cleanup
        waitFor(15000);

//        for (String key : users.keySet()) {
//            users.get(key).leaveMeeting();
//        }
//        host.endConference();
    }
}
