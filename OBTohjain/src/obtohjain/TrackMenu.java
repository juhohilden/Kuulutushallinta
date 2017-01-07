package obtohjain;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 *
 * @author Juho
 */
public class TrackMenu {
    
    // Music Played in tasks need to be send to terminals before playing
    // Only server can send music to terminals but its not show on mesages?
    // When getting tracks from server the path to music directory need to be set on server
    private Track[] trackList = null;
    
    public Track[] getTracklist(){
        return trackList;
    }
    public void getTrackListFromTerminals(Connection connection, int id){
        // Command id for getting terminals tracklist
        int cmdid = 50;
        // Create byte array for getting terminals tracks
        byte[] terminalsTracks = byteArrayFillerForTerminalsTracks(cmdid, id);
        // Sending array to server
        try{
            connection.getDataoutputStream().write(terminalsTracks, 0, terminalsTracks.length);
        }catch(Exception e){
            System.out.println(e);
        }
        try{
            connection.getDataoutputStream().flush();
        }catch(Exception e){
            System.out.println(e);
        }
        // Initializing array for new terminal track information
        byte[] newTerminalTracks = new byte[1024];
        int newTerminalTracksCount = 0;
        // Getting the terminals tracks information from server
        try {
            newTerminalTracksCount = connection.getBufferedInputStream().read(newTerminalTracks);
        } catch (Exception e) {
            System.out.println(e);
        }
        // Checking if we realy get terminals tracks information
        if(newTerminalTracks[0] == 50){
            // Initializing tracklist
            trackList = new Track[newTerminalTracks[1]];
            int trackInfoLenght = 0;
            for(int i = 0; i < trackList.length; i++){
                // Creating temporary track object to place in array
                Track tempTrack = new Track();
                // Set id for track
                tempTrack.setId(i);
                // Getting the track name from byte array
                int trackNameLenght = newTerminalTracks[5 + trackInfoLenght];
                char[] trackName = new char[trackNameLenght];
                for(int j = 0; j < trackNameLenght; j++){
                    trackName[j] =(char) newTerminalTracks[trackInfoLenght + 9 + j];
                }
                tempTrack.setName(String.copyValueOf(trackName));
                // Transfering byte array to int through bytebuffer to get song duration
                byte[] toBigInt = new byte[4];
                for(int j = 0; j < toBigInt.length; j++){
                    toBigInt[j] = newTerminalTracks[trackInfoLenght + 9 + trackNameLenght + j];
                }
                ByteBuffer buffer = ByteBuffer.wrap(toBigInt);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                tempTrack.setDuration(secondsToDuration(buffer.getInt())); // Get wrong duration from terminal but problems is whit saving??
                // Transfering byte array to int through bytebuffer to get filesize
                byte[] toBigInt2 = new byte[4];
                for(int j = 0; j < toBigInt2.length; j++){
                    toBigInt2[j] = newTerminalTracks[trackInfoLenght + 13 + trackNameLenght + j];
                }
                ByteBuffer buffer2 = ByteBuffer.wrap(toBigInt2);
                buffer2.order(ByteOrder.LITTLE_ENDIAN);
                tempTrack.setFileSize(Integer.toString(buffer2.getInt()/1024) + " kb");
                trackList[i] =  tempTrack;
                // Getting the count of already used data
                trackInfoLenght = trackInfoLenght + trackNameLenght + 12;
            }
        }else{
            System.out.println("Is not terminal information");
        }
        
    }
    
    // Get servers tracklist
    public void getServerTrackList(Connection connection){
        // Command id for getting servers tracklist
        int cmdid = 48;
        // Create byte array for servers tracks
        byte[] serversTracks = byteArrayFillerForServersTracks(cmdid);
        // Sending array to server
        try{
            connection.getDataoutputStream().write(serversTracks, 0, serversTracks.length);
        }catch(Exception e){
            System.out.println(e);
        }
        try{
            connection.getDataoutputStream().flush();
        }catch(Exception e){
            System.out.println(e);
        }
        // Initializing array for new servers track information
        byte[] newServersTracks = new byte[1024];
        int newServerTracksCount = 0;
        // Getting the terminals tracks information from server
        try {
            newServerTracksCount = connection.getBufferedInputStream().read(newServersTracks);
        } catch (Exception e) {
            System.out.println(e);
        }
         // Checking if we realy get terminals tracks information
        if(newServersTracks[0] == 48){
            // Initializing tracklist
            trackList = new Track[newServersTracks[5]];
            int trackInfoLenght = 0;
            for(int i = 0; i < trackList.length; i++){
                // Creating temporary track object to place in array
                Track tempTrack = new Track();
                // Set id for track
                tempTrack.setId(i);
                // Getting the track name from byte array
                int trackNameLenght = newServersTracks[9 + trackInfoLenght];
                char[] trackName = new char[trackNameLenght];
                for(int j = 0; j < trackNameLenght; j++){
                    trackName[j] =(char) newServersTracks[trackInfoLenght + 13 + j];
                }
                tempTrack.setName(String.copyValueOf(trackName));
                // Getting the track size from byte array
                int trackSizeLenght = newServersTracks[13 + trackInfoLenght + trackNameLenght];
                char[] trackSize = new char[trackNameLenght];
                for(int j = 0; j < trackSizeLenght; j++){
                    trackSize[j] =(char) newServersTracks[trackInfoLenght + 17 + j + trackNameLenght];
                }
                tempTrack.setFileSize(String.copyValueOf(trackSize));
                //Getting if server track is folder instead of file
                if(newServersTracks[trackInfoLenght + 17 + trackNameLenght + trackSizeLenght] == 0){
                    tempTrack.fileIsFolder();
                }
                // Getting the track duration from byte array
                int trackDurationLenght = newServersTracks[21 + trackInfoLenght + trackNameLenght + trackSizeLenght];
                char[] trackDuration = new char[trackDurationLenght];
                for(int j = 0; j < trackDurationLenght; j++){
                    trackDuration[j] =(char) newServersTracks[trackInfoLenght + 25 + j + trackNameLenght + trackSizeLenght];
                }
                tempTrack.setDuration(String.copyValueOf(trackDuration));
                trackList[i] =  tempTrack;
                // Getting the count of already used data
                trackInfoLenght = trackInfoLenght + trackNameLenght + trackSizeLenght +  trackDurationLenght + 16;
            }
        }else{
            System.out.println("Wrong information");
        }
    }
    
    // Play song from terminal
    public void playTrack(Connection connection, int id, String username, int terminalId){
        if(id > trackList.length){
            return;
        }
        // Command id for playing track
        int cmdid = 70;
        // Create byte array for playing track
        byte[] playingTrack = byteArrayFillerForTrackPlaying(cmdid, id, username, terminalId);
        // Sending array to server
        try{
            connection.getDataoutputStream().write(playingTrack, 0, playingTrack.length);
        }catch(Exception e){
            System.out.println(e);
        }
        try{
            connection.getDataoutputStream().flush();
        }catch(Exception e){
            System.out.println(e);
        }
        // Initializing array for new terminal track information
        byte[] newTerminalInfo = new byte[1024];
        int newTerminalInfoCount = 0;
        // Getting the terminals tracks information from server
        try {
            newTerminalInfoCount = connection.getBufferedInputStream().read(newTerminalInfo);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    // Stop playing track from terminal
    public void stopTrack(Connection connection, String username, int terminalId){
        // Command id for playing track
        int cmdid = 71;
        // Create byte array for playing track
        byte[] stopTrack = byteArrayFillerForStopTrack(cmdid, username, terminalId);
        // Sending array to server
        try {
            connection.getDataoutputStream().write(stopTrack, 0, stopTrack.length);
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            connection.getDataoutputStream().flush();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
    public void printTracks(){
        for(Track track : trackList){
            System.out.println("Id: " + track.getId() );
            System.out.println("Name: " + track.getName());
            System.out.println("Duration: " +  track.getDuration());
            System.out.println("Filesize: " + track.getFileSize());
        }
    }
    
    // Fill array for terminal tracks getter
    private byte[] byteArrayFillerForTerminalsTracks(int cmdid, int id){
        // Creating the byte array for getting terminals tracks
        byte[] terminalsTracks = new byte[9];
        // cmdid for getting terminals tracks
        terminalsTracks[0] = (byte)cmdid;
        // Filling track getter byte array
        terminalsTracks[1] = (byte)9;
        terminalsTracks[5] = (byte)id;
        return terminalsTracks;
    }
    
    
    
    // Fill array for server tracks getter
    private byte[] byteArrayFillerForServersTracks(int cmdid){
        // Creating the byte array for getting servers tracks
        byte[] serversTracks = new byte[9];
        // cmdid for getting servers tracks
        serversTracks[0] = (byte)cmdid;
        // Filling track getter byte array
        serversTracks[1] = (byte)9;
        serversTracks[5] = (byte)0;
        return serversTracks;
    }
    
    // Fill array for playing terminals track
    private byte[] byteArrayFillerForTrackPlaying(int cmdid, int id, String username, int terminalId){
        String name = trackList[id].getName();
        System.out.println("playing "+name);
        String duration =  trackList[id].getDuration();
        int nameLenght = name.length();
        int durationLenght = duration.length();
        int usernameLenght = username.length();
        int totalLenght = 21+nameLenght+durationLenght+usernameLenght;
        // Creating the byte array for playing terminals tracks
        byte[] playTrack = new byte[totalLenght];
        // Filling track player byte array
        playTrack[0] = (byte)cmdid;
        playTrack[1] = (byte)totalLenght;
        playTrack[5] = (byte)usernameLenght;
        byte[]usernameByte = username.getBytes();
        System.arraycopy(usernameByte, 0, playTrack, 9, usernameLenght);
        playTrack[9+usernameLenght] = (byte) terminalId;
        playTrack[13+usernameLenght] = (byte) nameLenght;
        byte[]nameByte = name.getBytes();
        System.arraycopy(nameByte, 0, playTrack, 17+usernameLenght, nameLenght);
        playTrack[17+usernameLenght+nameLenght] = (byte) durationLenght;
        byte[]durationByte = duration.getBytes();
        System.arraycopy(durationByte, 0, playTrack, 21+usernameLenght+nameLenght, durationLenght);
        return playTrack;
    }
    
    // Fill array for stoping track
    private byte[] byteArrayFillerForStopTrack(int cmdid, String username, int terminalId){
        int usernameLenght = username.length();
        int totalLenght = 13+usernameLenght;
        // Creating the byte array for playing terminals tracks
        byte[] stopTrack = new byte[totalLenght];
        // Filling track player byte array
        stopTrack[0] = (byte)cmdid;
        stopTrack[1] = (byte)totalLenght;
        stopTrack[5] = (byte)usernameLenght;
        byte[]usernameByte = username.getBytes();
        System.arraycopy(usernameByte, 0, stopTrack, 9, usernameLenght);
        stopTrack[9+usernameLenght] = (byte) terminalId;
        return stopTrack;
    }
    
    // Change seconds to duration string
    private String secondsToDuration(int s){
        int seconds = s;
        int minutes = 0;
        int hours = 0;
        String sec;
        String min;
        String h;
        while (seconds >= 60) {
            minutes++;
            seconds = seconds - 60;
        }
        while (minutes >= 60) {
            hours++;
            minutes = minutes - 60;
        }
        if(hours < 10){
            h = "0" + hours;
        }else{
            h = "" + hours;
        }
        if(minutes < 10){
            min = "0" + minutes;
        }else{
            min = "" + minutes;
        }
        if(seconds < 10){
            sec = "0" + seconds;
        }else{
            sec = "" + seconds;
        }
        return h+"-"+min+"-"+sec;
    }
}
