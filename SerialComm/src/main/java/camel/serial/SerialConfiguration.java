package camel.serial;

/**
 * Parse a given uri and set the configuration for the specified parameters in the uri
 *
 */
public class SerialConfiguration {

    private String portName;
    private int baudrate;
    private int databits;
    private int stopbits;
    
    /**
     *  PARITY_ODD   = 1;
     *  PARITY_EVEN  = 2;
     *  PARITY_MARK  = 3;
     *  PARITY_SPACE = 4;
     */
    private int parity;
	
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	
	public int getBaudrate() {
		return baudrate;
	}
	public void setBaudrate(int baudrate) {
		this.baudrate = baudrate;
	}
	
	public int getDatabits() {
		return databits;
	}
	public void setDatabits(int databits) {
		this.databits = databits;
	}
	
	public int getStopbits() {
		return stopbits;
	}
	public void setStopbits(int stopbits) {
		this.stopbits = stopbits;
	}
	
	public int getParity() {
		return parity;
	}
	public void setParity(int parity) {
		this.parity = parity;
	}
}
