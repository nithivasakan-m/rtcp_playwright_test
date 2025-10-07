package copy;

import com.microsoft.playwright.Page;

public class ClientMediaValidator 
{
	public static boolean checkAudioUpstreamSent(Page page)
	{
		return true;
	}
	public static boolean checkDownstreamScreenShareFrameDecoded(Page page)
	{
		return true;
	}
	
	public static boolean checkDownstreamVideoFrameDecoded(Page page, String ssrcIdForUserId)
	{
		return true;
	}
	
	public static boolean checkAudioUpstreamLevel(Page page) 
	{
		return true;
	}
	
	public static boolean checkAudioDownstreamLevel(Page page)
	{
		return false;
	}
	public static void clearStatsInfo(Page page) {
		// TODO Auto-generated method stub
		
	}

}
