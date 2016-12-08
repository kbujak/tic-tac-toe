package circleandcross;

import java.util.ArrayList;
import java.util.List;
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

public class GameBoard extends Application{
    private List<Button> buttonList= new ArrayList<>();
    private Scene scene;
    private GridPane root;
    private Game game;
    
    @Override
    public void start(Stage stage) throws Exception {
        root = new GridPane();
        
        scene = new Scene(root,300,500);
        scene.getStylesheets().add(
        GameBoard.class.getResource("/CSS/GraphicSource.css").toExternalForm());
        
        stage.setTitle("Kółko i krzyżyk");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
        
        setGameField(0, 0);
        setGameField(1, 0);
        setGameField(2, 0);
        setGameField(0, 1);
        setGameField(1, 1);
        setGameField(2, 1);
        setGameField(0, 2);
        setGameField(1, 2);
        setGameField(2, 2);
        
        game = new Game(buttonList);
        
        Button newGame = new Button("New Game");
        newGame.setId("newGameBtn");
        newGame.setMinWidth(100);
        newGame.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if (!game.isGameStarted()){
                    game.start();
                }
            }
        
    });
        root.add(newGame, 1, 6,2, 1);
        
        
        VBox scoreTable = new VBox(30);
        scoreTable.setPadding(new Insets(15,15,15,15));
        scoreTable.setStyle("-fx-border-color: #000000;");
        scoreTable.setMinHeight(100);
        scoreTable.setMinWidth(270);
        root.add(scoreTable, 0, 4,3, 2);
        
        HBox p1Data = new HBox(80);
        p1Data.getChildren().addAll(game.getPlayer(1).getScoreName(),
                game.getPlayer(1).getScoreLabel());
        HBox p2Data = new HBox(80);
        p2Data.getChildren().addAll(game.getPlayer(2).getScoreName(),
                game.getPlayer(2).getScoreLabel());
        
        scoreTable.getChildren().addAll(p1Data,p2Data);
    }
    
    private void setGameField(int col, int row){
        Button btn = new Button();
        buttonList.add(btn);
        btn.setMinWidth(80);
        btn.setMinHeight(80);
        root.add(btn, col, row);
        
        btn.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if (game.isGameStarted()){
                    String sign = game.makeTurn(btn.getText(), row, col);
                    btn.setText(sign);
                    btn.setFont(Font.font(30));
                }
            }
            
        });
    }
    
    public static void main(String[] args){
        launch(args);
    }
    
    
}