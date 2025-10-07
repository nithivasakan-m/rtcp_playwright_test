package com;
import com.microsoft.playwright.*;

public class CDPRemoteConnect {
    public static void main(String[] args) {
    	try {
    		Playwright playwright = Playwright.create();
    	    Browser browser = playwright.chromium().connectOverCDP("http://localhost:9222");
    	    Page page = browser.newPage();
    	    page.navigate("https://example.com");
    	    System.out.println(page.title());
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
