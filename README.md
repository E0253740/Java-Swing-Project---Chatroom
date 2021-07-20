# Java Swing Project - Online Chatroom
This is project is a practice of Java Swing and Socket.<br/>
Two main Java files can be found here: 
1. **BeatBoxFinal** - for client side (客户端)
2. **MusicServer** - for server side (服务器)

## BeatBoxFinal
This java class includes the application of multiple **data structures** such as array, list (ArrayList & LinkedList) and HashMap. <br/>
Java Swing package is used for basic **GUI** (Graph User Interface). <br/>
Specially, we can see **inner class** here, which is quite special and the application here is a good demonstration of how we apply inner class concept.<br/>
The details is commented in the java file, while I'll show the visual of progress here.<br/>

**startUp** is the method that calls the other methods and create socket connection <br/>
**buildGUI** is where we use Java Swing to build the GUI (visualisation) <br/><br/>
**A Brief Overview of Client's GUI:** <br/><br/>
![image](https://user-images.githubusercontent.com/66471809/126269443-41c340bf-80e9-4af4-bdb0-a53ef6a20bcb.png)
These are the classes used from Java Swing:
JFrame: For the frame of the whole design <br/>
JPanel: We place the JPanel on the frame so that we can add more functions on it<br/>
JButton: An important function, we add ActionListener on the JButton so that when we click on the button, it'll execuate specific function, then we are able to achieve simple human-computer interaction. **Notice:** For each JButton, there should be a ***inner class*** which implements ***ActionListener*** <br/>



