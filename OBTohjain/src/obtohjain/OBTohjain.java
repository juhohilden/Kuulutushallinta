package obtohjain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Juho
 */
public class OBTohjain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        /*
        try{
            ConnectionTest test = new ConnectionTest();
        }catch(Exception e){
            System.out.println(e);
        }
        */
        Logger logger = LoggerFactory.getLogger(OBTohjain.class);
        
        logger.debug("Testiloggaus");
        logger.error("Error testaus");

        Controller controller = new Controller();
        controller.createConnection("192.168.2.233");
        controller.login("t", "t");
        controller.createTerminalMenu();
        //controller.tulosta();

        //controller.setChangeActiveState(102);
        //controller.setChangeActiveState(111);

        //controller.setChangeActiveState(9);
        
        //controller.setChangeActiveState(2);
        //controller.setChangeActiveState(3);
        

        
        
        //controller.getServersTracks();
        //controller.getTerminalsTracks();
        //ontroller.playTrack(2);
        //controller.broadCast();
        //controller.createAudioFile(0, "test");


       
        //controller.playFile("Bitlips-test.wav", i);
        
        List<Terminal> terminals = new ArrayList<Terminal>();
        //terminals.add(new Terminal(87));
        terminals.add(new Terminal(9));
        //controller.playFile("test.wav", j);
        controller.playFile("Bitlips-test.wav", terminals);
        
        //controller.playFile(name, i);
        /*try {
            
            Thread.sleep(10000);
            
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }*/
        //controller.stopBroadcast(i);
        
        //controller.stopCreatingFile();
        

        //controller.playFile("Bitlips-test.wav");
        //controller.playFile("test.wav");
//        
//        try {
//            
//            Thread.sleep(10000);
//            
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
//        //controller.stopCreatingFile();
        //controller.stopBroadcast();

        //controller.stopTrack();*/
        //
        
    }
    
}
