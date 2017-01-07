package obtohjain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Iterator;

/**
 *
 * @author Juho
 */
public class Controller implements Broadcast.OnPagingCompleteListener  {
    // Mikko: this was not used so I commented it out.
    private TerminalMenu terminalMenu=null;
    private Connection connection;
    private Authentication authentication=null;
    private MicReader micReader=null;
    private String username;
    private File currentFile=null;
    //private boolean micTaken=false;
    private TrackMenu trackMenu=null;
    private OnBroadcastComplete listener;
    private List<Broadcast> broadcasts;
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
        // Stoping all broadcast before stopping connection
        if(broadcasts.size() > 0){
            stopBroadcast(terminalMenu.getTerminals());
        }
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
        }
    }
    
    // Get if mic is taken to inform user
    public boolean isMicUsed(){
        if(micReader == null){
            micReader = new MicReader();
            return micReader.getRecordingState();
        }
        return micReader.getRecordingState();
    }
    
    // Return terminals and terminals states
    public List<Terminal> getTerminals(){
        // Get list of all terminals if possible otherwise give list with one temp terminal
        if(terminalMenu != null){
            return terminalMenu.getTerminals();
        }else{
            List<Terminal> nullTerminals = new ArrayList<Terminal>();
            Terminal nullTerminal = new Terminal();
            nullTerminals.add(nullTerminal);
            return nullTerminals;
        }        
    }

    // Return terminals with ids
    public List<Terminal> getTerminal(int[] ids){
        // Get list of terminals with given ids if possible otherwise give list with one temp terminal
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
        // Avoiding null pointer
        if(terminalMenu == null){
            return;
        }
        // Avoiding null pointer
        if(terminals == null){
           return; 
        }
        // Creating temp arraylist about given terminal whose volume is realy about to change
        List<Terminal> tempTerminals = new ArrayList<Terminal>();
        for(int i = 0; i < terminals.size(); i++){
            for(Terminal terminal : terminalMenu.getTerminals()){
                if (terminal != null && terminals.get(i) != null) {
                    if (terminals.get(i).getId() == terminal.getId() && volume != terminal.getVolume()) {
                        tempTerminals.add(terminals.get(i));
                    }
                }
            }
        }
        // If someones volume is realy changing
        if(tempTerminals.size() >= 1){
            terminalMenu.changeVolume(connection,volume,tempTerminals);
            terminalMenu.readNewTerminalInfo(connection);
        }
    }
    
    // Get track information from terminals
    public void getTerminalsTracks(List<Terminal> terminals){
        // Avoiding null pointer
        if(terminalMenu == null){
            return;
        }
        // Avoiding null pointer
        if(terminals == null){
            return;
        }
        // Initializing trackmenu if it isnt already
        if(trackMenu == null){
            trackMenu = new TrackMenu();
        }
        // Trying to read and assigne trackinfo to all given terminals // Sometimes only first command send
        for (int i = 0; i < terminals.size(); i++) {
            trackMenu.getTrackListFromTerminals(connection, terminals.get(i).getId());
        }
        for(int i = 0; i < terminals.size(); i++){
            terminalMenu.setTracklist(terminals.get(i).getId(), trackMenu.getTracklist());
        }
    }
    
    
    // Play terminal track with id 
    public void playTrack(int id, List<Terminal> terminals){
        // Avoiding null pointer
        if(terminals == null){
            return;
        }
        // Avoiding null pointer
        if(terminalMenu == null){
            return;
        }
        // If terminals are available
        terminals = terminalMenu.getAvailableTerminals(terminals);
        // Avoiding null pointer
        if(terminals == null){
            return;
        }
        // Initializing trackMenu if isnt already
        if (trackMenu == null) {
            trackMenu = new TrackMenu();
            for (int i = 0; i < terminals.size(); i++) {
                trackMenu.getTrackListFromTerminals(connection, terminals.get(i).getId());
            }
        }
        // Trying to play given file from all given terminals // Sometimes only first play command is send
        for (int i = 0; i < terminals.size(); i++) {
            trackMenu.playTrack(connection, id, username, terminals.get(i).getId());
        }
    }
    
    // Manualy stop playing selected track
    public void stopTrack(List<Terminal> terminals){
        // Avoiding null pointer
        if (trackMenu == null) {
            return;
        }
        // Avoiding null pointer
        if(terminals == null){
            return;
        }
        for (int i = 0; i < terminals.size(); i++) {
            trackMenu.stopTrack(connection, username, terminals.get(i).getId());          
        }
    }
    
    // Get tracks from server
    public Track[] getServersTracks(){
        if(trackMenu == null){
            trackMenu = new TrackMenu();
        }
        trackMenu.getServerTrackList(connection);
        return trackMenu.getTracklist();
    }
    
    
    // Broadcast sound from mic to ip speakers
    public void broadCast(List<Terminal> terminals){
        // Avoiding null pointer
        if(terminals == null){
            return;
        }
        // Avoiding null pointer
        if(terminalMenu == null){
            return;
        }
        // If terminals  are available
        terminals = terminalMenu.getAvailableTerminals(terminals);
        // Avoiding null pointer
        if(terminals == null){
            return;
        }
        // Initializing micreader if it isnt already
        if(micReader == null){
            micReader = new MicReader();
        }
        // Check is some other method already use mic
        if(micReader.getRecordingState()){
            return;
        }
        // Trying to get udpsocket and broadcast
        try{
            micReader.readMic(41000);
            UDPSocket udpSocket = connection.getUDPSocket();
            if(udpSocket == null){
                return;
            }
            Broadcast broadcast = new Broadcast(connection, terminals, username, (int)micReader.getBroadcastFormat().getSampleRate(), udpSocket, terminalMenu, this);
            broadcast.sendBroadcastData(micReader);
            broadcasts.add(broadcast);
        }catch(Exception e){
            System.out.println("Broadcast failed" + e);
        }
    }
    
    // Stop broadcast on given terminals
    public void stopBroadcast(List<Terminal> terminals){
        // Avoiding null pointer
        if(terminals == null){
            return;
        }
        // Iterating if broadcasts are still going
        Boolean stopBCastSend = false;
        Iterator<Broadcast> iterBroadcasts = broadcasts.iterator();
        int counter=0;
        while(iterBroadcasts.hasNext()){
            System.out.println("Broadcast "+counter);
            counter++;
            Broadcast tempBCast = iterBroadcasts.next();
            Iterator<Terminal> iterTerminal = tempBCast.getTerminals().iterator();
            List<Terminal> stopBroadcastTerminal = new ArrayList<Terminal>();
            // Removing all empty broadcasts
            if(tempBCast.getTerminals().isEmpty()){
                iterBroadcasts.remove();
                System.out.println("Empty broadcast removed");
            // If command to stop broadcast havent been send to server send one
            }else{
                while(iterTerminal.hasNext()){
                    Terminal tempTerminal = iterTerminal.next();
                    System.out.println("Terminal "+tempTerminal.getId());
                    Iterator<Terminal> iterTerminals = terminals.iterator();                    
                    while(iterTerminals.hasNext()){
                        Terminal tempTerminals = iterTerminals.next();
                        System.out.println("Terminal "+tempTerminals.getId());
                        if(tempTerminal.getId() == tempTerminals.getId()){
                            stopBroadcastTerminal.add(tempTerminals);
                        }
                    }
                }
                if(stopBroadcastTerminal.size() > 0){
                    tempBCast.stopBroadcast(stopBroadcastTerminal);
                    stopBCastSend = true;
                    if (tempBCast.getTerminals().isEmpty()) {
                        iterBroadcasts.remove();
                        System.out.println("Empty broadcast removed");
                    }
                }
            }
        }
    }
    
    // Create new audio file with name (Will block everything else so need fix)
    public void createAudioFile(int sampleRate, String name){
        if(micReader == null){
            micReader = new MicReader();
        }
        // Check is some other method already use mic
        if(micReader.getRecordingState()){
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
        micReader.stopRecord();
    
    }
    
    // Send local sound file through udp to speakers
    public void playFile(String name, List<Terminal> terminals){
        // Avoiding null pointer
        if(terminals == null){
            return;
        }
        // Avoiding null pointer
        if(terminalMenu == null){
            return;
        }
        // If terminals with ids are available
        terminals = terminalMenu.getAvailableTerminals(terminals);
        // Avoiding null pointer
        if(terminals == null){
            return;
        }
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
        // Initializing micreader if it alreasy isnt
        if(micReader == null){
            micReader = new MicReader();
        }
        // Try to get udpsocket and broadcast given file
        try {
            UDPSocket udpSocket = connection.getUDPSocket();
            if(udpSocket == null){
                return;
            }
            Broadcast broadcast = new Broadcast(connection, terminals, username, micReader.getFileSampleRate(currentFile), udpSocket, terminalMenu, this);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            broadcast.sendWaveSoundFileData(currentFile);
            broadcasts.add(broadcast);
            //actionMenu.sendMp3SoundFileData(connection, currentFile);
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
