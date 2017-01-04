package obtohjain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 *
 * @author Juho
 */
public class Controller implements Broadcast.OnPagingCompleteListener  {
    // Mikko: this was not used so I commented it out.
    //private Terminal[] terminals;
    //private Terminal[] activeTerminals;
    private TerminalMenu terminalMenu=null;
    private Connection connection;
    private Authentication authentication=null;
    private MicReader micReader=null;
    //private Broadcast broadcastMenu=null;
    private String username;
    private File currentFile=null;
    private boolean micTaken=false;
    //private boolean udpTaken=false;
    private TrackMenu trackMenu=null;
    private OnBroadcastComplete listener;
    //private int[] broadcastingIds = new int[32];
    //private int broadcastingIdsCount=0;
    List<Broadcast> broadcasts;
    private List<Terminal> activeTerminals; 
    
    public Controller() {
        broadcasts = new ArrayList<Broadcast>();
    }
    
    public Controller(OnBroadcastComplete listener){
        this.listener = listener;
    }
    
    public interface OnBroadcastComplete{
        public void onBroadcastComplete();
    }
    
    // Create connection
    public boolean createConnection(String ip) throws IOException{
        connection = new Connection(ip);
        return connection.testConnection();
    }
    
    // End connection
    public void endConnection(){
        connection.endConnection();
    }
    
    // Login to server
    public int login(String username, String password){
        authentication = new Authentication(connection);
        this.username = username;
        int reply = authentication.login(username, password);
        return reply;
    }
    
    // Create terminal menu
    public void createTerminalMenu(){
        if(authentication != null){
            terminalMenu = new TerminalMenu(authentication.getTerminalInfo());
            //terminalMenu.printTerminalsInfo();
        }
    }
    
    // Return terminals and terminals states
    public List<Terminal> getTerminals(){
        if(terminalMenu != null){
            return terminalMenu.getTerminals();
        }else{
            List<Terminal> nullTerminals = new ArrayList<Terminal>();
            Terminal nullTerminal = new Terminal();
            nullTerminals.add(nullTerminal);
            return nullTerminals;
        }        
    }
    
    // Return terminals and terminals states
    /*public List<Terminal> getTerminalsLists(){
        if(terminalMenu != null){
            //return terminalMenu.getTerminals();
            return terminalMenu.getTerminals();
        }else{
            Terminal nullTerminal = new Terminal();
            Terminal[] nullTerminals = {nullTerminal};
            return nullTerminals;
        }        
    }*/
    
    
    // Return terminals with ids
    public List<Terminal> getTerminal(int[] ids){
        if(terminalMenu != null && ids != null && ids.length >= 1){
            List<Terminal> terminals = new ArrayList<Terminal>();
            for(int i = 0; i < ids.length; i++){
                Terminal terminal = terminalMenu.getTerminals(ids[i]);
                terminals.add(terminal);
            }
            return terminals;
        }else{
            List<Terminal> nullTerminals = new ArrayList<Terminal>();
            Terminal nullTerminal = new Terminal();
            nullTerminals.add(nullTerminal);
            return nullTerminals;
        }
    }
    
    // Change volume of active terminals
    public void changeVolume(int volume, List<Terminal>terminals){
        if(terminalMenu == null){
            return;
        }
        if(terminals == null){
           return; 
        }
        List<Terminal> tempTerminals = new ArrayList<Terminal>();
        for(int i = 0; i < terminals.size(); i++){
            for(Terminal terminal : terminalMenu.getTerminals()){
                if(terminals.get(i).getId() == terminal.getId() && volume != terminal.getVolume()){
                    tempTerminals.add(terminals.get(i));
                }
            }
        }
        if(tempTerminals.size() >= 1){
            terminalMenu.changeVolume(connection,volume,tempTerminals);
            terminalMenu.readNewTerminalInfo(connection);
        }
        //terminalMenu.printTerminalsInfo();
    }
    
    // Change terminal to active
    /*public void setChangeActiveState(int id){
        terminalMenu.changeTerminalActiveState(id);
    }*/
    
    // Get track information from terminals
    public void getTerminalsTracks(List<Terminal> terminals){
        if(terminalMenu == null){
            return;
        }
        if(terminals == null){
            return;
        }
        if(trackMenu == null){
            trackMenu = new TrackMenu();
        }
        /*activeTerminals = terminalMenu.getActiveTerminals();
        for (Terminal activeTerminal : activeTerminals) {
            trackMenu.getTrackListFromTerminals(connection, activeTerminal.getId());
        }*/
        for (int i = 0; i < terminals.size(); i++) {
            trackMenu.getTrackListFromTerminals(connection, terminals.get(i).getId());
        }
        for(int i = 0; i < terminals.size(); i++){
            terminalMenu.setTracklist(terminals.get(i).getId(), trackMenu.getTracklist());
        }
        // terminalInfo maybe need to be read
        //terminalMenu.printTerminalsInfo();
        //trackMenu.printTracks();
    }
    
    
    // Play terminal track with id 
    // Change logic work with given ids instead of activeTerminals
    public void playTrack(int id, List<Terminal> terminals){
        // Check if ids were sent
        if(terminals == null){
            return;
        }
        // If terminalmenu exists
        if(terminalMenu == null){
            return;
        }
        // If terminals with ids are available
        terminals = terminalMenu.getAvailableTerminals(terminals);
        if(terminals == null){
            return;
        }

        if (trackMenu == null) {
            trackMenu = new TrackMenu();
            /*for (Terminal activeTerminal : activeTerminals) {
                trackMenu.getTrackListFromTerminals(connection, activeTerminal.getId());
            }*/
            for (int i = 0; i < terminals.size(); i++) {
                trackMenu.getTrackListFromTerminals(connection, terminals.get(i).getId());
            }
        }
        /*for (Terminal activeTerminal : activeTerminals) {
            trackMenu.playTrack(connection, id, username, activeTerminal.getId());
        }*/
        for (int i = 0; i < terminals.size(); i++) {
            
            trackMenu.playTrack(connection, id, username, terminals.get(i).getId());
        }
        terminalMenu.readNewTerminalInfo(connection);
    }
    
    // Manualy stop playing selected track
    public void stopTrack(List<Terminal> terminals){
        if (trackMenu == null) {
            return;
        }
        if(terminals == null){
            return;
        }
        /*for (Terminal activeTerminal : activeTerminals) {
            trackMenu.stopTrack(connection, username, activeTerminal.getId());
        }*/
        for (int i = 0; i < terminals.size(); i++) {
            trackMenu.stopTrack(connection, username, terminals.get(i).getId());          
        }
        // Need check if terminal is sending first
        terminalMenu.readNewTerminalInfo(connection);
    }
    
    // Get tracks from server
    public Track[] getServersTracks(){
        if(trackMenu == null){
            trackMenu = new TrackMenu();
        }
        trackMenu.getServerTrackList(connection);
        trackMenu.printTracks();
        return trackMenu.getTracklist();
        //
    }
    
    
    // Broadcast sound from mic to ip speakers
    public void broadCast(List<Terminal> terminals){
        // Checking if we have everything initialized and initializing if not
        /*if(connection.getUdpState() == false){
            connection.initializeUDPSocket();
        }*/
        /*if(broadcastMenu == null){
            broadcastMenu = new Broadcast();
        }*/
        // Check if ids were sent
        if(terminals == null){
            return;
        }
        // If terminalmenu exists
        if(terminalMenu == null){
            return;
        }
        // If terminals with ids are available
        terminals = terminalMenu.getAvailableTerminals(terminals);
        // Checking if terminal is allready broadcasting
        /*for(Broadcast curBCast: broadcasts){
            List<Terminal> tempTerminals = curBCast.getTerminals().;
            for(int i = 0; i < tempIds.length; i++){
                for(int j = 0; j < ids.length; j++){
                    if(tempIds[i] == ids[j]){
                        return;
                    }
                }
            }
        }*/
        if(terminals == null){
            return;
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
            Broadcast broadcast = new Broadcast(connection, terminals, username, (int)micReader.getBroadcastFormat().getSampleRate(), udpSocket, this);
            terminalMenu.readNewTerminalInfo(connection);
            broadcast.sendBroadcastData(micReader);
            broadcasts.add(broadcast);
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    // Stop broadcast on active terminals
    // Find way call instance of broadcast
    public void stopBroadcast(List<Terminal> terminals){
        /*if(broadcastMenu == null){
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
        broadcastMenu.stopBroadcast(connection, ids, username);*/
        // Check if ids were sent
        if(terminals == null){
            return;
        }
        Broadcast tempForStop=null;
        for(Broadcast curBCast: broadcasts){
            //int[] tempIds = curBCast.getIds();
            /*for(int i = 0; i < curBCast.getTerminals().size(); i++){
                for(int j = 0; j < terminals.size(); j++){
                    if(tempIds[i] == ids[j]){
                        tempForStop = curBCast;
                    }
                }
            }*/
            if(terminals.size() == curBCast.getTerminals().size() && terminals.contains(curBCast.getTerminals())){
                tempForStop = curBCast;
            }
        }
        if(tempForStop != null){
            tempForStop.stopBroadcast();
            terminalMenu.readNewTerminalInfo(connection);
            if(tempForStop.isMicBroadcast()){
                micTaken = false;
            }
            broadcasts.remove(tempForStop);
        }
        
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
            return;
        }
        if(micReader.getRecordingState() == false){
            return;
        }
        //System.out.println("mic? " + micTaken);
        // Check is some other method already use mic
        if(micTaken == true){
            micTaken = false;
            //System.out.println("free mic");
        }else{
            return;
        }
        //System.out.println("stop");
        micReader.stopRecord();
    
    }
    
    // Send local sound file through udp to speakers
    public void playFile(String name, List<Terminal> terminals){
        // Check if ids were sent
        if(terminals == null){
            return;
        }
        // If terminalmenu exists
        if(terminalMenu == null){
            return;
        }
        // If terminals with ids are available
        terminals = terminalMenu.getAvailableTerminals(terminals);
        if(terminals == null){
            return;
        }
        // Checking if terminal is allready broadcasting
        /*for(Broadcast curBCast: broadcasts){
            int[] tempIds = curBCast.getIds();
            for(int i = 0; i < tempIds.length; i++){
                for(int j = 0; j < ids.length; j++){
                    if(tempIds[i] == ids[j]){
                        return;
                    }
                }
            }  
        }*/
        // Select file to play
        if(name != null ){
            currentFile = new File(name);
        }else{
            currentFile = new File("Temp.wav");// Maybe crash here
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
            Broadcast broadcast = new Broadcast(connection, terminals, username, micReader.getFileSampleRate(currentFile), udpSocket, this);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            //actionMenu.broadCast(connection, activeTerminals, username, 41000);
            broadcast.sendWaveSoundFileData(currentFile);
            broadcasts.add(broadcast);
            //actionMenu.sendMp3SoundFileData(connection, currentFile);
            terminalMenu.readNewTerminalInfo(connection);
        } catch (Exception e) {
            System.out.println("Playfile broadcast error: " + e);
        }
    }

    @Override
    public void onPagingComplete() {
         System.out.println("OnPagingComplete");
        currentFile.delete();
        if(listener != null){
            listener.onBroadcastComplete();
        }
    }
    
    
}
