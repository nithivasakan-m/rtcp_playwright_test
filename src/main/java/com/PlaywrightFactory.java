package com;

import com.microsoft.playwright.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import copy.Constants.Browsers;

public class PlaywrightFactory {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private boolean headless = false;

    // Default constructor
    public PlaywrightFactory() {}

    // Constructor with browser type + optional path
    public PlaywrightFactory(Browsers browserType, String binaryPath) {
        launchBrowser(browserType, binaryPath);
    }

    public PlaywrightFactory(Browsers browserType) {
        this(browserType, null);
    }

    // ---------- Browser Launch ----------
    public PlaywrightFactory chrome()   { return launchBrowser(Browsers.CHROMIUM, null); }
    public PlaywrightFactory firefox()  { return launchBrowser(Browsers.FIREFOX, null); }
    public PlaywrightFactory safari()   { return launchBrowser(Browsers.WEBKIT, null); }
    public PlaywrightFactory edge(String path)   { return launchBrowser(Browsers.EDGE, path); }
    public PlaywrightFactory brave(String path)  { return launchBrowser(Browsers.BRAVE, path); }
    public PlaywrightFactory ula(String path)    { return launchBrowser(Browsers.ULA, path); }

    // Core launcher
    public PlaywrightFactory launchBrowser(Browsers browserType, String binaryPath) {
        playwright = Playwright.create();

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setArgs(getBrowserArgs(browserType));

        // Only set binary path if provided
        if (binaryPath != null) {
            Path path = getAbsolutePath(binaryPath);
            if (path != null) {
                launchOptions.setExecutablePath(path);
            }
        }

        switch (browserType) {
            case CHROMIUM, BRAVE, EDGE, ULA -> browser = playwright.chromium().launch(launchOptions);
            case FIREFOX -> browser = playwright.firefox().launch(launchOptions);
            case WEBKIT -> browser = playwright.webkit().launch(launchOptions);
            default -> throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }

        context = browser.newContext(
                new Browser.NewContextOptions().setPermissions(Arrays.asList("microphone", "camera"))
        );
        page = context.newPage();
        return this;
    }

    // ---------- Helpers ----------
    private Path getAbsolutePath(String paths) {
        try {
            return Paths.get(paths);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> getBrowserArgs(Browsers browserType) {
        return switch (browserType) {
            case CHROMIUM, BRAVE, EDGE, ULA -> Arrays.asList(
                    "--use-fake-ui-for-media-stream",
                    "--use-fake-device-for-media-stream",
                    "--allow-file-access-from-files",
                    "--no-sandbox",
                    "--start-fullscreen"
            );
            case FIREFOX -> Arrays.asList(
                    "-headless"
            );
            case WEBKIT -> Collections.emptyList();
		default -> throw new IllegalArgumentException("Unexpected value: " + browserType);
        };
    }

    // ---------- Getters ----------
    public Playwright getPlaywright() { return playwright; }
    public com.microsoft.playwright.Browser getBrowser() { return browser; }
    public BrowserContext getContext() { return context; }
    public Page getPage() { return page; }
    public void setHeadless(boolean headless) { this.headless = headless; }

    // ---------- Cleanup ----------
    public void close() {
        if (page != null) page.close();
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
