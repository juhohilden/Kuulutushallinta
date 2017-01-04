package obtohjain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jl.converter.Converter;

/**
 *
 * @author Juho
 */
public class Broadcast {
    
    private boolean playSoundFile = false;
    private Connection currentConnection;
    private boolean playBroadcast = false;
    private MicReader micRe;
    private ByteArrayInputStream in;
    private AudioInputStream tempStream;
    private ByteArrayOutputStream out;
    private File soundFile;
    private Converter converter;
    //private int[] ids;
    private List<Terminal> terminal;
    private String username;
    private Connection connection;
    //private DatagramSocket tempUdp;
    private UDPSocket udpSocket;
    private boolean micBroadcast = false;
    //private Controller controller;
    
    private OnPagingCompleteListener listener;
    
    public interface OnPagingCompleteListener{
        public void onPagingComplete();
    }
    
    // Method for opening terminals for stream
    public Broadcast(Connection connection, List<Terminal> terminal, String username, int sampleRate, UDPSocket udpSocket, OnPagingCompleteListener listener){
        // Initialize private variables ids, username, connection for this broadcast
        //this.ids = ids;
        this.terminal = terminal;
        this.username = username;
        this.connection = connection;
        this.udpSocket = udpSocket;
        //this.controller = controller;
        this.listener = listener;
        // Command id for starting broadcast
        int cmdid = 61;
        // Create array about broadcast information for server
        byte[] broadCast;
        broadCast = byteArrayFillerForBroadcast(cmdid, terminal, username, udpSocket.getPort(), sampleRate);
        //System.out.println("port " + udpSocket.getPort());
        // Sending array to server
        try{
            connection.getDataoutputStream().write(broadCast, 0, broadCast.length);
        }catch(Exception e){
            System.out.println(e);
            System.out.println("Broadcast getDataOutputStream error: " + e);
        }
        try{
            connection.getDataoutputStream().flush();
        }catch(Exception e){
            System.out.println(e);
            System.out.println("Broadcast getDataOutputStream flush error: " + e);
        }
           
    }
    
    // Send data captured by microphone to ip speakers
    public void sendBroadcastData(MicReader micR) {
        micRe = micR;
        currentConnection = connection;
        micBroadcast = true;
        // Test if micreader is recording
        playBroadcast = micRe.getRecordingState();
        Thread s = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int read;
                    byte[] buff = new byte[1024];
                    DatagramSocket d = udpSocket.getDatagramSocket();
                    while (playBroadcast) {
                        // Wait while before trying to get data to send
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        // Copying micreaders ByteArrayOutputStream to local outputstream
                        out = micRe.getOut();
                        byte audioData[] = out.toByteArray();
                        // Reseting ByteArrayOutputStream to keep track what is already read
                        micRe.resetOut();
                        // Checking if we have data to send
                        if (audioData.length > 0) {
                            // Puting our audio byte data to AudioInputStream
                            in = new ByteArrayInputStream(audioData);
                            tempStream = new AudioInputStream(in, micRe.getBroadcastFormat(), audioData.length);
                            // Splitting our data from AudioInputStream to smaller packets to send through udp
                            while ((read = tempStream.read(buff, 0, buff.length)) > 0) {
                                DatagramPacket packet = new DatagramPacket(buff, buff.length, udpSocket.getInetAddress(), udpSocket.getPort());
                                packet.setData(buff);
                                try {
                                    d.send(packet);
                                } catch (Exception e) {
                                    System.out.println("Packet " + e);
                                }
                                try {
                                    Thread.sleep(10);
                                } catch (Exception e) {
                                    System.out.println(e);
                                }
                            }
                            // Closing the temorary streams
                            tempStream.close();
                            in.close();
                            // Checking if micreader is still recording
                            //playBroadcast = micRe.getRecordingState();
                        }

                    }
                    //stopBroadcast();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Broadcast Finished");
            }
        });
        s.start();
    }
    
    // Send sound file to ip speakers
    public void sendWaveSoundFileData(File file) {
        currentConnection = connection;
        soundFile = file;
        Thread s;
        s = new Thread(new Runnable() {
            @Override
            public void run() {
                double durationInSeconds = 30000;
                int read;
                // Creating AudioInputStream from file if it is not null
                if (soundFile != null) {
                    try {
                        tempStream = AudioSystem.getAudioInputStream(soundFile);
                        AudioFormat format = tempStream.getFormat();
                        long frames = tempStream.getFrameLength();
                        durationInSeconds = (frames + 0.0) / format.getFrameRate() + 0.5;
                        System.out.println("Sound file length in seconds: " + durationInSeconds);
                    } catch (UnsupportedAudioFileException | IOException ex) {
                        Logger.getLogger(Broadcast.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                byte[] buff = new byte[1024];
                DatagramSocket d = udpSocket.getDatagramSocket();
                playSoundFile = true;
                // Reading the file to byte array                    
                try {
                    while ((read = tempStream.read(buff, 0, buff.length)) > 0 && playSoundFile) {
                        int time1 = (int) System.currentTimeMillis();
                        // Creating Datagram packet
                        DatagramPacket packet = new DatagramPacket(buff, buff.length, udpSocket.getInetAddress(), udpSocket.getPort());
                        packet.setData(buff);
                        // Sending DatagramPacket
                        try {
                            d.send(packet);
                        } catch (Exception e) {
                            System.out.println("Packet " + e);
                        }
                        // Waiting a bit before trying send another packet
                        try {
                            Thread.sleep(20);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                    tempStream.close();
                     if (listener != null) {
                        Thread.sleep(((long) durationInSeconds) * 1000L);
                        stopBroadcast();
                        System.out.println("OnPagingComplete threadin sisällä");
                        listener.onPagingComplete();
                    }
                    //controller.stopBroadcast(ids);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        s.start();
    }
    
    // Sample mp3 need to be changed to wav fully so we can have proper samplerate for working stream.
    // Send sound file to ip speakers(Maybe needed look into it later) 
    /*public void sendMp3SoundFileData(Connection connection, File file) {
        currentConnection = connection;
        soundFile = file;
        converter = new Converter();
        Thread s;
        s = new Thread(new Runnable() {
            @Override
            public void run() {
                int read;
                // Creating AudioInputStream from file if it is not null
                if (soundFile != null) {
                    try{
                        converter.convert("Kalimba.mp3", "Temp.wav");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    soundFile = new File("Temp.wav");
                    try {
                        tempStream = AudioSystem.getAudioInputStream(soundFile);
                    } catch (UnsupportedAudioFileException | IOException ex) {
                        Logger.getLogger(ActionMenu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                byte[] buff = new byte[1024];
                DatagramSocket d = currentConnection.getDatagramSocket();
                playSoundFile = true;
                while (playSoundFile) {
                    // Reading the file to byte array                    
                    try {
                        while ((read = tempStream.read(buff, 0, buff.length)) > 0) {
                            int time1 = (int) System.currentTimeMillis();
                            // Creating Datagram packet
                            DatagramPacket packet = new DatagramPacket(buff, buff.length, currentConnection.getAddress(), currentConnection.getUDPPort());
                            packet.setData(buff);
                            // Sending DatagramPacket
                            try {
                                d.send(packet);
                            } catch (Exception e) {
                                System.out.println("Packet " + e);
                            }
                            // Waiting a bit before trying send another packet
                            try {
                                Thread.sleep(10);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

            }
        });
        s.start();
    }*/
    
    // Get if this broadcast is using mic
    public boolean isMicBroadcast(){
        return micBroadcast;
    }
    
    // Check if we are broadcasting
    public boolean isBroadcastOn(){
        if(playSoundFile || playBroadcast){
            return true;
        }else{
            return false;
        }
    }
    
    // Get this broadcasts ids
    /*public int[] getIds(){
        return ids;
    }*/
    
    // Get this broadcasts terminals
    public List<Terminal> getTerminals(){
        return terminal;
    }
    
    // Method for stopping broadcast manualy
    public void stopBroadcast(){
        if (playSoundFile || playBroadcast) {
            // Command id for stoping broadcast
            int cmdid = 62;
            // Create array about stopping broadcast information for server
            byte[] broadCastStopper;
            broadCastStopper = byteArrayFillerForBroadcastStopper(cmdid, terminal, username);
            // Sending array to server
            try {
                connection.getDataoutputStream().write(broadCastStopper, 0, broadCastStopper.length);
            } catch (Exception e) {
                System.out.println(e);
            }
            try {
                connection.getDataoutputStream().flush();
            } catch (Exception e) {
                System.out.println(e);
            }
            // Stoping sending threads if they are still on
            playSoundFile = false; // Maybe different stopers depending on broadcast type
            playBroadcast = false;
            // Free udpSockets
            connection.freeUDPSocket(udpSocket);
            if(micBroadcast){
                micRe.stopReadMic();
            }
        }
    }
    
    // Method for creating byte array for informing server about broadcast
    private byte[] byteArrayFillerForBroadcast(int cmdid, List<Terminal> activeTerminals, String username, int udpPort, int sampleRate){
        // Usernames lenght 
        int usernameLenght = username.length();
        // Terminal count
        //int terminalCount = ids.length;
        int terminalCount = activeTerminals.size();
        int totalLenght = 21 + usernameLenght + (4 * terminalCount);
        // Initialize array
        byte[] broadCast = new byte[totalLenght];
        // Filling broadcast byte array
        broadCast[0] = (byte)cmdid;
        broadCast[1] = (byte)totalLenght;
        broadCast[5] = (byte)terminalCount;
        for(int i = 0; i < terminalCount; i++){
            //broadCast[9 + (4 * i)] = (byte)ids[i];
            broadCast[9 + (4 * i)] = (byte)activeTerminals.get(i).getId();
        }
        broadCast[9 + (4 * terminalCount)] = (byte)usernameLenght;
        byte[]usernameByte = username.getBytes();
        System.arraycopy(usernameByte, 0, broadCast, 13+(4 * terminalCount), usernameLenght);
        BigInteger portNumber = BigInteger.valueOf(udpPort);
        byte[] port = portNumber.toByteArray();
        broadCast[13 + (4 * terminalCount) + usernameLenght] = port[1];
        broadCast[13 + (4 * terminalCount) + usernameLenght + 1] = port[0];
        BigInteger sampleNumber = BigInteger.valueOf(sampleRate);
        byte[] sample = sampleNumber.toByteArray();
        broadCast[17 + (4 * terminalCount) + usernameLenght] = sample[1];
        broadCast[17 + (4 * terminalCount) + usernameLenght + 1] = sample[0];
        return broadCast;
    }
    
     // Method for creating byte array for informing server about broadcast
    private byte[] byteArrayFillerForBroadcastStopper(int cmdid, List<Terminal> activeTerminals,String username){
        // Usernames lenght 
        int usernameLenght = username.length();
        // Terminal count
        //int terminalCount = ids.length;
        int terminalCount = activeTerminals.size();
        int totalLenght = 13 + usernameLenght + (4 * terminalCount);
        // Initialize array
        byte[] broadCastStopper = new byte[totalLenght];
        // Filling broadcast byte array
        broadCastStopper[0] = (byte)cmdid;
        broadCastStopper[1] = (byte)totalLenght;
        broadCastStopper[5] = (byte)terminalCount;
        for(int i = 0; i < terminalCount; i++){
           //roadCastStopper[9 + (4 * i)] = (byte)ids[i];
           broadCastStopper[9 + (4 * i)] = (byte)activeTerminals.get(i).getId();
        }
        broadCastStopper[9 + (4 * terminalCount)] = (byte)usernameLenght;
        byte[]usernameByte = username.getBytes();
        System.arraycopy(usernameByte, 0, broadCastStopper, 13+(4 * terminalCount), usernameLenght);
        return broadCastStopper;
    }
    
}
