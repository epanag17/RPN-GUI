package rpnsim.application.rpn;

import javafx.util.Pair;

public class LabelItem {

	private boolean token;
	private boolean negative;
	private Token A;
	private Token B;
	
	public boolean isToken() {
		return token;
	}
	public boolean isBond() {
		return !token;
	}
	public boolean isNegative() {
		return negative;
	}
	public boolean isPositive() {
		return !negative;
	}
	
	public LabelItem(Token A ) {
		this.negative = false;
		this.token = true;
		this.A = A;
	}	
	public LabelItem(Token A, boolean negative) {
		this.negative = negative;
		this.token = true;
		this.A = A;
	}
	public LabelItem(Token A, Token B) {
		this.negative = false;
		this.token = false;
		this.A = A;
		this.B = B;
	}
	public LabelItem(Token A, Token B, boolean negative) {
		this.negative = negative;
		this.token = false;
		this.A = A;
		this.B = B;
	}
	
	public Token getToken() {
		return A;
	}
	
	public Pair<Token,Token> getBond(){
		return new Pair<Token,Token>(A,B);
	}
	
	public boolean hasToken( Token other ) {
		if( A == other )
			return true;
		if( B == other )
			return true;
		return false;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if( other instanceof LabelItem ) {
			LabelItem otherLabel = (LabelItem)other;
			if( otherLabel.token != this.token)
				return false;
			if( otherLabel.negative != this.negative )
				return false;
			
			if( otherLabel.token ) {
				return otherLabel.A == this.A;
			}
			else {
				if( otherLabel.A == this.A && otherLabel.B == this.B )
					return true;
				if( otherLabel.B == this.A && otherLabel.A == this.B )
					return true;
				return false;
			}
		}
		
		return false;
	}
}
