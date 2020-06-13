package rpnsim.application.simulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.util.Pair;
import rpnsim.application.rpn.Arrow;
import rpnsim.application.rpn.LabelItem;
import rpnsim.application.rpn.Place;
import rpnsim.application.rpn.RPN;
import rpnsim.application.rpn.Token;
import rpnsim.application.rpn.Transition;

public class RPNUtils {

	private RPN rpn;
	public RPNUtils( RPN rpn ) {
		this.rpn = rpn;
	}
	
	public Set<LabelItem> getBondsFrom( Set<LabelItem> labelItems ){
		Set<LabelItem> bonds = new HashSet<LabelItem>();
		
		for( LabelItem item : labelItems ) {
			if( item.isBond() && item.isPositive() ) {
				bonds.add( item );
			}
		}
		
		return bonds;
	}
	
	public Set<LabelItem> F(Transition transition, Place place){
		Set<LabelItem> set = new HashSet<LabelItem>();
		
		// Find arrow that connects to place
		for( Arrow arrow : transition.getArrows() ) {
			if( arrow.getDestination() == place ) {
				set.addAll( arrow.getSet() );
				break;
			}
		}
		
		return set;
	}
	
	public ArrayList< Set<LabelItem> > outLabels(Transition transition){
		ArrayList< Set<LabelItem> > arraySet = new ArrayList<Set<LabelItem>>();
		// Find arrow that connects to place
		for( Arrow arrow : transition.getArrows() ) {
			if( arrow.getSource() == transition ) 
				arraySet.add(  arrow.getSet() );
		}
		return arraySet;
	}	

	protected Set<LabelItem> expandSet( Set<LabelItem> set ){
		Set<LabelItem> expandedSet = new HashSet<LabelItem>();
		for( LabelItem item : set ) {
			
			if( item.isBond() && item.isPositive() ) {
				Pair<Token,Token> bond = item.getBond();
				LabelItem newItemA = new LabelItem(bond.getKey());
				LabelItem newItemB = new LabelItem(bond.getValue());				
				expandedSet.add(newItemA);
				expandedSet.add(newItemB);
			}
			expandedSet.add(item);
		}
		
		return expandedSet;
	}
	
	
	
	public Set<LabelItem> intersection( Set<LabelItem> setA, Set<LabelItem> setB ) {
		Set<LabelItem> setC = new HashSet<LabelItem>();
		
		Set<LabelItem> expandedSetA = expandSet(setA);
		Set<LabelItem> expandedSetB = expandSet(setB);
		
		for( LabelItem itemA : expandedSetA ) {
			for( LabelItem itemB : expandedSetB ) {
				
				if( itemA.equals(itemB) )
					setC.add(itemA);
				
			}
		}
		
		return setC;
	}
	
	public boolean subsetOf( Set<LabelItem> setA, Set<LabelItem> setB ) {
		
		for( LabelItem itemA : setA ) {
			boolean contains = false;
			
			for(LabelItem itemB : setB ) {
				if( itemA.equals(itemB) ) {
					contains = true;
					break;
				}
			}
			if(contains == false)
				return false;
		}
		return true;
	}
	
	public Set<Token> getTokensFrom( Set<LabelItem> labelItems ){
		Set<Token> tokens = new HashSet<Token>();
		
		for( LabelItem item : labelItems ) {
			
			if( item.isToken() && item.isPositive() ) {
				tokens.add( item.getToken() );
			}
			
			// If bond a-b, then it's assumed that we also have a,b tokens
			if( item.isBond() && item.isPositive() ) {
				Pair<Token,Token> bond = item.getBond();
				tokens.add( bond.getKey() );
				tokens.add(bond.getValue() );
			}
		}
		
		return tokens;
	}
	
	public boolean compare( Set<Token> setA, Set<Token> setB ) {
		return setB.containsAll( setA ) && setA.containsAll( setB );
	}
	
	public boolean compareLabelItems( Set<LabelItem> setA, Set<LabelItem> setB ) {
		return setB.containsAll( setA ) && setA.containsAll( setB );
	}
	
	
	public Set<LabelItem> pre(Transition transition){
		Set<LabelItem> set = new HashSet<LabelItem>();
		
		// Find incoming arrows
		ArrayList<Arrow> arrows = transition.getArrows();
		for(Arrow arrow : arrows ) {
			
			if( arrow.getDestination() == transition ) {
				set.addAll(  arrow.getSet() ); // Union
			}
		}
		return set;
	}
	
	
	public Set<LabelItem> post(Transition transition){
		Set<LabelItem> set = new HashSet<LabelItem>();
		
		// Find outcoming arrows
		ArrayList<Arrow> arrows = transition.getArrows();
		for(Arrow arrow : arrows ) {
			
			if( arrow.getSource() == transition ) {
				set.addAll(  arrow.getSet() ); // Union		
			}
		}
		return set;
	}
}
