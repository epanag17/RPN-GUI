package rpnsim.application.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import javafx.util.Pair;
import rpnsim.application.model.Marking;
import rpnsim.application.model.RPN;
import rpnsim.application.model.Transition;
import rpnsim.application.simulator.Execution;
import rpnsim.application.simulator.ForwardExecution;
import rpnsim.application.simulator.ReverseExecution;

public class Search {

	protected RPN rpn;
	protected ForwardExecution forward;
	protected ReverseExecution reverse;
	
	
	public Search(RPN rpn, ForwardExecution forward, ReverseExecution reverse) {
		super();
		this.rpn = rpn;
		this.forward = forward;
		this.reverse = reverse;
	}


	Set<Node> getNeighbors(Node current) {
		
		rpn.setMarking(current.marking);
		
		Set<Transition> forwardTransitions = forward.getEnabledTransitions();
		Set<Transition> reverseTransitions = new HashSet<>();
		if(reverse!= null)
		reverseTransitions = reverse.getEnabledTransitions();
		Set<Node> neighbors = new HashSet<Node>(); 
		
		for( Transition transition : forwardTransitions ) {
			
			Marking newMarking = new Marking(current.marking);
			rpn.setMarking(newMarking);
			forward.fireTransition(transition, current.depth);
			
			Node neighbor = new Node(newMarking, current.depth + 1 );
			Edge edge = new Edge(current,neighbor,transition, forward);
			
			current.children.add(edge);
			neighbor.fromParent = edge;
			
			neighbors.add(neighbor);
		}
		for( Transition transition : reverseTransitions ) {
			Marking newMarking = new Marking(current.marking);
			rpn.setMarking(newMarking);
			reverse.fireTransition(transition);
			
			Node neighbor = new Node(newMarking, current.depth -1 );
			Edge edge = new Edge(current,neighbor,transition, reverse);
			
			current.children.add(edge);
			neighbor.fromParent = edge;
			
			neighbors.add(neighbor);
		}
		
		return neighbors;
	}
	
	
	protected ArrayList<Edge> getReversePath( Graph graph, Node endNode ){
		ArrayList<Edge> path = new ArrayList<Edge>();
		Node node = endNode;
		while( node != graph.root ) {
			path.add(node.fromParent);
			node = node.fromParent.source;
		}
		return path;
	}
	
	
	public ArrayList<Edge> search(Marking targetMarking) {
		

		//rpn marking
		Marking startMarking = rpn.getMarking();
		Marking rpnMarking = new Marking(startMarking);
		
		int history = 1;
		
		
		Node rootNode = new Node(rpnMarking,1);
		

		Queue< Node > queue = new LinkedList< Node >();
		Set<Marking> visited = new HashSet<Marking>();
		
		// Add initial state
		queue.add(rootNode);
		Graph graph = new Graph(rootNode);
		
		int counter = 0;
		
		// BFS algorithm
		while( !queue.isEmpty() ) {
		
			// Get next
			Node node = queue.poll();		
			
			// Check marking
			if( visited.contains(node.marking) )
				continue;
			
			
			System.out.println("iteration: "+counter);
			System.out.println(".."+node.marking);
			counter++;
			
			// Go to marking
			rpn.setMarking(node.marking);
			
			
			if(targetMarking.subsetOf(node.marking))
			{
				System.out.println("Found path!");
				ArrayList<Edge> path = getReversePath( graph, node );
				Collections.reverse(path);
				rpn.setMarking(startMarking);
				return path;
			}
			
			// Piase tous geitones
			Set<Node> neighbors = getNeighbors(node);
			for( Node neighbor : neighbors ) {
				queue.add(neighbor);
			}
			
			// Episkef8ikame ton kombo
			visited.add(node.marking);
			
			

		}
		
		
		rpn.setMarking(startMarking);
		System.out.println("No path found");
		return null;
	}
	
	
	
}
