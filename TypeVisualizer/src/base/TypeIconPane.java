package base;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.layout.StackPane;

/**
 * @author Sam Hooper
 *
 */
public class TypeIconPane extends StackPane {
	
	public DoubleBinding centerXBinding() {
		return this.layoutXProperty().add(this.widthProperty().divide(2));
	}
	
	public DoubleBinding bottomYBinding() {
		return this.layoutYProperty().add(this.heightProperty());
	}
	
}
