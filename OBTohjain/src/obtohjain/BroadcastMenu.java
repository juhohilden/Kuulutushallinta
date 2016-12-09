package obtohjain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jl.converter.Converter;

/**
 *
 * @author Juho
 */
public class BroadcastMenu {
    
    private boolean playSoundFile = false;
    private Connection currentConnection;
    private boolean playBroadcast = false;
    private MicReader micRe;
    private ByteArrayInputStream in;
    private AudioInputStream tempStream;
    private ByteArrayOutputStream out;
    private File soundFile;
    private Converter converter;
    
    // Method for opening terminals for stream
    public void broadCast(Connection connection, Terminal[] activeTerminals, String username, int sampleRate){    
        // Command id for starting broadcast
        int cmdid = 61;
        // Create array about broadcast information for server
        byte[] broadCast;
        broadCast = byteArrayFillerForBroadcast(cmdid, activeTerminals, username, connection.getUDPPort(), sampleRate);
        // Sending array to server
        try{
            connection.getDataoutputStream().write(broadCast, 0, broadCast.length);
        }catch(Exception e){
            System.out.println(e);
        }
        try{
            connection.getDataoutputStream().flush();
        }catch(Exception e){
            System.out.println(e);
        }
           
    }
    
    // Send data captured by microphone to ip speakers
    public void sendBroadcastData(MicReader micR, Connection connection) {
        micRe = micR;
        currentConnection = connection;
        // Test if micreader is recording
        playBroadcast = micRe.getRecordingState();
        Thread s = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int read;
                    byte[] buff = new byte[1024];
                    DatagramSocket d = currentConnection.getDatagramSocket();
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
                                DatagramPacket packet = new DatagramPacket(buff, buff.length, currentConnection.getAddress(), currentConnection.getUDPPort());
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
                            playBroadcast = micRe.getRecordingState();
                        }

                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Broadcast Finished");
            }
        });
        s.start();
    }
    
    // Send sound file to ip speakers
    public void sendWaveSoundFileData(Connection connection, File file) {
        currentConnection = connection;
        soundFile = file;
        Thread s;
        s = new Thread(new Runnable() {
            @Override
            public void run() {
                int read;
                // Creating AudioInputStream from file if it is not null
                if (soundFile != null) {
                    try {
                        tempStream = AudioSystem.getAudioInputStream(soundFile);
                    } catch (UnsupportedAudioFileException | IOException ex) {
                        Logger.getLogger(BroadcastMenu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                byte[] buff = new byte[1024];
                DatagramSocket d = currentConnection.getDatagramSocket();
                playSoundFile = true;
                while (playSoundFile) {
                    // Reading the file to byte array                    
                    try {
                        while ((read = tempStream.read(buff, 0, buff.length)) > 0 && playSoundFile) {
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
                        tempStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        s.start();
    }
    
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
    
    // Check if we are broadcasting
    public boolean isBroadcastOn(){
        if(playSoundFile || playBroadcast){
            return true;
        }else{
            return false;
        }
    }
    
    // Method for stopping broadcast manualy
    public void stopBroadcast(Connection connection, Terminal[] activeTerminals, String username){
        // Command id for stoping broadcast
        int cmdid = 62;
        // Create array about stopping broadcast information for server
        byte[] broadCastStopper;
        broadCastStopper = byteArrayFillerForBroadcastStopper(cmdid, activeTerminals, username);
        // Sending array to server
        try{
            connection.getDataoutputStream().write(broadCastStopper, 0, broadCastStopper.length);
        }catch(Exception e){
            System.out.println(e);
        }
        try{
            connection.getDataoutputStream().flush();
        }catch(Exception e){
            System.out.println(e);
        }
        // Stoping sending threads if they are still on
        playSoundFile = false;
        playBroadcast = false;
    }
    
    // Method for creating byte array for informing server about broadcast
    private byte[] byteArrayFillerForBroadcast(int cmdid, Terminal[] activeTerminals, String username, int udpPort, int sampleRate){
        // Usernames lenght 
        int usernameLenght = username.length();
        // Terminal count
        int terminalCount = activeTerminals.length;
        int totalLenght = 21 + usernameLenght + (4 * terminalCount);
        // Initialize array
        byte[] broadCast = new byte[totalLenght];
        // Filling broadcast byte array
        broadCast[0] = (byte)cmdid;
        broadCast[1] = (byte)totalLenght;
        broadCast[5] = (byte)terminalCount;
        for(int i = 0; i < terminalCount; i++){
            broadCast[9 + (4 * i)] = (byte)activeTerminals[i].getId();
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
    private byte[] byteArrayFillerForBroadcastStopper(int cmdid, Terminal[] activeTerminals, String username){
        // Usernames lenght 
        int usernameLenght = username.length();
        // Terminal count
        int terminalCount = activeTerminals.length;
        int totalLenght = 13 + usernameLenght + (4 * terminalCount);
        // Initialize array
        byte[] broadCastStopper = new byte[totalLenght];
        // Filling broadcast byte array
        broadCastStopper[0] = (byte)cmdid;
        broadCastStopper[1] = (byte)totalLenght;
        broadCastStopper[5] = (byte)terminalCount;
        for(int i = 0; i < terminalCount; i++){
            broadCastStopper[9 + (4 * i)] = (byte)activeTerminals[i].getId();
        }
        broadCastStopper[9 + (4 * terminalCount)] = (byte)usernameLenght;
        byte[]usernameByte = username.getBytes();
        System.arraycopy(usernameByte, 0, broadCastStopper, 13+(4 * terminalCount), usernameLenght);
        return broadCastStopper;
    }
    
}
