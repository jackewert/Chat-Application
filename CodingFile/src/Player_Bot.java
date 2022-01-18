import java.util.Random;

/**
 * extends the PlayerForGame class but includes a method to randomly pick a move since the computer player or bot
 * can't actually make that decision on its own
 */
public class Player_Bot extends PlayerForGame
{
    /**
     * field to allow a random number generation
     */
    private Random random = new Random();

    /**
     * constructor that calls on the super class PlayerForGame to actually instantiate the player
     *
     * @param playerName chosen name for the computer player, determined by calling function
     */
    public Player_Bot(String playerName)
    {
        super(playerName);
    }

    /**
     * uses a randomly generated number from 1 to 5 to pick the MOVE the bot uses
     *
     * @return randomly selected MOVE
     */
    public MOVE botMove()
    {
        MOVE botMove;

        int num = random.nextInt(5)+1;
        switch (num)
        {
            case 1: botMove = MOVE.ROCK;
                break;
            case 2: botMove = MOVE.PAPER;
                break;
            case 3: botMove = MOVE.SCISSORS;
                break;
            case 4: botMove = MOVE.LIZARD;
                break;
            case 5:
            default: botMove = MOVE.SPOCK; //also the default MOVE if something goes wrong
                break;
        }
        return botMove;
    }
}
