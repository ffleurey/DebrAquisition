package org.thingml.debraquisition;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 *
 * @author franck
 */
public class SerialDriver implements SerialPortEventListener {
    
    /*
     * Implemenatation of the observer pattern
     */
    protected ArrayList<DataListener> listeners = new ArrayList<DataListener>();
    public void addDataListener(DataListener l) {
        listeners.add(l);
    }
    public void removeDataListener(DataListener l) {
        listeners.remove(l);
    }
    public void clearDataListeners() {
        listeners.clear();
    }
    
    /*
     * Implementation of the serial port
     */
    
    public SerialPort serialPort = null;
    
    public boolean connect(String portName) {
        
        serialPort = new SerialPort(portName);
        try {
            serialPort.openPort();

            serialPort.setParams(SerialPort.BAUDRATE_9600,
                                 SerialPort.DATABITS_8,
                                 SerialPort.STOPBITS_1,
                                 SerialPort.PARITY_NONE);

            //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
            //                              SerialPort.FLOWCONTROL_RTSCTS_OUT);

            serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);

            //serialPort.writeString("Hello!");
        }
        catch (SerialPortException ex) {
            System.err.println("Error opening serial port \""+portName+"\" : " + ex);
            return false;
        }
        return true;
    }
    
    public void disconnect() {
        try {
            serialPort.closePort();
        } catch (SerialPortException ex) {
            System.err.println("Error closing serial port : " + ex);
        }
    }
    
    /*
     * This procedure receives messages like ":attribute_name=value;" (where value is an integer).
     * Whenever a message is received it is forwarded to all listeners.
     */
    
    String buffer = "";
    Pattern msg_pattern = Pattern.compile(":(\\w+)=(\\d+);");
    
    @Override
    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                buffer += serialPort.readString(event.getEventValue());
                
                while (buffer.indexOf(';') > 0) {
                    String input = buffer.substring(0, buffer.lastIndexOf(';')+1);
                    buffer = buffer.substring(buffer.indexOf(';')+1);
                    
                    Matcher msg_matcher = msg_pattern.matcher(input);
                    
                    while (msg_matcher.find()) {
                        for (DataListener l : listeners)
                        l.incomingValue(msg_matcher.group(1), Integer.parseInt(msg_matcher.group(2)));
                    }
                }
            }
            catch (SerialPortException ex) {
                System.err.println("Error receiving from serial port: " + ex);
            }
        }
    }
    
}
