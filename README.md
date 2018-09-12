# LedControl
Java client that connects to Arduino via USB and controls a ledstrip

## Two modules in this project

### SerialComm
This is a custom Camel component for serial (USB) communication and is used by the WebControl java client.

### WebControl
This is my java web client that connect to my arduino via the USB port.
The webclient runs on a desktop but ulimately it will be installed on the Raspberry Pi.


## Notes

Link: [serial-communication-in-java-with-raspberry-pi-and-rxtx](http://eclipsesource.com/blogs/2012/10/17/serial-communication-in-java-with-raspberry-pi-and-rxtx/)

Installeren java lib: `sudo apt-get install librxtx-java`

serial device op mijn RPi: `/dev/ttyACM0`