package rpnsim.application.simulator;

import java.util.ArrayList;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Pair;
import rpnsim.application.rpn.Arrow;
import rpnsim.application.rpn.LabelItem;
import rpnsim.application.rpn.Place;
import rpnsim.application.rpn.RPN;
import rpnsim.application.rpn.Token;
import rpnsim.application.rpn.Transition;

public class RPNVerifier {
	
	static RPN rpn;
	
	public RPNVerifier(RPN rpn) {
		this.rpn = rpn;
	}
	
	public static void setRPN(RPN rpn) {
		RPNVerifier.rpn = rpn;
	}
	
	public static EventHandler<ActionEvent> clicked = new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(final ActionEvent event) {
	    	if(RPNVerifier.verify())
	    		System.out.println("the rpn is valid!!!");
	    	else
	    		System.out.println("the rpn isn't valid...");
	    	
	    	event.consume();
	    }
	};
	
	private ArrayList<Token> initTokenList(ArrayList<Arrow> list, boolean isNegative){
		ArrayList<Token> temp = new ArrayList<Token>();
		if(list.isEmpty()) 
			return temp;
		if(isNegative) {
			temp.addAll(list.get(0).getTokens(true));
			return temp;
		}
		temp.addAll(list.get(0).getTokens(false));
		return temp;
	}
	
	public static boolean verify() {
		
		ArrayList<Transition> transitions = rpn.getTransitions();
		RPNUtils u = new RPNUtils( RPNVerifier.rpn );
		
		for( Transition transition : transitions ) {
			Set<LabelItem> pret = u.pre(transition);
			Set<LabelItem> postt = u.post(transition);
			
			
			// Definition 2.1
			{
				Set<Token> tokensLeft = u.getTokensFrom(pret);
				Set<Token> tokensRight = u.getTokensFrom(postt);
				if( !u.compare(tokensLeft, tokensRight) ) {
					System.out.println("Definition 2. failed");
					return false;
				}
			}
			
			// Definition 2.2
			{
				Set<LabelItem> leftBonds = u.getBondsFrom(pret);
				if( !u.subsetOf(leftBonds, postt) ) {
					System.out.println("Definition 2.2 failed");
					return false;
				}
			}
			
			// Definition 2.3
			{
				ArrayList<Place> places = rpn.getPlaces();
				ArrayList<Set<LabelItem>> outLabels = u.outLabels(transition);
				for( int i=0; i<outLabels.size(); i++ ) {
					for( int j=i+1; j<outLabels.size(); j++ ) {
						
						Set<LabelItem> setA = outLabels.get(i);
						Set<LabelItem> setB = outLabels.get(j);
						
						Set<LabelItem> intersection = u.intersection(setA, setB);
						
						if(!intersection.isEmpty()) {
							System.out.println("Definition 2.3 failed");
							return false;
						}
						
					}
				}
			}
		}
		
		
		
		
		boolean v = true;
		if(v==true)
			return true;
		
		
		
		for(Transition transition:transitions) {
			ArrayList<Arrow> arrows = transition.getArrows();
			
			ArrayList<Arrow> incoming_arrows = new ArrayList<>();
			ArrayList<Arrow> outcoming_arrows = new ArrayList<>();
			for(Arrow arrow:arrows) {
				if(arrow.getDestination().equals(transition)) {
					incoming_arrows.add(arrow);
				}
				if(arrow.getSource().equals(transition)) {
					outcoming_arrows.add(arrow);
				}
			}
			
			/*
			//elegxw an otidipote pou eiserxetai e3erxetai
			if(!enterAndExitElement(incoming_arrows,outcoming_arrows,false)) {
				System.err.println("Failed verification: Rule1");
				return false;
			}
			
			//blepw kata poso ekeina pou e3erxontai, ypirxan kai apo prin
			if(!enterAndExitElement(outcoming_arrows,incoming_arrows,true)) {
				System.err.println("Failed verification: Rule2");
				return false;
			}
			*/
			
			//elegxos an eiserxontai ta idia tokens/bonds 2 h perissoteres fores
			/*if(hasDuplicateElements(incoming_arrows)){
				System.err.println("Failed verification: Rule2");
				return false;
			}*/
			
			/*
			//elegxos an e3erxontai ta idia tokens/bonds 2 h perissoteres fores
			if(hasDuplicateElements(outcoming_arrows)){
				System.err.println("Failed verification: Rule3");
				return false;
			}
			*/
			
		}
		
		
		return true;
	}
	
	
	private boolean hasDuplicateTokens(ArrayList<Token> iteratedList, ArrayList<Token> list) {
		for(Token token:iteratedList) {
			if(list.contains(token))
				return true;
		}
		return false;
	}
	
	private boolean hasDuplicateBonds(ArrayList<Pair<Token,Token>> listA, ArrayList<Token> listB) {
		for(Pair<Token,Token> bondA:listA) {
			Token tokenA = bondA.getKey();
			Token tokenB = bondA.getValue();
			System.out.println("come on..");
			if(listB.contains(tokenA) && listB.contains(tokenB)){
				
				return true;
					
			}
			else {
				
				listB.add(tokenA);
				listB.add(tokenB);
			}
				
			
			
			
		}
		return false;
	}
	
	private boolean hasDuplicateElements(ArrayList<Arrow> arrows) {
		
		ArrayList<Token> tokens= initTokenList(arrows, false);
		ArrayList<Token> negTokens= initTokenList(arrows, true);
		//ArrayList<Pair<Token, Token>> bonds = initBondList(arrows, false);
		//ArrayList<Pair<Token, Token>> negBonds = initBondList(arrows, true);
		
		for(int i = 1; i<arrows.size();i++) {
			ArrayList<Token> current_arrow_tokens = arrows.get(i).getTokens(false);
			if(hasDuplicateTokens(current_arrow_tokens, tokens))
				return true;
			else
				tokens.addAll(arrows.get(i).getTokens(false));
			
			ArrayList<Token> current_arrow_negTokens = arrows.get(i).getTokens(true);
			if(hasDuplicateTokens(current_arrow_negTokens, negTokens))
				return true;
			else
				negTokens.addAll(arrows.get(i).getTokens(true));
			
			ArrayList<Pair<Token,Token>> current_arrow_bonds = arrows.get(i).getBonds(false);
			if(hasDuplicateBonds(current_arrow_bonds, tokens))
				return true;
			else {
				//bonds.addAll(arrows.get(i).getBonds());
			}
				
			
			ArrayList<Pair<Token,Token>> current_arrow_negBonds = arrows.get(i).getBonds(true);
			if(hasDuplicateBonds(current_arrow_negBonds, negTokens))
				return true;
			else {
				//negBonds.addAll(arrows.get(i).getNegBonds());
			}
				
			
		}
		
		return false;
	}
	
	
	private boolean enterAndExitElement(ArrayList<Arrow> incoming_arrows,ArrayList<Arrow> outcoming_arrows, boolean reverseChecking) {
		//boolean isValid = true;
		for(Arrow incoming_arrow:incoming_arrows) {
			
			ArrayList<Token> incoming_tokens = incoming_arrow.getTokens(false);
			/*for(Token incoming_token:incoming_tokens) {
				for(Arrow outcoming_arrow:outcoming_arrows) {
					ArrayList<Token> outcoming_tokens = outcoming_arrow.getTokens();
					if(outcoming_tokens.contains(incoming_token))
						isValid = true;
				}
			}*/
			if( !enterAndExitToken(incoming_tokens, outcoming_arrows, false, reverseChecking))
				return false;
			
			ArrayList<Token> incoming_negTokens = incoming_arrow.getTokens(true);
			if(!enterAndExitToken(incoming_negTokens, outcoming_arrows, true, reverseChecking))
				return false;
			
			ArrayList<Pair<Token,Token>> incoming_bonds = incoming_arrow.getBonds(false);
			if(!enterAndExitBond(incoming_bonds, outcoming_arrows, false, incoming_arrows, reverseChecking))
				return false;
			
			ArrayList<Pair<Token,Token>> incoming_negBonds = incoming_arrow.getBonds(true);
			if(!enterAndExitBond(incoming_negBonds, outcoming_arrows, true, incoming_arrows, reverseChecking))
				return false;
			
		}
		
		return true;
	}
	
	private boolean enterAndExitToken(ArrayList<Token> incoming_tokens, ArrayList<Arrow> outcoming_arrows, boolean isNegative, boolean reverseChecking) {
		if(incoming_tokens.isEmpty())
			return true;
		
		for(Token incoming_token:incoming_tokens) {
			boolean isExist = false;
			for(Arrow outcoming_arrow:outcoming_arrows) {
				ArrayList<Token> outcoming_tokens = null;
				ArrayList<Pair<Token,Token>> outcoming_bonds = null;
				if(isNegative) {
					outcoming_tokens= outcoming_arrow.getTokens(true);
					outcoming_bonds = outcoming_arrow.getBonds(true);
				}	
				else {
					outcoming_tokens= outcoming_arrow.getTokens(false);
					outcoming_bonds = outcoming_arrow.getBonds(false);
				}
				
				if(outcoming_tokens.contains(incoming_token)) {
					isExist = true;
					break;
				}
				else {
					if(!reverseChecking) {
						for(Pair<Token,Token> bond:outcoming_bonds) {
							Token tokenA = bond.getKey();
							Token tokenB = bond.getValue();
							if((tokenA == incoming_token) || (tokenB == incoming_token)) {
								isExist = true;
								break;
							}
						}
					}
					
				}
					
			}
			if(!isExist)
				return false;
		}
		return true;
	}
	
	private boolean enterAndExitBond(ArrayList<Pair<Token,Token>> incoming_bonds, ArrayList<Arrow> outcoming_arrows, boolean isNegative,ArrayList<Arrow> incoming_arrows, boolean reverseChecking) {
		if(incoming_bonds.isEmpty())
			return true;
		
		for(Pair<Token,Token> incoming_bond:incoming_bonds) {
			Token tokenA = incoming_bond.getKey();
			Token tokenB = incoming_bond.getValue();	
			boolean isExist = false;
				
			for(Arrow outcoming_arrow:outcoming_arrows) {
				ArrayList<Pair<Token,Token>> outcoming_bonds = null;
				if(isNegative)
					outcoming_bonds = outcoming_arrow.getBonds(true);
				else
					outcoming_bonds = outcoming_arrow.getBonds(false);
				
				if(!reverseChecking) {
					for(Pair<Token,Token> outcoming_bond:outcoming_bonds) {
						if(tokenA == outcoming_bond.getKey() || tokenA == outcoming_bond.getValue()) {
							if(tokenB == outcoming_bond.getKey() || tokenB == outcoming_bond.getValue()) {
								isExist = true;
								break;
							}
						}
					}
				}
				else {
					//first case: ypirxe desmos sta incoming arrows
					for(Pair<Token,Token> outcoming_bond:outcoming_bonds) {
						if(tokenA == outcoming_bond.getKey() || tokenA == outcoming_bond.getValue()) {
							if(tokenB == outcoming_bond.getKey() || tokenB == outcoming_bond.getValue()) {
								isExist = true;
								break;
							}
						}
					}
					
					//second case: den ypirxe desmos sta incoming arrows
					if(!isExist) {
						ArrayList<Token> temp = new ArrayList<>();
						temp.add(tokenA);
						temp.add(tokenB);
						if(enterAndExitToken(temp, incoming_arrows, isNegative, false)) {
							isExist = true;
							break;
						}
					}
				}
				
					
				
			}
				
			if(!isExist)
				return false;
				
		}
		
		return true;
	}

}
