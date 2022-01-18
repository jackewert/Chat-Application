import javax.swing.*;

/**
 * This class instantiates a ChatBotClient object, and sets the GUI up. It closes the client on exit, and calls the
 * runClient() method on the new ChatBotClient.
 *
 * @author Tyler Wessels, Stephanie Waddell, and Jack Ewert
 * @see ChatBotClient
 */
public class RunClient {
    /**
     * This is the main method that sets up the ChatBotClient.
     *
     * @param args not used in this program
     */
    public static void main(String[] args) {

        ChatBotClient application = new ChatBotClient(); // create client
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.runClient(); // run client application
    }
}
