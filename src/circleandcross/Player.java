/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package circleandcross;

import javafx.scene.control.Label;

/**
 *
 * @author Bujag
 */
public class Player {
    private Label scoreName = new Label("Player Name");
    private boolean currentTurn = false;
    private Sign sign;
    private String name;
    private int score;
    private Label scoreLabel = new Label("Score: " + String.valueOf(score));
    
    public void setName(String name){
        this.name = name;
        this.scoreName.setText(name + "(" + sign +") :");
    }
    
    public String getName(){
        return name;
    }
    
    public void setSign(Sign sign){
        this.sign = sign;
    }
    
    public Sign getSign(){
        return sign;
    }
    
    public void setTurn(boolean turn){
        this.currentTurn = turn;
    }
    
    public boolean getTurn(){
        return currentTurn;
    }
    
    public Label getScoreName(){
        return scoreName;
    }
    
    public int getScore(){
        return score;
    }
    
    public Label getScoreLabel(){
        return scoreLabel;
    }
    
    public void addPoint(){
        scoreLabel.setText("Score: " + ++score);
    }
    
    public void clear(){
        this.name = "";
        this.score = 0;
        this.scoreName.setText("Player Name");
        scoreLabel.setText("Score: " + score);
    }
}
