package application;

import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

//functions to add and create panels
public class Panels {
	
	//combines 2 gridpanes into a stackpane
	public static StackPane addStackPane(GridPane grid1, GridPane grid2) {
	    StackPane stack = new StackPane();
	     
	    stack.getChildren().addAll(grid1,grid2);

	    return stack;
	}
	
	
	//creates a horizontal grid with c columns.
	public static GridPane addGridPane(Pos p, int top, int right, int bottom, int left) {
		GridPane grid = new GridPane();
		grid.setAlignment(p);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(top, right, bottom, left));
		return grid;
	}
	
	//create a gridpane containing gridpane rows
	public static GridPane mergeGridPanes(GridPane[] grids, Pos p) {
		GridPane grid = new GridPane();
		grid.setAlignment(p);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		int length = grids.length;
		for(int i = 0; i < length; i++) {
		    grid.add(grids[i], 0, i);
		}
		return grid;
	}
	
	//add top panels in the grid
	public static void addGuessPanels(GridPane g) {
		final int WORDLE_LENGTH = 5;
		g.setPrefSize(250, 50);
			
		for (int i = 0; i < WORDLE_LENGTH; i++) {
			Label x = new Label("");
			x.setMinSize(50, 50);
			x.setStyle("-fx-background-color: #141414");
			g.add(x, i, 0);
		}
	}
		
	//add the keyboard row given a row
	public static void addKeyboardRow(GridPane g, int num) {
		Keyboard k = new Keyboard();
		char[] letters = k.getLetters();
		g.setPrefSize(450, 50);
		switch(num) {
			//create first row of keyboard
		case 0:
			for (int i = 0; i < 10; i++) {
				Button x = new Button(letters[i] + "");
				x.setMinSize(30, 50);
				x.setPrefSize(30, 50);
				g.add(x, i, 0);
			}
			break;
				
			//create second row of the keyboard
		case 1:
			for (int i = 0; i < 9; i++) {
				Button x = new Button(letters[i + 10] + "");
				x.setMinSize(30, 50);
				x.setPrefSize(30, 50);
				g.add(x, i, 0);
			}
			break;
				
			//create third row of the keyboard
		default:
			Button x = new Button("ENTER");
			x.setMinSize(60, 50);
			x.setPrefSize(60, 50);
			g.add(x, 0, 0);
			for (int i = 1; i < 8; i++) {
				Button y = new Button(letters[i + 18] + "");
				y.setMinSize(30, 50);
				y.setPrefSize(30, 50);
				g.add(y, i, 0);
			}
			Button z = new Button("BACK");
			z.setMinSize(60, 50);
			z.setPrefSize(60, 50);
			g.add(z, 8, 0);
			break;
		}
	}
	
}
