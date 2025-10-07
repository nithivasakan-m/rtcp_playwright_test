package test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.options.Cookie;
import rtcplatform.automation.Constants;

import com.microsoft.playwright.Page;

public class Spotlight {
	
	private List<Cookie> cookies;
	private static ResourceBundle config = ResourceBundle.getBundle("config");
	public static boolean isLoginSessionStored = false;
	public static boolean headless = true;
	private boolean login(Page page, String loginUrl, String userName , String password)
	{
		try
		{
			page.navigate(loginUrl);
			takeScreenshot(page, "Before login");
			page.locator("#" + Constants.LOGIN_ID_XPATH).fill(userName);
	        page.locator("#" + Constants.NEXT_BUTTON_XPATH).click();
	        page.waitForTimeout(1000);
	        page.locator("#" + Constants.PASSWORD_XPATH).fill(password);
	        page.locator("#" + Constants.NEXT_BUTTON_XPATH).click();
	        page.waitForTimeout(1000);
	        cookies = page.context().cookies();
	        System.out.println("Login successfull and Cookies stored = "+cookies);
	        takeScreenshot(page, "after login");
			return true;
		}catch(Exception e)
		{
			System.out.println("Login Failed : "+e.getMessage());
			takeScreenshot(page, "Login Failed");
			return false;
		}
		
	}
	private void launchBrowser()
	{
		try
		{
			
			String loginURL = config.getString("loginURL");
			String userName = config.getString("username");
			String password = config.getString("password");
			if(isEmptyOrNull(loginURL) || isEmptyOrNull(userName) || isEmptyOrNull(userName))
			{
				System.out.println("loginUURL or UserName or Password not given");
				return;
			}
			if(login(page,loginURL,userName,password))
				isLoginSessionStored = true;
			
			
			
		}catch(Exception e)
		{
			System.out.println("Getting Excepiton while lanunch oa browser : "+e.getMessage());
		}
	}
	private void takeScreenshot(Page page , String screenshotFileName)
	{
		page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(screenshotFileName+".png")));
	}
	private boolean isEmptyOrNull(String values)
	{
		return values == null || values.isEmpty();
	}
	public static void main(String[]args)
	{
		
	}
}
