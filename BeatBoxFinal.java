import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BeatBoxFinal {
    JFrame theFrame;
    JPanel mainPanel;
    JList incomingList;
    JTextField userMessage;
    ArrayList<JCheckBox> checkboxList;  // checkbox存储在ArrayList中
    int nextNum;
    Vector<String> listVector = new Vector<>();
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
    }

    public void startUp (String name){
        userName = name;
        // open connection to the server
        try {
            Socket sock = new Socket("localhost",5000);
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

        // 创建右侧Button Box
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
        incomingList = new JList();
        incomingList.addListSelectionListener(new MyListSelectionListener());
        incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incomingList);
        buttonBox.add(theList);
        incomingList.setListData(listVector);


        // 创建左侧Name Box
        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for(int i = 0; i<16; i++){
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST,buttonBox);
        background.add(BorderLayout.WEST,nameBox);

        theFrame.getContentPane().add(background);

        // 创建中间 mainPanel, 并且添加16*16 grid
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

    // Important: 将复选框状态转换为MIDI事件并加到track上
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
                } // 如果有勾选，将关键字值放在数组的该位置上，不然的话就补0
                else tracklist.add(null);
            }
            makeTracks(tracklist); // 创建此乐器的事件并加到track上
            track.add(makeEvent(192,9,1,0,15));
        }
        track.add(makeEvent(192,9,1,0,15));
        try{
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        }catch (Exception e){e.printStackTrace();}
    }

    public class MyStartListener implements ActionListener{ // 第一个内部类，是按钮的监听者
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
            sequencer.setTempoFactor((float)(tempoFactor *1.03)); // 节奏引子，预设为1.0 每次调整3%
        }
    }

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
         float tempoFactor = sequencer.getTempoFactor();
           sequencer.setTempoFactor((float)(tempoFactor * .97));
        }
    }

    public class MySendListener implements ActionListner {
        public void actionPerformed (ActionEvent a) {
            
        }
    }

    public void makeTracks (int[] list){
        for(int i=0;i<16;i++){
            int key = list[i];

            if (key != 0) {
                track.add(makeEvent(144,9,key, 100, i));
                track.add(makeEvent(128,9,key, 100, i+1));
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

