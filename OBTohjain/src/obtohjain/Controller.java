package obtohjain;

import java.io.File;
import java.net.DatagramSocket;

/**
 *
 * @author Juho
 */
public class Controller {
    // Mikko: this was not used so I commented it out.
    //private Terminal[] terminals;
    //private Terminal[] activeTerminals;
    private TerminalMenu terminalMenu;
    private Connection connection;
    private Authentication authentication;
    private MicReader micReader=null;
    //private Broadcast broadcastMenu=null;
    private String username;
    private File currentFile=null;
    private boolean micTaken=false;
    //private boolean udpTaken=false;
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
    public void changeVolume(int volume, int[] ids){
        terminalMenu.changeVolume(connection,volume,ids);
        terminalMenu.readNewTerminalInfo(connection);
    }
    
    // Change terminal to active
    /*public void setChangeActiveState(int id){
        terminalMenu.changeTerminalActiveState(id);
    }*/
    
    // Get track information from terminals
    public void getTerminalsTracks(int[] ids){
        if(trackMenu == null){
            trackMenu = new TrackMenu();
        }
        /*activeTerminals = terminalMenu.getActiveTerminals();
        for (Terminal activeTerminal : activeTerminals) {
            trackMenu.getTrackListFromTerminals(connection, activeTerminal.getId());
        }*/
        for (int i = 0; i < ids.length; i++) {
            trackMenu.getTrackListFromTerminals(connection, ids[i]);
        }
        trackMenu.printTracks();
    }
    
    // Play terminal track with id 
    // Change logic work with given ids instead of activeTerminals
    public void playTrack(int id, int[] ids){
        //activeTerminals = terminalMenu.getActiveTerminals();
        if (trackMenu == null) {
            trackMenu = new TrackMenu();
            /*for (Terminal activeTerminal : activeTerminals) {
                trackMenu.getTrackListFromTerminals(connection, activeTerminal.getId());
            }*/
            for (int i = 0; i < ids.length; i++) {
                trackMenu.getTrackListFromTerminals(connection, ids[i]);
            }
        }
        /*for (Terminal activeTerminal : activeTerminals) {
            trackMenu.playTrack(connection, id, username, activeTerminal.getId());
        }*/
        for (int i = 0; i < ids.length; i++) {
            trackMenu.playTrack(connection, id, username, ids[i]);
        }
    }
    
    // Manualy stop playing selected track
    public void stopTrack(int[] ids){
        if (trackMenu == null) {
            return;
        }
        /*for (Terminal activeTerminal : activeTerminals) {
            trackMenu.stopTrack(connection, username, activeTerminal.getId());
        }*/
        for (int i = 0; i < ids.length; i++) {
            trackMenu.stopTrack(connection, username, ids[i]);
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
    public void broadCast(int[] ids){
        // Checking if we have everything initialized and initializing if not
        /*if(connection.getUdpState() == false){
            connection.initializeUDPSocket();
        }*/
        /*if(broadcastMenu == null){
            broadcastMenu = new Broadcast();
        }*/
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
        /*if(udpTaken == false){
            udpTaken = true;
        }else{
            return;
        }*/
        // Getting currently active terminals
        //activeTerminals = terminalMenu.getActiveTerminals();
        try{
            micReader.readMic(41000);
            UDPSocket udpSocket = connection.getUDPSocket();
            if(udpSocket == null){
                return;
            }
            Broadcast broadcast = new Broadcast(connection, ids, username, (int)micReader.getBroadcastFormat().getSampleRate(), udpSocket);
            terminalMenu.readNewTerminalInfo(connection);
            broadcast.sendBroadcastData(micReader);
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    // Stop broadcast on active terminals
    // Find way call instance of broadcast
    /*public void stopBroadcast(int[] ids){
        if(broadcastMenu == null){
            broadcastMenu = new Broadcast();
        }
        if(broadcastMenu.isBroadcastOn()==false){
            return;
        }
        if(micReader == null){
            micReader = new MicReader();
        }
        // Getting currently active terminals
        //activeTerminals = terminalMenu.getActiveTerminals();
        micReader.stopReadMic();
        broadcastMenu.stopBroadcast(connection, ids, username);
    }*/
    
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
    public void playFile(String name, int[] ids){
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
        /*if(connection.getUdpState() == false){
            connection.initializeUDPSocket();
        }*/
        /*if(broadcastMenu == null){
            broadcastMenu = new Broadcast();
        }*/
        if(micReader == null){
            micReader = new MicReader();
        }
        // Check if other method use udp socket
        /*if(udpTaken == false){
            udpTaken = true;
        }else{
            return;
        }*/
        // Getting currently active terminals
        //activeTerminals = terminalMenu.getActiveTerminals();
        try {
            UDPSocket udpSocket = connection.getUDPSocket();
            if(udpSocket == null){
                return;
            }
            Broadcast broadcast = new Broadcast(connection, ids, username, micReader.getFileSampleRate(currentFile), udpSocket);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            //actionMenu.broadCast(connection, activeTerminals, username, 41000);
            broadcast.sendWaveSoundFileData(currentFile);
            //actionMenu.sendMp3SoundFileData(connection, currentFile);
            terminalMenu.readNewTerminalInfo(connection);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
}
