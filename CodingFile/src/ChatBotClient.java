import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

/**
 * This class helps create the GUI and server connection for the client in this application. The GUI contains
 * an editable text field at the top where the user can type to other users in the chat room. When the user first connects
 * to the server, they are given the option on joining one of the two channels. They must input either 1 or 2, which corresponds
 * to which channel they join. Once they join the channel, they can live chat with whoever is also in their channel
 * The next area on the GUI is the text area. This outputs the connection problems/steps in getting connected.
 * This also displays all of the messages that have been sent by this user, the other users, and the server. It
 * shows who sent the message, and what the message is. The runClient method sets up the whole connection to the server,
 * and allows the user to begin chatting. Once the user is done on the server, they can type TERMINATE which will notify the server that they
 * are exiting, and then the client is disconnected.
 *
 * @see ChatBotServer
 * @see JFrame
 */
public class ChatBotClient extends JFrame {

    /**
     * This boolean is false until the user has chosen a legitimate channel to enter.
     */
    private boolean chosenChannel=false;

    /**
     * This textfield becomes editable once the user connects to server. It is where the user types the messages
     * they want to send to the other users.
     */
    private final JTextField userInput;
    /**
     * This JTextArea contains all the messages to the client. This includes if they are able to connect, messages they send,
     * messages from other users, and messages from the server.
     */
    private final JTextArea outputText;

    /**
     * This Socket is the one that connects to the server and provides the connection while the user is using the program
     */
    private Socket connection;
    /**
     * This stream contains the output which the user wants to send to the server.
     */
    private ObjectOutputStream output;
    /**
     * This stream contains any output by the server, and takes in those messages.
     */
    private ObjectInputStream input;

    /**
     * Constructor that names the GUI Client. It instantiates the JFrame which contains the GUI for the user. It
     * adds the text field, and uses an anonymous inner class to provide an action listener for it. This
     * listener takes in which file the user is searching for, and then sends that to the server. At first this is set
     * to uneditable, just in case there are problems connecting to the server. It then adds the text area
     * which contains all of the messages for the client. It also will contain the contents of the text file
     * they search for if it exists.
     */
    public ChatBotClient(){
        super("Client");

        userInput= new JTextField();
        userInput.addActionListener(    // Anonymous Inner Class
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            if(!chosenChannel){
                                if(!e.getActionCommand().equals("1") &&!e.getActionCommand().equals("2")){
                                    outputText.append("\nPlease choose a legal channel (1 or 2)\n");
                                }
                                else {
                                    output.writeObject(e.getActionCommand());
                                    output.flush();
                                    chosenChannel=true;
                                }
                            }
                            else {
                                if(e.getActionCommand().equals("!play"))
                                {
                                    Application.launch(GameRPS.class);
                                }
                                output.writeObject(e.getActionCommand());
                                output.flush();
                            }
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                        userInput.setText("");
                    }
                }
        );
        userInput.setEditable(false);
        add(userInput, BorderLayout.SOUTH);
        outputText=new JTextArea();
        add(new JScrollPane(outputText), BorderLayout.CENTER);
        outputText.setEditable(false);
        setVisible(true);
        outputText.setBackground(Color.YELLOW);
        outputText.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.BLACK));
        setSize(500,400);
    }

    /**
     * This method is where the client attempts to connect to the server, and then proceed to find files. There are four
     * different steps for connection. First a socket is made to connect to the server, then input and output streams
     * are created between the server and the client. Next the actual trying to receive files and operate on the server
     * is happening. Then a successful exit is attempted by the client. All of these methods are made seperately from
     * the runClient method to help modularization of the class.
     *
     */
    public void runClient() {
        try {
            //////////// STEP 1: CREATE A SOCKET TO CONNECT TO SERVER ////////////
            connect();

            //////////// STEP 2: GET SOCKET'S IO STREAMS ////////////
            getSocketIOAddress();

            //////////// STEP 3: PERFORM PROCESSING ////////////
            processing();

        } catch (IOException ioexception) {
            outputText.append(ioexception + "\n");
            ioexception.printStackTrace();
        }

        //////////// STEP 4: CLOSE THE CONNECTION ////////////
        finally {
            try{
                endConnection();
            }
            catch (IOException ioexception){
                outputText.append(ioexception+"\n");
                ioexception.printStackTrace();
            }
        }
    }

    /**
     * This method attempts to connect to the server. It throws an exception if there is a problem. If not, it prints
     * off that it has successfully connected.
     *
     * @throws IOException Exception if there is a problem connecting to the server
     */
    private void connect() throws IOException{
        // THIS LINE IS FOR TESTING
        //outputText.append("\nTrying to connect to the server\n");
        connection = new Socket(InetAddress.getLocalHost(), 23600);
        soundEffect("jpEngineers.wav");
        outputText.append("Welcome to the chatApp!\n");
        outputText.append("Which channel do you want to connect to? (1 or 2):");

        // THIS LINE IS FOR TESTING
        //outputText.append("\nConnected to: " + InetAddress.getLocalHost());
    }

    /**
     * This method gets the IO streams using the socket connection. It attempts to get both an output and an input
     * stream, in this case Object class ones for both of those. This is since most of the stream is String in this
     * program, which is a subclass of Object.
     *
     * @throws IOException Exception if either of the streams are unable to connect
     */
   private void getSocketIOAddress() throws IOException{
       // THIS LINE IS FOR TESTING
       //outputText.append("\nAttempting to get I/O streams\n");
       output = new ObjectOutputStream(connection.getOutputStream());
       output.flush();


       input = new ObjectInputStream(connection.getInputStream());

       // THIS LINE IS FOR TESTING
       //outputText.append("\nGot I/O streams\n");
    }

    /**
     * This method is where most of the server to client interactions happen. It sets the text field to be editable at the
     * top, and this allows the client to enter a file name for a certain file they want from the server. The server
     * then searches for the file, and will either send back the contents to be printed on the client GUI, or send a message
     * saying that the file was not found. The client can do this for as many times as they want.
     * This connection happens until the client enters TERMINATE, to exit connection from the server.
     *
     * @throws IOException Exception if there is any problem with server connections
     */
    private void processing() throws IOException{

        //outputText.append("What do you want to tell bot: ");
        userInput.setText("");
        userInput.setEditable(true);
        String message="";

        do // Starts getting messages from server
        {
            try // Reading and displaying the messages
            {
                message = (String) input.readObject(); // read new message
                outputText.append("\n" + message);
                outputText.setCaretPosition(outputText.getDocument().getLength());//autoscroll down feature
                soundEffect("cowMoo.wav");
            } catch (ClassNotFoundException classNotFoundException) {
                outputText.append("\nUnknown object type received");
            }


        } while (!message.equals("Server: TERMINATE"));

    }


    /**
     * This method is involved with making sure all of the connections are closed. It closes the socket, the
     * output stream, and the input stream.
     *
     * @throws IOException Exception if there are any errors when disconnecting from the server
     */
    private void endConnection() throws IOException {
        outputText.append("\nClosing connection");
        userInput.setEditable(false);

        output.close();
        input.close();
        connection.close();
        outputText.append("\nSession Over\n");
    }

    /**
     * SoundEffect uses the filename of a wav file to play the audio clip using AudioInputStream
     * WAV files are from wavsource.com
     * @param filename is the location of the wav file
     */
    public void soundEffect(String filename){
        try {
            URL location = this.getClass().getClassLoader().getResource(filename);
            AudioInputStream audioIn= AudioSystem.getAudioInputStream(location);
            Clip audio=AudioSystem.getClip();
            audio.open(audioIn);
            audio.start();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

}

