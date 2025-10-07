package test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

public class Factory {

    boolean headless = true;

    public void loadPage(Page page, String URL) {
        try {
            page.navigate(URL);
            page.waitForLoadState(LoadState.LOAD);
        } catch (Exception e) {
            System.out.println("Error occurred while loading page: " + e.getMessage());
        }
    }

    public Page chrome() {
        return createBrowser("chrome", null);
    }

    public Page chrome(String chromeBinaryPath) {
        return createBrowser("chrome", chromeBinaryPath);
    }

    public Page firefox() {
        return createBrowser("firefox", null);
    }

    public Page safari() {
        return createBrowser("safari", null);
    }

    public Page brave(String braveBinaryPath) {
        return createBrowser("brave", braveBinaryPath);
    }

    public Page getBrowser(String browserName, String binaryPath) {
        return createBrowser(browserName, binaryPath);
    }

    private Page createBrowser(String browserName, String binaryPath) {
        try {
            Playwright playwright = Playwright.create();
            List<String> args = new ArrayList<>();
            BrowserType.LaunchOptions options = null;
            
            Browser browser = null;
            switch (browserName.toLowerCase()) {
                case "chrome":
                {
                	options = new BrowserType.LaunchOptions()
                            	.setHeadless(headless)
                            	.setArgs(args);

                    if (binaryPath != null && !binaryPath.isEmpty()) {
                        options.setExecutablePath(Paths.get(binaryPath));
                    }

                    browser = playwright.chromium().launch(options);
                }
                    break;
                case "firefox":
                {
                    browser = playwright.firefox().launch(options);
                }
                    break;
               
                case "webkit":
                {
                    browser = playwright.webkit().launch(options);
                }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browserName);
            }

            BrowserContext context = browser.newContext();
            return context.newPage();

        } catch (Exception e) {
            System.out.println("Exception while creating browser: " + e.getMessage());
            return null;
        }
    }
}
