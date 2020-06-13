package rpnsim.application.model;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Place extends Node {

	
	public Place(RPN rpn, String name) {
		super(rpn,name);
	}

	private Set<Token> getTokenSet(){
		Marking marking = rpn.marking;
		
		Set<Token> tokenSet  = new HashSet<Token>();
		for( Map.Entry<Token,Place> entrySet : marking.tokenIntoPlace.entrySet()) {
			Place place = entrySet.getValue();
			Token token = entrySet.getKey();
			if( this == place ) 
				tokenSet.add(token);
		}
		
		return tokenSet;
	}
	
	public boolean containsToken(Token token) {
		Set<Token> tokenSet  = getTokenSet();
		return tokenSet.contains(token);
	}
	
	public Token[] getTokens(){

		Set<Token> tokenSet  = getTokenSet();
		Token[] tokens = new Token[tokenSet.size()];
		tokenSet.toArray(tokens);
		return tokens;
	}
	
	@Override
	public boolean setName(String newName) {
		
		String oldName = this.name;
		
		if(!super.setName(newName))
			return false;
		
		// Fix hashmap
		rpn.places.remove(oldName);
		rpn.places.put(newName, this);
		
		return true;
	}
	
	public void moveToken(Token token) {
		rpn.moveToken(this, token);
	}
	
}
