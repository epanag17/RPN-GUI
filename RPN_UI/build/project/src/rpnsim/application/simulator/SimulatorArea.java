package rpnsim.application.simulator;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import rpnsim.application.ScrollArea;
import rpnsim.application.rpn.SelectableNode;

public class SimulatorArea extends ScrollArea {

	public SimulatorArea(AnchorPane fx_scrollArea) {
		super(fx_scrollArea);
		this.fx_scrollArea.setOnMouseClicked(mouseClicked);
		// TODO Auto-generated constructor stub
	}

	
	EventHandler<MouseEvent> mouseClicked = new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(final MouseEvent event) {
	    	
	    	System.out.println("Updating!");
	    	updateArrowsBonds();
	        event.consume();
	    }
	};
	
}
