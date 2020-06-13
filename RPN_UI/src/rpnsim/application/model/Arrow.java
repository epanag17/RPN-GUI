package rpnsim.application.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.util.Pair;

public class Arrow {

	Node source;
	Node destination;

	Set<LabelItem> items = new HashSet<LabelItem>();

	public Arrow(Node source, Node destination) {
		this.source = source;
		this.destination = destination;
	}
	
	public Node getSource() {
		return source;
	}
	
	public Node getDestination() {
		return destination;
	}
	
	public Set<LabelItem> getSet() {
		return items;
	}

	public void addToken(Token token) {
		// tokens.add(token);
		items.add(new LabelItem(token));
	}

	public void addNegativeToken(Token token) {
		// negTokens.add(token);
		items.add(new LabelItem(token, true));
	}

	public void addBond(Token A, Token B) {
		// bonds.add( new Pair<Token,Token>(A,B) );
		items.add(new LabelItem(A, B));
	}

	public void addBond(Pair<Token, Token> bond) {
		// bonds.add( new Pair<Token,Token>(A,B) );
		items.add(new LabelItem(bond.getKey(), bond.getValue()));
	}

	public void addNegativeBond(Token A, Token B) {
		// negBonds.add( new Pair<Token,Token>(A,B) );
		items.add(new LabelItem(A, B, true));
	}

	public void addNegativeBond(Pair<Token, Token> bond) {
		// bonds.add( new Pair<Token,Token>(A,B) );
		items.add(new LabelItem(bond.getKey(), bond.getValue(), true));
	}

	public ArrayList<Token> getTokens(boolean negative) {
		ArrayList<Token> tokens = new ArrayList<Token>();
		for (LabelItem item : items) {
			if (item.isToken() && (item.isNegative() == negative))
				tokens.add(item.getToken());
		}
		return tokens;
	}

	public ArrayList<Pair<Token, Token>> getBonds(boolean negative) {
		ArrayList<Pair<Token, Token>> bonds = new ArrayList<Pair<Token, Token>>();
		for (LabelItem item : items) {
			if (item.isBond() && (item.isNegative() == negative))
				bonds.add(item.getBond());
		}
		return bonds;
	}
}
