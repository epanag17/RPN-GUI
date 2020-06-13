package rpnsim.application.model;

import java.util.HashSet;
import java.util.Set;

public class Token implements Comparable {

	public String name;
	public RPN rpn;
	public final Place initial_place;

	public Token(RPN rpn, Place initialPlace, String name) {
		this.name = name;
		this.initial_place = initialPlace;
		this.rpn = rpn;
	}

	public Place getPlace() {
		Marking marking = rpn.marking;
		Place place = marking.tokenIntoPlace.get(this);
		return place;
	}

	/**
	 * Find connected tokens recursively (for tokens that are not directly connected)
	 * 
	 * @param connected
	 */
	protected void getConnected(Set<Token> connected) {

		if (connected.contains(this))
			return;

		connected.add(this);

		Marking marking = rpn.marking;
		Set<Token> connectedTokens = marking.tokenConnections.get(this);
		if( connectedTokens == null )
			return;
		
		for (Token connectedToken : connectedTokens)
			connectedToken.getConnected(connected);

	}

	public Token[] getConnected() {
		Set<Token> connected = new HashSet<Token>();
		this.getConnected(connected);
		
		Token[] tokenArray = new Token[connected.size()];
		connected.toArray(tokenArray);
		return tokenArray;
	}

	protected boolean isConnected(Set<Token> visited, Token other) {

		visited.add(this);

		Marking marking = rpn.marking;
		Set<Token> connectedTokens = marking.tokenConnections.get(this);

		if( connectedTokens == null )
			return false;
		
		// Termatiki periptwsi ena.
		// Afou mesa sta connected tokens yparxei ekeino poy psaxnoume
		if (connectedTokens.contains(other)) {
			return true;
		}

		for (Token connectedToken : connectedTokens) {
			// Kane anadromi an den episkeptikes to sygkekrimeno token
			if (!visited.contains(connectedToken))
				return connectedToken.isConnected(visited, other);
		}

		return false;
	}

	public boolean isConnected(Token other) {
		Set<Token> visited = new HashSet<Token>();
		return isConnected(visited, other);
	}

	public void createBond(Token B) {

		rpn.marking.setTokenConnection(this, B);

	}

	public void removeBond(Token B) {
		rpn.marking.unsetTokenConnection(this, B);
	}

	public String getName() {
		return name;
	}
	
	public boolean nameUnique(String newName) {
		// Check if name is unique
		if( rpn.tokens.containsKey(newName) ) {
			System.err.println("Name '"+newName+ "' is already taken by a token");
			return false;
		}
		return true;
	}
	
	
	public boolean setName(String name) {
		
		if(!nameUnique(name))
			return false;
		
		// Fix hashmap
		rpn.tokens.remove(this.name);
		rpn.tokens.put(name, this);
		
		this.name = name;
		
		return true;
	}
	
	public String toString() {
		return name;
	}

	public Set<Token> getConnectedSet() {
		
		Set<Token> connected = new HashSet<Token>();
		this.getConnected(connected);
		
		return connected;
	}

	@Override
	public int compareTo(Object obj) {
		if( !(obj instanceof Token) )
			return 1;
		Token other = (Token) obj;
		return name.compareTo(other.name);
	}

}
