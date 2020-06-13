package rpnsim.application.properties;

import java.util.HashSet;
import java.util.Set;

public class Graph {

	public Set<Node> nodes = new HashSet<Node>();
	public Node root;
	
	public Graph( Node root ) {
		this.root = root;
		nodes.add(root);
	}
	
}




