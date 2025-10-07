package com;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

public class PlaywrightUtils {

    private static final int DEFAULT_TIMEOUT = 3000;
    private static final int LONG_TIMEOUT = 10000;
    
    public void loadPage(Page page, String URL) {
        page.navigate(URL);
        page.waitForLoadState(LoadState.LOAD);
    }

    public Locator locatElement(Page page, String selector) {
        Locator element = page.locator(selector);
        element.waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT));
        return element;
    }

    public boolean typeText(Page page, String selector, String text) {
        try {
            Locator inputField = locatElement(page, selector);
            if (inputField.isVisible()) {
                inputField.fill(text); // clear() is not necessary
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error typing into: " + selector + " - " + e.getMessage());
        }
        return false;
    }

    public String getAttributeValue(Page page, String selector, String attribute) {
        try {
            Locator element = locatElement(page, selector);
            return element.getAttribute(attribute);
        } catch (Exception e) {
            System.out.println("Exception while getting attribute: " + e.getMessage());
            return null;
        }
    }

    public boolean hoverAndClick(Page page, String selector) {
        try {
            Locator element = locatElement(page, selector);
            element.hover();
            if (element.isVisible()) {
                element.click();
                System.out.println("Hovered and clicked: " + selector);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Failed to hover and click: " + selector + " - " + e.getMessage());
        }
        return false;
    }
    public void hover(Page page , String selector)
    {
    	try
    	{
    		Locator element = locatElement(page, selector);
    		element.hover();
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    public String getAttributeByXpath(Page page, String xpath, String attributeName) {
        Locator element = page.locator("xpath=" + xpath);
        if (element.count() == 0) {
            System.out.println("Element not found for XPath: " + xpath);
            return null;
        }
        String value = element.first().getAttribute(attributeName);
        System.out.println("Attribute '" + attributeName + "' value: " + value);
        return value;
    }

    public boolean click(Page page, String selector) {
        try {
            Locator element = locatElement(page, selector);
            if (element.isVisible()) {
                element.click();
                System.out.println("Clicked element: " + selector);
                return true;
            } else {
                System.out.println("Element not visible: " + selector);
            }
        } catch (Exception e) {
            System.out.println("Failed to click: " + selector + " - " + e.getMessage());
        }
        return false;
    }
    public Object executeJS(Page page,String script)
    {
    	try {
    		return page.evaluate("() =>"+script);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }
    public boolean clickByXpath(Page page, String xpath) {
        return click(page, "xpath=" + xpath);
    }

    public void waitForVisibilityAndClick(Page page, String selector) {
        try {
            page.locator(selector).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
            click(page, selector);
        } catch (Exception e) {
            System.out.println("Error waiting and clicking on: " + selector + " - " + e.getMessage());
        }
    }

    public static boolean waitFor(Page page, String selector, int timeout) {
        try {
            page.locator(selector).waitFor(new Locator.WaitForOptions().setTimeout(timeout));
            System.out.println("Element is visible: " + selector);
            return true;
        } catch (Exception e) {
            System.out.println("Timeout waiting for: " + selector);
            return false;
        }
    }

    public static boolean waitFor(Page page, String selector) {
        return waitFor(page, selector, DEFAULT_TIMEOUT);
    }
}
