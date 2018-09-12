package ledcontrol.serialcomm.model;

/**
 * Created by Stephan on 21-12-2015.
 */
public class Status {
    private String effect;
    private int brightness;
    private boolean autoBrightness;
    private int noOfLeds;
    private int fps;
    private int freeMemory;
    private boolean debugOn;

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public boolean isAutoBrightness() {
        return autoBrightness;
    }

    public void setAutoBrightness(boolean autoBrightness) {
        this.autoBrightness = autoBrightness;
    }

    public int getNoOfLeds() {
        return noOfLeds;
    }

    public void setNoOfLeds(int noOfLeds) {
        this.noOfLeds = noOfLeds;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(int freeMemory) {
        this.freeMemory = freeMemory;
    }

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }
}
