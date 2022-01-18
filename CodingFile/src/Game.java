import java.util.Scanner;

/**
 * enumerated class to keep track of which players are eliminated and which ones won
 */
enum PLAYER_STATUS {
    P("playing"),
    E("eliminated"),
    W("winner");

    /**
     * never actually called on but explains to any coder what the different statuses represent
     */
    private String description;

    PLAYER_STATUS(String description)
    {
        this.description = description;
    }
}

/**
 * Class that controls and implements a game of Rock, Paper, Scissors, Lizard, Spock
 *
 */
public class Game
{
    /**
     * Creates an array of players, the first position is always the Bot and the remaining are the various users.
     * Currently implementation is hard set for 1 human and the bot but game is set up to be able to allow more
     */
    private PlayerForGame[] players;
    /**
     * controls the bot's game choices since the bot isn't a sentient being
     */
    private Player_Bot botPlayer = new Player_Bot("Mr. Potato Head");
    /**
     * stores the moves for all players. Only contains the moves of the current turn
     */
    private MOVE[] allMoves;
    /**
     * used to keep track of if players have been eliminated, are still playing, or and the winner
     */
    private PLAYER_STATUS[] status;
    /**
     * used for clarity, represents the number of users + the one bot. Using this field you could potentially choose
     * to have multiple bots in each game but current code only has 1 bot
     */
    private int totalPlayers;
    /**
     * Static array that helps the program determine which moves beat and loose to which other moves. Each row contains
     * the relationships for a single move. The numbers in that row are 1 if the move in that column looses to it, -1
     * if the row's move looses to the move in that column, and 0 when it matches with itself. This is a numerical
     * representation of the string print out showed in the 'showGameRules()' method
     */
    private static final int[][] WHO_BEATS_WHO = {{0,-1,1,1,-1},{1,0,-1,-1,1},{-1,1,0,1,-1},{-1,1,-1,0,1},{1,-1,1,-1,0}};

    /**
     * Constructor that takes in how many players (other than the bot). The number of players could be determined based
     * on how many players are in the server channel or could be determined based on which players actually want to play
     *
     * @param numPlayers number of human players
     */
    public Game(int numPlayers)
    {
        this.totalPlayers = numPlayers + 1;
        this.players = new PlayerForGame[totalPlayers];
        this.allMoves = new MOVE[totalPlayers];
        this.status = new PLAYER_STATUS[totalPlayers];
        for(int i = 0; i < totalPlayers; i++)
        {
            status[i] = PLAYER_STATUS.P;
        }

        players[0] = botPlayer;

    /*    //following loop can be implemented if multiple human players but is currently not utilized

        for(int i = 1; i < totalPlayers; i++)
        {
            Scanner input = new Scanner(System.in);
            System.out.println("Player name:   ");
            String playerName = input.nextLine();
            players[i] = new PlayerForGame(playerName);
        }*/

        //instead the human player is
        players[1] = new PlayerForGame("client user");
    }

    /**
     * The logic that occurs after all players choose their move
     *
     * @param humanMove string representation of the player's move
     */
    public void gameTurn(String humanMove)
    {
        MOVE botMoveThisTurn = botPlayer.botMove(); //calls the Player_Bot class to randomly pick the bot's move
        allMoves[0] = botMoveThisTurn; //places the bots move into the array that stores everyone's current move

        MOVE userMove;
        switch (humanMove)
        {
            case "ROCK": userMove = MOVE.ROCK;
                break;
            case "PAPER": userMove = MOVE.PAPER;
                break;
            case "SCISSORS": userMove = MOVE.SCISSORS;
                break;
            case "LIZARD": userMove = MOVE.LIZARD;
                break;
            case "SPOCK":
            default:
                userMove = MOVE.SPOCK;
                break;
        }

        allMoves[1] = userMove;

        int roundResult = checkMovePair(allMoves[0].getName(), allMoves[1].getName());
        if(roundResult == 1)
        {
            status[0] = PLAYER_STATUS.W;
            status[1] = PLAYER_STATUS.P;
        }
        else if(roundResult == -1)
        {
            status[0] = PLAYER_STATUS.P;
            status[1] = PLAYER_STATUS.W;
        }
        else
        {
            status[0] = PLAYER_STATUS.P;
            status[1] = PLAYER_STATUS.P;
        }

    }

    /**
     * get method used to allow another class to access this field
     *
     * @return array housing the status of each player
     */
    public PLAYER_STATUS[] getStatus() {
        return status;
    }

    /**
     * Takes in the current players move and also the move of another player and returns the win/lose/tie result from
     * the pair. Done this way because the result is different from each player's point of view.
     *
     * Example player1: rock and player2: paper. Since paper covers rock the result is 1 from player2 perspective but
     * -1 from player1 perspective
     *
     * @param first move from current player's perspective
     * @param second move made by player that isn't the current player
     * @return result from current player's perspective
     */
    private static int checkMovePair(String first, String second)
    {
        // simple convert from String to location and look up
        String[] moveToLocation = {"R","P","Sc","L","Sp"};
        int rowLoc = 0;
        int colLoc = 0;
        for(int i = 0; i < 5; i++)
        {
            if(first.equals(moveToLocation[i]))
            {
                rowLoc = i;
            }
            if(second.equals(moveToLocation[i]))
            {
                colLoc = i;
            }
        }

        return WHO_BEATS_WHO[rowLoc][colLoc];
    }

    /**
     * Class that computes the output displayed to the current GUI implementation of the game. Geared specifically for
     * 1 computer player and 1 human player
     *
     * @return string that will get printed out to the user's GUI of the game
     */
    public String resultsToShow()
    {
        String result = "\nComputer move: " + allMoves[0].getDisplayName() + "\n";
        result+= "Your move: " + allMoves[1].getDisplayName() + "\n";

        int winnerDeterminer = checkMovePair(allMoves[0].getName(), allMoves[1].getName());
        if(winnerDeterminer == 1)
        {
            result+= "Round winner: computer\n";
        }
        else if(winnerDeterminer == -1)
        {
            result+= "Round winner: you\n";
        }
        else
        {
            result+= "TIED ROUND\n";
        }
        return result;
    }

    /**
     * Simple print out so players know which moves conquer over others. not currently used but useful if choose to
     * expand the current implementation
     */
    public void showGameRules()
    {
        System.out.println("\tSCISSORS cuts PAPER\n" +
                "\tPAPER covers ROCK\n" +
                "\tROCK crushes LIZARD\n" +
                "\tLIZARD poisons SPOCK\n" +
                "\tSPOCK smashes SCISSORS\n" +
                "\tSCISSORS decapitates LIZARD\n" +
                "\tLIZARD eats PAPER\n" +
                "\tPAPER disproves SPOCK\n" +
                "\tSPOCK vaporizes ROCK\n" +
                "\tand as it always has, ROCK crushes SCISSORS");
    }
}// end of Class

// large section of code that implemented logic for more than 2 players but is unnecesary for current application

/*        //create matrix to check turn results. uses the stored matrix to lookup what to use
        int[][] allResults = new int[totalPlayers][totalPlayers];
        for(int i = 0; i < allMoves.length; i++) //loops through each person's move
        {
            int[] result = new int[allMoves.length]; //creates the array to store result for a single person's move
            //check each move for beating others
            for(int j = 0; j < allMoves.length; j++)//loops through for each individual to compare to everyone else
            {
                if(j != i)//compares current player's move to all others because (order matters)
                {
                    result[j] = checkMovePair(allMoves[i].getName(), allMoves[j].getName());
                }
                else // don't need to compare to own move but sets to zero to make sure no previous stored value is used
                {
                    result[j] = 0;
                }
            }
            allResults[i] = result; //adds current player's individual array to the matrix
        }

        /* Logic for coder reference
           - see if someone beats others without anyone beating them (INSTANT WIN)
           - if no clear winner
                see if someone was beat without beating anyone else (PLAYER OUT)
        */
/*        int numWinners = 0; // used to keep track of for logic purposes
        int locationOfLastPlayerWithWinningStatus = 0; //if only one winner, this provides immediate look up instead of looping and searching
        for(int i = 0; i < totalPlayers; i++)//loops through the players
        {
            int wins = 0;//number times player beat another player's move
            int losses = 0;//number times player looses to another player's move
            for(int j = 0; j < totalPlayers; j++) //loops through everyone elses moves for a single player
            {
                switch (allResults[i][j]) // gets player to player result from set up menu
                {
                    case 1:
                        wins++;
                        break;
                    case -1:
                        losses++;
                        break;
                    default:
                        break;
                }
            }
            if(wins > 0 && losses == 0) //if person beat other players and no one beat them (INSTANT WIN)
            {
                numWinners++;//total number of people with a winning turn
                status[i] = PLAYER_STATUS.W; // sets status in array for later viewing
                locationOfLastPlayerWithWinningStatus = i; //allows immediate look up instead of looping through again
            }
            if(losses > 0 && wins == 0) //if beat by other players but doesn't beat anyone else (ELIMINATED)
            {
                status[i] = PLAYER_STATUS.E; //updates status
            }
*/
      //  }//end of looping through players

// reminder below code is for more than 2 players
  /*      if(numWinners == 1)// if only 1 person had winning status
        {
            System.out.println("We have a clear winner:  " +
                        players[locationOfLastPlayerWithWinningStatus].getPlayerUserName());
            gameOver = true;
        }
        else if(numWinners > 1) //can implement a tie breaker here or just let all of them win
        {
            System.out.println("need tie breaker");
            //only players with W status move on
        }
        else //we don't have a winner
        {*/
        /* Logic for coder reference
           - if more than 1 player still remains
                (NOT DONE) update remaining player's points.... for every time you were beat decrease points by 1
                if anyone reaches zero points they are automatically out
           - if one player left (THEY WIN)
           - if everyone removed(BOT WINS BY DEFAULT OR GAME BEATS ALL USERS)
        */
 /*           int remainingPlayers = 0; //keeps track of non-eliminated players
            int lastPlayerLocationNotEliminated = 0; //allows immediate look up instead of looping through again
            for (int i = 0; i < totalPlayers; i++) {
                if (status[i] != PLAYER_STATUS.E) //as long as player isn't eliminated
                {
                    remainingPlayers++;
                    lastPlayerLocationNotEliminated = i;
                }
            }
            // below commented section is only for when more than 2 players
            if(remainingPlayers > 1)
            {
                //make sure only non eliminated players can continue
               System.out.print("The following players advance: ");
                for(int i = 0; i < totalPlayers; i++)
                {
                    if(status[i] == PLAYER_STATUS.P)
                    {
                        System.out.print(players[i].getPlayerUserName() + "  ");
                    }
                }
            }
            else if(remainingPlayers == 1) //last player left
            {
                System.out.println("Winner is: " + players[lastPlayerLocationNotEliminated].getPlayerUserName());
                gameOver = true;
            }
            else if(remainingPlayers == 0) // everyone eliminated
            {
                System.out.println("There is no winner, game eliminates everyone");
                gameOver = true;
            }
        }
        //this is end of turn*/