
package copy;

import java.util.Hashtable;

import copy.Constants.Browsers;
import copy.Constants.Platform;

public class BrowserSpecs {
	private Browsers browserType;
	private String browserVersion;
	private Platform platform;
	private String driverPath;
	private String binaryPath = null;
	private String customBinaryPath = null;
	private String extension = null;
	private String profile = null;
	
	public BrowserSpecs()
	{
	}
	public BrowserSpecs(BrowserSpecs specs)
	{
		this.browserType = specs.getBrowser();
		this.browserVersion = specs.getVersion();
		this.platform = specs.getPlatform();
		this.driverPath = specs.getDriverPath();
		this.binaryPath = specs.getBinaryPath();
		this.customBinaryPath = specs.getCustomBinaryPath();
		this.extension = specs.getExtension();
		this.profile = specs.getProfile();
	}
	public void setBrowser(String browserType)
	{
		this.browserType = Browsers.getBrowserbyName(browserType);
	}

	public Browsers getBrowser()
	{	
		return this.browserType;
	}

	public void setBrowserVersion(String browserVersion)
	{
		this.browserVersion = browserVersion;
	}

	public String getVersion()
	{ 
		return browserVersion;
	}

	public void setPlatform(String platform)
	{
		this.platform = Platform.getPlatformbyName(platform);
	}

	public Platform getPlatform()
	{	
		return this.platform;
	}

	public void setDriverPath(String driverPath)
	{
		this.driverPath = driverPath;
	}

	public String getDriverPath()
	{
		return driverPath;
	}

	public void setBinaryPath(String binaryPath)
	{
		this.binaryPath = binaryPath;
	}

	public String getBinaryPath()
	{
		return binaryPath;
	}

	public void setCustomBinaryPath(String binaryPath)
	{
		this.customBinaryPath = binaryPath;
	}

	public String getCustomBinaryPath()
	{
		return customBinaryPath;
	}

	public String getExtension()
	{
		return this.extension;
	}

	public String getProfile()
	{
		return this.profile;
	}
}
