package obtohjain;

import java.io.IOException;

/**
 *
 * @author Juho
 */
public class Authentication {
    
    private Connection connection = null;
    // Array for login success
    private byte[] loginReply;
    // Bytes on loginReply array
    private int loginReplyCount;
    // Array for permission info
    private byte[] permissionInfo;
    // Bytes on permissionInfo array
    private int permissionInfoCount;
    // Array for Terminal information
    private byte[] terminalInfo;
    // Bytes on terminalInfo array
    private int terminalInfoCount;
    // Array for server time information
    private byte[] timeInformation;
    // Bytes on time informaiotn array
    private int timeInformationCount;
    // Array for terminal grouping information
    private byte[] terminalGrouping;
    // Bytes on terminal grouping information
    private int terminalGroupingCount;
    // Array for server track list information 
    private byte[] trackListInfo;
    // Bytes on tracklist info array
    private int trackListInfoCount;
    
    public Authentication(Connection connection){
        this.connection = connection;
        loginReply = new byte[64];
        loginReplyCount = 0;
        permissionInfo = new byte[64];
        permissionInfoCount = 0;
        terminalInfo = new byte[1024];
        terminalInfoCount = 0;
        timeInformation = new byte[64];
        timeInformationCount = 0;
        terminalGrouping = new byte[128];
        terminalGroupingCount = 0;
        trackListInfo = new byte[1024];
        trackListInfoCount = 0;
    }
    
    // For logining to server
    public int login(String username, String password){
        //Checking if we have connection to server
        if(connection.testConnection() == false){
            return 9;
        }
        // Command id for login
        int cmdid = 41;
        // Create byte array for login
        byte[] login = byteArrayFillerForLogin(cmdid, username, password);
        // Sending array to server
        try{
            connection.getDataoutputStream().write(login, 0, login.length);
        }catch(Exception e){
            System.out.println(e);
        }
        try{
            connection.getDataoutputStream().flush();
        }catch(Exception e){
            System.out.println(e);
        }
        // Reading authentication reply from server to buffer
        try {
            loginReplyCount = connection.getBufferedInputStream().read(loginReply);
        } catch (Exception e) {
            System.out.println(e);
        }
        // Checking authenticaion reply
        switch ((int)loginReply[1]) {
            // If authentication reply succesful
            case 0:
                //Sending server a request about initial information
                cmdid = 40;
                byte[] getInformation = byteArrayFillerForLogin(cmdid, username, password);
                try{
                    connection.getDataoutputStream().write(getInformation, 0, getInformation.length);
                }catch(Exception e){
                    System.out.println(e);
                }
                try{
                    connection.getDataoutputStream().flush();
                }catch(Exception e){
                    System.out.println(e);
                }
                // Reading permission information
                try {
                    permissionInfoCount = connection.getBufferedInputStream().read(permissionInfo);
                } catch (IOException e) {
                    System.out.println(e);
                }
                // Reading terminal information if possible
                try{
                    terminalInfoCount = connection.getBufferedInputStream().read(terminalInfo);
                }catch(Exception e){
                    System.out.println(e);
                }
                // Reading server time information
                try{
                    timeInformationCount = connection.getBufferedInputStream().read(timeInformation);
                }catch(Exception e){
                    System.out.println(e);
                }
                // Reading terminal grouping information
                try{
                    terminalGroupingCount = connection.getBufferedInputStream().read(terminalGrouping);
                }catch(Exception e){
                    System.out.println(e);
                }
                // test
                try{
                    trackListInfoCount = connection.getBufferedInputStream().read(trackListInfo);
                }catch(Exception e){
                    System.out.println(e);
                }

                return 0;
            // If authentication reply require repeat login
            case 1:
                return 1;
            // If authentication reply sends username or password error
            case 2:
                return 2;
            // If authentication reply send other error
            case 3:
                return 3;
            default:
                break;
        }
        // If authentication reply wasnt send from server 
        return 8;
    }
    
    private byte[] byteArrayFillerForLogin(int cmdid, String username, String password){
        // Setting the lenght variables of login packets
        int usernameLenght = username.length();
        int passwordLenght = password.length();
        int loginLenght = 13 + usernameLenght + passwordLenght;
        // Transforming password and username to byte arrays
        byte[] usernameByte = username.getBytes();
        byte[] passwordByte = password.getBytes();
        // Creating the byte array for login
        byte[] login = new byte[loginLenght];
        // cmdid for loging
        login[0] = (byte)cmdid;
        // Filling login byte array
        login[1] = (byte)loginLenght;
        login[5] = (byte)usernameLenght;
        System.arraycopy(usernameByte, 0, login, 9, usernameLenght);
        login[9+usernameLenght] = (byte)passwordLenght;
        System.arraycopy(passwordByte, 0, login, 13+usernameLenght, passwordLenght);
        return login;
    }

    public byte[] getPermissionInfo() {
        return permissionInfo;
    }

    public int getPermissionInfoCount() {
        return permissionInfoCount;
    }

    public byte[] getTerminalInfo() {
        return terminalInfo;
    }

    public int getTerminalInfoCount() {
        return terminalInfoCount;
    }
    
    public byte[] getTimeInformation() {
        return timeInformation;
    }

    public int getTimeInformationCount() {
        return timeInformationCount;
    }
    
    public byte[] getTerminalGrouping() {
        return terminalGrouping;
    }

    public int getTerminalGroupingCount() {
        return terminalGroupingCount;
    }

    public byte[] getTrackListInfo() {
        return trackListInfo;
    }

    public int getTrackListInfoCount() {
        return trackListInfoCount;
    }
}
