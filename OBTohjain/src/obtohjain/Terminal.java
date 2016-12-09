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
public class Terminal {

    
    private int id;
    private int volume;
    private boolean onlineStatus;
    private String macAddress;
    private String name;
    private String ipAddress;
    private int taskStatus;
    private boolean onUse;
    private Track[] trackList;
    
    public Terminal() {
        this.id = 0;
        this.volume = 0;
        this.onlineStatus = false;
        this.macAddress = null;
        this.name = null;
        this.ipAddress = null;
        this.taskStatus = 0;
        this.onUse = false;
        this.trackList = null;
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
        this.ipAddress = ipAddress;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public boolean isOnUse() {
        return onUse;
    }

    public void setOnUse(boolean onUse) {
        this.onUse = onUse;
    } 

    public void setTracklist(Track[] trackList){
        this.trackList = trackList;
    }
    
    public Track[] getTracklist(){
        return trackList;
    }
    
}
