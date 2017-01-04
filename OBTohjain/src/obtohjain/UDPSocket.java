package obtohjain;

import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Juho
 */
public class UDPSocket{
    
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
            //bCast = InetAddress.getByName("192.168.0.102");
            bCast = InetAddress.getByName("255.255.255.255");
            //udpState = true;
        }catch(Exception e){
            System.out.println("InitializeUDPSocket error: " + e);
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
