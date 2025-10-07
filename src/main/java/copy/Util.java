package copy;

import com.microsoft.playwright.Page;

import copy.Constants.MediaFileType;

public class Util {
	public static String fetchMediaPath(MediaFile media, Constants.Browsers browser)
	{
		String extension = null;

		if(media.getType().equals(MediaFileType.VIDEO))
		{
			if (browser == Constants.Browsers.CHROME)
			{
				extension = ".y4m"; //NO I18N
			}
			else
			{
				extension = ".mp4"; //NO I18N
			}
		}
		else 
		{
			extension = ".wav"; //NO I18N
		}
		return media.getFilePath() + media.getFileName() + extension;
	}
	
	public static boolean isNull(String st)
	{
		return (st == null || st == "" || st.equals("null"));
	}
	
	public static boolean isPageNull(Page page)
	{
		if(page == null)
		{
			return true;
		}
		return false;
	}
	public static void waitAround(int durationInMillisecond) 
	{
		try 
		{
			Thread.sleep(durationInMillisecond);
		} 
		catch (InterruptedException e) 
		{
			Thread.currentThread().interrupt();
		}
	}
	
}
