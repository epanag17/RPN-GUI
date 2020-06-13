package rpnsim.application.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import rpnsim.application.ScrollArea;
import rpnsim.application.editor.EditorArea;
import rpnsim.application.editor.NameEditPopup;

import rpnsim.application.model.RPN;
import rpnsim.application.model.Transition;
import rpnsim.application.model.Node;
import rpnsim.application.model.Place;

public class TransitionUI extends MovableNode {
	
	public static int counter = 0;
	//private String name;
	
	@FXML private Label transitionLabel;
	@FXML private Label history;
	@FXML private AnchorPane box;
	
	//private Label transitionLabel;
	
	
	public void updateHistory() {
		
		ArrayList<Integer> historyList = ((Transition)myNode).getHistoryList();
		
		if(!historyList.isEmpty()) {
			history.setText(historyList.toString());
		}
		else history.setText("");
	}
	
	
	public void rename(String newName) {
		super.rename(newName);
		
		this.name = myNode.getName();
		transitionLabel.setText(this.name);
	}
	
	public void loadFXML() {

		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/transition.fxml")
				);
		
		fxmlLoader.setRoot(this); 
		fxmlLoader.setController(this);
		
		try { 
			fxmlLoader.load();
        
		} catch (IOException exception) {
		    throw new RuntimeException(exception);
		}
		
	}
	
	public TransitionUI(RPN rpn) {
		super(rpn,(Node)rpn.addTransition(),DragContainer.DragTransition);
		loadFXML();
		
		Transition transition = (Transition)myNode;
		
		transitionLabel.setText(transition.getName());
		
		
		group = new Group(this);
		
    	if( rpn.scrollArea != null ) {
    		rpn.scrollArea.addNode( this );
    	}
	}
	
	public TransitionUI(RPN rpn, String name, Point2D position) {
		super(rpn,(Node)rpn.addTransition(),DragContainer.DragTransition);
		
		
		Transition transition = (Transition)myNode;
		transition.setName(name);
		
		this.position = position;
		loadFXML();
		transitionLabel.setText(transition.getName());
		
		
		group = new Group(this);
		
    	if( rpn.scrollArea != null ) {
    		rpn.scrollArea.addNode( this );
    	}
	}
	
	@Override
	protected void setHightlight(/*boolean active*/) {
		//if(this.isSelected)
			box.getStyleClass().add("selectedNode");
	}
	
	@Override
	protected void removeHightlight() {
		box.getStyleClass().remove("selectedNode");
	}
	
	
	public Point2D findPointIntersectionOfLineSegments(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
		double x1 = p1.getX(); double y1 = p1.getY();
		double x2 = p2.getX(); double y2 = p2.getY();
		double x3 = p3.getX(); double y3 = p3.getY();
		double x4 = p4.getX(); double y4 = p4.getY();
		double ta=((y3-y4)*(x1-x3)+(x4-x3)*(y1-y3))/((x4-x3)*(y1-y2)-(x1-x2)*(y4-y3));
		
		double tb=((y1-y2)*(x1-x3)+(x2-x1)*(y1-y3))/((x4-x3)*(y1-y2)-(x1-x2)*(y4-y3));
		if(ta>=0 && ta<=1 && tb>=0 && tb<=1) {
			//the line sections intersects, so find point intersection
			double x = x1 + ta*(x2-x1);
			double y = y1+ta*(y2-y1);
			return new Point2D(x,y);
		}
		return null;
	}
	

	@Override
	public Point2D getCenter() {
		Point2D global = new Point2D(this.getLayoutX(),this.getLayoutY());
		Point2D local = new Point2D(box.getLayoutX(),box.getLayoutY());
		double width = box.getWidth();
		double height = box.getHeight();
		
		Point2D start = new Point2D( global.getX() + local.getX(), global.getY() + local.getY() );
		
		Point2D center = new Point2D(start.getX() + width/2, start.getY() + height/2);
		return center;
	}
	
	@Override
	public Point2D getIntersectionPoint(Point2D p3, Point2D p4) {
		double width = box.getWidth();
		double height = box.getHeight();
		double x = box.getLayoutX()+this.getLayoutX();
		double y = box.getLayoutY()+this.getLayoutY();
		
		//check left line of transition
		Point2D p1 = new Point2D(x,y);
		Point2D p2 = new Point2D(x,y+height);
		if(findPointIntersectionOfLineSegments(p1,p2,p3,p4)!=null) {
			//intersect
			return findPointIntersectionOfLineSegments(p1,p2,p3,p4);
		}
		//check right line of transition
		p1 = new Point2D(x+width,y);
		p2 = new Point2D(x+width,y+height);
		if(findPointIntersectionOfLineSegments(p1,p2,p3,p4)!=null) {
			//intersect
			return findPointIntersectionOfLineSegments(p1,p2,p3,p4);
			
		}
		
		//check up line of transition
		p1 = new Point2D(x,y);
		p2 = new Point2D(x+width,y);
		if(findPointIntersectionOfLineSegments(p1,p2,p3,p4)!=null) {
			//intersect
			return findPointIntersectionOfLineSegments(p1,p2,p3,p4);	
		}
		
		//check down line of transition
		p1 = new Point2D(x,y+height);
		p2 = new Point2D(x+width,y+height);
		if(findPointIntersectionOfLineSegments(p1,p2,p3,p4)!=null) {
			//intersect
			return findPointIntersectionOfLineSegments(p1,p2,p3,p4);
			
			
		}
		return null;
	}
	
	protected void onDoubleClick() {
		if( rpn.scrollArea instanceof EditorArea )
			new NameEditPopup("Transition",this);
		
	}
	

	@Override
	public List<Pair<String,Object>> getDataList(){
		ArrayList<Pair<String,Object>> data = new ArrayList<Pair<String,Object>>();
		
		data.add( new Pair<String, Object>("name", this.myNode.getName() ));
		double x = getPosition().getX();
		double y = getPosition().getY();
		data.add( new Pair<String, Object>("x", Double.toString(x) ));
		data.add( new Pair<String, Object>("y", Double.toString(y) ));
		
		return data;
	}
	
	@Override
	public void refresh() {
		
		// Is the transition removed?
		if( rpn.getTransition(myNode.name) == null ) {
			delete();
		
			// TODO: cause everything to refresh
			
			return;
		}
		
		updateHistory();
	}
	
	@Override
	public void delete() {
		
		// Remove place from the RPN
		rpn.removeTransition((Transition)myNode);
		
		// Remove this object from the view
		rpn.scrollArea.removeNode( this );
	
		rpn.scrollArea.refreshAll();
	}
	
	/*
	@Override
	public void delete() {
		
		// Delete all arrows
		deleteAllArrows();
		
		// Delete self
		
		//rpn.scrollArea.removeNode( this );	
		AnchorPane fxscroll = (AnchorPane)this.getParent();
		fxscroll.getChildren().remove(this);
		
		
		SelectableNode node = (SelectableNode)this;
		RPNUI temp = node.rpn;
		temp.delete(node);
		

	}
	*/
}
