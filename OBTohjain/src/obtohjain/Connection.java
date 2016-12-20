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
    private Socket socket;
    //Streams used by client
    private DataOutputStream out;
    private BufferedReader in;
    private BufferedInputStream ins; // ehkä turha
    // Datagramsocket for udp connection
    //private DatagramSocket udpSocket;
    // Static port of server
    private int port = 40000;
    // Static local port for udp connection
    //private int udpPort; // cant use same udp port all the time
    // Server ip
    private String ip;
    // InetAdress used for broadcast
    //private InetAddress bCast;
    // Is udp socket initialized
    //private boolean udpState;
    // Is udp socket taken
    //private boolean udpTaken;
    // List of useable UDPSockets
    private UDPSocket[] udpSockets = new UDPSocket[10];
    
    // Creating connection for controller
    public Connection(String ip){  
        try{
            this.ip = ip;
            socket = new Socket(ip, port);
            out = new DataOutputStream(socket.getOutputStream());
            ins = new BufferedInputStream(socket.getInputStream());
            for(int i = 0; i < udpSockets.length; i++){
                udpSockets[i] = new UDPSocket(14000 + i);
            }
            //udpState =false;
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
    // Need multiple udpSockets for multiple broadcasts
    /*public void initializeUDPSocket(){
        try{
            udpSocket = new DatagramSocket(14002);
            udpSocket.setBroadcast(true);
            //bCast = InetAddress.getByName("192.168.0.102");
            bCast = InetAddress.getByName("255.255.255.255");
            //udpState = true;
        }catch(Exception e){
            System.out.println(e);
        }
    }*/
    
    // Create temp udp socket for udp broadcast
    /*public DatagramSocket createUDPSocket(){
        DatagramSocket tempUdp = null;
        try{
            tempUdp = new DatagramSocket(13000); // work with ports between 13000 - 15000 atleast those work
            System.out.println("port " + tempUdp.getLocalPort());
            tempUdp.setBroadcast(true);
            //bCast = InetAddress.getByName("192.168.0.102");
            bCast = InetAddress.getByName("255.255.255.255");
            //udpState = true;
        }catch(Exception e){
            System.out.println(e);
        }
        return tempUdp;
    }*/
    
    
    
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
    
    // Getter for UDPSockets
    public UDPSocket getUDPSocket(){
        UDPSocket udpSocket = null;
        for (UDPSocket udpSocket1 : udpSockets) {
            if(udpSocket1.getOnUse() == false){
               udpSocket = udpSocket1;
               udpSocket.setOnUse();
               break;
            }
        }
        return udpSocket;
    }
    
    // Free udpSocket
    public void freeUDPSocket(UDPSocket udpSocket){
        for (UDPSocket udpSocket1 : udpSockets) {
            if(udpSocket1.getPort() == udpSocket.getPort()){
               udpSocket1.free();
               break;
            }
        }
    }
    

    
    // Getter for UDP socket
    /*public DatagramSocket getDatagramSocket(){
        return udpSocket;
    }*/
    
    // Getter for local UDP port
    /*public int getUDPPort(){
        return udpPort;
    }*/
    
    // Getter for server port
    public int getPort(){
        return port;
    }
    
    // Get server ip
    public String getIP(){
        return ip;
    }
    
    // Get InetAddress for broadcast
    /*public InetAddress getAddress(){
        return bCast;
    }*/
    
    // Get if udpSocket is already initialized
    /*public boolean getUdpState(){
        return udpState;
    }*/
    
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
