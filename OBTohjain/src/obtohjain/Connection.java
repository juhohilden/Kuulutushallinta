package obtohjain;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
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
    // Streams used by client
    private DataOutputStream out;
    private BufferedReader in;
    private BufferedInputStream ins;
    // Static port of server
    private int port = 40000;
    // Server ip
    private String ip;
    // List of useable UDPSockets
    private UDPSocket[] udpSockets = new UDPSocket[10];
    
    // Creating connection for controller
    public Connection(String ip)throws IOException{  
            this.ip = ip;
            socket = new Socket(ip, port);
            out = new DataOutputStream(socket.getOutputStream());
            ins = new BufferedInputStream(socket.getInputStream());
            for(int i = 0; i < udpSockets.length; i++){
                udpSockets[i] = new UDPSocket(13000 + i);
            }
    }    
    
    // Testing if connection succeeded
    public boolean testConnection(){ 
        if(socket != null && socket.isConnected()){
            return true;
        }else{
            return false;
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
    
    // Getter for server port
    public int getPort(){
        return port;
    }
    
    // Get server ip
    public String getIP(){
        return ip;
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
