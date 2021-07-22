# Java Swing Project - Online Chatroom
This is project is a practice of Java Swing and Socket.<br/>
Two main Java files can be found here: 
1. **BeatBoxFinal** - for client side (客户端)
2. **MusicServer** - for server side (服务器)<br/>

Throught the Java Socket API, we are able to create a chatroom that allows online chat & customize music. Java Swing is used for GUI, which is the most direct part user can get access to.

## BeatBoxFinal.java
This java class includes the application of multiple **data structures** such as array, list (ArrayList & LinkedList) and HashMap. <br/>
Java Swing package is used for basic **GUI** (Graph User Interface). <br/>
Specially, we can see **inner class** here, which is quite special and the application here is a good demonstration of how we apply inner class concept.<br/>
The details is commented in the java file, while I'll show the visual of progress here.<br/>

**startUp** is the method that calls the other methods and create socket connection <br/>
**buildGUI** is where we use Java Swing to build the GUI (visualisation) <br/><br/>
**A Brief Overview of Client's GUI:** <br/><br/>
![image](https://user-images.githubusercontent.com/66471809/126646912-138bc467-97ef-446e-82c2-ef018d2deeed.png)
These are the classes used from Java Swing:
**JFrame**: For the frame of the whole design <br/>
**JPanel**: We place the JPanel on the frame so that we can add more functions on it<br/>
**JButton**: An important function, we add ActionListener on the JButton so that when we click on the button, it'll execuate specific function, then we are able to achieve simple human-computer interaction. **Notice:** For each JButton, there should be a ***inner class*** which implements ***ActionListener*** <br/>
**JTextField**: The textfield for the user to send message, what user types here would be stored as String by .getText() method and then sent to the online server.<br/>
**JCheckBox**: The 16 * 16 girds. By selecting the checkbox, different track and sequence would be created.<br/>
**JScrollPanel** This area is to show the message that has been sent to the server. A scroll strip would appear if there's multiple messages.<br/>
class RemoteReader is used for I/O Stream to set up the connection between client and server.

