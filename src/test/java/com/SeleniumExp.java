package com;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumExp {
	
	public static void main(String[] args) {
        runLocalTest();
    }

    public static void runLocalTest() {
       try
       {
    	   WebDriver driver  = new SafariDriver();
    	   driver.navigate().to("https://youtube.com");;
       }catch(Exception e)
       {
    	   e.printStackTrace();
       }
    }
}
