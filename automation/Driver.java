package rtcplatform.automation;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class Drivers {
    private static final Logger LOGGER = Logger.getLogger(Drivers.class.getName());
    private static Properties properties;
    
    public static WebDriver getDriver(String browser,Properties properties) {
        if (properties == null) {
            return null;
        }
        Drivers.properties = properties;
        switch (browser.toLowerCase()) {
            case "chrome":
                return createWebDriver(new ChromeOptions(), null);
            case "firefox":
                return createWebDriver(new FirefoxOptions(), null);
            case "edge":
                return createWebDriver(new EdgeOptions(), null);
            case "brave":
                return createWebDriver(new ChromeOptions(), properties.getProperty("braveBinaryPath"));
            case "ulaa":
                return createWebDriver(new ChromeOptions(), properties.getProperty("ulaaBinaryPath"));
            case "safari":
                return safariDriver();
            default:
                LOGGER.log(Level.WARNING, "Unsupported browser: " + browser);
                return null;
        }
    }
    @SuppressWarnings("unused")
	private static WebDriver createWebDriver(Object options){
	    return createWebDriver(options, null);
    }
    private static WebDriver createWebDriver(Object options, String binaryPath) {
        try {
            String nodeURL = properties.getProperty(Constants.NODEURL);

            if (options instanceof ChromeOptions) {
                ChromeOptions chromeOptions = (ChromeOptions) options;
                if (binaryPath != null) chromeOptions.setBinary(binaryPath);
                setCommonOptions(chromeOptions);
                return new RemoteWebDriver(new URL(Constants.NODEURL), chromeOptions);
            } else if (options instanceof FirefoxOptions) {
                FirefoxOptions firefoxOptions = (FirefoxOptions) options;
                setCommonOptions(firefoxOptions);
                return new RemoteWebDriver(new URL(Constants.NODEURL), firefoxOptions);
            } else if (options instanceof EdgeOptions) {
                EdgeOptions edgeOptions = (EdgeOptions) options;
                setCommonOptions(edgeOptions);
                return new RemoteWebDriver(new URL(Constants.NODEURL), edgeOptions);
            }
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "[Driver][createWebDriver] Invalid node URL", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[Driver][createWebDriver] WebDriver initialization failed", e);
        }
        return null;
    }

    private static void setCommonOptions(Object options) {
        if (options instanceof ChromeOptions) {
            ChromeOptions chromeOptions = (ChromeOptions) options;
            chromeOptions.addArguments("--disable-popup-blocking", "--use-fake-ui-for-media-stream", "--start-maximized", "--disable-notifications");
            LOGGER.log(Level.INFO, "[Driver][setCommonOptions] Chrome options set");
        } else if (options instanceof FirefoxOptions) {
            FirefoxOptions firefoxOptions = (FirefoxOptions) options;
            firefoxOptions.addArguments("--disable-popup-blocking", "--start-maximized");
            LOGGER.log(Level.INFO, "[Driver][setCommonOptions] Firefox options set");
        } else if (options instanceof EdgeOptions) {
            EdgeOptions edgeOptions = (EdgeOptions) options;
            //edgeOptions.addArguments("--disable-popup-blocking", "--start-maximized");
            LOGGER.log(Level.INFO, "[Driver][setCommonOptions] Edge options set");           
        }
    }
    private static WebDriver safariDriver() {
        try {
            String nodeURL = properties.getProperty(Constants.NODEURL);
            if (nodeURL == null || nodeURL.isEmpty()) {
                LOGGER.log(Level.SEVERE, "Node URL is not set for Safari WebDriver");
                return null;
            }
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName("safari");
            return new RemoteWebDriver(new URL(nodeURL), capabilities);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "[Driver][safariDriver] Invalid node URL", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[Driver][safariDriver] WebDriver initialization failed", e);
        }
        return null;
    }
    public static Properties getProperty()
    {
        return Drivers.properties;
    }
}
