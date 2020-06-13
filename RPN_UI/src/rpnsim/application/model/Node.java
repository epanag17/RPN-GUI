package rpnsim.application.model;

import java.util.Map;
import java.util.Set;

public class Node {

	
	public String name;

	protected RPN rpn;
	
	public Node(RPN rpn, String name) {
		this.name = name;
		this.rpn = rpn;
	}
	
	public Arrow[] getArrows() {
		Set<Arrow> arrowSet = rpn.nodeArrows.get(this);
		if( arrowSet == null ) 
			return new Arrow[0];
		
		Arrow[] arrows = new Arrow[arrowSet.size()];
		arrowSet.toArray(arrows);
		return arrows;
	}
	
	public void disconnectArrows() {
		rpn.nodeArrows.remove(this);
		for (Map.Entry<Node, Set<Arrow>> entrySet : rpn.nodeArrows.entrySet()) {

			Set<Arrow> arrowSet = entrySet.getValue();
			Arrow[] arrowArray = new Arrow[arrowSet.size()];
			arrowSet.toArray(arrowArray);
			for (Arrow arrow : arrowArray) {
				if (arrow.destination == this)
					arrowSet.remove(this);
			}
			if (arrowSet.isEmpty())
				rpn.nodeArrows.remove(entrySet.getKey());
		}
	}
	
	
	public String getName() {
		return name;
	}
	
	
	
	public boolean nameUnique(String newName) {
		// Check if name is unique
		if( rpn.places.containsKey(newName) ) {
			System.err.println("Name '"+newName+ "' is already taken by a place");
			return false;
		}
		if( rpn.transitions.containsKey(newName) ) {
			System.err.println("Name '"+newName+ "' is already taken by a transition");
			return false;
		}
		return true;
	}
	
	/**
	 * Changes the name of a place.
	 * But only if name is not take by another place.
	 * 
	 * @param newName
	 * @return true if new name is unique
	 */
	public boolean setName(String newName) {
		
		if( !nameUnique(newName) )
			return false;
		
		// Change name
		this.name = newName;
		
		return true;
	}
	
	
	public String toString() {
		return name;
	}
}
