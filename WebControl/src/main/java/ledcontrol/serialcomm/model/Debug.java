package ledcontrol.serialcomm.model;

public class Debug {
    private boolean debugOn;


    public Debug() {
    }

    public Debug(boolean debugOn) {
        this.debugOn = debugOn;
    }

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }
}
