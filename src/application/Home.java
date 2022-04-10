package application;

//imports , commented imports are unused
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;


public class Home extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//set icons and title
			primaryStage.getIcons().add(new Image("assets/logo.JPG"));
			primaryStage.setTitle("Pink Wordle");
			primaryStage.setMinHeight(200);
			primaryStage.setMinWidth(300);
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
			Dictionary dict = new Dictionary();
			dict.chooseWord(Keyboard.chooseLetter());
			
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
			StackPane stack = Panels.addStackPane(grid1, grid2);
			
			//add display
			Display d = new Display();
			Keyboard.setFunctions(grids1, grids2, d, dict, alert, stack);
			
			
			stack.getChildren().add(1, alert);
			BackgroundFill background_fill = new BackgroundFill(Color.rgb(20, 20, 20), 
                    CornerRadii.EMPTY, Insets.EMPTY);

			// create Background
			Background background = new Background(background_fill);
			stack.setBackground(background);
			Scene scene = new Scene(stack, 500, 650);
	        primaryStage.setScene(scene);
	        scene.getStylesheets().add
			 (getClass().getResource("../assets/themes.css").toExternalForm());
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
