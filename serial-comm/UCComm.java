package de.bio_photonics.uccomm;

import com.fazecast.jSerialComm.*;

/** Class with convenient methods to communicate with
 * our microcontroller. 
 */
public class UCComm {

    final SerialPort ucPort;

    /** Run UCComm with a given port */
    public UCComm(SerialPort p) {
	ucPort = p;	
    };

    /** Try to auto-detect the Arduino port. 
     *  Connects to the first Arduino found in the port list,
     *  throws an IOException if no Arduino port is found. */
    public UCComm() throws java.io.IOException {
	
	SerialPort [] portList = SerialPort.getCommPorts();
	SerialPort port=null;
	for ( SerialPort p : portList ) {
	    if (p.getDescriptivePortName().equals("Arduino Uno")) {
		port=p;
		break;
	    }
	}
	if (port==null)
	    throw new java.io.IOException("No 'Arduino Uno' port found");
	ucPort = port;
	ucPort.setBaudRate( 38400 );
	ucPort.openPort();
    }

    /** close the serial port */
    public void close() {
	ucPort.closePort();
    }

    /** get the underlying serial port */
    public SerialPort port() {
	return ucPort;
    }


    /** pause the current thread of ms milliseconds */
    public void sleep(int ms) {
	try {
	    Thread.sleep(ms);
	} catch (java.lang.InterruptedException e) {

	}
    }

    /** return however many bytes are currently waiting in the serial buffer */
    public byte [] readBytes() {
	byte [] dat = new byte [ucPort.bytesAvailable()];
	ucPort.readBytes( dat, dat.length);
	return dat;
    }

    /** return whatever has been written to the serial port as string, assuming its UTF8 */
    public String readString() {
	byte [] c = readBytes();
	return new String( c, java.nio.charset.StandardCharsets.UTF_8);	
    }

    /** read until a newline (ascii 10) is received, return the line assuming UTF8 */
    public String readLine() {
	// TODO: implement


    }
    




    /** write bytes */
    public void writeBytes(byte [] a) {
	ucPort.writeBytes(  a  , a.length);
    }


    /** write a char to the output, char has to be ASCII (0..127) */
    public void writeChar(char a) {
	if ( a > 127 )
	    throw new RuntimeException("char value not in ascii (0..127)");
	ucPort.writeBytes( new byte [] { (byte)a } , 1);
    }

    /** write a string to the port, in UTF8 encoding */
    public void writeString(String a) {
	byte [] b;
	try {
	    b = a.getBytes("UTF-8");
	} catch ( java.io.UnsupportedEncodingException e) {
	    throw new RuntimeException("UTF-8 should be supported!");
	}
	ucPort.writeBytes(  b  , b.length);
    }


    public static void main(String [] arg ) throws java.io.IOException {

	UCComm comm;
	try {
	    comm = new UCComm();
	} catch (java.io.IOException e) {
	    System.out.println("Port not found");
	    return;
	}

	if (arg.length==0)
        	comm.writeChar('c');
	else
		comm.writeString(arg[0]);

	comm.sleep(50);
	//String res = comm.readString();
	byte [] res = comm.readBytes();

	for ( byte b : res )
	    System.out.println("b: "+b);

	//System.out.println(res);



    }

}
