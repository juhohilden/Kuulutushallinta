package obtohjain;

/**
 *
 * @author Juho
 */
public class Terminal {

    
    private int id;
    private int volume;
    private boolean onlineStatus;
    private String macAddress;
    private String name;
    private String ipAddress;
    private int taskStatus;
    private Track[] trackList;
    private String currentUser;
    
    public Terminal() {
        this.id = 0;
        this.volume = 0;
        this.onlineStatus = false;
        this.macAddress = null;
        this.name = null;
        this.ipAddress = null;
        this.taskStatus = 0;
        this.trackList = null;
        this.currentUser = null;
    }
    
    public Terminal(int id){
        this();
        this.id = id;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        if(volume < 0){
            volume = 0;
        }else if(volume > 10){
            volume = 10;
        }else{
            this.volume = volume;
        } 
    }

    public boolean getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress.substring(0, 15);
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setTracklist(Track[] trackList){
        this.trackList = trackList;
    }
    
    public Track[] getTracklist(){
        return trackList;
    }
    
    public void setCurrentUser(String tempTrack){
        this.currentUser = tempTrack;
    }
    
    public String getCurrentUser(){
        return currentUser;
    }
    
}
