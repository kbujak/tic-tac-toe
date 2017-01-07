/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package circleandcross;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
//import static pl.bujak.moje.Print.*;

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
    int[][] virtualBoard = new int[15][15];
    
    Game(List buttonList){
        this.buttonList = buttonList;
        setPlayer(1);
        setPlayer(2);
        setBoard();
    }
    private void setBoard(){
    	for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				virtualBoard[i][j] = -1;
			}
		}
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
    
    
    
    
//    public String makeTurn(String text, int row, int col, boolean circle){
//        if (text.equals("")){
//        	
//            turnCount++;
//            Player currentPlayer;
//            Player otherPlayer;
//            if (getPlayer(1).getTurn()){
//                currentPlayer = getPlayer(1);
//                otherPlayer = getPlayer(2);
//            }else{
//                currentPlayer = getPlayer(2);
//                otherPlayer = getPlayer(1);
//            }
//            if (currentPlayer.getSign() == Sign.CIRCLE){
//                text = "O";
//                virtualBoard[row][col] = 0;
//            }else{
//                text = "X";
//                virtualBoard[row][col] = 1;
//            }
//            
//            currentPlayer.setTurn(false);
//            otherPlayer.setTurn(true);
//            
//            if(checkBoard(currentPlayer.getSign().ordinal())){
//                currentPlayer.addPoint();
//                for (Button btn: buttonList){
//                    btn.setText("");
//                }
//                
//                for(int i = 0; i < 15; i++){
//                    for(int j = 0; j < 15; j++){
//                        virtualBoard[i][j] = -1;
//                    }
//                }
//                isPlayerWinner(currentPlayer);
//                turnCount = 0;
//                return "";
//                
//            }else if (turnCount >= 225){
//                for (Button btn: buttonList){
//                    btn.setText("");
//                }
//                for(int i = 0; i < 15; i++){
//                    for(int j = 0; j < 15; j++){
//                        virtualBoard[i][j] = -1;
//                    }
//                }
//                turnCount = 0;
//                return "";
//            }
//                     
//        }
//        return text;
//    }
    
     boolean checkBoard(int signValue) {

		if (checkHorizontal(signValue))
			return true;
		if (checkVertical(signValue))
			return true;
		if (checkSkew(signValue))
			return true;

		return false;
	}

	private boolean checkHorizontal(int signValue) {
		int counter = 0;
		for (int row = 0; row < 15; row++) {
			counter = 0;
			for (int i = 0; i < 15; i++) {
				if (virtualBoard[row][i] == signValue){
					counter++;
					if(counter == 5)
						return true;
				}
				else
					counter = 0;
			}
		}

		return counter >= 5;
	}

	private boolean checkVertical(int signValue) {
		int counter = 0;

		for (int column = 0; column < 15; column++) {
			counter = 0;
			for (int i = 0; i < 15; i++) {
				if (virtualBoard[i][column] == signValue){
					counter++;
					if(counter == 5)
						return true;
				}
				else
					counter = 0;
			}
		}

		return false;
	}

	private boolean checkSkew(int signValue) {
		int counter = 0;
		int dim = 15;

		for( int k = 0 ; k < dim * 2 ; k++ ) {
	        for( int j = 0 ; j <= k ; j++ ) {
	            int i = k - j;
	            if( i < dim && j < dim ) {
	            	if (virtualBoard[i][j] == signValue){
						counter++;
						if(counter == 5)
							return true;
					}
					else
						counter = 0;
	            }
	        }
	        counter = 0;
	    }
		
		
		
		for(int i=10; i>=0; i--){
			int k=0;
			for(int j=i; j<15; j++, k++){
				if (virtualBoard[j][k] == signValue){
					counter++;
					if(counter == 5)
						return true;
				}
				else
					counter = 0;
			}
			counter = 0;
		}
		
		for(int i=10; i>=0; i--){
			int k=0;
			for(int j=i; j<15; j++, k++){
				if (virtualBoard[k][j] == signValue){
					counter++;
					if(counter == 5)
						return true;
				}
				else
					counter = 0;
			}
			counter = 0;
		}

		return false;
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
