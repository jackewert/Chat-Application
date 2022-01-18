import java.util.Scanner;

/**
 * used to test the game logic without starting up the server. interacts with the command window and asks for the
 * player's move. With this it is a must that the MOVES be typed in specifically as 'R', 'P', 'Sc', 'L', or 'Sp' because
 * the Game would usually get the MOVE from the enumerations so there isn't any implementation of typing in a wrong move
 */
public class TestGame
{
    /**
     * instance of the Game to test
     */
    private static Game testPlay;

    /**
     * Just a simple run method for java used to test the game code without having to connect to extra components
     *
     * @param args required inputs for a java program to run main
     */
    public static void main(String[] args)
    {
        int numPlayer = -1;
        Scanner input = new Scanner(System.in);
        System.out.println("How many players?    ");
        numPlayer = input.nextInt();

        testPlay = new Game(numPlayer);
        //testPlay.showGameRules();
        //Player_Bot botPlay = new Player_Bot("bot");
        boolean over = false;
        while(!over)
        {
            testPlay.gameTurn("ROCK");
        }
    }
}
