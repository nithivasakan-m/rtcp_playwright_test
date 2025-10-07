package copy;

import com.microsoft.playwright.*;

import copy.Constants.Browsers;

import java.nio.file.Paths;
import java.util.*;

public class PlaywrightFactory {

    private final Playwright playwright;
    private final Browser browser;

    // Constructor initializes Playwright + Browser
    public PlaywrightFactory(Browsers browserType, Capability capability, BrowserSpecs specs) {
        this.playwright = Playwright.create();
        this.browser = createBrowser(browserType, capability, specs);
    }

    // -------------------- Create Browser --------------------
    private Browser createBrowser(Browsers browserType, Capability capability, BrowserSpecs specs) {
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(capability.isHeadless());

        // binary path (custom chrome version etc.)
        String binaryPath = specs.getBinaryPath();
        if (capability.isNullVideoDecoderEnabled()) {
            binaryPath = specs.getCustomBinaryPath();
        }
        if (binaryPath != null && !binaryPath.isEmpty()) {
            launchOptions.setExecutablePath(Paths.get(binaryPath));
        }

        // Add browser flags
        List<String> args = new ArrayList<>();
        if (capability.isFakeMedia()) {
            args.add("--use-fake-ui-for-media-stream");
            args.add("--use-fake-device-for-media-stream");
            args.add("--enable-automation");
            args.add("--no-sandbox");
            args.add("--disable-infobars");
            args.add("--disable-browser-side-navigation");
            args.add("--disable-gpu");
            args.add("--start-maximized");
            args.add("--window-size=1920,1080");
        }
        if (capability.getVideoFile() != null) {
            args.add("--use-file-for-fake-video-capture=" + Util.fetchMediaPath(capability.getVideoFile(), specs.getBrowser()));
        }
        if (capability.getAudioFile() != null) {
            args.add("--use-file-for-fake-audio-capture=" + Util.fetchMediaPath(capability.getAudioFile(), specs.getBrowser()));
        }

        if (capability.getFlags() != null) {
            args.addAll(capability.getFlags());
        }
        launchOptions.setArgs(args);

        // Launch specific browser
        switch (browserType) {
            case CHROME:
            case EDGE:
            case BRAVE:
            case ULAA:
                return playwright.chromium().launch(launchOptions);
            case FIREFOX:
                return playwright.firefox().launch(launchOptions);
            case WEBKIT:
                return playwright.webkit().launch(launchOptions);
            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
    }

    // -------------------- Create Context --------------------
    public BrowserContext createContext(Capability capability, String testId, String roomId, String sessionId) {
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();

        // viewport
        if (capability.getWindowSize() != null && capability.getWindowSize().contains("x")) {
            try {
                String[] parts = capability.getWindowSize().split("x");
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);
                contextOptions.setViewportSize(width, height);
            } catch (Exception ignored) {}
        }

        // media permissions
        if (capability.isFakeMedia()) {
            contextOptions.setPermissions(Arrays.asList("microphone", "camera"));
        }

        // downloads
//        String downloadFilePath = ConfManager.getReportsBasePath() + "/" + testId + "/" + roomId + "/" + sessionId + "/";
        contextOptions.setAcceptDownloads(true);

        // video recording
        if (capability.isRecordVideo() && capability.getRecordVideoDir() != null) {
            contextOptions.setRecordVideoDir(Paths.get(capability.getRecordVideoDir()));
        }

        return browser.newContext(contextOptions);
    }

    // -------------------- Create Page --------------------
    public Page createPage(Capability capability, String testId, String roomId, String sessionId) {
        BrowserContext context = createContext(capability, testId, roomId, sessionId);
        return this.page = context.newPage();
    }

    // -------------------- Accessors --------------------
    public Playwright getPlaywright() { return playwright; }
    public Browser getBrowser() { return browser; }
    private Page page;


    public Page getPage() {
        if (this.page == null) {
            throw new IllegalStateException("Page not created yet! Call createAndStorePage() first.");
        }
        return this.page;
    }


    // -------------------- Cleanup --------------------
    public void close() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
