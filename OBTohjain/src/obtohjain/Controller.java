/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obtohjain;

import java.io.File;

/**
 *
 * @author Juho
 */
public class Controller {
    private Terminal[] terminal;
    private Terminal[] activeTerminals;
    private TerminalMenu terminalMenu;
    private Connection connection;
    private Authentication authentication;
    private MicReader micReader=null;
    private BroadcastMenu broadcastMenu=null;
    private String username;
    private File currentFile=null;
    private boolean micTaken=false;
    private boolean udpTaken=false;
    private TrackMenu trackMenu=null;

    public Controller() {
    }
    
    // Create connection
    public void createConnection(String ip){
        connection = new Connection(ip);
    }
    
    // End connection
    public void endConnection(){
        connection.endConnection();
    }
    
    // Login to server
    public void login(String username, String password){
        authentication = new Authentication(connection);
        this.username = username;
        int reply = authentication.login(username, password);
    }
    
    // Create terminal menu
    public void createTerminalMenu(){
        terminalMenu = new TerminalMenu(authentication.getTerminalInfo(), authentication.getTerminalInfoCount());
        //terminalMenu.printTerminalsInfo();
    }
    
    // Change volume of active terminals
    public void changeVolume(int volume){
        terminalMenu.changeVolume(connection,volume);
        terminalMenu.readNewTerminalInfo(connection);
    }
    
    // Change terminal to active
    public void setChangeActiveState(int id){
        terminalMenu.changeTerminalActiveState(id);
    }
    
    // Get track information from terminals
    public void getTerminalsTracks(){
        if(trackMenu == null){
            trackMenu = new TrackMenu();
        }
        activeTerminals = terminalMenu.getActiveTerminals();
        for (Terminal activeTerminal : activeTerminals) {
            trackMenu.getTrackListFromTerminals(connection, activeTerminal.getId());
        }
        trackMenu.printTracks();
    }
    
    // Play terminal track
    public void playTrack(int id){
        activeTerminals = terminalMenu.getActiveTerminals();
        if (trackMenu == null) {
            trackMenu = new TrackMenu();
            for (Terminal activeTerminal : activeTerminals) {
                trackMenu.getTrackListFromTerminals(connection, activeTerminal.getId());
            }
        }
        for (Terminal activeTerminal : activeTerminals) {
            trackMenu.playTrack(connection, id, username, activeTerminal.getId());
        }
    }
    
    // Manualy stop playing selected track
    public void stopTrack(){
        if (trackMenu == null) {
            return;
        }
        for (Terminal activeTerminal : activeTerminals) {
            trackMenu.stopTrack(connection, username, activeTerminal.getId());
        }
    }
    
    // Get tracks from server
    public void getServersTracks(){
        if(trackMenu == null){
            trackMenu = new TrackMenu();
        }
        trackMenu.getServerTrackList(connection);
        trackMenu.printTracks();
    }
    
    
    // Broadcast sound from mic to ip speakers
    public void broadCast(){
        // Checking if we have everything initialized and initializing if not
        if(connection.getUdpState() == false){
            connection.initializeUDPSocket();
        }
        if(broadcastMenu == null){
            broadcastMenu = new BroadcastMenu();
        }
        if(micReader == null){
            micReader = new MicReader();
        }
        // Check is some other method already use mic
        if(micTaken == false){
            micTaken = true;
        }else{
            return;
        }
        // Check if other method use udp socket
        if(udpTaken == false){
            udpTaken = true;
        }else{
            return;
        }
        // Getting currently active terminals
        activeTerminals = terminalMenu.getActiveTerminals();
        try{
            micReader.readMic(41000);
            broadcastMenu.broadCast(connection, activeTerminals, username, (int)micReader.getBroadcastFormat().getSampleRate());
            terminalMenu.readNewTerminalInfo(connection);
            broadcastMenu.sendBroadcastData(micReader, connection);
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    // Stop broadcast on active terminals
    public void stopBroadcast(){
        if(broadcastMenu == null){
            broadcastMenu = new BroadcastMenu();
        }
        if(broadcastMenu.isBroadcastOn()==false){
            return;
        }
        if(micReader == null){
            micReader = new MicReader();
        }
        // Getting currently active terminals
        activeTerminals = terminalMenu.getActiveTerminals();
        micReader.stopReadMic();
        broadcastMenu.stopBroadcast(connection, activeTerminals, username);
    }
    
    // Create new audio file with name (Will block everything else so need fix)
    public void createAudioFile(int sampleRate, String name){
        if(micReader == null){
            micReader = new MicReader();
        }
        // Check is some other method already use mic
        if(micTaken == false){
            micTaken = true;
        }else{
            return;
        }
        micReader.startRecord(sampleRate, name);
    }
    
    // Stop recording audio file
    public void stopCreatingFile(){
        if(micReader == null){
            micReader = new MicReader();
        }
        System.out.println("mic? " + micTaken);
        // Check is some other method already use mic
        if(micTaken == true){
            micTaken = false;
            System.out.println("free mic");
        }else{
            return;
        }
        System.out.println("stop");
        micReader.stopRecord();
    
    }
    
    // Send local sound file through udp to speakers
    public void playFile(String name){
        // Select file to play
        if(name != null ){
            currentFile = new File(name);
        }else{
            currentFile = new File("Temp.wav");
        }
        // If there is no file to select end method
        if(currentFile == null){
            return;
        }
        // Checking if we have everything initialized and initializing if not
        if(connection.getUdpState() == false){
            connection.initializeUDPSocket();
        }
        if(broadcastMenu == null){
            broadcastMenu = new BroadcastMenu();
        }
        if(micReader == null){
            micReader = new MicReader();
        }
        // Check if other method use udp socket
        if(udpTaken == false){
            udpTaken = true;
        }else{
            return;
        }
        // Getting currently active terminals
        activeTerminals = terminalMenu.getActiveTerminals();
        try {
            broadcastMenu.broadCast(connection, activeTerminals, username, micReader.getFileSampleRate(currentFile));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            //actionMenu.broadCast(connection, activeTerminals, username, 41000);
            broadcastMenu.sendWaveSoundFileData(connection, currentFile);
            //actionMenu.sendMp3SoundFileData(connection, currentFile);
            terminalMenu.readNewTerminalInfo(connection);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
}
