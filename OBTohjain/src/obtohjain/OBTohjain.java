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
public class OBTohjain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        /*
        try{
            ConnectionTest test = new ConnectionTest();
        }catch(Exception e){
            System.out.println(e);
        }
        */
        

        Controller controller = new Controller();
        controller.createConnection("192.168.0.104");
        controller.login("t", "t");
        controller.createTerminalMenu();
        //controller.tulosta();
        controller.setChangeActiveState(102);
        controller.setChangeActiveState(111);
        
        
        //controller.getServersTracks();
        //controller.getTerminalsTracks();
        //ontroller.playTrack(2);
        //controller.broadCast();
        //controller.createAudioFile(0, "test");
        controller.playFile("Bitlips-test.wav");
        //controller.playFile("test.wav");
        try {
            
            Thread.sleep(10000);
            
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        //controller.stopCreatingFile();
        //controller.stopBroadcast();
        //controller.stopTrack();*/
        //
        
    }
    
}
