import java.util.Random;

/**
 * enumerated class for the fun of the bot having multiple "personalities" to respond with
 */
enum BOT_NAME {
    MR("[Mr. Potato Head]:  "),
    MRS("[Mrs. Potato Head]:  "),
    JR("[Junior]:  ");

    /**
     * the 'personality' answering at the time
     */
    private String nameBot;

    private BOT_NAME(String name)
    {
        this.nameBot = name;
    }

    public String getNameBot() {
        return nameBot;
    }
}

/**
 * The  code that works the Chat bot and its features. It is small now but more features are being worked on for the
 * future
 */
public class ChatBot
{
    /**
     * holds the string that the bot responds to the user. is actually passed through the server of the connection to
     * all clients so all clients can see it's response.
     *
     * later additions can include bot responding only to 1 user like a private conversation
     */
    private String outputToServer;
    /**
     * stores the bot's current 'personality'
     */
    private String name = (BOT_NAME.JR).getNameBot();
    /**
     * used to randomly pick which 'personality' is responding to the clients
     */
    private Random random = new Random();

    /**
     * constructor that is called by the server so the 1 bot can connect to every client
     */
    public ChatBot()
    {
        outputToServer = "Bot connected!";
    }

    /**
     * simple get method for another class to receive the last output from the bot
     *
     * @return string that holds the last bot's response
     */
    public String getOutputToServer()
    {
        return outputToServer;
    }

    /**
     * method that uses a random number generator to pick which 'personality' the bot will respond with. It currently
     * picks randomly for each response. With the use of the enum class you could have one bot have several 'characters'
     * that respond to the user in different ways. This is currently not implemented but is planned for release later
     *
     * @return bot's current 'personality' represented by a String
     */
    private String pickResponderName()
    {
        int num = random.nextInt(3)+1;
        switch (num)
        {
            case 1: name = (BOT_NAME.MR).getNameBot();
                break;
            case 2: name = (BOT_NAME.MRS).getNameBot();
                break;
            case 3: name = (BOT_NAME.JR).getNameBot();
                break;
            default:
        }
        return name;
    }

    /**
     * main functionality of the bot. holds the responses based on the user's input. Can be easily expanded as more
     * features are added.
     *
     * The bot only responds if the user's input starts with a '!' and even then only pays attention to the first
     * word entered. This allows for more dynamic and funnier conversation features.
     *
     * @param inputFromClient string sent by client to server.
     * @return string output to the client by the server
     */
    public String botCommands(String inputFromClient)
    {

        String botReturnResponse = "";
        try
        {
            inputFromClient = inputFromClient.substring(0, inputFromClient.indexOf(' '));
        }catch (IndexOutOfBoundsException e)
        {
            //nothing really has to change
        }

        if(inputFromClient != null)
        {
            char first = inputFromClient.charAt(0);
            if(first == '!')
            {
                botReturnResponse = pickResponderName();
                switch (inputFromClient)
                {
                    case "!hi":
                    case "!Hi":
                    case "!Hello":
                    case "!hello":
                    case "!hey":
                    case "!Hey":
                        botReturnResponse+= "Hi there! Do you want to play a game?\n";
                        break;
                    case "!yes":
                    case "!Yes":
                    case "!y":
                    case "!Y":
                        botReturnResponse+= "Great! Today's game is: RPSLS\n";
                        break;
                    case "!game":
                    case "!Game":
                        botReturnResponse+= "The game is ";
                    case "!?":
                    case "!what":
                        botReturnResponse+= "Rock Paper Scissors Lizard Spock! DUH!\n";
                        break;
                    case "!no":
                    case "!No":
                    case "!n":
                    case "!N":
                        botReturnResponse+= "No means yes! So we play now.\n";
                        break;
                    case "!help":
                    case "!Help":
                    case "!h":
                    case "!H":
                        displayListOfChatCommands();
                        botReturnResponse = getOutputToServer();
                        break;
                    case "!why":
                    case "!Why":
                        botReturnResponse += "cuz I said so.\n";
                        break;
                    case "!who":
                    case "!Who":
                        botReturnResponse+= "YOU\n";
                        break;
                    case "!play":
                        botReturnResponse+= "Hope you had as much fun as I did\n";
                        break;
                    case "!lets":
                    case "!Lets":
                        botReturnResponse+= "Is that like the bad touch?\n";
                        break;
                    default:
                        botReturnResponse+= "I don't understand. Try '!help'\n";
                }
            }
        }// end of if statement
        else{
            botReturnResponse = "\n";
        }

        return botReturnResponse;
    } // end of bot commands method

    /**
     * called on to help a user know what commands that bot has available. This must unfortunately be updated with every
     * new feature.
     *
     * It would be a good idea to implement commands into an enumerated class and loop through enumerated values for
     * better design and security
     */
    private void displayListOfChatCommands()
    {
        outputToServer = "\n*********LIST OF POSSIBLE CHAT COMMANDS***********\n" +
                "!hi\t!hello\t!yes\t!no\t!?\n!what\t!game\t!why\t!who\t!lets\n!play";
    }
}
