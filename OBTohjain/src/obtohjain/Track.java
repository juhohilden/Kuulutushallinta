package obtohjain;

/**
 *
 * @author Juho
 */
public class Track {

    private int id;
    private String name;
    private String duration;
    private String fileSize;
    private boolean folder;
    
    public Track(){
        id = 0;
        name = null;
        duration = null;
        fileSize = null;
        folder = false;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    
    public void fileIsFolder(){
        folder = true;
    }
    
    public boolean isFileFolder(){
        return folder;
    }
}
