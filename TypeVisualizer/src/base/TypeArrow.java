package base;

import java.util.Objects;

/**
 * @author Sam Hooper
 *
 */
public class TypeArrow extends Arrow {
	
	private final TypeIcon source, dest;
	
	public TypeArrow(TypeIcon source, TypeIcon dest) {
		super();
		Objects.requireNonNull(source);
		Objects.requireNonNull(dest);
		this.source = source;
		this.dest = dest;
	}
	
	/** Removes this {@code TypeArrow} from the outgoingArrows list of {@code source} and the incomingArrows
	 * list of {@code dest}. Both lists <b>must</b> contain this arrow. Use {@link #severRemaining()} if one or
	 * both lists may not contain the arrow.
	 */
	public void sever() {
		boolean removedProperly = source.outgoingArrows.remove(this);
		removedProperly &= dest.incomingArrows.remove(this);
		if(!removedProperly)
			throw new IllegalArgumentException(String.format(
					"This arrow is not properly connected. Either the source or dest does not have this arrow as an "
					+ "incoming/outgoing arrow. source=%s, dest=%s", source, dest));
	}
	
	void severRemaining() {
		source.outgoingArrows.remove(this);
		dest.incomingArrows.remove(this);
	}
	
	public TypeIcon getSource() {
		return source;
	}
	public TypeIcon getDest() {
		return dest;
	}
	
	@Override
	public String toString() {
		return String.format("TypeArrow[src=%s, dest=%s]",
				source.getIconType().getSimpleName(), dest.getIconType().getSimpleName());
	}
	
}
