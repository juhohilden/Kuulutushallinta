package obtohjain;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author Juho
 */
public class Connection {
    
    // Socket used for communicating with server
    public Socket socket;
    //Streams used by client
    public DataOutputStream out;
    private BufferedReader in;
    public BufferedInputStream ins; // ehkä turha
    // Datagramsocket for udp connection
    public DatagramSocket udpSocket;
    // Static port of server
    private int port = 40000;
    // Static local port for udp connection
    private int udpPort = 14002;
    // Server ip
    public String ip;
    // InetAdress used for broadcast
    public InetAddress bCast;
    // Is udp socket initialized
    private boolean udpState;
    
    // Creating connection for controller
    public Connection(String ip){  
        try{
            this.ip = ip;
            socket = new Socket(ip, port);
            out = new DataOutputStream(socket.getOutputStream());
            ins = new BufferedInputStream(socket.getInputStream());
            udpState =false;
        }
        catch(Exception e){
            System.out.println(e);
        }  
    }
    
    // Testing if connection succeeded
    public boolean testConnection(){ 
        if(socket.isConnected()){
            return true;
        }else{
            return false;
        }  
    }
    
    // Initialize UDP socket for broadcast
    public void initializeUDPSocket(){
        try{
            udpSocket = new DatagramSocket(udpPort);
            udpSocket.setBroadcast(true);
            //bCast = InetAddress.getByName("192.168.0.102");
            bCast = InetAddress.getByName("255.255.255.255");
            udpState = true;
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    // Getter for DataOutputStream
    public DataOutputStream getDataoutputStream(){
        return out;
    }
    
    // Getter for BufferedReader
    public BufferedReader getBufferedReader(){
        return in;
    }
    
    // Getter for BufferedInputStream
    public BufferedInputStream getBufferedInputStream(){
        return ins;
    }
    
    // Getter for UDP socket
    public DatagramSocket getDatagramSocket(){
        return udpSocket;
    }
    
    // Getter for local UDP port
    public int getUDPPort(){
        return udpPort;
    }
    
    // Getter for server port
    public int getPort(){
        return port;
    }
    
    // Get server ip
    public String getIP(){
        return ip;
    }
    
    // Get InetAddress for broadcast
    public InetAddress getAddress(){
        return bCast;
    }
    
    // Get if udpSocket is already initialized
    public boolean getUdpState(){
        return udpState;
    }
    
    // Close socket connection
    public void endConnection(){
        try{
            out.close();
            ins.close();
            socket.close();
        }catch(Exception e){
            System.out.println(e);
        }  
    }   
}
