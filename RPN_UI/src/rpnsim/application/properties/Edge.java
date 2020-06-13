package rpnsim.application.properties;

import rpnsim.application.model.Transition;
import rpnsim.application.simulator.Execution;

public class Edge {

	public Node source;
	public Node destination;
	public Transition t;
	public Execution ex;
	
	public Edge(Node source, Node destination, 
			Transition t, Execution ex) {
		super();
		this.source = source;
		this.destination = destination;
		this.t = t;
		this.ex = ex;
	}
	
	
}





