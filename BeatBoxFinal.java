import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BeatBoxFinal {
    JFrame theFrame;
    JPanel mainPanel;
    JList<String> incomingList;
    JTextField userMessage;
    ArrayList<JCheckBox> checkboxList;  // checkbox is stored in ArrayList
    int nextNum;
    Vector<String> listVector = new Vector<String>();
    String userName;
    ObjectOutputStream out;
    ObjectInputStream in;
    HashMap<String,boolean[]> otherSeqsMap = new HashMap<>();
    
    Sequencer sequencer;
    Sequence sequence;
    Sequence mySequence = null;
    Track track;
    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", 
    "Open Hi-Hat","Acoustic Snare", "Crash Cymbal", "Hand Clap", 
    "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", 
    "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo", 
    "Open Hi Conga"};
    int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
    public static void main(String[] args) {
        new BeatBoxFinal().startUp(args[0]); // e.g. %java BeatBoxFinal theFlash
        // args[0] is the screen name or username
    }

    public void startUp (String name){
        userName = name;
        // open connection to the server
        try {
            Socket sock = new Socket("localhost",4242); // Here I just use the local host, choose port 4242
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
            Thread remote = new Thread(new RemoteReader());
            remote.start();
        } catch (Exception ex) {
            System.out.println("Couldn't connect - you'll have to play alone.");
        }
        setUpMidi();
        buildGUI();
    }
    public void buildGUI(){ // For visualization, build the window and outlook with java swing
        theFrame = new JFrame("Cyber Beatbox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // create Button Box on the right
        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton sendIt = new JButton("sendIt");
        sendIt.addActionListener(new MySendListener());
        buttonBox.add(sendIt);

        userMessage = new JTextField();
        buttonBox.add(userMessage);

        // The components which can show the message received
        incomingList = new JList<String>();
        incomingList.addListSelectionListener(new MyListSelectionListener());
        incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incomingList);
        buttonBox.add(theList);
        incomingList.setListData(listVector);


        // create Name Box on the left
        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for(int i = 0; i<16; i++){
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST,buttonBox);
        background.add(BorderLayout.WEST,nameBox);

        theFrame.getContentPane().add(background);

        // create mainPanel in the middle, and add 16*16 grid
        GridLayout grid = new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for(int i=0; i<256; i++){
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();
        
        theFrame.setBounds(50,50,300,300);
        theFrame.pack();
        theFrame.setVisible(true);

    }

    public void setUpMidi(){
        try{
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        }
        catch(Exception e) {e.printStackTrace();}
    }

    // Important: transfer the selection to MIDI and add onto the track
    public void buildTrackAndStart(){
        ArrayList<Integer> tracklist = null; // This will hold the instrument for each
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for(int i=0; i<16; i++){
            tracklist = new ArrayList<Integer>();

            for(int j=0; j<16; j++){
                JCheckBox jc = (JCheckBox) checkboxList.get(j+(16*i));
                if(jc.isSelected()) {
                    int key = instruments[i];
                    tracklist.add(key);
                } 
                // If it's selected put the key at the position, or add 0
                else tracklist.add(null);
            }
            makeTracks(tracklist); // add to track
        }
        track.add(makeEvent(192,9,1,0,15));
        try{
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        }catch (Exception e){e.printStackTrace();}
    }

    public class MyStartListener implements ActionListener{ // this is the first inne class, is listener of the button
        public void actionPerformed(ActionEvent a){
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a){
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a){
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor *1.03)); // first set as 1.0, then adjust 3% every time
        }
    }

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
         float tempoFactor = sequencer.getTempoFactor();
           sequencer.setTempoFactor((float)(tempoFactor * .97));
        }
    }

    public class MySendListener implements ActionListener {
        public void actionPerformed (ActionEvent a) {
            // make an arraylist of just the STATE of the checkboxes
            boolean[] checkboxState = new boolean[256];
            for(int i = 0; i < 256; i++) {
                JCheckBox check = (JCheckBox) checkboxList.get(i);
                if(check.isSelected()) checkboxState[i] = true;
            }
            String messageToSend = null;
            try {
                out.writeObject(userName + nextNum + ": " + userMessage.getText());
                out.writeObject(checkboxState);
            } catch (Exception ex) {
                System.out.println("Sorry dude. Could not send it to the server");
            }
            userMessage.setText("");
        }
    }

    public class MyListSelectionListener implements ListSelectionListener {
        public void valueChanged (ListSelectionEvent le) {
            if (!le.getValueIsAdjusting()) {
                String selected = (String) incomingList.getSelectedValue();
                if(selected!=null) {
                    boolean[] selectedState = (boolean[]) otherSeqsMap.get(selected);
                    changeSequence(selectedState);
                    sequencer.stop();
                    buildTrackAndStart();
                } // When the user clicks information, will load sequence and start playing
            }
        }
    }

    public class RemoteReader implements Runnable {
        boolean[] checkboxState = null;
        String nameToShow = null;
        Object obj = null;
        public void run() {
            try {
                while((obj=in.readObject()) != null) {
                    System.out.println("got an object from server");
                    System.out.println(obj.getClass());
                    String nameToShow = (String) obj;
                    checkboxState = (boolean[]) in.readObject();
                    otherSeqsMap.put(nameToShow, checkboxState);
                    listVector.add(nameToShow);
                    incomingList.setListData(listVector);
                }
            } catch (Exception ex) {ex.printStackTrace();}
        }
    }

    public class MyPlayMineListener implements ActionListener {
        public void actionPerformed (ActionEvent a) {
            if (mySequence != null) {
                sequence = mySequence;
            }
        }
    }

    public void changeSequence (boolean[] checkboxState) {
        for(int i = 0; i < 256; i++) {
            JCheckBox check = (JCheckBox) checkboxList.get(i);
            if(checkboxState[i]) {
                check.setSelected(true);
            } else {check.setSelected(false);}
        }
    }

    public void makeTracks (ArrayList<Integer> list){
        Iterator<Integer> it = list.iterator();
        for(int i=0;i<16;i++){
            Integer num = (Integer) it.next();
            if (num != null) {
                int numKey = num.intValue();
                track.add(makeEvent(144,9,numKey, 100, i));
                track.add(makeEvent(128,9,numKey, 100, i+1));
            }
        }
    }

    public  MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);

        } catch(Exception e) {e.printStackTrace(); }
        return event;
    }
    
}

