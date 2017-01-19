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
	private boolean firstSent = true;
	private boolean firstReceived = true;
	private int turnCount;
	int[][] virtualBoard = new int[15][15];
	// #monostate
	static private boolean gameStarted = false;
	private static Game instance;

	// #singletone
	public static Game getInstance(List<Button> buttonList2){
		if(instance != null) {
			return instance;
		} else {
			instance = new Game(buttonList2);
			return instance;
		}
	}

	// #builder
	Game(List<Button> buttonList){
		this.buttonList = buttonList;
		p1 = new Player();
		// use of #prototype
		p2 = p1.clone();
		setBoard();
	}

	private void setBoard(){
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				virtualBoard[i][j] = -1;
			}
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
		if(p1.getSign() != null )
			p1.setSign(Sign.values()[0]);
	}

	public boolean isGameStarted(){
		return gameStarted;
	}

	public void initializePlayerServer(){
		p1.setSign(Sign.values()[1]);
		p1.setTurn(true);
		System.out.println("dd");
	}


	public void sendDataToSocket(int row, int col, DataOutputStream dos, Button btn) {
		Sign sign = p1.getSign(); 
		boolean yourTurn = p1.getTurn();
		boolean gameState = false;
		String info;

		System.out.println("tura: " + yourTurn);
		if(yourTurn){
			if (sign == Sign.CIRCLE) {
				if(virtualBoard[row][col] == -1)
				{
					virtualBoard[row][col] = 0;
					btn.setText("O");
				}
				else
					return;
				

			}

			else {
				if (virtualBoard[row][col] == -1) {
					virtualBoard[row][col] = 1;
					btn.setText("X");
				}
				else
					return;
				
			}


			int signInt = (sign == Sign.CIRCLE) ? 0 : 1;
			if(checkBoard(signInt)){
				System.out.println("wygra�e�");
				clearBoard();
				gameState = true;
				p1.addPoint();
			}

			p1.setTurn(false);
			if(!firstSent){
				info = "" + row + "&" + col;
			}else{
				info = "" + row + "&" + col + "&" + p1.getName();
				firstSent = false;
			}                        

			try {
				if(gameState){
					if(!isPlayerWinner(p1)){
						dos.writeUTF("clear");                                    
					}else{
						dos.writeUTF("winner");
						firstSent=true;
						playerWins(p1);
					}
				}else{
					dos.writeUTF(info);
					System.out.println("Wysy�am " + info);   
				}                            
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


	public void getDataFromSocket(DataInputStream dis) {
		Sign sign = p1.getSign();
		boolean yourTurn = p1.getTurn();

		System.out.println("jestem tutaj");
		if (!yourTurn) {
			try {
				// odczytuj� i rozkodowuj� informacj�
				String info = dis.readUTF();
				System.out.println(info);
				if(info.equals("clear")){
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							clearBoard();
							p2.addPoint();
						}
					});
				}else if(info.equals("winner")){
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							clearBoard();
							playerLose(p1);
						}

					});
					firstReceived = true;
				}else{
					String[] split = info.split("&");
					// System.out.println("Odczyta�em " +info);
					String rowRead = split[0];
					String colRead = split[1];
					if(firstReceived){
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								p2.setName(split[2]);
							}
						});
						firstReceived = false;
					}

					int row = Integer.parseInt(rowRead);
					int col = Integer.parseInt(colRead);

					if (sign == Sign.CIRCLE) {
						virtualBoard[row][col] = 1;
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
						System.out.println(15 * col + row);

						Platform.runLater(new Runnable() {
							Button btn = buttonList.get(15 * col + row);

							@Override
							public void run() {
								btn.setText("O");
							}
						});
					}

				}

				int signInt = (sign == Sign.CIRCLE) ? 0 : 1;
				if(checkBoard(signInt))
					System.out.println("wygra�e�");

				p1.setTurn(true);

			} catch (IOException e) {
				e.printStackTrace();
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

	private boolean isPlayerWinner(Player player){
		if (player.getScore() == 1){
			return true;
		}
		return false;
	}

	private void playerWins(Player player){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Winner");
		alert.setHeaderText(player.getName() + " wins this match");
		alert.setContentText("Congratulations");
		getPlayer(1).clear();
		alert.showAndWait();
		gameStarted = false;
	}

	private void playerLose(Player player){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Loser");
		alert.setHeaderText(getPlayer(2).getName() + " wins this match");
		alert.setContentText("Try Again");
		getPlayer(1).clear();
		alert.showAndWait();
		gameStarted = false;
	}

}
