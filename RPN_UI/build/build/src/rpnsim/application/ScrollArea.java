package rpnsim.application;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import jfxtras.animation.Timer;
import rpnsim.application.rpn.Arrow;
import rpnsim.application.rpn.Bond;
import rpnsim.application.rpn.Place;
import rpnsim.application.rpn.RPN;
import rpnsim.application.rpn.Transition;

public class ScrollArea {

	protected boolean changed = false;
	
	protected boolean savedOnce = false;
	
	
	protected AnchorPane fx_scrollArea;
	protected RPN rpn;
	
	protected Timer updateTimer;
	
	
	
	public void SomethingChanged() {
		changed = true;
	}
	
	public void NewFile() {
		changed = false;
		savedOnce = false;
	}
	
	public void SavedChanges() {
		changed = false;
		savedOnce = true;
	}
	
	
	public void clearAll() {
		fx_scrollArea.getChildren().clear();
	}
	
	public String test() {
		return fx_scrollArea.getChildren().toString();
	}
	
	public AnchorPane getfxScrollArea() {
		return fx_scrollArea;
	}
	
	public ScrollArea(AnchorPane fx_scrollArea) {
		this.fx_scrollArea = fx_scrollArea;
		
		loadRPN( new RPN(this) );
		
		// Updates arrows and bonds after a few miliseconds
		updateTimer = new Timer(new Runnable() {
			@Override
			public void run() {
				
				System.out.println("Updating arrows and bonds");
	    		for(Arrow arrow : rpn.getArrows() )
	    			arrow.update();
	    		for(Bond bond : rpn.getBonds() )
	    			bond.update();
	    		updateTimer.stop();
			}
		});
		updateTimer.setDelay(Duration.millis(100.0));
	}
	
	public void addNode(Node node) {
		this.fx_scrollArea.getChildren().add(node);
	}
	
	public void removeNode(Node node) {
		this.fx_scrollArea.getChildren().remove(node);
	}
	
	public void updateArrowsBonds() {
		updateTimer.restart();
	}
	
	public void loadRPN( RPN rpn ) {
		
		// Clear everything in scroll area
		clearAll();
		
		this.rpn = rpn;
		this.rpn.setScrollArea(this);
		
		// Add places, transitions, arrows and bonds to scroll area
		ArrayList<Place> places = rpn.getPlaces();
		for(Place place : places)
			fx_scrollArea.getChildren().add( place );
		
		ArrayList<Transition> transitions = rpn.getTransitions();
		for(Transition transition : transitions)
			fx_scrollArea.getChildren().add( transition );
		
		ArrayList<Arrow> arrows = rpn.getArrows();
		for(Arrow arrow : arrows)
			fx_scrollArea.getChildren().add( arrow.getGroup() );
		
		Bond[] bonds = rpn.getBonds();
		for(Bond bond : bonds)
			fx_scrollArea.getChildren().add( bond.getGroup() );				
		
	}
	
	public RPN getRPN() {
		return rpn;
	}
	
	
	
}
