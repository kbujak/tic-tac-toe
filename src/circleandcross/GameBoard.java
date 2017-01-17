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
//import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.application.Platform;

public class GameBoard extends Application implements Runnable{
	private List<Button> buttonList = new ArrayList<>();
	private Scene scene;
	private GridPane root;
	private Game game = new Game(buttonList);

	private String ip = "localhost";
	private int port = 22222;
	private Scanner scanner = new Scanner(System.in);
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	private ServerSocket serverSocket;
//	private boolean yourTurn = false;
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
		port = 6004;
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

		if (!unableToCommunicateWithOpponent) {
			
			game.getDataFromSocket(dis);
			
			
			
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
		
//		yourTurn = true;
		circle = false;

		
		//sets player1 as cross
		game.initializePlayerServer();
		
	}
	
	
	
	
	@Override
	public void start(Stage stage) throws Exception {
		root = new GridPane();

		scene = new Scene(root, 800, 800);
		scene.getStylesheets().add(GameBoard.class.getResource("/CSS/GraphicSource.css").toExternalForm());

		stage.setTitle("Kółko i krzyżyk");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();

		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				setGameField(i, j);
			}
		}

		//game = new Game(buttonList);

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
		//game.start();
		root.add(newGame, 0, 17, 3, 2);

		VBox scoreTable = new VBox(30);
		scoreTable.setPadding(new Insets(15, 15, 15, 15));
		scoreTable.setStyle("-fx-border-color: #000000;");
		scoreTable.setMinHeight(200);
		scoreTable.setMinWidth(400);
		root.add(scoreTable, 0, 20, 12, 2);

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
					if (accepted  && !unableToCommunicateWithOpponent) {
						game.sendDataToSocket(row, col, dos, btn);

					}

				}
			}

		});
	}

	public static void main(String[] args) {
		launch(args);
	}




}