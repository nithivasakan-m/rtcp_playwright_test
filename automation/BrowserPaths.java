package rtcplatform.automation;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

public class BrowserPaths {

	public static void main(String[] args) {
		Properties p = System.getProperties();
		Enumeration<?> e = p.keys();
		
		while(e.hasMoreElements())
		{
			System.out.println(e.nextElement()+" = "+p.getProperty((String) e.nextElement()));
		}
		System.out.println(p.getProperty("os.name"));
	}

}
