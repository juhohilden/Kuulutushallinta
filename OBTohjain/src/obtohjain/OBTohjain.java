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
        //controller.setChangeActiveState(102);
        //controller.setChangeActiveState(111);
        
        
        //controller.getServersTracks();
        //controller.getTerminalsTracks();
        //ontroller.playTrack(2);
        //controller.broadCast();
        //controller.createAudioFile(0, "test");

        int[] i = {102};
        int[] j = {111};
        int[] ij = {102, 111};
        //controller.playFile("Bitlips-test.wav", i);
        
        controller.playFile("test.wav", j);
        controller.playFile("Bitlips-test.wav", i);
        
        //controller.playFile(name, i);
        /*try {
            
            Thread.sleep(10000);
            
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }*/
        //controller.stopBroadcast(i);
        
        //controller.stopCreatingFile();
        
        //controller.stopTrack();*/
        //
        
    }
    
}
