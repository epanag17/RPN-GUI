package rpnsim.application.rpn;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import rpnsim.application.editor.EditorArea;
import rpnsim.application.editor.PopupWindow;

public class Token extends EditableNode {

	//private String name;
	
	private Text label;
	private Circle circle;
	
	private Place place;
	public final Place initial_place;
	
	private static int counter = 0;
	
	protected ArrayList<Bond> bonds;
	

	public Bond[] getBonds() {
		Bond[] array = new Bond[bonds.size()];
		bonds.toArray(array);
		return array;
	}
	
	

	protected boolean isConnected(Set<Token> visited, Token other) {
		
		visited.add(this);
		
		for(Bond bond:bonds) {
			Token source = bond.getSource();
			Token destination = bond.getDestination();
			if(source == this) {
				if(destination == other)
					return true;
				else {
					if(!visited.contains(destination))
						return destination.isConnected(visited,other);
				}
			}
			
			if(destination == this) {
				if(source == other)
					return true;
				else {
					if(!visited.contains(source))
						return source.isConnected(visited, other);
				}
			}
		}
		
		return false;
	}
	
	
	public boolean isConnected(Token other) {
		Set<Token> visited = new HashSet<Token>();
		return isConnected(visited,other);
	}
	
	
	
	public void getConnected(Set<Token> connected) {
		
		if( connected.contains(this) )
			return;
		
		connected.add(this);
		
		for(Bond bond:bonds) {
			Token source = bond.getSource();
			Token destination = bond.getDestination();
			if(source == this) 
				destination.getConnected(connected);
			if(destination == this) 
				source.getConnected(connected);
		}
	}
	
	public Set<Token> getConnected(){
		Set<Token> connected = new HashSet<Token>();
		this.getConnected(connected);
		return connected;
	}
	
	public Place getPlace() {
		return place;
	}
	
	public void setPlace(Place place) {
		this.place = place;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public Circle getCircle() {
		return circle;
	}

	public Text getLabel() {
		return label;
	}
	
	public int getIndex() {
		return place.getIndex(this);
	}
	
	public void rename(String newName) {
		this.name = newName;
		label.setText(newName);
		
	}
	
	public Token( RPN rpn, Place place, String tokenName) {
		super(rpn);
		//super(5,5,5);
		circle = new Circle(5,5,5);
		circle.getOnMouseClicked();
		
		bonds = new ArrayList<>();
		
		this.name = tokenName;
		this.place = place;
		this.initial_place = place;
		label = new Text(name);
		
		circle.setOnMouseClicked(mouseClicked);
	}
	
	public Token( RPN rpn, Place place) {
		super(rpn);
		//super(5,5,5);
		circle = new Circle(5,5,5);
		circle.getOnMouseClicked();
		this.initial_place = place;
		
		bonds = new ArrayList<>();
		
		Token.counter++;
		
		this.name = "b"+counter;
		this.place = place;
		label = new Text(name);
		
		circle.setOnMouseClicked(mouseClicked);
	    //Text tokenId = new Text(tokenID);
	}
	
	private void initBond(EditorArea editor) {
		Bond bond = new Bond(this.rpn);
		bond.setSource(this);
		
		Point2D endPoint = getCenter();
		bond.setEndPoint( endPoint.getX(), endPoint.getY());
		bond.update();
		
		editor.addNode(bond.getGroup());
		editor.setCurrentBond(bond);
	}
	
	public Point2D getCenter() {
		//Bounds bounds = circle.localToScene(circle.getBoundsInLocal());
		//return new Point2D( bounds.getCenterX(), bounds.getCenterY() );
		
		Point2D globalPosition = new Point2D(0,0);
		
		Node node = circle;
		
		AnchorPane scrollArea = rpn.scrollArea.getfxScrollArea();
		while( node != scrollArea ) {
			if(node == null)
				break;
			
			Point2D localPos = new Point2D( node.getLayoutX(), node.getLayoutY() );
			globalPosition = globalPosition.add(localPos);
			
			node = node.getParent();
		}	
		
		globalPosition = globalPosition.add( circle.getRadius(), circle.getRadius() );
		
		return globalPosition;
	}
	
	protected void onDoubleClick() {
		new PopupWindow("Token",this);
		
	}
	
	public Bond createBond(Token other) {
		Bond bond = new Bond(this.rpn);
		bond.setSource(this);
		
		bond.setDestination(other);
		bond.update();
		
		bonds.add( bond );
		other.bonds.add(bond);
		rpn.bonds.add(bond);
		
		rpn.scrollArea.addNode(bond.getGroup());
		
		return bond;
	}
	
	@Override
	protected void onLeftClick() {
		super.onLeftClick();
		
		
		if(!(rpn.scrollArea instanceof EditorArea))
			return;
		
		System.out.println("Left Clicked a Token Node");
		//System.out.println("Token Center: "+getCenter());
		
		
		EditorArea editor = (EditorArea)rpn.scrollArea;
		String tool = editor.getSelectedTool();
		
		switch(tool) {
		
		case "BOND":
			
			Bond currentBond = editor.getCurrentBond();
			if( currentBond == null ) {
				initBond(editor);
			}
			else {
				
				Token source = currentBond.getSource();
				
				if( source == this ) {
					System.out.println("Cannot create bond with self");
					return;
				}
				
				if( source.place != this.place ) {
					System.out.println("Cannot create bond with token in different place");
					return;
				}
				
					
				currentBond.setDestination(this);
				currentBond.update();
				
				bonds.add( currentBond );
				source.bonds.add(currentBond);
				
				System.out.println("Bonds in source token "+source.bonds);
				System.out.println("Bonds in destination token "+bonds);
				
				rpn.bonds.add(currentBond);
				editor.setCurrentBond(null);
				
			}
			
			
			break;
			
		}
	}
	
	protected void removeAllBonds() {
		System.out.println("Removing all bonds");
		
		Bond[] bondArray = new Bond[bonds.size()];
	    bonds.toArray(bondArray);
		for(int i=0; i<bondArray.length; i++) {
			Bond bond = bondArray[i];
			removeBond(bond);
		}

	}
	
	public void removeBond(Bond bond) {
		
		Token source = bond.getSource();
		Token destination = bond.getDestination();
		
		// Remove from RPN
		rpn.delete(bond);
		
		// Remove from lists
		source.bonds.remove(bond);
		destination.bonds.remove(bond);
		
		// Remove from UI
		rpn.scrollArea.removeNode(bond.getGroup());
		
	}
	
	protected void removeHightlight() {
		circle.getStyleClass().remove("selectedToken");
	}
	
	protected void setHightlight() {
		circle.getStyleClass().add("selectedToken");
	}
	
	@Override
	public void delete() {
		System.out.println("Deleting Token from Place!");
		place.removeToken(this);
	}
	
}
