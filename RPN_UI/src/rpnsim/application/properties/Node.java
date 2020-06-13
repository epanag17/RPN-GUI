package rpnsim.application.properties;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import rpnsim.application.model.Marking;
import rpnsim.application.model.Transition;
import rpnsim.application.simulator.ForwardExecution;
import rpnsim.application.simulator.ReverseExecution;

public class Node {

	public Marking marking;
	public Set<Edge> children = new HashSet<Edge>();
	public Edge fromParent;
	public int depth;
	
	public Node(Marking marking, int depth) {
		this.marking = marking;
		this.depth = depth;
	}

	@Override
	public int hashCode() {
		return marking.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return marking.equals(obj);
	}
	
	public String toString() {
		return marking.toString();
	}
	
}


