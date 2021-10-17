package base;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.scene.*;
import javafx.scene.shape.*;

/**
 * This class is taken from stackoverflow.com/a/41353991/11788023
 * @author jewelsea (stackoverflow.com/users/1155209/jewelsea)
 *
 */
public class Arrow extends Group {

    private final Line line;

    public Arrow() {
        this(new Line(), new Line(), new Line());
    }

    private static final double arrowLength = 20;
    private static final double arrowWidth = 7;

    private Arrow(Line line, Line arrow1, Line arrow2) {
        super(line, arrow1, arrow2);
        this.line = line;
        InvalidationListener updater = o -> {
        	double startX = getStartX(), startY = getStartY();
            double endX = getEndX(), endY = getEndY();

            arrow1.setEndX(endX);
            arrow1.setEndY(endY);
            arrow2.setEndX(endX);
            arrow2.setEndY(endY);

            if (endX == startX && endY == startY) {
                // arrow parts of length 0
                arrow1.setStartX(endX);
                arrow1.setStartY(endY);
                arrow2.setStartX(endX);
                arrow2.setStartY(endY);
            } else {
                double factor = arrowLength / Math.hypot(startX-endX, startY-endY);
                double factorO = arrowWidth / Math.hypot(startX-endX, startY-endY);

                // part in direction of main line
                double dx = (startX - endX) * factor;
                double dy = (startY - endY) * factor;

                // part ortogonal to main line
                double ox = (startX - endX) * factorO;
                double oy = (startY - endY) * factorO;

                arrow1.setStartX(endX + dx - oy);
                arrow1.setStartY(endY + dy + ox);
                arrow2.setStartX(endX + dx + oy);
                arrow2.setStartY(endY + dy - ox);
            }
        };

        // add updater to properties
        startXProperty().addListener(updater);
        startYProperty().addListener(updater);
        endXProperty().addListener(updater);
        endYProperty().addListener(updater);
        updater.invalidated(null);
    }

    // start/end properties

    public final void setStartX(double value) {
        line.setStartX(value);
    }

    public final double getStartX() {
        return line.getStartX();
    }

    public final DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public final void setStartY(double value) {
        line.setStartY(value);
    }

    public final double getStartY() {
        return line.getStartY();
    }

    public final DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    public final double getEndX() {
        return line.getEndX();
    }

    public final DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    public final void setEndY(double value) {
        line.setEndY(value);
    }

    public final double getEndY() {
        return line.getEndY();
    }

    public final DoubleProperty endYProperty() {
        return line.endYProperty();
    }

}
