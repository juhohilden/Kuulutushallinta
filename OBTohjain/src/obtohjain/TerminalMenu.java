/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obtohjain;

/**
 *
 * @author Juho
 */
public class TerminalMenu { 
    
    // Array for all terminals and their info
    private Terminal[] terminals;
    // Array about terminal info from server
    private byte[] terminalInfo;
    private String musicName;
    private int terminalCount;
    private int terminalInfoCount;
    
    public TerminalMenu(byte[] terminalInfo, int terminalInfoCount){
        // Saving termial info locally
        this.terminalInfo = terminalInfo;
        // How many bytes of terminal info there are
        this.terminalInfoCount = terminalInfoCount;
        terminalInfotoTerminalArray(terminalInfo);
        
    }
    
    // For changing which terminals are used
    public  int changeTerminalActiveState(int id){
        for (Terminal terminal : terminals) {
            if (id == terminal.getId()) {
                if (terminal.isOnUse()) {
                    terminal.setOnUse(false);
                    return 1;
                } else {
                    terminal.setOnUse(true);
                    return 0;
                }
            }
        }
        return 2;
    }
    
    // Get active terminals
    public Terminal[] getActiveTerminals(){
        Terminal[] aTerminals;
        // How many terminals are used
        int numberOfActiveTerminals = 0;
        for (Terminal terminal : terminals) {
            if (terminal.isOnUse()) {
                numberOfActiveTerminals++;
            }
        }
        aTerminals = new Terminal[numberOfActiveTerminals];
        int i = 0;
        int terminalPointer = 0;
        while(i < aTerminals.length){
            if(terminals[terminalPointer].isOnUse()){
                aTerminals[i] = terminals[i];
                i++;
            }
            terminalPointer++;
        }
        /*for(int i = 0; i < terminals.length; i++){
            if(terminals[i].isOnUse()){
                ids[i] = terminals[i].getId();
                System.out.println("aktive " + ids[i]);
            }
        }*/
        return aTerminals;
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
            System.out.println("selected " + terminal.isOnUse());
            System.out.println("music " + musicName);
        }
    }
    
    // Change volume of terminal
    public void changeVolume(Connection connection, int volume){
        // Getting selected terminals ids;
        int ids[]=null;
        Terminal[] aTerminals = getActiveTerminals();
        for(int i = 0; i < aTerminals.length; i++){
            ids[i] = aTerminals[i].getId();
        }
        // Command id for login
        int cmdid = 92;
        // Create byte array for volume Change
        byte[] volumeChange = byteArrayFillerForVolumeChange(cmdid, ids, volume);
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
    private byte[] byteArrayFillerForVolumeChange(int cmdid, int[] ids, int volume){
        // Setting the lenght for volume change
        int volumeLenght = 13 + 4 * ids.length;
        // Creating the byte array for login
        byte[] volumeChange = new byte[volumeLenght];
        // cmdid for volume change
        volumeChange[0] = (byte)cmdid;
        // Filling login byte array
        volumeChange[1] = (byte)volumeLenght;
        volumeChange[5] = (byte)volume;
        volumeChange[9] = (byte)(ids.length * 4); // terminal count for volume change
        for(int i = 0; i < ids.length; i++){
            volumeChange[13 + (4 * i)] = (byte)ids[i];
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
            System.out.println(e);
        }
        terminalInfo = newTerminalInfo;
        terminalInfotoTerminalArray(terminalInfo);
    }
    
    // Putting the streamed byte arrays terminal info to terminal array
    private void terminalInfotoTerminalArray(byte[] terminalInfo){
        terminalCount = terminalInfo[5];
        boolean[] states = null;
        // If this isnt first time geting terminal info
        if (terminals != null) {
            states = new boolean[terminals.length];
            // Saving the states of terminals
            for (int i = 0; i < terminals.length; i++) {
                states[i] = terminals[i].isOnUse();
            }
            // How many bytes of terminal info there are
            terminals = new Terminal[terminalCount];
        }else{
            // How many bytes of terminal info there are
            terminals = new Terminal[terminalCount];
        }
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
        for(int i = 0; i < terminals.length; i++){
            Terminal t = new Terminal();
            terminals[i] = t;
            //If states exist assign states
            if(states != null){
                terminals[i].setOnUse(states[i]);
            }
            // Checking terminals online status
            terminals[i].setOnlineStatus(terminalInfo[terminalInfoLenght + 13] == 1);
            // Checking terminal id
            terminals[i].setId(terminalInfo[terminalInfoLenght + 17]);
            // Checking terminals name
            terminalNameLenght = terminalInfo[terminalInfoLenght + 21];
            name = new char[terminalNameLenght];
            for(int j = 0; j < terminalNameLenght; j++){
                name[j] =(char) terminalInfo[terminalInfoLenght + 25 + j];
            }
            terminals[i].setName(String.copyValueOf(name));
            // Checking mac address
            macAddressLenght = terminalInfo[terminalInfoLenght + terminalNameLenght + 25];
            macAddress = new char[macAddressLenght];
            for(int j = 0; j < macAddressLenght; j++){
                macAddress[j] =(char) terminalInfo[terminalInfoLenght + terminalNameLenght + 29 + j];
            }
            terminals[i].setMacAddress(String.copyValueOf(macAddress));
            // Checking ip address
            ipAddress = new char[32];
            for(int j = 0; j < 32; j++){
                ipAddress[j] = (char) terminalInfo[terminalInfoLenght + terminalNameLenght + macAddressLenght + 29 + j];
            }
            terminals[i].setIpAddress(String.copyValueOf(ipAddress));
            // Checking volume
            terminals[i].setVolume(terminalInfo[terminalInfoLenght + terminalNameLenght + macAddressLenght + 61]);
            // Checking task status
            terminals[i].setTaskStatus(terminalInfo[terminalInfoLenght + terminalNameLenght + macAddressLenght + 69]); 
            // Checking music name
            musicNameLenght = terminalInfo[terminalInfoLenght + terminalNameLenght + macAddressLenght + 73];
            musicName = new char[musicNameLenght];
            for(int j = 0; j < musicNameLenght; j++){
                musicName[j] =(char) terminalInfo[terminalInfoLenght + terminalNameLenght + macAddressLenght + 73 + j];
            }
            this.musicName = String.copyValueOf(musicName);
            //System.out.println("muusiikki  : "+this.musicName);
            // Changing terminalInfoLenght to access array for next terminal
            //System.out.println("Bittejä terminaalissa " + terminalInfo[terminalInfoLenght + 9]);
            terminalInfoLenght = terminalInfoLenght +  terminalInfo[terminalInfoLenght + 9] + 4;
            //System.out.println(terminalInfoLenght);
        }
    }
}
