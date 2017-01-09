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
        
        
//
//        final Controller controller = new Controller();
//        controller.createConnection("192.168.2.233");
//        controller.login("t", "t");
//        controller.createTerminalMenu();
        //controller.tulosta();

        

        
        
        //controller.getServersTracks();
        //controller.getTerminalsTracks();
        //ontroller.playTrack(2);
        //controller.broadCast();
        //controller.createAudioFile(0, "test");


       
        //controller.playFile("Bitlips-test.wav", i);
        
        //terminals.add(new Terminal(87));
        
        String address = "192.168.2.233";
        String username = "admin";
        String password = "admin";
        List<Terminal> terminals = new ArrayList<Terminal>();
        //int[] ids = new int[args.length-3];
        if(args.length > 3) {
            address = args[0];
            username = args[1];
            password = args[2];
            for(int i = 3; i < args.length; i++) {
                //terminals.add(new Terminal(Integer.parseInt(args[i])));
                //ids[i-3] = Integer.parseInt(args[i]);
            }
        }

        Controller controller = new Controller();
        controller.createConnection(address);
        controller.login(username, password);
        controller.createTerminalMenu();
        
        for(Terminal t : controller.getTerminals()){
            logger.debug("Terminal id: " + t.getId());
        }

        controller.playFile("Bitlips-test.wav", controller.getTerminals());
                
                 
        
//        
//        Thread s = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                
//                List<Terminal> terminals = new ArrayList<Terminal>();
//                terminals.add(new Terminal(9));
//                controller.playFile("Bitlips-test.wav", terminals);
//            }
//        });
//        
//        Thread x = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                
//                List<Terminal> terminals = new ArrayList<Terminal>();
//                terminals.add(new Terminal(87));
//                controller.playFile("Bitlips-test.wav", terminals);
//            }
//        });
//        
//        x.start();
//        try {
//            Thread.sleep(1500);
//        } catch (Exception e) {
//        }
//        s.start();
        
        //controller.playFile("test.wav", j);;
        
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
