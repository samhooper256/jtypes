package base;

import java.lang.reflect.Modifier;
import java.util.*;

import fxutils.Backgrounds;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;

/**
 * @author Sam Hooper
 *
 */
public class TypeIcon {
	
	public final ArrayList<TypeArrow> outgoingArrows, incomingArrows;
	
	private final TypeIconPane iconPane;
	private final Class<?> type;
	
	public TypeIcon(final Class<?> type) {
		super();
		outgoingArrows = new ArrayList<>();
		incomingArrows = new ArrayList<>();
		iconPane = new TypeIconPane();
		this.type = type;
		if(type.isInterface())
			iconPane.setBackground(Backgrounds.of(Color.AQUA));
		else if(type.isEnum())
			iconPane.setBackground(Backgrounds.of(Color.PINK));
		else if(Modifier.isAbstract(type.getModifiers()))
			iconPane.setBackground(Backgrounds.of(Color.BLUE));
		else
			iconPane.setBackground(Backgrounds.of(Color.LIGHTGREEN));
		iconPane.setPadding(new Insets(10));
		Label label = new Label(type.getSimpleName());
		iconPane.getChildren().add(label);
		iconPane.setOnDragDetected(mouseEvent -> {
			Dragboard db = iconPane.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
			content.putString("");
			db.setContent(content);
			mouseEvent.consume();
		});
		ContextMenu contextMenu = new ContextMenu();
        MenuItem addDirectSupertypes = new MenuItem("Add direct supertypes");
        addDirectSupertypes.setOnAction(ae -> TypeBoard.INSTANCE.addDirectSupertypesOfExistingType(type));
        MenuItem addDirectSubtypes = new MenuItem("Add direct subtypes");
        addDirectSubtypes.setOnAction(ae -> TypeBoard.INSTANCE.addDirectSubtypesOfExistingType(type));
        MenuItem hide = new MenuItem("Hide");
        hide.setOnAction(actionEvent -> TypeBoard.INSTANCE.removeIcon(this));
        contextMenu.getItems().addAll(addDirectSupertypes, addDirectSubtypes, hide);
        
        iconPane.setOnMouseClicked(me -> {
        	if(me.getButton() == MouseButton.SECONDARY)
        		contextMenu.show(iconPane, me.getScreenX(), me.getScreenY());
        });
	}
	
	public TypeIconPane getPane() {
		return iconPane;
	}
	
	/** Returns the {@link Class} this TypeIcon is representing */
	public Class<?> getIconType() {
		return type;
	}
	
	public TypeArrow createSupertypeArrowTo(TypeIcon subtypeIcon) {
		TypeArrow a = new TypeArrow(this, subtypeIcon);
		a.startXProperty().bind(iconPane.centerXBinding());
		a.startYProperty().bind(iconPane.bottomYBinding());
		a.endXProperty().bind(subtypeIcon.iconPane.centerXBinding());
		a.endYProperty().bind(subtypeIcon.iconPane.layoutYProperty());
		outgoingArrows.add(a);
		subtypeIcon.incomingArrows.add(a);
		return a;
	}
	
	/** @code icon} must be connected to this {@code TypeIcon} by an {@link #outgoingArrows outgoing arrow}.*/
	public TypeArrow getOutgoingArrowWithDestOf(TypeIcon icon) {
		for(TypeArrow arrow : outgoingArrows)
			if(arrow.getDest().equals(icon))
				return arrow;
		throw new IllegalArgumentException(icon + " is not a destination of one of this TypeIcon's outgoing arrows");
	}
	
	public boolean isDestOfArrowFrom(TypeIcon icon) {
		for(TypeArrow arrow : icon.outgoingArrows)
			if(arrow.getDest().equals(this))
				return true;
		return false;
	}
	
	/** Returns the {@link TypeArrow} outgoing from {@code icon} whose destination is this {@link TypeIcon}.
	 * Throws an {@link IllegalArgumentException} if {@code icon} does not have an outgoing arrow that connects to this
	 * {@code TypeIcon}. */
	public TypeArrow arrowFrom(TypeIcon icon) {
		for(TypeArrow arrow : icon.outgoingArrows)
			if(arrow.getDest().equals(this))
				return arrow;
		throw new IllegalArgumentException("there is no outgoing arrow from the given icon that connects to this "
					+ "TypeIcon");
	}
	
	public TypeIcon getSuperclassIconOrNull() {
		for(TypeArrow incomingArrow : incomingArrows)
			if(!incomingArrow.getSource().getIconType().isInterface())
				return incomingArrow.getSource();
		return null;
	}
	
	public Set<TypeIcon> getSupertypeIcons() {
		Set<TypeIcon> supertypeIcons = new HashSet<>();
		for(TypeArrow a : incomingArrows)
			supertypeIcons.add(a.getSource());
		return supertypeIcons;
	}
	
	public Set<TypeIcon> getSubtypeIcons() {
		Set<TypeIcon> subtypeIcons = new HashSet<>();
		for(TypeArrow a : outgoingArrows)
			subtypeIcons.add(a.getDest());
		return subtypeIcons;
	}
	
	public TypeArrow getSuperclassArrowOrNull() {
		for(TypeArrow incomingArrow : incomingArrows)
			if(!incomingArrow.getSource().getIconType().isInterface())
				return incomingArrow;
		return null;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null || getClass() != obj.getClass())
			return false;
		return Objects.equals(type, ((TypeIcon) obj).type);
	}
	
	@Override
	public String toString() {
		return String.format("TypeIcon[type=%s]", type);
	}	
	
}
