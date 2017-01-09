package obtohjain;

import java.net.DatagramSocket;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Juho
 */
public class UDPSocket{
    
    Logger logger = LoggerFactory.getLogger(UDPSocket.class);
    
    private DatagramSocket udpSocket;
    private int port;
    private InetAddress bCast;
    private boolean onUse;
    
    public UDPSocket(int port){
        this.port = port;
        onUse = false;
        try{
            udpSocket = new DatagramSocket();//port // work with ports between 13000 - 15000 atleast those work
            udpSocket.setBroadcast(true);
            bCast = InetAddress.getByName("255.255.255.255");
        }catch(Exception e){
            logger.error("Failed creating UDP socket. ",e);
        }
    }
    
    public void setOnUse(){
        onUse = true;
    }
    
    public void free(){
        onUse = false;
    }
    
    public int getPort(){
        return port;
    }
    
    public DatagramSocket getDatagramSocket(){
        return udpSocket;
    }
    
    public InetAddress getInetAddress(){
        return bCast;
    }
    
    public boolean getOnUse(){
        return onUse;
    }
}
