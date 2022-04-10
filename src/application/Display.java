package application;


import java.io.FileNotFoundException;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

//class that manages display for the wordle game
public class Display {
	
	private final String keyboard = "QWERTYUIOPASDFGHJKLZXCVBNM";
	
	private boolean animating;
	private boolean locked;
	private boolean full;
	
	private char[][] screen;
	private int[] letter_colors;	
	private int cur_row;
	private int cur_col;
	

	//constructs an matrix that represents the current game state
	public Display() {
		//letter colors is the letter keyboard
		//let 0 be original, 1 be blacked, 2 be yellow, and 3 be pinked
		letter_colors = new int[26];
		screen = new char[6][5];
		cur_row = 0;
		cur_col = 0;
		locked = false;
		full = false;
	}
	
	//return current wordle row
	public int getCurrentRow() {
		return cur_row;
	}
	
	//return current wordle column
	public int getCurrentCol() {
		return cur_col;
	}
	
	//set the current row and col to the next one
	public void next() {
		if(cur_row > 5) 
			return;
		if(cur_col == 4 && !locked) {
			full = true;
		}else if (cur_col == 4){
			cur_col = 0;
			cur_row++;
		}else {
			//not at the end
			cur_col++;
		}
	}
	
	//simulates a backspace 
	public void back() {
		//need to somehow delete the last panel
		if(cur_col == 0) {
			return;
		}
		if(screen[cur_row][cur_col] != 0 && cur_col == 4) {
			screen[cur_row][cur_col] = 0;
			full = false;
			return;
		}
		cur_col--;
		screen[cur_row][cur_col] = 0;
		full = false;
	}
	
	//only called if cur row is 5, shouldn't
	//be in any other context. locks the current row
	//and reveals the letters.
	public void enter(GridPane[] panels, GridPane[] keys, Dictionary d, Label alert, StackPane stack) {
		if(!full) {
			showMessage(0, alert, d, stack);
			animating = true;
			wiggleRow(panels);
			return;
		}
		if(cur_row > 5) {
			return;
		}
		String word = "" + screen[cur_row][0] + screen[cur_row][1] + screen[cur_row][2] + screen[cur_row][3] + screen[cur_row][4];
		if(d.containsWord(word)) {
			int[] colors = checkWord(d);	
			animating = true;
			flipRow(alert, panels, keys, colors, d, stack);
		}else {
	        showMessage(-1, alert, d, stack);
	        animating = true;
	        wiggleRow(panels);
		}
		
	}
	
	//moves the letters up if we guessed the word
	private void finish(Label alert, GridPane[] panels, Dictionary d, StackPane stack) {
		
		showMessage(cur_row + 1, alert, d, stack);
		for(int i = 0; i < 5; i++) {
			TranslateTransition translate = new TranslateTransition();  
	        translate.setByY(-30);  
	        translate.setDuration(Duration.millis(150));  
	        translate.setCycleCount(2);  
	        translate.setAutoReverse(true);   
	        translate.setNode(panels[cur_row].getChildren().get(i));  
	        translate.setDelay(Duration.millis(150*i));
	        translate.play();
		}
		
	}
	
	//shows the error message/ win message
	private void showMessage(int message_no, Label alert, Dictionary d, StackPane stack) {
		switch(message_no) {
		case -1:
			alert.setText("Not in word list");
			break;
		case -0:
			alert.setPadding(new Insets(15, 40, 15, 40));
			alert.setText("Not enough letters");
			break;
		case 1:
			alert.setPadding(new Insets(15, 20, 15, 20));
			alert.setText("Genius");
			break;
		case 2:
			alert.setPadding(new Insets(15, 20, 15, 20));
			alert.setText("Magnificent");
			break;
		case 3:
			alert.setPadding(new Insets(15, 20, 15, 20));
			alert.setText("Impressive");
			break;
		case 4:
			alert.setPadding(new Insets(15, 20, 15, 20));
			alert.setText("Splendid");
			break;
		case 5:
			alert.setPadding(new Insets(15, 20, 15, 20));
			alert.setText("Great");
			break;
		case 6:
			alert.setPadding(new Insets(15, 20, 15, 20));
			alert.setText("Phew");
			break;
		default:
			alert.setPadding(new Insets(15, 20, 15, 20));
			alert.setText(d.getCurrentWord());
			break;
		}
		
		FadeTransition fadein = new FadeTransition();  
	    fadein.setDuration(Duration.millis(50));  
	    fadein.setFromValue(0);  
	    fadein.setToValue(1);  
	    fadein.setNode(alert);  
	    
	    FadeTransition fadeout = new FadeTransition();  
	    fadeout.setDuration(Duration.millis(50));  
	    fadeout.setFromValue(1);  
	    fadeout.setToValue(0);
	    fadeout.setNode(alert);  
	    
	    FadeTransition stay = new FadeTransition();  
	    stay.setDuration(Duration.millis(1500));  
	    stay.setFromValue(1);  
	    stay.setToValue(1);
	    stay.setNode(alert);  
	    
	    SequentialTransition seqT = new SequentialTransition (fadein, stay, fadeout); 
	    seqT.play();
	    if(message_no >= 1) {
	    	seqT.setOnFinished(event -> goNext(stack, d));
	    }
	   
	}
	
	//prints the finish message
	private void goNext(StackPane stack, Dictionary d) {
		//create gridpane and add labels and button
		GridPane g = Panels.addGridPane(Pos.CENTER, 20, 20, 20, 20);
		g.setMinSize(stack.getHeight(), stack.getWidth());
		g.setStyle("-fx-background-color: #404040;");
		
		Label text1 = new Label("The Word Was:");
		text1.setStyle("-fx-border-width: 0px;  -fx-background-color: #404040;");
		text1.setTranslateY(-50);
		g.add(text1, 0, 0);
		
		Label text2 = new Label(d.getCurrentWord());
		text2.setStyle("-fx-border-width: 0px;  -fx-background-color: #404040; -fx-font-size: 35px; -fx-text-fill: #F71DB3;");
		text2.setTranslateX(25);
		g.add(text2, 0, 0);
		
		Button play_again = new Button("Play Again");
		play_again.setStyle("-fx-background-color: #F71DB3; -fx-font-size: 18px;");
		g.add(play_again, 0, 1);
		
		// action event
        EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
	        {	
            	Keyboard.blink(play_again);
            	//repeat action in main except just add to the existing scene
            	//create rows for the wordle
    			GridPane[] grids1 = new GridPane[6];
    			for(int i = 0; i < grids1.length; i++) {
    				if(i == 0) {
    					grids1[i] = Panels.addGridPane(Pos.CENTER, 20, 20, 0, 20);
    				}else if (i == grids1.length) {
    					grids1[i] = Panels.addGridPane(Pos.CENTER, 0, 20, 20, 20);
    				}else {
    					grids1[i] = Panels.addGridPane(Pos.CENTER, 0, 20, 0, 20);
    				}
    				Panels.addGuessPanels(grids1[i]);
    			}
    			
    			//create dictionary and set word with starting letter
    			Dictionary dict = null;
				try {
					dict = new Dictionary();
					dict.chooseWord(Keyboard.chooseLetter());
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			
    			//add keyboard
    			GridPane[] grids2 = new GridPane[3];
    			for(int i = 0; i < grids2.length; i++) {
    				grids2[i] = Panels.addGridPane(Pos.CENTER, 0, 0, 0, 0);
    				Panels.addKeyboardRow(grids2[i], i);
    			}
    			
    			//alert label
    			Label alert = new Label("LOL");
    			alert.setTranslateY(-230);
    			alert.setPadding(new Insets(15,50,15,50));
    			alert.setId("alert");
    			
    			//partition into 2 gridpanes
    			GridPane grid1 = Panels.mergeGridPanes(grids1, Pos.TOP_CENTER);
    			GridPane grid2 = Panels.mergeGridPanes(grids2, Pos.BOTTOM_CENTER);
    			
    			//add gridpanes to stackpanes
    			StackPane stack1 = Panels.addStackPane(grid1, grid2);
    			
    			//add display
    			Display d = new Display();
    			Keyboard.setFunctions(grids1, grids2, d, dict, alert, stack1);
    			
    			
    			stack1.getChildren().add(1, alert);
    			BackgroundFill background_fill = new BackgroundFill(Color.rgb(20, 20, 20), 
                        CornerRadii.EMPTY, Insets.EMPTY);

    			// create Background
    			Background background = new Background(background_fill);
    			stack1.setBackground(background);
    			stack1.setTranslateY(-stack.getHeight());
    			stack.getChildren().add(stack1);
    			
    			TranslateTransition translate1 = new TranslateTransition();  
    			translate1.setByY(stack.getHeight());  
    			translate1.setDuration(Duration.millis(500));
    			translate1.setNode(stack1);
    			translate1.play();
            }
        };
        play_again.setOnAction(event2);
        
		play_again.setTranslateX(29);
		g.setTranslateY(-stack.getHeight());
		stack.getChildren().add(g);
		
		
		//set transition
		TranslateTransition translate = new TranslateTransition();  
		translate.setByY(stack.getHeight());  
		translate.setDuration(Duration.millis(500));
		translate.setNode(g);
		translate.play();
	    translate.setOnFinished(event1 -> freeChildren(stack));
	}
	
	private void freeChildren(StackPane stack) {
		for(int i = 0; i < stack.getChildren().size() - 1; i++) {
			stack.getChildren().remove(i);
		}
	}
	
	//wiggles the row if not right
	private void wiggleRow(GridPane[] panels) {
	    TranslateTransition translate = new TranslateTransition();  
        translate.setByX(10);  
        translate.setDuration(Duration.millis(30));  
        translate.setCycleCount(2);  
        translate.setAutoReverse(true);   
        translate.setNode(panels[cur_row]);  
        
        TranslateTransition translate2 = new TranslateTransition();  
        translate2.setByX(-10);  
        translate2.setDuration(Duration.millis(30));  
        translate2.setCycleCount(2);  
        translate2.setAutoReverse(true);   
        translate2.setNode(panels[cur_row]);  
        SequentialTransition seqT = new SequentialTransition (translate, translate2); 
        seqT.setCycleCount(2);
	    seqT.play();
	    seqT.setOnFinished(event -> {animating = false;});
	}
	
	//flips the row revealing the letters
	private void flipRow(Label alert, GridPane[] panels, GridPane[] keys, int[] colors, Dictionary d, StackPane stack) {
		
		for(int i = 0; i < 5; i++) {
			Label l = (Label) panels[cur_row].getChildren().get(i);
			//Instantiating RotateTransition class   
	        RotateTransition rotate1 = new RotateTransition();  
	          
	        //Setting Axis of rotation   
	        rotate1.setAxis(Rotate.X_AXIS);  
	          
	        // setting the angle of rotation   
	        rotate1.setByAngle(-90);    
	        
	        rotate1.setDuration(Duration.millis(250));
	              
	        //setting Rectangle as the node onto which the   
	// transition will be applied  
	        rotate1.setNode(l);  
	        int color = colors[i];
	        rotate1.setOnFinished(event -> reveal(l, color));
	        
	        RotateTransition rotate2 = new RotateTransition();   
	        rotate2.setAxis(Rotate.X_AXIS);  
	        rotate2.setByAngle(90);    
	        rotate2.setDuration(Duration.millis(250));
	        rotate2.setNode(l);  
	          
	        //playing the transition    
	        SequentialTransition seqT = new SequentialTransition (rotate1, rotate2); 
	        seqT.setDelay(Duration.millis(i*400));
	        seqT.play();
	        if(i == 4) {
	        	seqT.setOnFinished(event -> up(alert, panels, keys, d, stack));
	        }
		}
	}
	
	//reveals the letter based on dictionary
	private void reveal(Label l, int color) {
		switch(color) {
		case 0:
			l.setStyle("-fx-border-color: #F71DB3; -fx-background-color: #F71DB3;");
			break;
		case 1:
			l.setStyle("-fx-border-color: #E7D21E; -fx-background-color: #E7D21E;");
			break;
		case 2:
			l.setStyle("-fx-background-color: #464646;");
			break;
		default:
			break;
		}
		
	}
	
	//checks to see if the word is a dictionary word when enter is called.
	//current word must exist for it to be called. returns an int array that
	//represents colors, 0 for pink, 1 for yellow, 2 for gray
	private int[] checkWord(Dictionary d) {
		String cur_word = d.getCurrentWord();
		if(cur_word == null) {
			return null;
		}
		int[] colors = new int[5];
		for(int i = 0; i < 5; i++) {
			if(cur_word.charAt(i) == screen[cur_row][i]) {
				int index = keyboard.indexOf(screen[cur_row][i]);
				letter_colors[index] = 3;
				colors[i] = 0;
			}else {
				//need to check none of the letters before is this letter
				int count = 0;
				for(int j = 0; j < 5; j++) {
					if(screen[cur_row][i] == cur_word.charAt(j)) {
						count++;
					}
					
				}
				for(int k = 0; k < 5; k++) {
					if(screen[cur_row][i] == screen[cur_row][k] && i != k) {
						if(k > i) {
							count = cur_word.charAt(k) == screen[cur_row][k] ? count - 1 : count;
						}else {
							count--;
						}
					}
				}
				if(count >= 1) {
					int index = keyboard.indexOf(screen[cur_row][i]);
					letter_colors[index] = (letter_colors[index] >= 0 && letter_colors[index] < 3) ? 2 : letter_colors[index];
					colors[i] = 1;
				}else {
					int index = keyboard.indexOf(screen[cur_row][i]);
					letter_colors[index] = (letter_colors[index] >= 0 && letter_colors[index] < 2) ? 1 : letter_colors[index];
					colors[i] = 2;
				}
				
			}
		}
		return colors;
	}
	
	//sets the position to the next row
	//also detects if the game is finished and send the alert.
	private void up(Label alert, GridPane[] panels, GridPane[] keys, Dictionary d, StackPane stack) {
		locked = true;
		String word = "" + screen[cur_row][0] + screen[cur_row][1] + screen[cur_row][2] + screen[cur_row][3] + screen[cur_row][4];
		if(word.equals(d.getCurrentWord())) {
			finish(alert, panels, d, stack);
			next();
		}else if (cur_row == 5) {
			showMessage(7, alert, d, stack);
			next();
		}else {
			next();
			full = false;
			animating = false;
			locked = false;
		}
		changeKeyboard(keys);
	}
	
	//change the letters of the keyboard after the word is guessed
	private void changeKeyboard(GridPane[] keys) {
		GridPane row1 = keys[0];
		GridPane row2 = keys[1];
		GridPane row3 = keys[2];
		Button b;
		for(int i = 0; i < 26; i++) {
			if(i < 10) {
				b = (Button) row1.getChildren().get(i);
			}else if(i < 19) {
				b = (Button) row2.getChildren().get(i-10);
			}else {
				b = (Button) row3.getChildren().get(i-18);
			}
			switch(letter_colors[i]) {
			case 0:
				break;
			case 1:
				KeyValue startKeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #808080;");
				KeyFrame startFrame = new KeyFrame(Duration.millis(50), startKeyValue);
				KeyValue mid1KeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #727272;");
				KeyFrame mid1Frame = new KeyFrame(Duration.millis(100), mid1KeyValue);
				KeyValue mid2KeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #5A5A5A;");
				KeyFrame mid2Frame = new KeyFrame(Duration.millis(150), mid2KeyValue);
				KeyValue endKeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #464646;");
				KeyFrame endFrame = new KeyFrame(Duration.millis(200), endKeyValue);
				Timeline timeline = new Timeline(startFrame, mid1Frame, mid2Frame, endFrame);
				if(b.getStyle() != "-fx-background-color: #464646;")
				timeline.play();
				break;
			case 2:
				startKeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #B1AE8D;");
				startFrame = new KeyFrame(Duration.millis(50), startKeyValue);
				mid1KeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #CDC884;");
				mid1Frame = new KeyFrame(Duration.millis(100), mid1KeyValue);
				mid2KeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #E0D869;");
				mid2Frame = new KeyFrame(Duration.millis(150), mid2KeyValue);
				endKeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #E7D21E;");
				endFrame = new KeyFrame(Duration.millis(200), endKeyValue);
				timeline = new Timeline(startFrame, endFrame);
				if(b.getStyle() != "-fx-background-color: #E7D21E;")
				timeline.play();
				break;
			case 3:
				startKeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #B199AA;");
				startFrame = new KeyFrame(Duration.millis(50), startKeyValue);
				mid1KeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #D08DBC;");
				mid1Frame = new KeyFrame(Duration.millis(100), mid1KeyValue);
				mid2KeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #E877C7;");
				mid2Frame = new KeyFrame(Duration.millis(150), mid2KeyValue);
				endKeyValue = 
	                    new KeyValue(b.styleProperty(), "-fx-background-color: #F71DB3;");
				endFrame = new KeyFrame(Duration.millis(200), endKeyValue);
				timeline = new Timeline(startFrame, endFrame);
				if(b.getStyle() != "-fx-background-color: #F71DB3;")
				timeline.play();
				break;
			default:
				break;
			}
			
		}
	}
	
	//sets the letters of the screen
	public void setLetter(char c, int row, int col) {
		if(row > 5 || row < 0 || col > 4 || col < 0) {
			return;
		}else if(full){
			return;
		}else{
			screen[row][col] = c;
		}
		next();
	}
	
	//returns if a row is fully filled
	public boolean rowFull() {
		return full;
	}
	
	public boolean outofBounds() {
		return cur_row > 5 || cur_col < 0 || cur_row < 0 || cur_col > 4;
	}
	
	//returns if a animation is being played
	public boolean animation() {
		return animating;
	}
}
