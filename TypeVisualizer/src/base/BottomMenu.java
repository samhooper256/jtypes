package base;

import fxutils.Borders;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * @author Sam Hooper
 *
 */
public class BottomMenu extends StackPane {
	
	private static final Border RED_BORDER = Borders.of(Color.RED);
	
	private final TextField field;
	private final Button addButton;
	
	public BottomMenu() {
		super();
		AnchorPane anchor = new AnchorPane();
		anchor.setPadding(new Insets(10));
		HBox hBox = new HBox();
		AnchorPane.setLeftAnchor(hBox, 0d);
		AnchorPane.setTopAnchor(hBox, 0d);
		field = new TextField();
		addButton = new Button("Add Type");
		addButton.setOnAction(actionEvent -> attemptToAddType());
		Button clearBoardButton = new Button("Clear Board");
		clearBoardButton.setOnAction(actionEvent -> clearBoard());
		AnchorPane.setRightAnchor(clearBoardButton, 0d);
		AnchorPane.setTopAnchor(clearBoardButton, 0d);
		hBox.getChildren().addAll(field, addButton);
		anchor.getChildren().addAll(hBox, clearBoardButton);
		getChildren().add(anchor);
	}
	
	private void clearBoard() {
		TypeBoard.INSTANCE.clear();
	}
	
	private void attemptToAddType() {
		String text = field.getText().strip();
		if(text.isEmpty()) {
			addButton.setBorder(null);
		}
		else {
			try {
				Class<?> clazz = Class.forName(text);
				TypeBoard.INSTANCE.addTypeIfAbsent(clazz);
				field.clear();
				addButton.setBorder(null);
			}
			catch (ClassNotFoundException e) {
				addButton.setBorder(RED_BORDER);
			}
		}
	}
	
}
