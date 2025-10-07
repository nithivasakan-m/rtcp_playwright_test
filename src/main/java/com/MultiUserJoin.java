package com;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

public class MultiUserJoin {
    static int TOTAL_USERS = 20; // Change this dynamically
    static String url = null;
    private static int height = 1490;
    private static int width = 800;
    // Barrier ensures all participants click join at the same time
    static CyclicBarrier barrier = new CyclicBarrier(TOTAL_USERS - 1, () ->
        System.out.println("✅ All users ready, joining now...")
    );

    public static void main(String[] args) {
        Random random = new Random();
        int sixDigit = 100000 + random.nextInt(900000);
        url = "https://prertcplatform.zoho.com/zvpqameetings.do?id=" + sixDigit;
        System.out.println("🎯 Meeting URL: " + url);

        ExecutorService executor = Executors.newFixedThreadPool(TOTAL_USERS);

        // First user = host
        executor.submit(() -> startHost());
        sleep(10000); // Wait for host to start

        // Rest users = participants
//        for (int i = 1; i < TOTAL_USERS-1; i++) {
//            int userId = i;
//            executor.submit(() -> runUserFlow(userId));
//        }
//        executor.submit(() -> runUserFlow(TOTAL_USERS));
//        System.out.println("All users submitted for joining");

        // Optional: Shutdown executor after all tasks are submitted
        
    }

    // Host starts the meeting
    private static void startHost() {
        try {
            Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setArgs(Arrays.asList("--use-fake-ui-for-media-stream", "--use-fake-device-for-media-stream", "--no-sandbox"))
            );

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                        .setViewportSize(1490, 800)   // Smaller size
                        .setPermissions(Arrays.asList("microphone", "camera"))
                );
            Page page = context.newPage();
            page.evaluate("document.documentElement.requestFullscreen();");
            page.navigate(url);
            
            sleep(5000);
            page.locator("xpath=//*[@purpose='startFromPreviewRoom']").click();
            System.out.println("👤 Host started the meeting...");

            // Keep host alive
            sleep(20000);
            
//            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void runUserFlow(int userId) {
        try {
            Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setArgs(Arrays.asList("--use-fake-ui-for-media-stream", "--use-fake-device-for-media-stream", "--no-sandbox","--maximize"))
            );

            BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
//                    .setViewportSize(640, 360)   // Smaller size
                    .setPermissions(Arrays.asList("microphone", "camera"))
            );

            Page page = context.newPage();
            page.navigate(url);

            // Wait for join button to be visible before reaching barrier
            page.locator("xpath=//*[@purpose='joinFromPreviewRoom']")
                .waitFor(new Locator.WaitForOptions().setTimeout(60000).setState(WaitForSelectorState.VISIBLE));

            barrier.await(); // Wait until ALL participants are ready

            page.locator("xpath=//*[@purpose='joinFromPreviewRoom']").click();

            System.out.println("🙋 User " + userId + " joined the meeting");

            sleep(15000); // Stay in meeting

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}