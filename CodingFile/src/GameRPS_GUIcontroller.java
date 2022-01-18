import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * class that contains all the developer designed code for the gameGUI
 */
public class GameRPS_GUIcontroller
{
    /**
     * List used so the client can only pick valid game moves
     */
    ObservableList<String> moveList = FXCollections.observableArrayList("ROCK", "PAPER", "SCISSORS",
                                                                            "LIZARD", "SPOCK");
    /**
     * textField that keeps track of how many times the computer has won the round
     */
    @FXML
    private TextField compWins;
    /**
     * textField that keeps track of how many times the human user has won the round
     */
    @FXML
    private TextField humanWins;
    /**
     * image display container to hold a visual representation of the game rules
     */
    @FXML
    private ImageView ruleVisual;
    /**
     * text area that updates after every round telling the user what the bot chose as a move, what their move was, and
     * who won or if there was a tie
     */
    @FXML
    private TextArea gameOutput;
    /**
     * word display of which moves beat what other moves. Is Sheldon Cooper's line from Big Bang Theory
     */
    @FXML
    private TextArea ruleWriting;
    /**
     * place for the user to decide and input how many rounds they want to play with the bot
     */
    @FXML
    private TextField numTurnsInput;
    /**
     * keeps track of each round and reminds the user how many rounds are left
     */
    @FXML
    private TextField remainingTurns;
    /**
     * used only once to tell the game that the user is ready to begin and has input the number of turns they want to
     * play. If the user doesn't input a number and arbitrary one is chosen and if the user inputs some character
     * that can not be turned into an integer the user will have to try again for the start button to perform its
     * function
     */
    @FXML
    private Button startButton;
    /**
     * used by the human player to tell the game they have chosen their next move
     */
    @FXML
    private Button moveButton;
    /**
     * allows the user to pick their move
     */
    @FXML
    private ComboBox moveOptions;
    /**
     * creates an instance of the game
     */
    private Game clientGame = new Game(1);

    /**
     * sets up the specific parameters desired by the developer upon opening the application
     */
    @FXML
    private void initialize()
    {
        compWins.setEditable(false);
        humanWins.setEditable(false);
        gameOutput.setEditable(false);
        ruleWriting.setEditable(false);
        remainingTurns.setEditable(false);
        numTurnsInput.setEditable(true);
        ruleVisual.setImage(new Image("spock.png"));
        moveButton.setDisable(true);
        moveButton.setVisible(false);
        startButton.setVisible(true);

        moveOptions.setItems(moveList);
        moveOptions.setDisable(true);
    }

    /**
     * class that handles the events of the user clicking on the buttons
     *
     * @param event the button that was clicked by the user
     */
    @FXML
    private void handleButtons(ActionEvent event)
    {
        int games = 1;
        String move = "ROCK";

        if(event.getSource() == startButton)
        {
            try {
                games = Integer.valueOf(numTurnsInput.getText());
                startButton.setDisable(true);
                startButton.setVisible(false);
                moveOptions.setDisable(false);
                moveButton.setDisable(false);
                moveButton.setVisible(true);
                numTurnsInput.setEditable(false);
            }
            catch(NumberFormatException e)
            {
                games = 5;
            }
            remainingTurns.setText(String.valueOf(games));
        }
        else if(event.getSource() == moveButton)
        {
            int turnsLeft = Integer.valueOf(remainingTurns.getText());
            if(turnsLeft >= 1)
            {
                move = (String) moveOptions.getSelectionModel().getSelectedItem();
                clientGame.gameTurn(move);
                gameOutput.appendText(clientGame.resultsToShow());
                PLAYER_STATUS[] status = new PLAYER_STATUS[2];
                status = clientGame.getStatus();
                if(status[0] == PLAYER_STATUS.W)
                {
                    int wins = Integer.valueOf(compWins.getText());
                    wins++;
                    compWins.setText(String.valueOf(wins));
                }
                else if(status[1] == PLAYER_STATUS.W)
                {
                    int wins = Integer.valueOf(humanWins.getText());
                    wins++;
                    humanWins.setText(String.valueOf(wins));
                }
                else
                {
                    //tie and no wins updated
                }
                turnsLeft--;
                remainingTurns.setText(String.valueOf(turnsLeft));
            }
            if(turnsLeft == 0)
            {
                moveButton.setDisable(true);
                gameOutput.appendText("*************** GAME OVER *****************");
            }
        }
    }//end of handle buttons method
}//end of game gui controller class
