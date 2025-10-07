package copy;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Browser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Capability {

    // ---------- Core Fields ----------
    private boolean headless = false;
    private boolean fakeMedia = false;
    private boolean enableNullVideoDecoder = false;

    private String windowSize = "1490x800";   // Example: "1280x720"
    private MediaFile videoFile;
    private MediaFile audioFile;
   

    private boolean recordVideo = false;
    private String recordVideoDir;         // Directory path for saving recorded videos

    private List<String> flags = new ArrayList<>();                // Browser launch arguments
    private Map<String, String> extraCapabilities = new HashMap<>(); // Any additional key-value capabilities

    public Capability() {}

    public Capability(Capability capability) {
        this.headless = capability.isHeadless();
        this.fakeMedia = capability.isFakeMedia();
        this.enableNullVideoDecoder = capability.isNullVideoDecoderEnabled();
        this.videoFile = capability.getVideoFile();
        this.audioFile = capability.getAudioFile();
        this.flags = new ArrayList<>(capability.getFlags());
        this.extraCapabilities = new HashMap<>(capability.getExtraCapabilities());
        this.windowSize = capability.getWindowSize();
        this.recordVideo = capability.isRecordVideo();
        this.recordVideoDir = capability.getRecordVideoDir();
    }

    public void enableHeadless() {
    	this.headless = true; 
    }
    public boolean isHeadless() {
    	return headless; 
    }

    public void enableFakeMedia() {
    	this.fakeMedia = true;
    }
    public boolean isFakeMedia() {
    	return fakeMedia; 
    }

    public void enableNullVideoDecoder() {
    	this.enableNullVideoDecoder = true; 
    }
    public boolean isNullVideoDecoderEnabled() { 
    	return enableNullVideoDecoder; 
    }

    public void setVideoFile(MediaFile videoFile) { 
    	this.videoFile = videoFile; 
    }
    public MediaFile getVideoFile() { 
    	return videoFile;
    }

    public void setAudioFile(MediaFile audioFile) { 
    	this.audioFile = audioFile; 
    }
    public MediaFile getAudioFile() { 
    	return audioFile;
    }

    public void addFlag(String flag) {
    	this.flags.add(flag);
    }
    public void setFlags(List<String> flags) { 
    	this.flags = new ArrayList<>(flags); 
    }
    public List<String> getFlags() { 
    	return flags;
    }

    public void addCapability(String key, String value) { 
    	this.extraCapabilities.put(key, value); 
    }
    public Map<String, String> getExtraCapabilities() { 
    	return extraCapabilities; 
    }

    public void setWindowSize(String windowSize) { 
    	this.windowSize = windowSize; 
    }
    public String getWindowSize() { 
    	return windowSize; 
    }

    public void enableVideoRecording(String path) {
        this.recordVideo = true;
        this.recordVideoDir = path;
    }
    public boolean isRecordVideo() { 
    	return recordVideo; 
    }
    public String getRecordVideoDir() { 
    	return recordVideoDir; 
    }


    // ---------- Conversion Methods ----------
    /** Convert to Playwright LaunchOptions */
    public BrowserType.LaunchOptions toLaunchOptions() {
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(headless);

        if (!flags.isEmpty()) {
            options.setArgs(flags);
        }
        return options;
    }

    /** Convert to Playwright ContextOptions */
    public Browser.NewContextOptions toContextOptions() {
        Browser.NewContextOptions options = new Browser.NewContextOptions();

        // Window size (e.g. "1280x720")
        if (windowSize != null && windowSize.contains("x")) {
            try {
                String[] parts = windowSize.split("x");
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);
                options.setViewportSize(width, height);
            } catch (Exception ignored) {}
        }
        if (fakeMedia) {
            options.setPermissions(Arrays.asList("microphone", "camera"));
        }

        // Video recording
        if (recordVideo && recordVideoDir != null) {
            options.setRecordVideoDir(getAbsolutePath(recordVideoDir));
        }

        return options;
    }


    // ---------- Utility ----------
    private Path getAbsolutePath(String pathStr) {
        try {
            return Paths.get(pathStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
