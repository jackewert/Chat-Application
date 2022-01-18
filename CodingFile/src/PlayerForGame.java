/**
 * enumerated class to keep allow only allowed moves to be played and to know which moves beat what
 */
enum MOVE {
    ROCK("R", "Sc", "L", "P", "Sp", "Rock"),
    PAPER("P", "R", "Sp", "Sc", "L", "Paper"),
    SCISSORS("Sc", "P", "L", "R", "Sp", "Scissors"),
    LIZARD("L", "P", "Sp", "R", "Sc", "Lizard"),
    SPOCK("Sp", "R", "Sc", "P", "L", "Spock");
    /**
     * String used to represent the move and check results in the Game class
     */
    private String name;
    /**
     * informs which moves loose to the current one
     */
    private String[] beats = new String[2];
    /**
     * informs which moves beat the current one
     */
    private String[] looses = new String[2];
    /**
     * name used to display to user
     */
    private String displayName;

    /**
     * constructor or enumerated MOVE class
     *
     * @param name name of the move
     * @param beats1 first move that looses to this move
     * @param beats2 second move that looses to this move
     * @param looses1 first move that beats this move
     * @param looses2 second move that beats this move
     * @param displayName string that the user can see and understand
     */
    private MOVE(String name, String beats1, String beats2, String looses1, String looses2, String displayName)
    {
        this.name = name;
        this.beats[0] = beats1;
        this.beats[1] = beats2;
        this.looses[0] = looses1;
        this.looses[1] = looses2;
        this.displayName = displayName;
    }

    /**
     * returns the name of the move. called on by Game class to check results
     *
     * @return name of the called upon move
     */
    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}//end of enumerated MOVE class

/**
 * Class to represent each player in the game. Has fields to store any information that may need to be looked at later
 * by the Game or the Server to know what to display
 */
public class PlayerForGame
{
    /**
     * number of points each player starts with. can be easily changed based on coder's preference
     */
    private static final int START_POINTS = 5;
    /**
     * way to identify the player, should be the user name for ease of identification to the server
     */
    private final String playerUserName;
    /**
     * current points value of the player. Updated by the Game class and if reaches zero the player is eliminated
     */
    private int playerPoints;
    /**
     * instance of the enumerated MOVE class to represent the most recently chosen choice by the player
     */
    private MOVE currMove;

    /**
     * Constructor for any player that will be participating in the game
     *
     * @param playerName identification for the individual player, should be their chat username so client can easily identify
     */
   public PlayerForGame(String playerName)
   {
       this.playerUserName = playerName;
       this.playerPoints = START_POINTS;
       this.currMove = MOVE.ROCK;
   }

    /**
     * updated when the player chooses their move. needs to be done so that other player's can't see this until everyone
     * has chosen their moves. could be implemented with a count down timer
     *
     * @param playMove String name of the enumerated MOVE the player chose
     */
    public void setCurrMove(String playMove) {
        // probably have to get user's input from somewhere
        for(MOVE move : MOVE.values())
        {
            if(move.getName().equals(playMove))
            {
                this.currMove = move;
            }
        }
    }

    /**
     * Allows the Game or server to get the player's identification so they can show who actually won or was eliminated
     *
     * @return individual player's identification
     */
    public String getPlayerUserName()
    {
        return playerUserName;
    }

    /**
     * complements the setCurrMove() method so that the Game can access the MOVE to determine results with other player's moves
     *
     * @return most recent MOVE chosen by the player
     */
    public MOVE getCurrMove() {
        return currMove;
    }
}
