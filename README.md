# LedControl
Java client that connects to Arduino via USB and controls a ledstrip

## Two modules in this project

### SerialComm
This is a custom Camel component for serial (USB) communication and is used by the WebControl java client.

### WebControl
This is my java web client that connect to my arduino via the USB port.
The webclient runs on a desktop but ulimately it will be installed on the Raspberry Pi.
