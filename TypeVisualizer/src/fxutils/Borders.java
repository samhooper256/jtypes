package fxutils;

import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

/**
 * @author Sam Hooper
 *
 */
public final class Borders {
	
	private Borders() {
		
	}
	
	public static Border of(Paint paint) {
		return of(paint, BorderStrokeStyle.SOLID);
	}
	
	public static Border of(Paint paint, BorderStrokeStyle style) {
		return of(paint, style, 1);
	}
	
	public static Border of(Paint paint, BorderStrokeStyle style, int thickness) {
		return new Border(new BorderStroke(paint, style, CornerRadii.EMPTY, new BorderWidths(thickness)));
	}
	
}
