
package obtohjain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 *
 * @author Juho
 */
public class TerminalMenu { 
    
    // Array for all terminals and their info
    List<Terminal> terminals;
    // Array about terminal info from server
    private int terminalCount;
    
    public TerminalMenu(byte[] terminalInfo){
        terminals = new ArrayList<Terminal>();
        terminalInfotoTerminalArray(terminalInfo);
        
    }

    public List<Terminal> getTerminals() {
        return terminals;
    }
    
    // For using terminal variables from other classes
    public Terminal getTerminals(int id){
        for (Terminal terminal : terminals) {
            if (id == terminal.getId()) {
                return terminal;
            }
        }
        return null;
    }
    
    // Test if terminals are available
    public List<Terminal> getAvailableTerminals(List<Terminal> terminals) {
        int availalbesCount = 0;
        Iterator<Terminal> iterTerminals = terminals.iterator();
        while(iterTerminals.hasNext()) {
            Terminal  terminal = iterTerminals.next();
            System.out.println("handle "+terminal.getId());
            if (terminal.getTaskStatus() == 11) {
                System.out.println("available id "+terminal.getId()+" task status was "+terminal.getTaskStatus());
                availalbesCount++;
            }else{
                System.out.println("remove "+terminal.getId()+" task status was "+terminal.getTaskStatus());
                iterTerminals.remove();
            }
        }
        if (availalbesCount != 0) {
            System.out.println("return terminals " + terminals.size());
            return terminals;
        }
        // If there werent any available terminals
        return null;
    }
    
    // Set track list for terminal
    public void setTracklist(int id, Track[] trackList){
        for (Terminal terminal : terminals) {
            if (id == terminal.getId()) {
                terminal.setTracklist(trackList);
            }
        }
    }
    
    // For testing
    public void printTerminalsInfo(){
        for (Terminal terminal : terminals) {
            System.out.println("id " + terminal.getId());
            System.out.println("ip " + terminal.getIpAddress());
            System.out.println("mac " + terminal.getMacAddress());
            System.out.println("name " + terminal.getName());
            System.out.println("online " + terminal.getOnlineStatus());
            System.out.println("task " + terminal.getTaskStatus());
            System.out.println("volume " + terminal.getVolume());
            //System.out.println("selected " + terminal.isOnUse());
            System.out.println("user " + terminal.getCurrentUser());
            if(terminal.getTracklist() != null){
                for(Track track : terminal.getTracklist()){
                System.out.println("Track " + track.getName());
                }
            }
        }
    }
    
    // Change volume of terminal
    public void changeVolume(Connection connection, int volume, List<Terminal> activeTerminals){
        // Command id for login
        int cmdid = 92;
        // Create byte array for volume Change
        byte[] volumeChange = byteArrayFillerForVolumeChange(cmdid, activeTerminals, volume);
        // Sending array to server
        try{
            connection.getDataoutputStream().write(volumeChange, 0, volumeChange.length);
        }catch(Exception e){
            System.out.println(e);
        }
        try{
            connection.getDataoutputStream().flush();
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    // Fill array for terminal volume change
    private byte[] byteArrayFillerForVolumeChange(int cmdid, List<Terminal> activeTerminals, int volume){
        // Setting the lenght for volume change
        int volumeLenght = 13 + 4 * activeTerminals.size();
        // Creating the byte array for login
        byte[] volumeChange = new byte[volumeLenght];
        // cmdid for volume change
        volumeChange[0] = (byte)cmdid;
        // Filling login byte array
        volumeChange[1] = (byte)volumeLenght;
        volumeChange[5] = (byte)volume;
        volumeChange[9] = (byte)(activeTerminals.size() * 4); // terminal count for volume change
        for(int i = 0; i < activeTerminals.size(); i++){
            volumeChange[13 + (4 * i)] = (byte)activeTerminals.get(i).getId();
        }
        return volumeChange;
    }
    
    // Update terminal menu after changes
    public void readNewTerminalInfo(Connection connection){
        byte[] newTerminalInfo = new byte[1024];
        int newTerminalInfoCount = 0;
        try {
            newTerminalInfoCount = connection.getBufferedInputStream().read(newTerminalInfo);
        } catch (Exception e) {
            System.err.println("ReadNewTerminalInfo:");
            e.printStackTrace();
        }
        byte[] terminalInfo = newTerminalInfo;
        terminalInfotoTerminalArray(terminalInfo);
        printTerminalsInfo();
    }
    
    // Putting the streamed byte arrays terminal info to terminal array
    private void terminalInfotoTerminalArray(byte[] terminalInfo){
        terminalCount = terminalInfo[5];
        terminals.removeAll(terminals);
        // Variables for managing right terminal and right terminal info
        int terminalInfoLenght = 0;
        int terminalNameLenght;
        int macAddressLenght;
        int musicNameLenght;
        char[] name;
        char[] macAddress;
        char[] ipAddress;
        char[] musicName;
        // Managing all terminals one by one from servers terminal info
        for(int i = 0; i < terminalCount; i++){
            Terminal t = new Terminal();
            // Checking terminals online status
            t.setOnlineStatus(terminalInfo[terminalInfoLenght + 13] == 1);
            // Checking terminal id
            t.setId(terminalInfo[terminalInfoLenght + 17]);
            // Checking terminals name
            terminalNameLenght = terminalInfo[terminalInfoLenght + 21];
            name = new char[terminalNameLenght];
            for(int j = 0; j < terminalNameLenght; j++){
                name[j] =(char) terminalInfo[terminalInfoLenght + 25 + j];
            }
            t.setName(String.copyValueOf(name));
            // Checking mac address
            macAddressLenght = terminalInfo[terminalInfoLenght + terminalNameLenght + 25];
            macAddress = new char[macAddressLenght];
            for(int j = 0; j < macAddressLenght; j++){
                macAddress[j] =(char) terminalInfo[terminalInfoLenght + terminalNameLenght + 29 + j];
            }
            t.setMacAddress(String.copyValueOf(macAddress));
            // Checking ip address
            ipAddress = new char[32];
            for(int j = 0; j < 32; j++){
                ipAddress[j] = (char) terminalInfo[terminalInfoLenght + terminalNameLenght + macAddressLenght + 29 + j];
            }
            t.setIpAddress(String.copyValueOf(ipAddress));
            // Checking volume
            t.setVolume(terminalInfo[terminalInfoLenght + terminalNameLenght + macAddressLenght + 61]);
            // Checking task status
            t.setTaskStatus(terminalInfo[terminalInfoLenght + terminalNameLenght + macAddressLenght + 69]); 
            // Checking music name
            musicNameLenght = terminalInfo[terminalInfoLenght + terminalNameLenght + macAddressLenght + 73];
            musicName = new char[musicNameLenght];
            for(int j = 0; j < musicNameLenght; j++){
                musicName[j] =(char) terminalInfo[terminalInfoLenght + terminalNameLenght + macAddressLenght + 77 + j];
            }
            t.setCurrentUser(String.copyValueOf(musicName));
            terminals.add(t);

            terminalInfoLenght = terminalInfoLenght +  terminalInfo[terminalInfoLenght + 9] + 4;
        }
    }
}
