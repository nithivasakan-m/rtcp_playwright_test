package com;


import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.LoadState;
import rtcplatform.automation.Constants;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Demo2 {

    private static String DOMAIN = "PRE-IDC";
    private static String CALL_KEY;
    private static String MEETING_URL;
    private static int THREAD_COUNT = 10;
    private static int USERS_PER_THREAD = 20;

    private List<Cookie> storedCookies;
    AtomicInteger totalUserCount = new AtomicInteger();
    private static final List<Page> pages = Collections.synchronizedList(new ArrayList<>());
    private final CountDownLatch loginLatch = new CountDownLatch(1);
    private final CountDownLatch readyToJoinLatch = new CountDownLatch(THREAD_COUNT);
    private final boolean isHeadless = true;

    private final HashMap<String, String> domains = new HashMap<>();
    private final HashMap<String, String[]> credentials = new HashMap<>();
    private final HashMap<String, String> loginUrl = new HashMap<>();

    {
        domains.put("LOCAL", "https://rtcplatform.localzoho.com/rtcpdemo.do");
        domains.put("PRE-LOCAL", "https://prertcplatform.localzoho.com/rtcpdemo.do");
        domains.put("IDC", "https://rtcplatform.zoho.com/rtcpdemo.do");
        domains.put("PRE-IDC", "https://prertcplatform.zoho.com/rtcpdemo.do");

        credentials.put("LOCAL", new String[]{"nithivasakan.m+t1@zohotest.com", "Nithitest"});
        credentials.put("PRE-LOCAL", new String[]{"nithivasakan.m+t1@zohotest.com", "Nithitest"});
        credentials.put("PRE-IDC", new String[]{"mohammed.thanweer+2us@zohotest.com", "RTCPlatform"});
        credentials.put("IDC", new String[]{"mohammed.thanweer+2us@zohotest.com", "RTCPlatform"});

        loginUrl.put("LOCAL", "https://accounts.localzoho.com");
        loginUrl.put("PRE-LOCAL", "https://accounts.localzoho.com");
        loginUrl.put("IDC", "https://accounts.zoho.com");
        loginUrl.put("PRE-IDC", "https://accounts.zoho.com");
    }

    public static void main(String[] args) {
        MeetingLoadTest test = new MeetingLoadTest();
        test.setDOMAIN("PRE-IDC");
        test.setCALL_KEY("7c51669d6f2da2e4ba3b4a00c05c9ca4527ece7966281632a9a15ac8ed6e9f3f2e340464443fa3dd82efe3f7bc89a2a4eaab16216c0ed5e64479040259bbb3487bcdee412692308e8cb38c0b1fb9d03388ce123bd315ca9c1d8c32f9006ea2f0387390b8847e33472d495ecb8623cf3a17e47beb27c56013812afb37385522c6643bdb42f546201049a89e56e01f79f4");
        test.startLoadTest();
    }

    public void startLoadTest() {
        getMeetingLink();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        executor.submit(() -> runUserFlow(USERS_PER_THREAD, true, "LoginThread"));

        for (int i = 1; i < THREAD_COUNT; i++) {
            final String threadName = "UserThread-" + i;
            executor.submit(() -> runUserFlow(USERS_PER_THREAD, false, threadName));
        }

        waitFor(60000);

        for (Page page : pages) {
            System.out.println("Closing page: " + page);
            page.context().close();
            page.close();
        }

        executor.shutdown();
    }

    private void runUserFlow(int users, boolean doLogin, String threadName) {
        try {
            Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(isHeadless)
                    .setArgs(Arrays.asList(
                            "--use-fake-ui-for-media-stream",
                            "--use-fake-device-for-media-stream",
                            "--use-file-for-fake-video-capture=/Users/nithi-22537/Downloads/output.y4m",
                            "--allow-file-access-from-files",
                            "--no-sandbox",
                            "--start-fullscreen"
                    )));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setPermissions(Arrays.asList("microphone", "camera")));

            if (!doLogin) {
                loginLatch.await();
                context.addCookies(storedCookies);
            } else {
                Page page = context.newPage();
                page.navigate(loginUrl.get(DOMAIN));
                if (performLogin(page)) {
                    storedCookies = page.context().cookies();
                    loginLatch.countDown();
                    System.out.println("✅ Login successful. Released other threads.");
                } else {
                    System.err.println("❌ Login failed.");
                    return;
                }
            }

            // 🔁 Sync threads for simultaneous join
            readyToJoinLatch.countDown();
            readyToJoinLatch.await();
            System.out.println("🟢 [" + threadName + "] All threads are ready. Starting user joins...");

            for (int i = 1; i <= users; i++) {
                Page userPage = context.newPage();
                userPage.navigate(MEETING_URL);
                userPage.waitForLoadState(LoadState.LOAD);
                userPage.locator("xpath=//*[@purpose='joinFromPreviewRoom']").click();

                int currentCount = totalUserCount.incrementAndGet();
                System.out.println("[" + threadName + "] User #" + currentCount + " joined.");
            }

            pages.addAll(context.pages());
            System.out.println("[" + threadName + "] Finished spawning " + users + " users.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean performLogin(Page page) {
        try {
            String username = credentials.get(DOMAIN)[0];
            String password = credentials.get(DOMAIN)[1];
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

    private void waitFor(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }

    public void setDOMAIN(String domain) { DOMAIN = domain; }
    public void setCALL_KEY(String callKey) { CALL_KEY = callKey; }
    public void setTHREAD_COUNT(int count) { THREAD_COUNT = count; }
    public void setUSER_PER_THREAD(int userCount) { USERS_PER_THREAD = userCount; }

    public String getMeetingLink() {
        return MEETING_URL = domains.get(DOMAIN) + "?usertype=joinee&callkey=" + CALL_KEY + "&conftype=video&iszohouser=false&isnewcss=true&isnewui=false";
    }
}
