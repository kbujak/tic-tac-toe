package circleandcross;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.application.Platform;

public class GameBoard extends Application implements Runnable{
	private List<Button> buttonList = new ArrayList<>();
	private Scene scene;
	private GridPane root;
	private Game game;

	private String ip = "localhost";
	private int port = 22222;
	private Scanner scanner = new Scanner(System.in);
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	private ServerSocket serverSocket;
	private boolean yourTurn = false;
	private boolean circle = true;
	private boolean accepted = false;
	private boolean unableToCommunicateWithOpponent = false;
	private Thread thread;
	private int errors = 0;
	

	
	public GameBoard(){
		Platform.setImplicitExit(false);
		System.out.println("Please input the IP: ");
		//ip = scanner.nextLine();
		ip = "localhost";
		System.out.println("Please input the port: ");
		//port = scanner.nextInt();
		port = 6000;
		while (port < 1 || port > 65535) {
			System.out.println("The port you entered was invalid, please input another port: ");
			port = scanner.nextInt();
		}
		
		if (!connect()) initializeServer();
		thread = new Thread(this, "TicTacToe");
		thread.start();
		
	}
	
	public void run() {
		while (true) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("Trying to read info");
			//System.out.println(circle);

			tick();
			if (!circle && !accepted) {
				listenForServerRequest();
			}

		}
	}

	
	private void tick() {
		if (errors >= 10) unableToCommunicateWithOpponent = true;

		if (!yourTurn && !unableToCommunicateWithOpponent) {
			try {
				//odczytuj� i rozkodowuj� informacj�
				String info = dis.readUTF();
				String[] split = info.split("&");
				//System.out.println("Odczyta�em " +info);
			    String rowRead = split[0];
			    String colRead = split[1];
			    
			    int row = Integer.parseInt(rowRead);
			    int col = Integer.parseInt(colRead);
			    
			    
				
				if (circle) {
					game.virtualBoard[row][col] = 1;
					System.out.println("IFzaznaczam vB["+row+"]"+"["+col+"] --- "+15 * row + col );
					System.out.println(15 * row + col );

					Platform.runLater(new Runnable() {
						Button btn = buttonList.get(15 * col + row);
						
						
						@Override
						public void run() {
							btn.setText("X");
						}
					});
				}

				else {
					game.virtualBoard[row][col] = 0;
					System.out.println("ELSEzaznaczam vB["+row+"]"+"["+col+"] --- ");
					System.out.println(15 * col + row );
					
					Platform.runLater(new Runnable() {
						Button btn = buttonList.get(15 * col + row);

						@Override
						public void run() {
							btn.setText("O");
						}
					});
				}
			    
				int sign = (circle) ? 0 : 1;
				if(game.checkBoard(sign))
					System.out.println("wygra�e�");
				
				yourTurn = true;
			} catch (IOException e) {
				e.printStackTrace();
				errors++;
			}
		}
	}
	
	private void listenForServerRequest() {
		Socket socket = null;
		try {
			socket = serverSocket.accept();
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			accepted = true;
			System.out.println("CLIENT HAS REQUESTED TO JOIN, AND WE HAVE ACCEPTED");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean connect() {
		try {
			socket = new Socket(ip, port);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			accepted = true;
		} catch (IOException e) {
			System.out.println("Unable to connect to the address: " + ip + ":" + port + " | Starting a server");
			return false;
		}
		System.out.println("Successfully connected to the server.");
		return true;
	}

	private void initializeServer() {
		try {
			serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
		} catch (Exception e) {
			e.printStackTrace();
		}
		yourTurn = true;
		circle = false;
	}
	
	
	
	
	@Override
	public void start(Stage stage) throws Exception {
		root = new GridPane();

		scene = new Scene(root, 800, 800);
		//scene.getStylesheets().add(GameBoard.class.getResource("/CSS/GraphicSource.css").toExternalForm());

		stage.setTitle("K�ko i krzy�yk");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();

		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				setGameField(i, j);
			}
		}

		game = new Game(buttonList);

		Button newGame = new Button("New Game");
		newGame.setId("newGameBtn");
		newGame.setMinWidth(100);
		newGame.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!game.isGameStarted()) {
					game.start();
				}
			}

		});
		game.start();
		root.add(newGame, 1, 6, 2, 1);

		VBox scoreTable = new VBox(30);
		scoreTable.setPadding(new Insets(15, 15, 15, 15));
		scoreTable.setStyle("-fx-border-color: #000000;");
		scoreTable.setMinHeight(200);
		scoreTable.setMinWidth(400);
		root.add(scoreTable, 0, 16, 3, 2);

		HBox p1Data = new HBox(80);
		p1Data.getChildren().addAll(game.getPlayer(1).getScoreName(), game.getPlayer(1).getScoreLabel());
		HBox p2Data = new HBox(80);
		p2Data.getChildren().addAll(game.getPlayer(2).getScoreName(), game.getPlayer(2).getScoreLabel());

		scoreTable.getChildren().addAll(p1Data, p2Data);
	}

	private void setGameField(int col, int row) {
		Button btn = new Button();
		buttonList.add(btn);
		btn.setMinWidth(30);
		btn.setMinHeight(30);
		root.add(btn, col, row);

		btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (game.isGameStarted()) {
					if (accepted && yourTurn && !unableToCommunicateWithOpponent) {
						// String sign = game.makeTurn(btn.getText(), row, col);
						// circle);

						// btn.setText(sign);
						// btn.setFont(Font.font(10));

						if (circle) {
							game.virtualBoard[row][col] = 0;
							btn.setText("O");

						}

						else {
							game.virtualBoard[row][col] = 1;
							btn.setText("X");
						}

						
						int sign = (circle) ? 0 : 1;
						if(game.checkBoard(sign))
							System.out.println("wygra�e�");
						yourTurn = false;
						String info = "" + row + "&" + col;

						try {
							dos.writeUTF(info);
							System.out.println("Wysy�am "+info);
							dos.flush();
						} catch (IOException e) {
							errors++;
							e.printStackTrace();
						}

					}

				}
			}

		});
	}

	public static void main(String[] args) {
		launch(args);
	}




}