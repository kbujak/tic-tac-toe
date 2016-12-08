/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package circleandcross;

import java.util.List;
import java.util.Random;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import static pl.bujak.moje.Print.*;

/**
 *
 * @author Bujag
 */
public class Game {
    private Player p1;
    private Player p2;
    private List<Button> buttonList;
    private boolean gameStarted = false;
    private int turnCount;
    private int[][] virtualBoard = 
        { {-1,-1,-1},
          {-1,-1,-1},
          {-1,-1,-1}
        };
    
    Game(List buttonList){
        this.buttonList = buttonList;
        setPlayer(1);
        setPlayer(2);
    }
    
    private void setPlayer(int id){
        if (id == 1){
            p1 = new Player();
        }else {
            p2 = new Player();
        }
    }
    
    public Player getPlayer(int id){
        if (id == 1){
            return p1;
        }else {
            return p2;
        }
    }
    
    public void start(){
        new NameCreator(getPlayer(1),getPlayer(2));
        gameStarted = true;
        shuffleSigns();
        shuffleTurns();
    }
    
    public boolean isGameStarted(){
        return gameStarted;
    }
    
    private void shuffleSigns(){
        Random rand = new Random();
        int idSign = rand.nextInt(Sign.values().length);
        p1.setSign(Sign.values()[idSign]);
        p2.setSign(Sign.values()[1 - idSign]);
    }
    
    private void shuffleTurns(){
        Random rand = new Random();
        int id = rand.nextInt(2);
        if (id == 0){
            p1.setTurn(true);
            p2.setTurn(false);
        }
    }
    
    public String makeTurn(String text, int row, int col){
        if (text.equals("")){
            turnCount++;
            Player currentPlayer;
            Player otherPlayer;
            if (getPlayer(1).getTurn()){
                currentPlayer = getPlayer(1);
                otherPlayer = getPlayer(2);
            }else{
                currentPlayer = getPlayer(2);
                otherPlayer = getPlayer(1);
            }
            if (currentPlayer.getSign() == Sign.CIRCLE){
                text = "O";
                virtualBoard[row][col] = 0;
            }else{
                text = "X";
                virtualBoard[row][col] = 1;
            }
            currentPlayer.setTurn(false);
            otherPlayer.setTurn(true);
            
            if(checkBoard(currentPlayer.getSign().ordinal())){
                currentPlayer.addPoint();
                for (Button btn: buttonList){
                    btn.setText("");
                }
                
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        virtualBoard[i][j] = -1;
                    }
                }
                isPlayerWinner(currentPlayer);
                turnCount = 0;
                return "";
                
            }else if (turnCount >= 9){
                for (Button btn: buttonList){
                    btn.setText("");
                }
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        virtualBoard[i][j] = -1;
                    }
                }
                turnCount = 0;
                return "";
            }
                     
        }
        return text;
    }
    
    private boolean checkBoard(int signValue){
        return ((virtualBoard[0][0] == signValue && virtualBoard[0][1] == signValue && virtualBoard[0][2] == signValue) ||
           (virtualBoard[1][0] == signValue && virtualBoard[1][1] == signValue && virtualBoard[1][2] == signValue) ||
           (virtualBoard[2][0] == signValue && virtualBoard[2][1] == signValue && virtualBoard[2][2] == signValue) ||
           (virtualBoard[0][0] == signValue && virtualBoard[1][0] == signValue && virtualBoard[2][0] == signValue) ||
           (virtualBoard[0][1] == signValue && virtualBoard[1][1] == signValue && virtualBoard[2][1] == signValue) ||
           (virtualBoard[0][2] == signValue && virtualBoard[1][2] == signValue && virtualBoard[2][2] == signValue) ||
           (virtualBoard[0][0] == signValue && virtualBoard[1][1] == signValue && virtualBoard[2][2] == signValue) ||
           (virtualBoard[0][2] == signValue && virtualBoard[1][1] == signValue && virtualBoard[2][0] == signValue)
                );   
    }
    
    private void isPlayerWinner(Player currentPlayer){
        if (currentPlayer.getScore() == 3){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Winner");
            alert.setHeaderText(currentPlayer.getName() + " wins this match");
            alert.setContentText("Congratulations");
            getPlayer(1).clear();
            getPlayer(2).clear();
            alert.showAndWait();
            gameStarted = false;
        }
    }
}
