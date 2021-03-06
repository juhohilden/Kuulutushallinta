package obtohjain;

import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Juho
 */
public class MicReader{
    
    Logger logger = LoggerFactory.getLogger(MicReader.class);
    
    private TargetDataLine mic;
    private AudioFormat broadcastFormat;
    private AudioFormat recordingFormat;
    private boolean recording;
    private File tempFile = new File("Temp.wav");
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private AudioInputStream ais;
    private byte[] data;
    private ByteArrayOutputStream out;
    private String fileName;
    
    public MicReader(){
        recording = false;
        out = new ByteArrayOutputStream();
        try{
            this.broadcastFormat = new AudioFormat(32000.0f, 16, 1, true, false);
            this.recordingFormat = new AudioFormat(8000.0f, 16, 1, true, false);
        }catch(Exception e){
            logger.error("Error in new AudioFormat.",e);
        }      
    }
    
    // Read mic to outputstream
    public void readMic(int sampleRate) {
        if(sampleRate > 32000){
            broadcastFormat = new AudioFormat((float)sampleRate, 16, 1, true, false);
        }
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, broadcastFormat);
            if(!AudioSystem.isLineSupported(info)){
                logger.debug("Line not suported");
            }
            mic = (TargetDataLine) AudioSystem.getTargetDataLine(broadcastFormat);
            mic = (TargetDataLine) AudioSystem.getLine(info);
            recording = true;
            mic.open(broadcastFormat);
            data = new byte[mic.getBufferSize()/5];
            mic.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread s = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (recording) {
                        int byteCount = mic.read(data, 0, data.length);
                        if (byteCount > 0) {
                            out.write(data, 0, byteCount);
                        }                        
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        s.start();
    }
   
   // Get ByteArrayOutputStream where microphone data is writen
   public ByteArrayOutputStream getOut(){
       return out;
   }
   
   // Reset ByteArrayOutputStream to avoid too big udp packets
   public void resetOut(){
       out.reset();
   }
   
   // Get format used for broadcasting
   public AudioFormat getBroadcastFormat(){
       return broadcastFormat;
   }
   
   // Get format used for creating recorded files
   public AudioFormat getRecordingFormat(){
       return recordingFormat;
   }
   
   // Get boolean if recording is still on
   public boolean getRecordingState(){
       return recording;
   } 
    
    // Create wav sound file
    public void startRecord(int sampleRate, String name){
        fileName = name;
        if(sampleRate > 8000){
            recordingFormat = new AudioFormat((float)sampleRate, 16, 1, true, false);
        }
        try{
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, recordingFormat);
            if(!AudioSystem.isLineSupported(info)){
                logger.debug("Line not suported");
            }
            mic = (TargetDataLine) AudioSystem.getTargetDataLine(recordingFormat);
            mic = (TargetDataLine) AudioSystem.getLine(info);
            mic.open(recordingFormat);
            mic.start();
            recording = true;
            Thread s = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.debug("recording");
                        ais = new AudioInputStream(mic);
                        if (fileName != null){
                            File newFile = new File(fileName + ".wav");
                            AudioSystem.write(ais, fileType, newFile);
                        } else {
                            AudioSystem.write(ais, fileType, tempFile);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            s.start();
            
        }catch(Exception e){
            logger.error("Start record failed. ", e);
        }
    }
    
    // Stop recording wav file
    public void stopRecord(){
        mic.stop();
        mic.close();
        recording = false;
        System.out.println("Mic Freed");
    }
    
    // Stop reading microphone for ByteArrayOutputStream
    public void stopReadMic(){
        mic.stop();
        mic.close();
        recording = false;
        System.out.println("Mic Freed");
    }
    
    // Get sampleRate of file
    public int getFileSampleRate(File file){
        try{
            AudioInputStream getFormat = AudioSystem.getAudioInputStream(file);
            int sRate = (int)getFormat.getFormat().getSampleRate();
            getFormat.close();
            return sRate;
        }catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }
    
}
