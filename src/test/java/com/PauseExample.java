package com;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.LoadState;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import rtcplatform.automation.Constants;

public class PauseExample {
    static String meetingLink = "https://rtcplatform.localzoho.com/rtcpdemo.do?usertype=joinee&callkey=bf22ff133f07ba7da5a1506b941942895a8a6b934e8d19e65f3bfccc561f6950ce5b4d3bcf36d5ebe749dc527f0afef59fe166b2ca1a78eee577fa3c58fd8b3f8204f91513a7885be4c6e31000986261d2515625a5c22888a7ed921d165d9f992f5d5b9fc02bc215e23d0bf08b4cdb2183b5e56430162353a267ae93022b2deae3a9b0aca684242a94cbe6842eaeb9bb&conftype=video&iszohouser=false&isnewcss=true&isnewui=false&e2eenable=false&livetranscription=false";
    int n = 20;
    List<Cookie> cookies;
    AtomicInteger totalUserCount = new AtomicInteger(n);
    boolean flag = true;
    AtomicBoolean loginSuccess = new AtomicBoolean(false);
    int waitCount = 5;
    List<Page> pages = Collections.synchronizedList(new ArrayList<>());

    public static void waitTenSec(long waitTime) {
        try { Thread.sleep(waitTime); } catch (Exception e) { e.printStackTrace(); }
    }

    public void runThread() {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Start initial thread to login
        executor.submit(() -> runPlaywrightTask(n, flag, "Thread " + 1));
        waitTenSec(10000);

        if (loginSuccess.get()) {
            System.out.println("Thread Loops Started...");
            for (int taskId = 2; taskId <= 5; taskId++) {
                int finalTaskId = taskId;
                executor.submit(() -> runPlaywrightTask(n, flag, "Thread " + finalTaskId));
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Optionally leave all pages
//        for (Page page : pages)
//            leave(page);
    }

    private void runPlaywrightTask(int n, boolean flag, String threadName) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(flag)
                    .setArgs(Arrays.asList("--use-fake-ui-for-media-stream", "--use-fake-device-for-media-stream")));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setPermissions(Arrays.asList("microphone", "camera")));

            Page page = context.newPage();
            page.navigate(meetingLink);

            if (loginSuccess.compareAndSet(false, true)) {
                login(page);
            } else {
                synchronized (this) {
                    context.addCookies(cookies);
                }
                page.reload();
            }

            pages.add(page);
            page.waitForLoadState(LoadState.LOAD);
            clickButton(page, Constants.JOINMEETING_XPATH);

            int count = 0;
            int count2 = 5;

            for (int i = 0; i < n; i++) {
                Page page2 = context.newPage();
                page2.navigate(meetingLink);
                page2.waitForLoadState(LoadState.LOAD);

                if (clickButton(page2, Constants.JOINMEETING_XPATH)) {
                    if (totalUserCount.decrementAndGet() == 0) {
                        System.out.println(threadName + " totalUserCount = " + totalUserCount.get() + " wait for 1 min");
                        waitTenSec(60000);
                    }
                }
                count++;

                if (count == count2) {
                    context = browser.newContext();
                    synchronized (this) {
                        context.addCookies(cookies);
                    }
                    count2 += 5;
                    System.out.println("Count = " + count + " new Browser Context created");
                }
            }

            System.out.println(threadName + " completed with count = " + count);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void leave(Page page) {
        try {
            clickButton(page, Constants.END_MEETING_XPATH);
            System.out.println("Leaving conference: " + page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login(Page page) {
        page.navigate("https://rtcplatform.localzoho.com");
        String username = "mohammed.thanweer+4us@zohotest.com";
        username = "nithivasakan.m+t1@zohotest.com";
        String password = "RTCPlatform";
        password = "Nithitest";

        page.locator("#" + Constants.LOGIN_ID_XPATH).fill(username);
        page.locator("#" + Constants.NEXT_BUTTON_XPATH).click();
        page.waitForTimeout(1000);

        page.locator("#" + Constants.PASSWORD_XPATH).fill(password);
        page.locator("#" + Constants.NEXT_BUTTON_XPATH).click();
        page.waitForTimeout(1000);

        synchronized (this) {
            cookies = page.context().cookies();
        }
        System.out.println("Login successful and cookies stored: " + cookies);
    }

    private boolean clickButton(Page page, String xpath) {
        try {
            page.locator("xpath=" + xpath).click();
            return true;
        } catch (Exception e) {
            System.out.println("Click failed for: " + xpath);
            return false;
        }
    }

    public static void main(String[] args) {
        new PauseExample().runThread();
    }
}
