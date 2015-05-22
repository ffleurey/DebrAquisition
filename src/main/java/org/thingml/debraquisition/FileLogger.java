package org.thingml.debraquisition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author franck
 */
public class FileLogger implements DataListener {

    FileWriter fw;
    
    public FileLogger(File f) throws IOException {
        fw = new FileWriter(f);
    }
    
    public void close() {
        try {
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(FileLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void incomingValue(String name, int value) {
        try {
            
            fw.append(Calendar.getInstance().getTime().toString());
            fw.append(", ");
            fw.append(name);
            fw.append(", " + value);
            fw.append("\n");
        } catch (IOException ex) {
            Logger.getLogger(FileLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
