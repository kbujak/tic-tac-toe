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
//import java.util.Random;

import javafx.application.Platform;
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
    
    
    // to jest wzorzec builder? tworzy nowe obiekty p1, p2 :)
    Game(List<Button> buttonList){
        this.buttonList = buttonList;
        p1 = new Player();
        p2 = new Player();
        setBoard();
    }
    
    private void setBoard(){
    	for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				virtualBoard[i][j] = -1;
			}
		}
    }
    
    // po  co to jest? 
//    private void setPlayer(int id){
//        if (id == 1){
//            p1 = new Player();
//        }else {
//            p2 = new Player();
//
//        }
//    }
    
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
        if(p1.getSign() != null )
        	p1.setSign(Sign.values()[0]);
        //p2.setSign(Sign.values()[1]);
        
        
       // shuffleSigns();
       // shuffleTurns();
    }
    
    public boolean isGameStarted(){
        return gameStarted;
    }
    
    public void initializePlayerServer(){
    	p1.setSign(Sign.values()[1]);
    	p1.setTurn(true);
    	System.out.println("dd");
    }
    
//    private void shuffleSigns(){
//        Random rand = new Random();
//        int idSign = rand.nextInt(Sign.values().length);
//        p1.setSign(Sign.values()[idSign]);
//        p2.setSign(Sign.values()[1 - idSign]);
//    }
    
//    private void shuffleTurns(){
//        Random rand = new Random();
//        int id = rand.nextInt(2);
//        if (id == 0){
//            p1.setTurn(true);
//            p2.setTurn(false);
//        }
//    }
    
	public void sendDataToSocket(int row, int col, DataOutputStream dos, Button btn) {
		Sign sign = p1.getSign(); 
		boolean yourTurn = p1.getTurn();
		
		if(yourTurn){
			if (sign == Sign.CIRCLE) {
				virtualBoard[row][col] = 0;
				btn.setText("O");

			}

			else {
				virtualBoard[row][col] = 1;
				btn.setText("X");
			}

//			int sign = (circle) ? 0 : 1;
//			if (game.checkBoard(sign))
//				System.out.println("wygra³eœ");
			
			//yourTurn = false;
			int signInt = (sign == Sign.CIRCLE) ? 0 : 1;
			if(checkBoard(signInt)){
				System.out.println("wygra³eœ");
				clearBoard(); //jak zrobic zeby to czyscilo plansze obu playerom?
			}
			
			p1.setTurn(false);
			String info = "" + row + "&" + col;

			try {
				dos.writeUTF(info);
				System.out.println("Wysy³am " + info);
				dos.flush();
			} catch (IOException e) {
				//errors++;
				e.printStackTrace();
			}
		}

	}
    
    
	public void getDataFromSocket(DataInputStream dis) {
		Sign sign = p1.getSign();
		boolean yourTurn = p1.getTurn();

		if (!yourTurn) {
			try {
				// odczytujê i rozkodowujê informacjê
				String info = dis.readUTF();
				String[] split = info.split("&");
				// System.out.println("Odczyta³em " +info);
				String rowRead = split[0];
				String colRead = split[1];

				int row = Integer.parseInt(rowRead);
				int col = Integer.parseInt(colRead);

				if (sign == Sign.CIRCLE) {
					virtualBoard[row][col] = 1;
					System.out.println("IFzaznaczam vB[" + row + "]" + "[" + col + "] --- " + 15 * row + col);
					System.out.println(15 * row + col);

					Platform.runLater(new Runnable() {
						Button btn = buttonList.get(15 * col + row);

						@Override
						public void run() {
							btn.setText("X");
						}
					});
					
					
				}

				else {
					virtualBoard[row][col] = 0;
					System.out.println("ELSEzaznaczam vB[" + row + "]" + "[" + col + "] --- ");
					System.out.println(15 * col + row);

					Platform.runLater(new Runnable() {
						Button btn = buttonList.get(15 * col + row);

						@Override
						public void run() {
							btn.setText("O");
						}
					});
				}

//				int sign = (circle) ? 0 : 1;
//				if (game.checkBoard(sign))
//					System.out.println("wygra³eœ");

				//yourTurn = true;
				int signInt = (sign == Sign.CIRCLE) ? 0 : 1;
				if(checkBoard(signInt))
					System.out.println("wygra³eœ");
				
				p1.setTurn(true);
				
			} catch (IOException e) {
				e.printStackTrace();
				//errors++;
			}
		}
	}
    
    
    
    public String makeTurn(String text, int row, int col, boolean circle){
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
                clearBoard();
                isPlayerWinner(currentPlayer);
                turnCount = 0;
                text = "";
                
            }else if (turnCount >= 225){
            	clearBoard();
                turnCount = 0;
                text = "";
            }
                     
        }
        return text;
    }
    
    private void clearBoard(){
        for (Button btn: buttonList){
            btn.setText("");
        }
        
        for(int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                virtualBoard[i][j] = -1;
            }
        }
    }
    
    boolean checkBoard(int signValue) {

		if (checkHorizontal(signValue) || checkVertical(signValue) || checkSkew(signValue))
			return true;
		else
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
    
    private void isPlayerWinner(Player player){
        if (player.getScore() == 3){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Winner");
            alert.setHeaderText(player.getName() + " wins this match");
            alert.setContentText("Congratulations");
            getPlayer(1).clear();
            getPlayer(2).clear();
            alert.showAndWait();
            gameStarted = false;
        }
    }
}
