package circleandcross;


import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bujag
 */
public class NameCreator{
	final private Stage stage;
	final private GridPane root;
	final private Scene scene;
	final private TextField player1TextField;
	final private TextField player2TextField;
	final private Player p1;
	final private Player p2;

	// #builder
	public NameCreator(Player p1, Player p2) {
		stage = new Stage();
		root = new GridPane();
		scene = new Scene(root,400,150);
		player1TextField = new TextField();
		player2TextField = new TextField();
		this.p1 = p1;
		this.p2 = p2;
		init();
	}

	private void init(){
		stage.setAlwaysOnTop(true);
		root.setHgap(5);
		root.setVgap(15);
		root.setPadding(new Insets(15,15,15,15));
		stage.setTitle("Start game!");
		stage.setScene(scene);
		stage.show();

		Label player1 = new Label("Your name: ");
		Label player2 = new Label("Oponent name: ");
		root.add(player1, 0, 0);
		root.add(player2, 0, 1);        

		root.add(player1TextField, 1, 0);

		p2.setName("Waiting for oponent");



		Button btn = new Button("Accept");
		btn.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				if (p1 != null && p2 != null){
					String p1Name = player1TextField.getText();

					if (!p1Name.equals("") ){
						p1.setName(p1Name);
						stage.close();
					}else{
						emptyTextFieldAlert();
					}
				}else{
					emptyPlayerInstance();
					System.exit(-1);
				}
			}

		});

		VBox vbtn = new VBox(10);
		vbtn.getChildren().add(btn);
		vbtn.setAlignment(Pos.CENTER);
		root.add(vbtn, 1, 2,2, 1);
	}

	private void emptyTextFieldAlert(){
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warrning Dialog");
		alert.setHeaderText("Warrning!");
		alert.setContentText("You haven't choose player names!");
		alert.showAndWait();
	}

	private void emptyPlayerInstance(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("Error");
		alert.setContentText("Player instance is null!");
		alert.showAndWait();
	}
}
