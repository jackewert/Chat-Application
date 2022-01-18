import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class creates the GUI and the server connection for the Server in this application. For the gui, it is just
 * a basic JFrame, and it has one JTextArea that contains any messages relevant to the server. This included things
 * like if the client connected, all of the messages sent by the client for both channels, how many users there are,
 * and also when clients disconnect and connect. There is also an inner private class, that does all of the processing
 * needed by the server. This inner class, is actually the code for a single client's thread it starts when they connect
 * to the server. This thread is executed when the client connects, then disconnects when they leave. It is still
 * a thread, but set to not active. The server also has a text field at the top where it can send messages to all
 * of the clients. The server class sets up the server, then creates an array of the private class instances so that
 * when a new client joins they have a thread they activate. Their position in the array corresponds to their user number.
 * There can be a total of 100 clients that can join in total. There is also a corresponding array that contains all of
 * the current users and which channel they are on. There are two total channels, and the user chooses which one they
 * want once they enter the server. The threads are started up using the executor service.
 */
public class ChatBotServer extends JFrame{

    /**
     * This ChatBot is the chat bot that the users can interact with special commands in either of the channels.
     * It has different responses based on what the user inputs.
     */
    private ChatBot bot = new ChatBot();
    /**
     * This ExecutorService is used to execute each of the client threads. Whenever a new client joins, a new thread
     * is created.
     */
    private ExecutorService executorService;
    /**
     * This is an array of the private inner class, SockServer. Each SockServer is an instance of a client thread.
     * This array contains each of the clients.
     */
    private SockServer [] sockServer;
    /**
     * This array is an integer array corresponding to the SockServer array. It contains either 0,1,2. If it is 0,
     * then that client thread isn't opened. If it is 1, the client thread is running and the client is on channel 1.
     * If it is 2, the client thread is running and the client is on channel 2.
     */
    private int[] userChannel;
    /**
     * This int corresponds to the total amount of clients that are active and inactive across both channels
     */
    private int totalUserConnections =1;
    /**
     * This int corresponds to the total amount of clients that are active across both channels
     */
    private static int numActiveUsers =0;
    /**
     * This int corresponds to the total amount of clients that are active on channel one
     */
    private int channel1Users=0;
    /**
     * This StringBuilder contains all of the written messages that ever happened on channel 1
     */
    private StringBuilder channel1;
    /**
     * This StringBuilder contains all of the written messages that ever happened on channel 2
     */
    private StringBuilder channel2;

    /**
     * This JTextField is editable, and allows the server to send messages to all of the clients
     */
    private JTextField userInput;

    /**
     * This int is the total number of clients that can be opened on the server, 100 for this program
     */
    private final int MAX_NUMBER_OF_CLIENTS;

    /**
     * This JTextArea contains all of the text that is relevant to the server when it is running.
     */
    private final JTextArea outputText;
    /**
     * This socket is what sets up the server for a client to connect to
     */
    private ServerSocket server;

    /**
     * This constructor creates the GUI used for the server. It sets up the JFrame, and instantiates
     * the JTextArea used for the text about the server. There is also an anonymous inner class, for when
     * the server user sends a message to the clients. This sets up the editable text field and receives the
     * input from there. It also sets up the output text area for the server to contain all of its messages.
     */
    public ChatBotServer() {

        super("Server");

        channel1=new StringBuilder();
        channel2=new StringBuilder();


        MAX_NUMBER_OF_CLIENTS =100;

        userChannel=new int[MAX_NUMBER_OF_CLIENTS];

        sockServer= new SockServer[MAX_NUMBER_OF_CLIENTS];
        executorService= Executors.newFixedThreadPool(MAX_NUMBER_OF_CLIENTS);

        userInput = new JTextField();
        userInput.setEditable(false);
        userInput.addActionListener(    // Annonymous inner class for Server user input
                new ActionListener() {

                    public void actionPerformed(ActionEvent event) {

                        for (int i = 1; i <= totalUserConnections; i++) {
                            if (sockServer[i].active)
                                try {
                                    sockServer[i].sendMessageToAllClients("Server: "+event.getActionCommand());
                                    break;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }
                        userInput.setText("");
                    }
                }
        );

        add(userInput, BorderLayout.NORTH);

        outputText=new JTextArea();
        add(new JScrollPane(outputText), BorderLayout.CENTER);
        setVisible(true);
        outputText.setEditable(false);
        setSize(300,200);
    }

    /**
     * This method uses a couple other methods in this class to set up the server. First it creates the server socket
     * to port 23600, and creates a backlog allowing 5 clients at a time to join. After the server is set up,
     * there is a while loop that runs until the server is terminated. During this time, a method allows connection from
     * clients. This is done using the sockServer array which contains all the threads of clients. When a client succesfully
     * connects, it instantiates a new thread for them in the array. It then executes the thread, and allows the client
     * to use the server through the inner class methods.
     */
    public void runServer() {
        try // Setting up the server
        {
            // STEP 1: CREATE SERVER SOCKET ////////////////////////////
            startServer();

            while (true) {
                try {
                    sockServer[totalUserConnections] = new SockServer(totalUserConnections);
                    sockServer[totalUserConnections].connect();
                    numActiveUsers++;

                    executorService.execute(sockServer[totalUserConnections]);

                } catch (EOFException eofexception) { // Once the client leaves, it sends an EOFException, and this will send this
                    // message to the client and the server
                    outputText.append("\nClient has disconnected\n");
                } finally {
                    ++totalUserConnections;
                }
            }
        }
        catch (IOException ioException) {
            outputText.append("\nCan't set up server on this port\n");
            ioException.printStackTrace(); // Prints off normal stack of where the errors occurred during the execution
        }
    }

    /**
     * This method sets up the server. It sets it to port 23600 and with a backlog of 5.
     *
     * @throws IOException exception if there is a problem setting up the server
     */
    private void startServer() throws IOException{
        outputText.append("\nAttempting to start up server\n");
        server = new ServerSocket(23600, 5);
        outputText.append("\nServer has been set up at "+ InetAddress.getLocalHost()+"\nThe Port is: 23600\n");
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * This is the private inner class which contains the code for setting up a client thread. It implements runnable,
     * and is ran using the executor service. Each instance is one client's connection to the server. It has the input and
     * output streams for the client to connect to the server. It has a main run method, which is run while the thread is up.
     * It gets the ObjectStreams for both the input and the output streams, and then does the processing. Once done processing,
     * it closes all of the streams and sockets correctly.
     */
    private class SockServer implements Runnable {

        /**
         * This stream outputs any information that the client might need
         */
        private ObjectOutputStream output;
        /**
         * This stream takes in any input from the client for the server
         */
        private ObjectInputStream input;
        /**
         * This socket is for the server to output and input streams
         */
        private Socket connection;
        /**
         * This int is the unique number of the current client's identifier
         */
        private int userNumber;
        /**
         * This boolean is turned true when the thread is running, and false when it isn't
         */
        private boolean active = false;
        /**
         * This boolean is only true for the very first message recieved from the client
         */
        private boolean firstMessage=true;

        /**
         * This is the constructor which sets up the thread. It only takes in on int, which is the unique
         * number identifying which thread this client is.
         *
         * @param userNumber int of the current client's identifier
         */
        public SockServer(int userNumber) {
            this.userNumber = userNumber;
        }

        /**
         * This method is what is run while the thread is alive for the client. It first get's the input and output streams
         * to the server. It then performs all of the processing for the client server interactions. Once done, the connection
         * is carefully and cleanly closed.
         */
        @Override
        public void run() {
            try {
                active = true;
                try {
                    getSocketIOAddress(); // get input & output streams
                    processing(); // process connection

                } catch (EOFException e) {
                    sendMessageToAllClients("User " + userNumber + " has left the lobby",userChannel[userNumber-1]);
                }
                finally {
                    endConnection();

                }

            }
            catch (IOException exception){
                exception.printStackTrace();
            }
        }

        /**
         * This method allows the server to accept connections from clients. It is ran while the server is up.
         *
         * @throws IOException exception if there is a problem connecting to the client
         */
        private void connect() throws IOException{
            connection = server.accept(); // Has server accept the connection
        }

        /**
         * This method helps the server get IO streams to a client. It creates both an input and an output stream between
         * the client.
         *
         * @throws IOException exception if there is a problem establishing the streams
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
         * This method helps successfully and safely terminate the server once it must be terminated. It closes all of the
         * streams and also the socket.
         *
         * @throws IOException exception if there is a problem shutting down the server
         */
        private void endConnection() throws IOException{
            numActiveUsers--;   // User has left the chat

            active = false;

            if(userChannel[userNumber-1]==1) {
                channel1Users--;
                sendMessageToAllClients("Users online in this channel lobby is now: " + channel1Users,1);
            }
            else{
                int channel2Users=numActiveUsers-channel1Users;
                sendMessageToAllClients("Users online in this channel lobby is now: " + channel2Users,2);
            }

            if(numActiveUsers ==0){
                userInput.setEditable(false);
            }

            output.close();
            input.close();
            connection.close();
        }

        /**
         * This method contains the framework for the main exchange between the client and the server. It takes in the user
         * input from the client and then prints it to all other clients on the same channel and also the server. The very first
         * message received from the client is which channel they chose to join, either one or two. This is processed, and
         * then the client joins their channel. This is shown to all the other current clients on the channel, and then
         * all of the previous messages before the current client joined are sent to the current client. Once connected,
         * whenever the client, any of the other clients on the channel, or the server sends a message it is output to the
         * client. This is done until the client disconnects. Once the client disconnects, it safely exits the server and
         * notifies other clients on the channel and also the server.
         *
         * @throws IOException exception if there is any problem processing the client requests.
         */
        private void processing() throws IOException{

                String message="";

                userInput.setEditable(true);

                do {
                    try {
                        message = (String) input.readObject();
                        if(firstMessage){
                            userChannel[userNumber-1]=Integer.parseInt(message);
                            message = "User " + userNumber + " has joined the lobby for channel "+userChannel[userNumber-1];
                            if(userChannel[userNumber-1]==1){
                                sockServer[userNumber].output.writeObject("\n------------------------ Previously on channel "+userChannel[userNumber-1]+" ------------------\n");
                                sockServer[userNumber].output.writeObject(channel1.toString());
                                sockServer[userNumber].output.writeObject("--------------------------------------------------------------\n");
                                channel1Users++;
                                sendMessageToAllClients(message,1);
                                sendMessageToAllClients("Users online now: " + channel1Users,1);

                                output.flush();
                            }
                            else{
                                sockServer[userNumber].output.writeObject("\n------------------------ Previously on channel "+userChannel[userNumber-1]+" ------------------\n");
                                sockServer[userNumber].output.writeObject(channel2.toString());
                                sockServer[userNumber].output.writeObject("--------------------------------------------------------------\n");
                                int channel2Users=numActiveUsers-channel1Users;
                                sendMessageToAllClients(message,2);
                                sendMessageToAllClients("Users online now: " + channel2Users,2);

                                output.flush();
                            }
                            firstMessage=false;

                        }
                        else {
                            sendMessageToAllClients("User " + userNumber + ": " + message,userChannel[userNumber-1],message);
                        }


                    } catch (ClassNotFoundException e) {
                        outputText.append("\nUnknown object type");
                        e.printStackTrace();
                    }
                } while (!message.equals("TERMINATE"));

        }

        /**
         * This method is used when both the server and all of the clients receive the same message. It takes in the message
         * and outputs it to the server JTextArea and also sends it through all of the output streams. It sends it to
         * every client that is connected to the server. It only sends it to the active clients. It also adds whatever
         * is sent to the string builders which contain records of all the files sent on both channel one and two.
         *
         * @param message String of the message to be displayed by both the server and the clients
         * @throws IOException exception if there is a problem displaying the message to either the client or the server
         */
        private void sendMessageToAllClients(String message) throws IOException {
            outputText.append("\n"+message);
            channel1.append(message).append("\n");
            channel2.append(message).append("\n");

            for (int i = 1; i <= totalUserConnections; i++) {
                if (sockServer[i].active) {
                    sockServer[i].output.writeObject(message);
                    output.flush();
                }
            }
        }

        /**
         * This method is used when both the server and all of the clients on one channel receive the same message. It takes in the message
         * and outputs it to the server JTextArea and also sends it through all of the output streams on one channel. It sends it to
         * every client that is connected to the client. It only sends it to the active clients. It also adds whatever
         * is sent to the string builders which contain records of all the files sent on either channel one or two.
         *
         * @param message String of the message to be displayed by both the server and the clients
         * @param channelNumber int of the channel the message should be sent to
         * @throws IOException exception if there is a problem displaying the message to either the client or the server
         */
        private void sendMessageToAllClients(String message,int channelNumber) throws IOException {
            outputText.append("\nChannel "+channelNumber+": "+message);
            if(channelNumber==1){
                channel1.append(message).append("\n");
            }
            else{
                channel2.append(message).append("\n");
            }


            for (int i = 1; i <= totalUserConnections; i++) {
                if (sockServer[i].active&&userChannel[i-1]==channelNumber) {
                    sockServer[i].output.writeObject(message);
                    output.flush();
                }
            }
        }

        /**
         * This method is the exact same as the two parameter version of this method, it just contains an extra string
         * which contains the original message without the user who sent it. This is used to be sent to the bot which
         * then provides a response if a user enters a certain message in.
         *
         * @param message String of the message to be displayed by both the server and the clients
         * @param channelNumber int of the channel the message should be sent to
         * @param message2 String of the message without the current user
         * @throws IOException exception if there is a problem displaying the message to either the client or the server
         */
        private void sendMessageToAllClients(String message,int channelNumber,String message2) throws IOException {
            outputText.append("\nChannel "+channelNumber+": "+message);
            String fromBot = bot.botCommands(message2);
            if(channelNumber==1){
                channel1.append(message).append("\n").append(fromBot).append("\n");
            }
            else{
                channel2.append(message).append("\n").append(fromBot).append("\n");
            }

            for (int i = 1; i <= totalUserConnections; i++) {
                if (sockServer[i].active&&userChannel[i-1]==channelNumber) {
                    sockServer[i].output.writeObject(message);
                    sockServer[i].output.writeObject(fromBot);
                    output.flush();
                }
            }
        }

    }


}
