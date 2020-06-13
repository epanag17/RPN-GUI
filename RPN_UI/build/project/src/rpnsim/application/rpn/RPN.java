package rpnsim.application.rpn;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import rpnsim.application.ScrollArea;
import rpnsim.application.editor.EditorArea;

public class RPN {

	ScrollArea scrollArea;
	
	ArrayList<Place> places = new ArrayList<>();
	ArrayList<Transition> transitions = new ArrayList<>();
	ArrayList<Token> tokens = new ArrayList<>();
	ArrayList<Bond> bonds = new ArrayList<>();
	ArrayList<Arrow> arrows = new ArrayList<>();

	
	public RPN( ScrollArea scrollArea ) {
		this.scrollArea = scrollArea;
	}
	
	public void setScrollArea( ScrollArea scrollArea ) {
		this.scrollArea = scrollArea;
	}
	
	public ScrollArea getScrollArea() {
		return scrollArea;
	}
	
	public ArrayList<Arrow> getArrows(){
		return arrows;
	}
	public Bond[] getBonds(){
		Bond[] bondArray = new Bond[bonds.size()];
		bonds.toArray(bondArray);
		return bondArray;
	}
	public ArrayList<Token> getTokens(){
		return tokens;
	}
	public ArrayList<Place> getPlaces() {
		return places;
	}
	public ArrayList<Transition> getTransitions() {
		return transitions;
	}
	
	
	public void delete( SelectableNode node ) {
		if(node instanceof Place)
			places.remove(node);
		else if(node instanceof Transition)
			transitions.remove(node);
		else if(node instanceof Token)
			tokens.remove(node);
		else if(node instanceof Bond)
			bonds.remove(node);
		else if(node instanceof Arrow)
			arrows.remove(node);
	}
	
	
	public Place addPlace() {
		Place place = new Place(this);
		places.add(place);
		
		return place;
	}
	
	public Transition addTransition() {
		Transition transition = new Transition(this);
		transitions.add(transition);
		return transition;
	}
	
	public void addToken(Token token) {
		tokens.add(token);
	}
	
	
	public void addPlaceNode(String name, Point2D position, ArrayList<String> tokens) {
		Place place = new Place(this,name, position, tokens);
		places.add(place);
	}
	
	public void addTransitionNode(String name, Point2D position) {
		Transition transition = new Transition(this, name, position);
		transitions.add(transition);
	}
	
	public Bond addBond(Token source, Token destination) {
		Bond bond = new Bond(this);
		
		bond.setSource(source);
		bond.setDestination(destination);
		
		source.bonds.add(bond);
		destination.bonds.add(bond);
		
		bond.update();
		bonds.add(bond);
		
		return bond;
	}
	
	public Arrow addArrow(String source, String destination) {
		Arrow arrow = new Arrow(this);
		for(Place place: places) {
			if(place.name.equals(source)) {
				arrow.setSource(place);
				place.arrows.add(arrow);
				System.out.println("Found place: "+place);
				break;
			}
			if(place.name.equals(destination)) {
				arrow.setDestination(place);
				place.arrows.add(arrow);
				break;
			}
		}
		for(Transition transition: transitions) {
			if(transition.name.equals(source)) {
				arrow.setSource(transition);
				transition.arrows.add(arrow);
				break;
			}
			if(transition.name.equals(destination)) {
				arrow.setDestination(transition);
				transition.arrows.add(arrow);
				break;
			}
		}
		
		arrows.add(arrow);
		return arrow;
	}
	
//	public void deletePlace(Place place) {
//		//delete connected arrows
//		for(Arrow arrow:place.arrows) {
//			EditorArea.singleton.deleteNode(arrow);
//		}
//		place.arrows.clear();
//		
//		places.remove(place);
//		place.delete();
//		System.out.println("place successfully deleted!");
//	}
//	public void deleteToken(Token token) {
//		tokens.remove(token);
//		token.delete();
//	}
//	public void deleteBond(Bond bond) {
//		bonds.remove(bond);
//		System.out.println("bond successfully deleted!");
//	}
//	public void deleteArrow(Arrow arrow) {
//		arrows.remove(arrow);
//		System.out.println("arrow successfully deleted!");
//	}
//
//	
//	public void deleteTransition(Transition transition) {
//		//delete connected arrows
//		for(Arrow arrow:transition.arrows) {
//			EditorArea.singleton.deleteNode(arrow);
//		}
//		transition.arrows.clear();
//		
//		//delete transition
//		transitions.remove(transition);
//		System.out.println("transition successfully deleted!");
//	}
	
	
	public Token findToken(String name) {
		for(Token token : tokens ) {
			if(token.name.equals(name) )
				return token;
		}
		return null;
	}
	
	public void disableMouseTransparentBond() {
		for(Bond bond:bonds) {
			bond.setMouseTransparent(false);
			bond.setMouseTransparent(false);
			
		}
		System.out.println("disable");
	}
	
	public void enableMouseTransparentBond() {
		for(Bond bond:bonds) {
			bond.setMouseTransparent(true);
			bond.setMouseTransparent(true);
		}
		System.out.println("enable");
	}
	
	
	/*
	public SelectableNode getSelectedNode() {
		SelectableNode node = null;
		
		for(Place place:places) {
			if(place.isSelected) {
				node = place;
				place.isSelected = false;
				return node;
			}
				
		}
		for(Transition transition:transitions) {
			if(transition.isSelected) {
				node = transition;
				transition.isSelected = false;
				return node;
			}
		}
		for(Token token:tokens) {
			if(token.isSelected) {
				node = token;
				token.isSelected = false;
				return node;
			}
		}
		for(Bond bond:bonds) {
			if(bond.isSelected) {
				node = bond;
				bond.isSelected = false;
				return node;
			}
		}
		for(Arrow arrow:arrows) {
			if(arrow.isSelected) {
				node = arrow;
				arrow.isSelected = false;
				return node;
			}
		}
		
		
		return null;
	}
	*/
}
