import javax.swing.*;

/**
 * This class helps test the ChatBotServer class and sets up a server. Once the server is set up, it exits when the JFrame is
 * closed. It runs the server using the runServer() method.
 *
 * @author Tyler Wessels, Stephanie Waddell, and Jack Ewert
 * @see ChatBotServer
 */
public class RunServer {
    /**
     * This is the main method that sets up the ChatBotServer.
     *
     * @param args not used in this program.
     */
    public static void main(String[] args) {

        ChatBotServer application = new ChatBotServer(); // create server
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.runServer(); // run server application
    }
}