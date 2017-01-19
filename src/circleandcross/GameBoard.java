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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
	//private Game game = new Game(buttonList);
	private Game game = Game.getInstance(buttonList);

	private String ip = "localhost";
	private int port = 22222;
	private Scanner scanner = new Scanner(System.in);
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	private ServerSocket serverSocket;
	private boolean circle = true;
	private boolean accepted = false;
	private boolean unableToCommunicateWithOpponent = false;
	private Thread thread;
	private int errors = 0;
	private boolean threadEnable = false;



	public GameBoard(){
		Platform.setImplicitExit(false);
		
		Stage ipStage = new Stage();
		GridPane ipGridPane = new GridPane();
		Scene ipScene = new Scene(ipGridPane,400,150);
		
		TextField ipTextField = new TextField();
		TextField portField = new TextField();
		
		ipStage.setAlwaysOnTop(true);
		ipGridPane.setHgap(5);
		ipGridPane.setVgap(15);
		ipGridPane.setPadding(new Insets(15,15,15,15));
		ipStage.setTitle("Connect yourself!");
		ipStage.setScene(ipScene);
		ipStage.show();
		
		Label ipName = new Label("Adres ip:");
		Label portName = new Label("Port: ");
		
		ipGridPane.add(ipName, 0, 0);
		ipGridPane.add(portName, 0, 1);
		
		ipGridPane.add(ipTextField, 1, 0);
		ipGridPane.add(portField, 1, 1);
		
		ipTextField.setText("localhost");
		portField.setText("6011");
		
		
		Button btn = new Button("Accept");
		
		btn.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				String ipChosen = ipTextField.getText();
				int portChosen = Integer.parseInt(portField.getText());
				
				
				if (portChosen > 1 && portChosen < 65535 ){
					ip = ipChosen;
					port = portChosen;
					ipStage.close();
					
					if (!connect()) initializeServer();
					threadEnable = true;
					
				}else{
					
					
				}
			}

		});

		VBox vbtn = new VBox(10);
		vbtn.getChildren().add(btn);
		vbtn.setAlignment(Pos.CENTER);
		ipGridPane.add(vbtn, 1, 2,2, 1);
		
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
			if(threadEnable){
				tick();
				if (!circle && !accepted) {
					listenForServerRequest();
				}
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

		circle = false;
		game.initializePlayerServer();
		

	}




	@Override
	public void start(Stage stage) throws Exception {
		root = new GridPane();

		scene = new Scene(root, 800, 800);
		scene.getStylesheets().add(GameBoard.class.getResource("/CSS/GraphicSource.css").toExternalForm());

		stage.setTitle("Kó³ko i krzy¿yk");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();

		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				setGameField(i, j);
			}
		}

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