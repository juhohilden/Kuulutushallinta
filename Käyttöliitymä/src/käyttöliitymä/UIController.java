/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package käyttöliitymä;

/**
 *
 * @author Juho
 * 
 */
import obtohjain.*;

public class UIController {
    
    Model model;
    View view;
    
    public UIController(){
        view = new View();
        model = new Model(view);
        view.assigneUIController(this);
        view.assigneModel(model);
        view.createLoginMenu();
    }
    
    /*public boolean connect(String ip){
        //String ip = "192.168.0.104";
        return model.createConnection(ip);
    }
    
    public int login(String username, String password){
        return model.login(username, password);
    }
    
    public Terminal[] getTerminals(){
        return model.getTerminals();
    }
    
    public void broadcast(int[] ids){
        model.broadcast(ids);
    }
    
    public void broadcastFile(String name, int[] ids){
        model.broadcastFile(name, ids);    
    }
    
    public void stopBroadcast(int[] ids){
        model.stopBroadcast(ids);
    }
    
    public void changeVolume(int volume, int[] ids){
        model.changeVolume(volume, ids);
    }
    
    public void recordTrack(String name){
        model.recordTrack(name);
    }
    
    public void stopRecording(){
        model.stopRecording();
    }
    
    public void playTrack(int trackId, int[] ids){
        model.playTerminalTrack(trackId, ids);
    }
    
    public void stopTrack(int[] ids){
        model.stopTrack(ids);
    }
    
    public Track[] getServerTrackList(){
        return model.getServerTrackList();
    }
    
    public Terminal[] getTerminalTracks(int[] ids){
        return model.getTerminalTracks(ids);
    }*/
}
