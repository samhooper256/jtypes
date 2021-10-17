package fxutils;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

/**
 * @author Sam Hooper
 *
 */
public final class Backgrounds {
	
	private Backgrounds() {
		
	}
	
	public static Background of(Paint paint) {
		return new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY));
	}
	
}
