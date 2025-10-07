package copy;

import java.io.File;
import java.util.Hashtable;

import copy.Constants.ConfigurationKeys;
import copy.Constants.MediaFileType;

public class MediaFile
{
	private String fileName;
	private int duration;
	private String filePath;
	private MediaFileType type;

	public MediaFile(MediaFileType type, Hashtable fileDetails)
	{
		this.type = type;
		this.fileName = ""+fileDetails.get(ConfigurationKeys.FILE_NAME);
		this.filePath =	""+fileDetails.get(ConfigurationKeys.FILE_PATH)+File.separator;
	}
	
	public void setFileName(String filename)
	{
		this.fileName = fileName;	
	}
	
	public String getFileName()
	{
		return fileName;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	public int getDuration()
	{
		return duration;
	}

	public void setFilePath(String filePath)
	{	
		this.filePath = filePath;
	}

	public String getFilePath()
	{
		return filePath;
	}
	
	public MediaFileType getType()
	{
		return type;
	}
}
