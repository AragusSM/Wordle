package application;


import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

//Class that simulates typing on the keyboard which reflects
//changes in the GUI
public class Keyboard{
	
	//letters ordered by keyboard, top to bottom, left to right
	private char[] letters = new char[26];
	
	//constructor that initializes the letters
	public Keyboard() {
		String keyboard = "QWERTYUIOPASDFGHJKLZXCVBNM";
		for(int i = 0; i < 26; i++) {
			letters[i] = keyboard.charAt(i);
		}
	}
	
	//pick a random starting letter for wordle
	public static char chooseLetter() {
		Random r = new Random();
		Keyboard k = new Keyboard();
		char random_char = k.letters[r.nextInt(26)];
		return random_char;
	}
	
	//called by outside functions to return the letters
	public char[] getLetters() {
		return letters;
	}
	
	//sets the effect of the onclick buttons of the keyboard
	public static void setFunctions(GridPane[] panels, GridPane[] keys, Display d, Dictionary dict, Label alert, StackPane stack) {
		if(keys.length > 3) {
			System.out.print("incorrect keyboard dimensions");
			return;
		}
		if(keys[0].getChildren().size() != 10 || keys[1].getChildren().size() != 9 ||
				keys[2].getChildren().size() != 9) {
			System.out.print("incorrect keyboard dimensions");
			return;
		}
		
		GridPane row1 = keys[0];
		setRowOne(panels, row1, d);
		GridPane row2 = keys[1];
		setRowTwo(panels, row2, d);
		setRowThree(panels, keys, d, dict, alert, stack);
		
	}
	
	//sets actions for first row of keyboard
	public static void setRowOne(GridPane[] panels, GridPane row1, Display d) {
		for(int i = 0; i < 10; i++) {
			Button keybutton;
			try {
				keybutton = (Button) row1.getChildren().get(i);
			}catch (Exception e){
				System.out.println("type cast exception");
				return;
			}
			// action event
	        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent e)
		        {	
	            	blink(keybutton);
	            	int row = d.getCurrentRow();
					int col = d.getCurrentCol();
					if(!d.rowFull() && !d.outofBounds()) {
						guess(panels, keybutton, row, col);
						d.setLetter(keybutton.getText().charAt(0), row, col);
					}
	            }
	        };
	        keybutton.setOnAction(event);
		}
	}
	
	//sets button actions for second row of the keyboard
	public static void setRowTwo(GridPane[] panels, GridPane row2, Display d) {
		for(int i = 0; i < 9; i++) {
			Button keybutton;
			try {
				keybutton = (Button) row2.getChildren().get(i);
			}catch (Exception e){
				System.out.println("type cast exception");
				return;
			}
			// action event
	        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent e)
		        {	
	            	blink(keybutton);
	            	int row = d.getCurrentRow();
					int col = d.getCurrentCol();
					if(!d.rowFull() && !d.outofBounds()) {
						guess(panels, keybutton, row, col);
						d.setLetter(keybutton.getText().charAt(0), row, col);
					}
	            }
	        };
	        keybutton.setOnAction(event);
		}
	}
	
	//sets the button actions for third row of the keyboard
	public static void setRowThree(GridPane[] panels, GridPane[] keys, Display d, Dictionary dict, Label alert, StackPane stack) {
		for(int i = 0; i < 9; i++) {
			
			Button keybutton;
			try {
				keybutton = (Button) keys[2].getChildren().get(i);
			}catch (Exception e){
				System.out.println("type cast exception");
				return;
			}
			EventHandler<ActionEvent> event;
			if(i == 0) {
				// action event
		        event = new EventHandler<ActionEvent>() {
		            public void handle(ActionEvent e)
			        {	
		            	blink(keybutton);
		            	if(!d.outofBounds() && !d.animation())
		            		d.enter(panels, keys, dict, alert, stack);
		            }
		        };
			}else if(i == 8) {
				// action event
		        event = new EventHandler<ActionEvent>() {
		            public void handle(ActionEvent e)
			        {	
		            	blink(keybutton);
		            	if(!d.animation() && !d.outofBounds()) {
			            	d.back();
			            	int row = d.getCurrentRow();
							int col = d.getCurrentCol();
			                erase(panels, row, col);
		            	}
		            }
		        };
			}else {
				// action event
		        event = new EventHandler<ActionEvent>() {
		            public void handle(ActionEvent e)
			        {	
		            	blink(keybutton);
		            	int row = d.getCurrentRow();
						int col = d.getCurrentCol();
						if(!d.rowFull() && !d.outofBounds()) {
							guess(panels, keybutton, row, col);
							d.setLetter(keybutton.getText().charAt(0), row, col);
						}
		            }
		        };
			}
	        keybutton.setOnAction(event);
		}
	}
	
	//changes the current panel's letter to the button typed
	public static void guess(GridPane[] grids, Button key, int row, int col) {
		GridPane grow = grids[row];
		try {
			Label panel = (Label) grow.getChildren().get(col);
			panel.setText(key.getText());
			panel.setStyle("-fx-border-color: #646464");
			flash(panel);
		}catch(Exception e) {
			System.out.println("type cast exception");
		}
	}
	
	
	//blinks the button
	public static void blink(Button key) {
		   FadeTransition fade = new FadeTransition();  
	          
	          
	        //setting the duration for the Fade transition   
	        fade.setDuration(Duration.millis(20));  
	          
	        //setting the initial and the target opacity value for the transition   
	        fade.setFromValue(1.0);  
	        fade.setToValue(0.3);  
	          
	        //setting cycle count for the Fade transition   
	        fade.setCycleCount(2);  
	          
	        //the transition will set to be auto reversed by setting this to true   
	        fade.setAutoReverse(true);  
	          
	        //setting Circle as the node onto which the transition will be applied  
	        fade.setNode(key);   
	        fade.play();
	}
	
	//flash after filling panel
	private static void flash(Label panel) {
		ScaleTransition grow = new ScaleTransition();  
        grow.setToX(1.08);
        grow.setToY(1.08);
        grow.setDuration(Duration.millis(20));
        grow.setAutoReverse(true);
        grow.setCycleCount(2);
        grow.setNode(panel);  
        
        ScaleTransition shrink = new ScaleTransition();  
        shrink.setToX(0.92);
        shrink.setToY(0.92);
        shrink.setDuration(Duration.millis(20));
        shrink.setAutoReverse(true);
        shrink.setCycleCount(2);
        shrink.setNode(panel); 
          
        //playing the transition    
        SequentialTransition seqT = new SequentialTransition (grow, shrink); 
        seqT.play();
	}
	
	//erases the previous guess
	public static void erase(GridPane[] grids, int row, int col) {
		GridPane grow = grids[row];
		try {
			Label panel = (Label) grow.getChildren().get(col);
			panel.setText("");
			panel.setStyle("-fx-border-color: #464646");
		}catch(Exception e) {
			System.out.println("type cast exception");
		}
	}
	
}
