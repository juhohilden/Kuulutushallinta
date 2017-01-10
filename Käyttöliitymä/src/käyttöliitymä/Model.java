/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package käyttöliitymä;

/**
 *
 * @author Juho
 */
import java.io.IOException;
import java.util.List;
import obtohjain.*;

public class Model {
    
    private Controller controller;
    private View view;
    private List<Terminal> termi;
    private boolean online;
    
    public Model(View view){
        controller = new Controller();
        this.view = view;
    }
    
    public boolean createConnection(String ip){
        try{
            return controller.createConnection(ip);
        }catch(IOException e){
            System.out.println("Login error" + e);
        }
        return false;
    }
    
    public int login(String username, String password){
        return controller.login(username, password);
    }
    
    public void createTerminalMenu(){
        controller.createTerminalMenu();
        controller.getServersTracks();
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println(e);
        }
        termi = controller.getTerminals();
        online =true;
        TerminalInfoListener terminalInfoListener =  new TerminalInfoListener();
        terminalInfoListener.start();
    }
    
    public List<Terminal> getTerminals(){
        return controller.getTerminals();
    }
    
    
    public void broadcast(List<Terminal> terminal){
        //System.out.println(""+ids[0]);
        controller.broadCast(terminal);
    }
    
    public void broadcastFile(String name,List<Terminal> terminal){
        controller.playFile(name, terminal);
    }
    
    public void stopBroadcast(List<Terminal> terminal){
        controller.stopBroadcast(terminal);
    }
    
    public void changeVolume(int volume, List<Terminal> terminal){
        controller.changeVolume(volume, terminal);
    }
    
    public void recordTrack(String name){
        controller.createAudioFile(0, name); // sample rate place holder
    }
    
    public void stopRecording(){
        controller.stopCreatingFile();     
    }
    
    public void playTerminalTrack(int trackId, List<Terminal> terminal){
        controller.playTrack(trackId, terminal);
    }
    
    public void stopTrack(List<Terminal>terminal){
        controller.stopTrack(terminal);
    }
    
    public void getServerTrackList(){
        controller.getServersTracks();
    }
    
    public Track[] showServersTracks(){
        return controller.showServersTracks();
    }
    
    public void getTerminalTracks(List<Terminal> terminal){
        controller.getTerminalsTracks(terminal);
        int[] ids = new int[terminal.size()];
        for(int i = 0; i < terminal.size(); i++){
            ids[i] = terminal.get(i).getId();
        }
        controller.getTerminal(ids);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public boolean isMicUsed(){
        return controller.isMicUsed();
    }
    
    public void endConnection(){
        online = false;
        controller.endConnection();
    }
   
    class TerminalInfoListener extends Thread {

        @Override
        public void run() {
            while (online) {
                if (termi != null) {
                    List<Terminal> tempTermi = controller.getTerminals();
                    view.update(tempTermi);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    /*if (tempTermi.get(0).getOnlineStatus() != false && tempTermi.get(0).getIpAddress() != null) {
                        for (Terminal termi1 : termi) {
                            for (Terminal termi2 : tempTermi) {
                                if (!termi1.equals(termi2)) {
                                    termi = tempTermi;
                                    view.update(termi);
                                }
                            }
                        }
                    }*/

                }
            }
        }
    }
}
