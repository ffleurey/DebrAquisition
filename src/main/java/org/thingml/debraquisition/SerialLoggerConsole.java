package org.thingml.debraquisition;

/**
 *
 * @author franck
 */
public class SerialLoggerConsole implements DataListener {

    @Override
    public void incomingValue(String name, int value) {
        System.out.println("-> " + name + " = " + value);
    }

    public static void main(String[] args) {
        String port = "/dev/ttyACM1";
        SerialDriver driver = new SerialDriver();
        try {
        System.out.println("Opening serial port " + port);
        driver.addDataListener(new SerialLoggerConsole());
        driver.connect(port);
        } catch(Exception e) {
            e.printStackTrace();
        } 
    }
    
}
