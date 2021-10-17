package base;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.*;

/**
 * @author Sam Hooper
 *
 */
public class Main extends Application{
	
	private static final double DEFAULT_WINDOW_WIDTH = 600, DEFAULT_WINDOW_HEIGHT = 400;
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		TypeBoard board = TypeBoard.INSTANCE;
		BorderPane root = new BorderPane(board.getRoot());
		root.setBottom(new BottomMenu());
		Scene scene = new Scene(root, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		primaryStage.setTitle("Type Visualizer");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
}
