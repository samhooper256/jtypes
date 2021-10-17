package base;

import java.util.*;

import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

/**
 * @author Sam Hooper
 *
 */
public enum TypeBoard {
	INSTANCE;
	
	private final StackPane root;
	private final Pane pane;
	private final Map<Class<?>, TypeIcon> iconMap;
	
	TypeBoard() {
		iconMap = new HashMap<>();
		root = new StackPane();
		pane = new Pane();
		root.getChildren().add(pane);
		root.setOnDragOver(dragEvent -> {
			if(dragEvent.getGestureSource() instanceof TypeIconPane) {
				TypeIconPane pane = (TypeIconPane) dragEvent.getGestureSource();
				dragEvent.acceptTransferModes(TransferMode.MOVE);
				double mouseX = dragEvent.getX(), mouseY = dragEvent.getY();
				pane.relocate(mouseX, mouseY);
			}
		});
		
	}
	
	public void addDirectSupertypesOfExistingType(Class<?> clazz) {
		TypeIcon clazzIcon = iconMap.get(clazz);
		Set<Class<?>> directSupertypes = TypeUtils.directSupertypesOf(clazz);
		Set<Class<?>> supertypesToAdd = new HashSet<>();
		for(Class<?> supertype : directSupertypes)
			if(!iconMap.containsKey(supertype))
				supertypesToAdd.add(supertype);
		addSupertypeIcons(clazzIcon, supertypesToAdd);
	}
	
	private void addSupertypeIcons(TypeIcon subtypeIcon, Set<Class<?>> supertypesToAdd) {
		final double width = root.getWidth();
		final double iconHeight = subtypeIcon.getPane().getHeight();
		double minLayoutY = subtypeIcon.getPane().getLayoutY() - 50 - iconHeight;
		final double subtypeX = subtypeIcon.getPane().getLayoutX();
		if(minLayoutY - iconHeight < 0)
			minLayoutY = 0;
		final int supertypeCount = supertypesToAdd.size();
		if((width - 100) / supertypeCount >= 100) {
			double totalXUsed = supertypeCount * 100;
			double x = Math.max(subtypeX - totalXUsed/2, 0);
			if(x + totalXUsed > width)
				x = width - totalXUsed;
			for(Class<?> supertypeClass : supertypesToAdd) {
				addIcon(new TypeIcon(supertypeClass), x, minLayoutY);
				x += 100;
			}
		}
		else {
			double x = 0, y = minLayoutY;
			int shift = 0;
			for(Class<?> supertypeClass : supertypesToAdd) {
				if(x > width) {
					x = 0;
					y -= iconHeight;
				}
				if(y < 0) {
					shift += 10;
					y = Math.max(minLayoutY - shift, 0);
				}
				addIcon(new TypeIcon(supertypeClass), x, y);
				x += 100;
			}
		}
		
	}
	
	/** Adds the direct subtypes of {@code clazz} to the board. <b>{@code clazz} must be on the board</b>.*/
	public void addDirectSubtypesOfExistingType(Class<?> clazz) {
		if(!iconMap.containsKey(clazz))
			throw new IllegalArgumentException(clazz + " is not on the board.");
		TypeIcon clazzIcon = iconMap.get(clazz);
		Set<Class<?>> directSubtypes = TypeUtils.directSubtypesOf(clazz);
		Set<Class<?>> subtypesToAdd = new HashSet<>();
		for(Class<?> subtypeOfClazz : directSubtypes)
			if(!iconMap.containsKey(subtypeOfClazz))
				subtypesToAdd.add(subtypeOfClazz);
		addSubtypeIcons(clazzIcon, subtypesToAdd);
	}
	
	private void addSubtypeIcons(TypeIcon supertypeIcon, Set<Class<?>> subtypesToAdd) {
		final double width = root.getWidth();
		final double height = root.getHeight();
		final double iconHeight = supertypeIcon.getPane().getHeight();
		double maxLayoutY = supertypeIcon.getPane().getLayoutY() + iconHeight + 50;
		final double supertypeX = supertypeIcon.getPane().getLayoutX();
		if(maxLayoutY + iconHeight > height)
			maxLayoutY = height - iconHeight;
		final int subtypeCount = subtypesToAdd.size();
		if((width - 100) / subtypeCount >= 100) {
			double totalXUsed = subtypeCount * 100;
			double x = Math.max(supertypeX - totalXUsed/2, 0);
			if(x + totalXUsed > width)
				x = width - totalXUsed;
			for(Class<?> subtypeClass : subtypesToAdd) {
				addIcon(new TypeIcon(subtypeClass), x, maxLayoutY);
				x += 100;
			}
		}
		else {
			double x = 0, y = maxLayoutY;
			int shift = 0;
			for(Class<?> subtypeClass : subtypesToAdd) {
				if(x > width) {
					x = 0;
					y += iconHeight;
				}
				if(y > height) {
					shift += 10;
					y = Math.min(maxLayoutY + shift, height - iconHeight);
				}
				addIcon(new TypeIcon(subtypeClass), x, y);
				x += 100;
			}
		}
	}
	
	/** If type represented by the given {@link TypeIcon} is already in this board, this method does nothing.
	 * This method updates {@link #iconMap} to contain the new icon. */
	public void addIcon(TypeIcon icon, double layoutX, double layoutY) {
		final Class<?> iconClass = icon.getIconType();
		if(iconMap.containsKey(iconClass)) throw new IllegalArgumentException("Board already contains " + icon);
		//First, hook up all of iconClass's direct supertypes, if they're present:
		Set<TypeIcon> supertypesToHook = new HashSet<>();
		outer1:
		for(final Map.Entry<Class<?>, TypeIcon> entry : iconMap.entrySet()) {
			final Class<?> clazz = entry.getKey();
			final TypeIcon clazzIcon = entry.getValue();
			if(clazz.isAssignableFrom(iconClass)) { //clazz :> iconClass
				//first, make sure there's not a TypeIcon already in supertypesToHook 
				//for a type T such that T != clazz and clazz :> T :> iconClass
				for(Iterator<TypeIcon> itr = supertypesToHook.iterator(); itr.hasNext();) {
					Class<?> T = itr.next().getIconType();
					if(T.isAssignableFrom(clazz)) // T :> clazz
						itr.remove();
					else if(clazz.isAssignableFrom(T) && T.isAssignableFrom(iconClass))
						continue outer1;
				}
				supertypesToHook.add(clazzIcon);
			}
		}
		//Hook the supertypes with arrows:
		for(TypeIcon supertypeToHook : supertypesToHook) {
			TypeArrow a = supertypeToHook.createSupertypeArrowTo(icon);
			pane.getChildren().add(a);
		}
		//Now, let's do subtypes:
		Set<TypeIcon> subtypesToHook = new HashSet<>();
		outer2:
		for(final Map.Entry<Class<?>, TypeIcon> entry : iconMap.entrySet()) {
			final Class<?> clazz = entry.getKey();
			final TypeIcon clazzIcon = entry.getValue();
			if(iconClass.isAssignableFrom(clazz)) {
				//There may be another type T on the board such that iconClass :> T :> clazz. In that case, we would not
				//want to make an arrow from iconClass to clazz.
				for(Iterator<TypeIcon> itr = subtypesToHook.iterator(); itr.hasNext();) {
					Class<?> T = itr.next().getIconType();
					if(clazz.isAssignableFrom(T))
						itr.remove();
					else if(iconClass.isAssignableFrom(T) && T.isAssignableFrom(clazz))
						continue outer2;
				}
				subtypesToHook.add(clazzIcon);
			}
		}
		//When we hook the subtypes, we also remove any extraneous arrows:
		for(TypeIcon subtypeToHook : subtypesToHook) {
			//iconClass is a class (not an interface)
			//For each type S that is represented by a TypeIcon that subtypeToHook currently has as its supertype,
			//we want to remove the arrow going to S if S :> iconClass. If S does not exist, do nothing.
			for(Iterator<TypeArrow> itr = subtypeToHook.incomingArrows.iterator(); itr.hasNext();) {
				TypeArrow supertypeArrow = itr.next();
				if(supertypeArrow.getSource().getIconType().isAssignableFrom(iconClass)) {
					removeArrowFromPane(supertypeArrow);
					itr.remove();
					if(!supertypeArrow.getSource().outgoingArrows.remove(supertypeArrow))
						throw new IllegalArgumentException("Invalid arrow detected. supertypeArrow=" + supertypeArrow);
				}
			}
			TypeArrow a = icon.createSupertypeArrowTo(subtypeToHook);
			pane.getChildren().add(a);
		}
		
		//Now just finish up:
		icon.getPane().setLayoutX(layoutX);
		icon.getPane().setLayoutY(layoutY);
		iconMap.put(iconClass, icon);
		pane.getChildren().add(icon.getPane());
	}
	
	public void addTypeIfAbsent(Class<?> clazz) {
		if(!iconMap.containsKey(clazz))
			addIcon(new TypeIcon(clazz), pane.getWidth() / 2, pane.getHeight() / 2);
	}
	
	/** <p>Removes the given icon from this board.
	 * Destroys all of the given {@code TypeIcon}'s arrows, and rebuilds new arrows connecting
	 * all of the {@link TypeIcon TypeIcons} that were previously connected to the given {@code TypeIcon} by an arrow
	 * so that the arrows still represent valid subtype/supertype relations, but none of them go
	 * through the given {@code TypeIcon}.</p>
	 * 
	 * <p>After this method call, the {@link TypeIcon#incomingArrows incoming arrows} and
	 * {@link TypeIcon#outgoingArrows outgoing arrows} of the given icon will be empty.</p>
	 */
	public void removeIcon(TypeIcon icon) {
		Class<?> iconClass = icon.getIconType();
		if(!iconMap.containsKey(iconClass)) throw new IllegalArgumentException(icon + " is not in this TypeBoard");
		//First, remove icon and all its arrows from the board:
		final Set<TypeIcon> subtypeIcons = icon.getSubtypeIcons();
		final Set<TypeIcon> supertypeIcons = icon.getSupertypeIcons();
		iconMap.remove(iconClass);
		((Pane) icon.getPane().getParent()).getChildren().remove(icon.getPane());
		removeAndSeverAllArrows(icon);
		//Now, we need to add new arrows to connect everything:
		for(TypeIcon supertypeIcon : supertypeIcons) {
			for(TypeIcon subtypeIcon : subtypeIcons) {
				if(!hasDirectedPathFrom(supertypeIcon, subtypeIcon)) {
					TypeArrow a = supertypeIcon.createSupertypeArrowTo(subtypeIcon);
					pane.getChildren().add(a);
				}
			}
		}
	}
	
	/** Removes from the pane and severs every arrow that is incoming or
	 * outgoing from the given icon. {@code icon} must not be {@code null} and must be in
	 * this {@link TypeBoard}.
	 */
	private void removeAndSeverAllArrows(TypeIcon icon) {
		removeAndSeverAllIncomingArrows(icon);
		removeAndSeverAllOutgoingArrows(icon);
	}

	private void removeAndSeverAllOutgoingArrows(TypeIcon icon) {
		for(Iterator<TypeArrow> itr = icon.outgoingArrows.iterator(); itr.hasNext();) {
			TypeArrow arrow = itr.next();
			removeArrowFromPane(arrow);
			boolean b = arrow.getDest().incomingArrows.remove(arrow);
			itr.remove();
			if(!b) throw new IllegalArgumentException("Invalid arrow detected. arrow=" + arrow);
		}
	}

	private void removeAndSeverAllIncomingArrows(TypeIcon icon) {
		for(Iterator<TypeArrow> itr = icon.incomingArrows.iterator(); itr.hasNext();) {
			TypeArrow arrow = itr.next();
			removeArrowFromPane(arrow);
			boolean b = arrow.getSource().outgoingArrows.remove(arrow);
			itr.remove();
			if(!b) throw new IllegalArgumentException("Invalid arrow detected. arrow=" + arrow);
		}
	}
	
	/**
	 *  <b>This method returns {@code false} if {@code source.equals(dest)}</b>
	 * @throws IllegalArgumentException if the board does not contain either of the
	 * given {@link TypeIcon TypeIcons}.
	 */
	public boolean hasDirectedPathFrom(TypeIcon source, TypeIcon dest) {
		for(TypeArrow sourceToSubtypeArrow : source.outgoingArrows) {
			TypeIcon subtypeIcon = sourceToSubtypeArrow.getDest();
			if(subtypeIcon.equals(dest))
				return true;
			else if(hasDirectedPathFrom(subtypeIcon, dest))
				return true;
		}
		return false;
	}
	
	/** <b>{@code arrow} must be on the board</b>. This method takes care of removing {@code arrow} from the
	 * {@link TypeIcon#outgoingArrows outgoing arrows} and {@link TypeIcon#incomingArrows incoming arrows} lists of the
	 * {@link TypeIcon TypeIcons} it connects.*/
	public void removeArrowAndSever(TypeArrow arrow) {
		removeArrowFromPane(arrow);
		arrow.sever();
	}
	
	public void removeArrowFromPane(TypeArrow arrow) {
		boolean arrowOnBoard = ((Pane) arrow.getParent()).getChildren().remove(arrow);
		if(!arrowOnBoard)
			throw new IllegalArgumentException("arrow is not on the board");
	}
	
	public StackPane getRoot() {
		return root;
	}

	public void clear() {
		for(Map.Entry<Class<?>, TypeIcon> entry : iconMap.entrySet()) {
			TypeIcon icon = entry.getValue();
			for(TypeArrow outgoingArrow : icon.outgoingArrows)
				removeArrowFromPane(outgoingArrow);
			((Pane) icon.getPane().getParent()).getChildren().remove(icon.getPane());
		}
		iconMap.clear();
	}
	
}