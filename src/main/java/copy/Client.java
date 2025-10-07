package copy;
import java.nio.file.Paths;
import java.util.Hashtable;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import copy.Constants.ConfigurationKeys;
import copy.Constants.MediaFileType;

public class Client {
    private final Playwright playwright;
    private final Browser browser;
    private final BrowserContext context;
    private final Page page;

    private final BrowserSpecs specs;
    private final Capability caps;

    public Client(Hashtable clientData) {
        this.specs = new BrowserSpecs();
        this.caps = new Capability();
        this.playwright = Playwright.create();

        
        String browserName = (String) clientData.get(ConfigurationKeys.BROWSER_NAME);
		specs.setBrowser(browserName);
		
		String browserVersion = (String) clientData.get(ConfigurationKeys.BROWSER_VERSION);
		specs.setBrowserVersion(browserVersion);
		
		specs.setPlatform((String)clientData.get(ConfigurationKeys.PLATFORM));
		
		String binaryPath = ""+clientData.get(ConfigurationKeys.BINARY_PATH);
		if(isNull(binaryPath))
		{
			specs.setBinaryPath(binaryPath);
		}
//		String customBinaryPath = ""+clientData.get(ConfigurationKeys.CUSTOM_BINARY_PATH);
//		if(isNull(customBinaryPath))
//		{
////			customBinaryPath = Util.getCustomBrowserBinaryPath(browserName, browserVersion);
//		}
//		specs.setCustomBinaryPath(customBinaryPath);

		if(clientData.containsKey(ConfigurationKeys.HEADLESS) && Boolean.parseBoolean(""+clientData.get(ConfigurationKeys.HEADLESS)))
		{
			caps.enableHeadless();
		}

		if(clientData.containsKey(ConfigurationKeys.FAKEMEDIA) && Boolean.parseBoolean(""+clientData.get(ConfigurationKeys.FAKEMEDIA)))
		{
			caps.enableFakeMedia();
		}

		if(clientData.containsKey(ConfigurationKeys.VIDEO))
		{
			Hashtable videoData = (Hashtable)clientData.get(ConfigurationKeys.VIDEO);
			MediaFile videoFile = new MediaFile(MediaFileType.VIDEO, videoData);
			caps.setVideoFile(videoFile);
		}

		if(clientData.containsKey(ConfigurationKeys.AUDIO))
		{
			Hashtable audioData = (Hashtable)clientData.get(ConfigurationKeys.AUDIO);
			MediaFile audioFile = new MediaFile(MediaFileType.AUDIO, audioData);
			caps.setAudioFile(audioFile);
		}
		
		// Launch
        this.browser = launchBrowser();
        this.context = browser.newContext(caps.toContextOptions());
        this.page = context.newPage();
    }

    private Browser launchBrowser() {
        BrowserType browserType;
        switch (specs.getBrowser()) {
            case CHROME:
            case EDGE:
            case BRAVE:
            case ULAA:
                browserType = playwright.chromium();
                break;
            case FIREFOX:
                browserType = playwright.firefox();
                break;
            case WEBKIT:
                browserType = playwright.webkit();
                break;
            default:
                throw new IllegalArgumentException("Unsupported: " + specs.getBrowser());
        }

        BrowserType.LaunchOptions launchOptions = caps.toLaunchOptions();
        if (specs.getBinaryPath() != null) {
            launchOptions.setExecutablePath(Paths.get(specs.getBinaryPath()));
        }
        return browserType.launch(launchOptions);
    }

    // Getters
    public Capability getCapability()
    {
    	return caps;
    }
    public BrowserSpecs getBrowserSpecs()
    {
    	return specs;
    }
    public Page getPage() { return page; }
    public BrowserContext getContext() { return context; }
    public Browser getBrowser() { return browser; }

    // New tab in same context
    public Page newTab() { return context.newPage(); }

    // New browser window (fresh context)
    public Page newBrowserWindow() { return browser.newContext(caps.toContextOptions()).newPage(); }

    // Close all
    public void close() {
        if (page != null) page.close();
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
    
    public static boolean isNull(String st)
	{
		return (st == null || st == "" || st.equals("null"));
	}
}
