# Java Swing Project - Online Chatroom
I did this project when I was learning "Head First Java". This is the project from the book, which is a good practice of Java knowledge. Following the instructions from the book, I did this project to practice my understandings about Java. <br/><br/>
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

The Midi package allows the user to create their own sequence and add to the track.<br/>
More details can be seen in the code.

## MusicServer.java
This file provides a virtual server for BeatBox progress, throught Java Socket and I/O stream, the BeatBox client is able to connect to the server at port 4242 and send messages. First, a Socket is created and input would be read through the input stream, then the message would be send to the server and presented to every user. 

![image](https://user-images.githubusercontent.com/66471809/127653064-a0725023-6836-4d60-ba2d-8afff1bc7e39.png)

## Start Progress
To start the progress, we start with the MusicServer.java. Compile it will command, then the port:4242 will be waiting for connection. <br/>
Then we start with BeatBoxFinal.java - the file would connect to port 4242. After the connection has been established, **"got a connection"** would be seen in MusicServer.java's command window. <br/>
What text the client send would be visible to all users who are connected to the server.
