package rpnsim.application.editor;


import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import jfxtras.animation.Timer;
import rpnsim.application.rpn.SelectableNode;
import rpnsim.application.rpn.Token;
import rpnsim.application.rpn.Transition;
import rpnsim.application.RPNApp;
import rpnsim.application.ScrollArea;
import rpnsim.application.rpn.Arrow;
import rpnsim.application.rpn.Bond;
import rpnsim.application.rpn.Place;
import rpnsim.application.rpn.RPN;

public class EditorArea extends ScrollArea {
	
	private EditorToolbox toolbox;
	
	private Arrow currentArrow = null;
	private Bond currentBond = null;
	
	@Override
	public void SomethingChanged() {
		super.SomethingChanged();
		RPNApp.AddAsterisk();
	}
	
	@Override
	public void SavedChanges() {
		super.SavedChanges();
		RPNApp.RemoveAsterisk();
	}
	
	
	
	
	public void setCurrentArrow(Arrow arrow) {
		this.currentArrow = arrow;
	}
	public Arrow getCurrentArrow() {
		return this.currentArrow;
	}
	
	public void setCurrentBond(Bond bond) {
		this.currentBond = bond;
	}
	public Bond getCurrentBond() {
		return this.currentBond;
	}
	
	

	
	public EditorArea(AnchorPane fx_editorScrollArea, EditorToolbox toolbox) {
		super(fx_editorScrollArea);
		this.toolbox = toolbox;
		
		this.fx_scrollArea.setOnMouseClicked(mouseClicked);
		this.fx_scrollArea.setOnMouseMoved(mouseMoved);
	
	}


	
	public Place addPlace(Point2D pos) {
		Place place = rpn.addPlace();
		addNode(place);
		//place.relocate(pos.getX()-20, pos.getY()-40);
		place.setPosition(new Point2D(pos.getX()-20, pos.getY()-40));
		return place;
	}
	
	public Transition addTransition(Point2D pos) {
		Transition transition = rpn.addTransition();
		addNode(transition);
		//transition.relocate(pos.getX(), pos.getY());
		transition.setPosition(new Point2D(pos.getX(), pos.getY()));
		return transition;
	}
	
	/*
	public void deleteNode(SelectableNode node) {
		
		// Delete from RPN
		if(node instanceof Place) {
			removeNode(node);
		}
		else if(node instanceof Transition){
			removeNode(node);
		}
		else if(node instanceof Token) {
			//rpn.deleteToken((Token) node);
		}
		else if(node instanceof Bond) {
			Bond bond = (Bond)node;
			removeNode(bond.getGroup());
			return;
		}
		else if(node instanceof Arrow) {
			//rpn.deleteArrow((Arrow) node);
			Arrow arrow = (Arrow)node;
			removeNode(arrow.getGroup());
			return;
		}
		
		
	}
	*/
	
	public String getSelectedTool() {
		return toolbox.getSelected();
	}
	
	private void onLeftClick(Point2D mousePos) {
		
		String tool = toolbox.getSelected();
		switch(tool) {
			
		case "PLACE": addPlace(mousePos); SomethingChanged(); break;
		case "TRANSITION": addTransition(mousePos); SomethingChanged(); break;
		case "SELECT":
			//rpn.disableMouseTransparentBond();
			//System.out.println("mpike");
			break;
		};
		
	}
	
	EventHandler<MouseEvent> mouseClicked = new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(final MouseEvent event) {
	    	
	    	Point2D mousePos = new Point2D( event.getX(), event.getY() );
	    	
	    	if( event.getButton() == MouseButton.PRIMARY )
	    		onLeftClick(mousePos);
	    	
	    	System.out.println(event.getClickCount());
	    	
	    	System.out.println("Selected button: "+toolbox.getSelected());
	        event.consume();
	    }
	};
	
	EventHandler<MouseEvent> mouseMoved = new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(final MouseEvent event) {
	    	
	    	//System.out.println("Mouse moved");
	    	Point2D mousePos = new Point2D( event.getX(), event.getY() );
	    	
	    	String tool = toolbox.getSelected();
	    	switch(tool) {
	    	case "ARROW":
	    		//System.out.println("Arrow tool");
	    		if( currentArrow != null ) {
	    			//System.out.println("Current arrow");
	    			currentArrow.setEndPoint( mousePos.getX(), mousePos.getY());
	    			currentArrow.update();
	    		}
	    		
	    		break;
	    		
	    	case "BOND":
	    		
	    		if(currentBond != null) {
	    			currentBond.setEndPoint( mousePos.getX(), mousePos.getY());
	    			currentBond.update();
	    		}
	    		break;
	    	
	    	}
	    }
	};	
	
	public EventHandler<KeyEvent> keyClicked = new EventHandler<KeyEvent>() {
	    @Override
	    public void handle(final KeyEvent keyEvent) {
	    	String keyCode = keyEvent.getCode().toString();
	    	if(keyCode.equals("DELETE")) {
	    		//deleteKeyPressed = true;
	    		System.out.println(keyCode);
	    		
	    		SelectableNode selectedNode = SelectableNode.currentSelected;
	    		selectedNode.delete();
	    		//deleteNode(selectedNode);
	    		
	    		//if(node!=null)
	    		//System.out.println("selected: "+node.hashCode());
	    	}
	    	
	    	
	    	keyEvent.consume();
	    }
	};
	
}
