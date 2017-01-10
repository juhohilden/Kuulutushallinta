/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package käyttöliitymä;

/**
 *
 * @author Juho
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import obtohjain.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

public class View extends JFrame {

    // Variables for all
    private UIController uiController;
    private Model model;
    private JPanel container;
    GridBagConstraints c;

    // Variables for login
    private JButton login;
    private JLabel message;
    private JLabel ipLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField ipField;
    private JTextField usernameField;
    private JTextField passwordField;

    // Variables for OBT UI
    private JPanel terminals;
    private JPanel soundfiles;
    private JPanel broadcast;

    // Terminals variables
    private JPanel[] terminalList;
    private JPanel terminalNames;
    private JLabel terminalIdLabel;
    private JLabel terminalNameLabel;
    private JLabel terminalIpLabel;
    private JLabel terminalMacLabel;
    private JLabel terminalVolumeLabel;
    private JLabel terminalStateLabel;
    private JCheckBox[] ids;
    private JTextField[] name;
    private JLabel[] ip;
    private JLabel[] mac;
    private JLabel[] state;
    private JFormattedTextField[] volume;

    // Soundfiles variables
    private JLabel soundFilesState;
    private JPanel statesPanel;
    private JPanel soundFilesButtons;
    private JPanel recordButtons;
    private JPanel localTracks;
    private JPanel terminalsTracks;
    private JButton recordFile;
    private JButton playFile;
    private JButton getTerminalFiles;
    private JButton stopRecordingFile;
    private JButton stopPlayingFile;
    private JTextField trackNameField;
    private ButtonGroup localTracksButtons;
    private JToggleButton[] fileNames;
    private JLabel tracklistName;
    private ButtonGroup terminalTracksButtons;
    private JToggleButton[] tracksNames;

    // Broadcasts variables
    private JButton bCast;
    private JButton broadcastFile;
    private JButton stopBCast;
    private JLabel broadcastState;

    // Terminals infos
    private List<Terminal> termi;
    private Track[] currentTracklist;

    public void assigneUIController(UIController uiController) {
        this.uiController = uiController;
    }

    public void assigneModel(Model model) {
        this.model = model;
    }

    public void createLoginMenu() {
        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        container = new JPanel();
        login = new JButton("Login");
        message = new JLabel("Enter your login information!");
        ipLabel = new JLabel("IP:");
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        ipField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JTextField(20);

        ipField.setMaximumSize(ipField.getPreferredSize());
        usernameField.setMaximumSize(usernameField.getPreferredSize());
        passwordField.setMaximumSize(passwordField.getPreferredSize());

        container.add(message);
        container.add(ipLabel);
        container.add(ipField);
        container.add(usernameLabel);
        container.add(usernameField);
        container.add(passwordLabel);
        container.add(passwordField);
        container.add(login);

        setContentPane(container);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        login.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.createConnection(ipField.getText())) {
                    int loginResult = model.login(usernameField.getText(), passwordField.getText());
                    if (loginResult == 0) {
                        createOBTUI();
                    } else if (loginResult == 1) {
                        message.setText("Authentication require repeat login! Try Again!");
                    } else if (loginResult == 2) {
                        message.setText("Wrong password or username! Try Again!");
                    } else if (loginResult == 3) {
                        message.setText("Error from server! Try Again!");
                    } else if (loginResult == 8) {
                        message.setText("No reply from server! Try Again!");
                    } else if (loginResult == 9) {
                        message.setText("Connection Lost! Try Again!");
                    }
                } else {
                    message.setText("Connection Failed! Try Again!");
                }
                //createOBTUI();
            }
        });

        setSize(300, 300);
        setVisible(true);
    }

    public void createOBTUI() {
        setTitle("Sound System Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        container.removeAll();

        // All mainmenus panels
        terminals = new JPanel();
        soundfiles = new JPanel();
        broadcast = new JPanel();

        container.add(terminals);
        container.add(soundfiles);
        container.add(broadcast);

        terminals.setBorder(BorderFactory.createTitledBorder("Terminals"));
        soundfiles.setBorder(BorderFactory.createTitledBorder("Soundfiles"));
        broadcast.setBorder(BorderFactory.createTitledBorder("Broadcast"));

        //Terminal menu
        terminals.setLayout(new GridLayout(2, 0));
        model.createTerminalMenu();
        termi = model.getTerminals();
        terminalList = new JPanel[termi.size() + 1];
        ids = new JCheckBox[termi.size()];
        name = new JTextField[termi.size()];
        ip = new JLabel[termi.size()];
        mac = new JLabel[termi.size()];
        state = new JLabel[termi.size()];
        volume = new JFormattedTextField[termi.size()];
        c = new GridBagConstraints();
        terminalNames = new JPanel();
        terminalNames.setLayout(new GridBagLayout());

        terminalIdLabel = new JLabel("Id");
        terminalNameLabel = new JLabel("Name");
        terminalIpLabel = new JLabel("Ip Address");
        terminalMacLabel = new JLabel("Mac Address");
        terminalVolumeLabel = new JLabel("volume");
        terminalStateLabel = new JLabel("State");

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        terminalNames.add(terminalIdLabel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.5;
        terminalNames.add(terminalNameLabel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.5;
        terminalNames.add(terminalIpLabel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 0.5;
        terminalNames.add(terminalMacLabel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 4;
        c.gridy = 0;
        c.weightx = 0.5;
        terminalNames.add(terminalVolumeLabel, c);
        c.gridx = 5;
        c.gridy = 0;
        c.weightx = 0.5;
        terminalNames.add(terminalStateLabel, c);

        terminalList[0] = terminalNames;
        terminals.add(terminalList[0]);

        // List all terminals
        for (int i = 1; i <= termi.size(); i++) {
            ids[i - 1] = new JCheckBox("" + termi.get(i - 1).getId());
            name[i - 1] = new JTextField("" + termi.get(i - 1).getName());
            ip[i - 1] = new JLabel("" + termi.get(i - 1).getIpAddress());
            mac[i - 1] = new JLabel("" + termi.get(i - 1).getMacAddress());
            state[i - 1] = new JLabel("" + termi.get(i - 1).getTaskStatus());

            NumberFormat longFormat = NumberFormat.getIntegerInstance();
            NumberFormatter numberFormatter = new NumberFormatter(longFormat);
            numberFormatter.setValueClass(Integer.class);
            numberFormatter.setAllowsInvalid(false);
            numberFormatter.setMinimum(0);
            numberFormatter.setMaximum(10);
            volume[i - 1] = new JFormattedTextField(numberFormatter);
            volume[i - 1].setText("" + termi.get(i - 1).getVolume());
            ChangeVolumeDocumentListener changeVolumeDocumentListener = new ChangeVolumeDocumentListener(termi.get(i - 1).getId(), i - 1);
            volume[i - 1].getDocument().addDocumentListener(changeVolumeDocumentListener);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = i;
            terminalNames.add(ids[i - 1], c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = i;
            terminalNames.add(name[i - 1], c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = i;
            terminalNames.add(ip[i - 1], c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 3;
            c.gridy = i;
            terminalNames.add(mac[i - 1], c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 4;
            c.gridy = i;
            terminalNames.add(volume[i - 1], c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 5;
            c.gridy = i;
            terminalNames.add(state[i - 1], c);

            terminalList[i] = terminalNames;
            terminals.add(terminalList[i]);
        }

        //Sounds menu
        recordFile = new JButton("Record File");
        playFile = new JButton("Play File");
        stopRecordingFile = new JButton("Stop Recording");
        getTerminalFiles = new JButton("Get Terminals Files");
        stopPlayingFile = new JButton("Stop File");
        trackNameField = new JTextField("Track Name!");
        soundFilesState = new JLabel("No action!");
        localTracks = new JPanel();
        terminalsTracks = new JPanel();
        soundFilesButtons = new JPanel();
        statesPanel = new JPanel();
        recordButtons = new JPanel();

        localTracks.setBorder(BorderFactory.createTitledBorder("Local Tracks"));
        terminalsTracks.setBorder(BorderFactory.createTitledBorder("Terminals Tracks"));

        soundfiles.setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        soundfiles.add(statesPanel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        soundfiles.add(recordButtons, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        soundfiles.add(soundFilesButtons, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        soundfiles.add(localTracks, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        soundfiles.add(terminalsTracks, c);
        statesPanel.add(soundFilesState);

        soundFilesButtons.setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        soundFilesButtons.add(playFile, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        soundFilesButtons.add(stopPlayingFile, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        soundFilesButtons.add(getTerminalFiles, c);

        recordButtons.setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        recordButtons.add(recordFile, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        recordButtons.add(stopRecordingFile, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        recordButtons.add(trackNameField, c);

        localTracks.setLayout(new GridBagLayout());

        // Getting local soundfiles
        File rootDir = new File("");
        File soundFilesDir = new File(rootDir.getAbsolutePath() + "/soundfiles");
        File[] files = soundFilesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wav");
            }
        });

        localTracksButtons = new ButtonGroup();
        fileNames = new JToggleButton[files.length];
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                fileNames[i] = new JToggleButton(files[i].getName());
                fileNames[i].setActionCommand(files[i].getName());
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = i;
                localTracksButtons.add(fileNames[i]);
                localTracks.add(fileNames[i], c);
            }
        }
        
        currentTracklist = model.showServersTracks();
        terminalsTracks.setLayout(new GridBagLayout());
        tracklistName = new JLabel("Empty Tracklist");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        terminalsTracks.add(tracklistName, c);
        if (currentTracklist != null) {
            terminalTracksButtons = new ButtonGroup();
            tracklistName.setText("Server Tracks! Choose terminal to play!");
            tracksNames = new JToggleButton[currentTracklist.length];
            for (int i = 0; i < currentTracklist.length; i++) {
                tracksNames[i] = new JToggleButton(currentTracklist[i].getName());
                tracksNames[i].setActionCommand("" + i); // Fill later
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = i + 1;
                terminalTracksButtons.add(tracksNames[i]);
                terminalsTracks.add(tracksNames[i], c);

            }
        }

        StartRecordingActionListener startRecordingActionListener = new StartRecordingActionListener();
        recordFile.addActionListener(startRecordingActionListener);

        StopRecordingActionListener stopRecordingActionListener = new StopRecordingActionListener();
        stopRecordingFile.addActionListener(stopRecordingActionListener);

        GetTerminalFilesActionListener getTerminalFilesActionListener = new GetTerminalFilesActionListener();
        getTerminalFiles.addActionListener(getTerminalFilesActionListener);

        PlayTerminalFilesActionListener playTerminalFilesActionListener = new PlayTerminalFilesActionListener();
        playFile.addActionListener(playTerminalFilesActionListener);

        StopPlayingFileActionListener stopPlayingFileActionListener = new StopPlayingFileActionListener();
        stopPlayingFile.addActionListener(stopPlayingFileActionListener);

        //Broadcast menu
        broadcastState = new JLabel("Not Broadcasting");
        bCast = new JButton("Broadcast");
        broadcastFile = new JButton("Broadcast File");
        stopBCast = new JButton("Stop Broadcast");

        broadcast.setLayout(new GridBagLayout());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        broadcast.add(broadcastState, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        broadcast.add(bCast, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        broadcast.add(broadcastFile, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        broadcast.add(stopBCast, c);

        BroadcastFileActionListener bCastFileActionList = new BroadcastFileActionListener();
        broadcastFile.addActionListener(bCastFileActionList);

        BroadcastActionListener bCastActionListener = new BroadcastActionListener();
        bCast.addActionListener(bCastActionListener);

        StopBroadcastActionListener stopBCastActionListener = new StopBroadcastActionListener();
        stopBCast.addActionListener(stopBCastActionListener);

        setSize(800, 800);
        setVisible(true);
    }
    
    public void update(List<Terminal> termi){
        
        this.termi = termi;

        for (int i = 1; i <= termi.size(); i++) {
            if (ids[i-1] != null && state[i-1] != null && mac[i-1] != null && ip[i-1] != null && name[i-1] != null) {
                ids[i - 1].setText("" + termi.get(i - 1).getId());
                name[i - 1].setText("" + termi.get(i - 1).getName());
                ip[i - 1].setText("" + termi.get(i - 1).getIpAddress());
                mac[i - 1].setText("" + termi.get(i - 1).getMacAddress());
                state[i - 1].setText("" + termi.get(i - 1).getTaskStatus());
            }
        }
    }

    class ChangeVolumeDocumentListener implements DocumentListener {

        int id;
        int terminalNumber;

        public ChangeVolumeDocumentListener(int id, int terminalNumber) {
            this.id = id;
            this.terminalNumber = terminalNumber;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            int volumeInt = Integer.parseInt(volume[terminalNumber].getText());
            System.out.println(volumeInt + " " + id);
            List<Terminal> tempTermi = new ArrayList<Terminal>();
            Terminal temp = null;
            for (Terminal termis : termi) {
                if (termis.getId() == id) {
                    temp = termis;
                }
                tempTermi.add(temp);
            }
            if (temp != null) {
                model.changeVolume(volumeInt, tempTermi);
            } else {
                System.out.println("No available terminal");
            }

        }

        @Override
        public void removeUpdate(DocumentEvent e) {
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
        }

    }

    class StartRecordingActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (model.isMicUsed() == false) {
                String name = trackNameField.getText();
                if (name.compareTo("Track Name!") != 0) {
                    File rootDir = new File("");
                    File soundFilesDir = new File(rootDir.getAbsolutePath() + "/OBTohjain/" + name);
                    model.recordTrack(soundFilesDir.getAbsolutePath());
                    soundFilesState.setText("Recording " + name);
                } else {
                    soundFilesState.setText("Name not possible!");
                }
            } else {
                soundFilesState.setText("Mic already taken");
            }
        }

    }

    class GetTerminalFilesActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            int idsCount = 0;
            for (int i = 0; i < termi.size(); i++) {
                if (ids[i].isSelected()) {
                    idsCount++;
                }
            }
            if (idsCount != 0) {
                int terminalIds[] = new int[idsCount];
                int idCount = 0;
                for (int i = 0; i < termi.size(); i++) {
                    if (ids[i].isSelected()) {
                        terminalIds[idCount] = termi.get(i).getId();
                        idCount++;
                    }
                }
                List<Terminal> tempTermi = new ArrayList<Terminal>();
                Terminal temp = null;
                for (Terminal termis : termi) {
                    for (Integer i : terminalIds) {
                        if (termis.getId() == i) {
                            temp = termis;
                            tempTermi.add(temp);
                        }
                    }
                }
                model.getTerminalTracks(tempTermi);
                terminalsTracks.removeAll();
                tracklistName = new JLabel("Empty Tracklist");
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = 0;
                terminalsTracks.add(tracklistName, c);
                Terminal selectedTerminal=null;
                for (int i = 0; i < termi.size(); i++) {
                    for (int j = 0; j < tempTermi.size(); j++) {
                        if (termi.get(i).getId() == tempTermi.get(j).getId()) {
                            selectedTerminal = termi.get(i);
                            i = termi.size();
                            j = tempTermi.size();
                        }
                    }
                }
                if (selectedTerminal != null && selectedTerminal.getTracklist() != null) {
                    terminalTracksButtons = new ButtonGroup();
                    tracklistName.setText(selectedTerminal.getName() + " Tracks");
                    currentTracklist = selectedTerminal.getTracklist();
                    tracksNames = new JToggleButton[currentTracklist.length];
                    for (int i = 0; i < currentTracklist.length; i++) {
                        tracksNames[i] = new JToggleButton(currentTracklist[i].getName());
                        tracksNames[i].setActionCommand("" + i); // Fill later
                        c.fill = GridBagConstraints.HORIZONTAL;
                        c.gridx = 0;
                        c.gridy = i + 1;
                        terminalTracksButtons.add(tracksNames[i]);
                        terminalsTracks.add(tracksNames[i], c);

                    }
                } else {
                    soundFilesState.setText("Terminal not found!");
                }
            } else {
                soundFilesState.setText("Select Terminal!");
            }
        }

    }

    class PlayTerminalFilesActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            int idsCount = 0;
            for (int i = 0; i < termi.size(); i++) {
                if (ids[i].isSelected()) {
                    idsCount++;
                }
            }
            if (idsCount != 0) {
                int playTrackIds[] = new int[idsCount];
                int idCount = 0;
                for (int i = 0; i < termi.size(); i++) {
                    if (ids[i].isSelected()) {
                        playTrackIds[idCount] = termi.get(i).getId();
                        idCount++;
                    }
                }
                List<Terminal> tempTermi = new ArrayList<Terminal>();
                Terminal temp = null;
                for (Terminal termis : termi) {
                    for (Integer i : playTrackIds) {
                        if (termis.getId() == i) {
                            temp = termis;
                            tempTermi.add(temp);
                        }
                    }
                }
                if (terminalTracksButtons.getSelection() != null) {
                    System.out.println(Integer.parseInt(terminalTracksButtons.getSelection().getActionCommand()) + " " + playTrackIds[0]);
                    model.playTerminalTrack(Integer.parseInt(terminalTracksButtons.getSelection().getActionCommand()), tempTermi);
                    soundFilesState.setText("Playing " + currentTracklist[Integer.parseInt(terminalTracksButtons.getSelection().getActionCommand())].getName());
                    termi = model.getTerminals();
                }
            } else {
                soundFilesState.setText("Select Terminal!");
            }
        }

    }

    class StopPlayingFileActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            int idsCount = 0;
            for (int i = 0; i < termi.size(); i++) {
                if (ids[i].isSelected()) {
                    idsCount++;
                }
            }
            if (idsCount != 0) {
                int stopTrackIds[] = new int[idsCount];
                int idCount = 0;
                for (int i = 0; i < termi.size(); i++) {
                    if (ids[i].isSelected()) {
                        stopTrackIds[idCount] = termi.get(i).getId();
                        idCount++;
                    }
                }
                List<Terminal> tempTermi = new ArrayList<Terminal>();
                Terminal temp = null;
                for (Terminal termis : termi) {
                    for (Integer i : stopTrackIds) {
                        if (termis.getId() == i) {
                            temp = termis;
                            tempTermi.add(temp);
                        }
                    }
                }
                model.stopTrack(tempTermi);
                soundFilesState.setText("Stoped playing");
                termi = model.getTerminals();
                System.out.println("" + termi.size());
            } else {
                soundFilesState.setText("Select Terminal!");
            }
        }

    }

    class StopRecordingActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (model.isMicUsed()) {
                model.stopRecording();
                soundFilesState.setText("Recorded New File");
                File rootDir = new File("");
                File soundFilesDir = new File(rootDir.getAbsolutePath() + "/soundfiles");
                File[] files = soundFilesDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".wav");
                    }
                });
                localTracks.removeAll();
                localTracksButtons = new ButtonGroup();
                fileNames = new JToggleButton[files.length];
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        fileNames[i] = new JToggleButton(files[i].getName());
                        fileNames[i].setActionCommand(files[i].getName());

                        c.fill = GridBagConstraints.HORIZONTAL;
                        c.gridx = 0;
                        c.gridy = i;
                        localTracksButtons.add(fileNames[i]);
                        localTracks.add(fileNames[i], c);
                    }
                }
            } else {
                soundFilesState.setText("Recording never started");
            }

        }

    }

    // Fix broadcast when terminal isnt selected
    class BroadcastActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (model.isMicUsed() == false) {
                int idsCount = 0;
                for (int i = 0; i < termi.size(); i++) {
                    if (ids[i].isSelected()) {
                        idsCount++;
                    }
                }
                if (idsCount != 0) {
                    int broadcastIds[] = new int[idsCount];
                    int idCount = 0;
                    for (int i = 0; i < termi.size(); i++) {
                        if (ids[i].isSelected()) {
                            broadcastIds[idCount] = termi.get(i).getId();
                            idCount++;
                        }
                    }
                    List<Terminal> tempTermi = new ArrayList<Terminal>();
                    Terminal temp = null;
                    for (Terminal termis : termi) {
                        for (Integer i : broadcastIds) {
                            if (termis.getId() == i) {
                                temp = termis;
                                tempTermi.add(temp);
                            }
                        }
                    }
                    broadcastState.setText("Broadcasting");
                    model.broadcast(tempTermi);
                    termi = model.getTerminals();
                } else {
                    broadcastState.setText("Mic already taken");
                }

            }
        }

    }

    class StopBroadcastActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int idsCount = 0;
            for (int i = 0; i < termi.size(); i++) {
                if (ids[i].isSelected()) {
                    idsCount++;
                }
            }
            if (idsCount != 0) {
                int broadcastIds[] = new int[idsCount];
                int idCount = 0;
                for (int i = 0; i < termi.size(); i++) {
                    if (ids[i].isSelected()) {
                        broadcastIds[idCount] = termi.get(i).getId();
                        idCount++;
                    }
                }
                List<Terminal> tempTermi = new ArrayList<Terminal>();
                Terminal temp = null;
                for (Terminal termis : termi) {
                    for (Integer i : broadcastIds) {
                        if (termis.getId() == i) {
                            temp = termis;
                            tempTermi.add(temp);
                        }
                    }
                }
                broadcastState.setText("Broadcast ended");
                model.stopBroadcast(tempTermi);
            } else {
                broadcastState.setText("Select Terminal");
            }
        }
    }

    class BroadcastFileActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (localTracksButtons.getSelection() != null) {
                int idsCount = 0;
                for (int i = 0; i < termi.size(); i++) {
                    if (ids[i].isSelected()) {
                        idsCount++;
                    }
                }
                if (idsCount != 0) {
                    int broadcastIds[] = new int[idsCount];
                    int idCount = 0;
                    for (int i = 0; i < termi.size(); i++) {
                        if (ids[i].isSelected()) {
                            broadcastIds[idCount] = termi.get(i).getId();
                            idCount++;
                        }
                    }
                    List<Terminal> tempTermi = new ArrayList<Terminal>();
                    Terminal temp = null;
                    for (Terminal termis : termi) {
                        for (Integer i : broadcastIds) {
                            if (termis.getId() == i) {
                                temp = termis;
                                tempTermi.add(temp);
                            }
                        }
                    }
                    File rootDir = new File("");
                    File soundFilesDir = new File(rootDir.getAbsolutePath() + "/soundfiles/" + localTracksButtons.getSelection().getActionCommand());
                    model.broadcastFile(soundFilesDir.getAbsolutePath(), tempTermi);
                    broadcastState.setText("Broadcasting " + localTracksButtons.getSelection().getActionCommand());
                } else {
                    broadcastState.setText("Select Terminal");
                }

            } else {
                broadcastState.setText("Select Song!");
            }
        }
    }

}
