package rpnsim.application.rpn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Pair;
import jfxtras.animation.Timer;
import jfxtras.scene.layout.CircularPane;
import rpnsim.application.editor.EditorArea;
import rpnsim.application.editor.PopupWindow;

public class Place extends MovableNode {

	public static int counter = 0;
	//private String name;
	
	private VBox vbox;
	private Text label;
	
	private CircularPane circle;
	private GridPane grid;
	
	private ArrayList<Token> tokens;
	
	public void rename(String newName) {
		this.name = newName;
		label.setText(newName);
	}
	
	
	public Place(RPN rpn, String name, Point2D position, ArrayList<String> tokenList) {
		super(rpn,DragContainer.DragPlace);
		
		this.rpn = rpn;
		this.name = name;
		this.position = position;
		tokens = new ArrayList<Token>();
		
		createPlace();
		
		for(String tokenName: tokenList) {
			grid.setPadding(new Insets(2, 2, 2, 2));
			grid.setVgap(2); 
			grid.setHgap(2);
			Token token = new Token(rpn,this, tokenName);
			grid.add(token.getCircle(), 0, tokens.size());
			grid.add(token.getLabel(), 1, tokens.size());
			tokens.add(token);
			rpn.addToken(token);
		}
		
		circle.getChildren().add(grid);
		
		
	}
	
	private void createPlace() {
		vbox = new VBox();
		this.getChildren().add(vbox);
		
		label = new Text(name);
		vbox.getChildren().add(label);
		
		circle = new CircularPane();
		vbox.getChildren().add(circle);
		
		
		circle.setShowDebug(Color.BLACK);
		grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setPadding(new Insets(30, 30, 30, 30));
		grid.setVgap(30); 
		grid.setHgap(30);
	}
	
	
	public Place(RPN rpn) {
		super(rpn,DragContainer.DragPlace);
		
		
		
		tokens = new ArrayList<Token>();
		
		this.rpn = rpn;
		
		name = "place"+counter;
		createPlace();
	     
	    circle.getChildren().add(grid);
		
		counter++;
	}
	
	public CircularPane getCircle() {
		return circle;
	}
	
	@Override
	public void delete() {
		
		// Delete all tokens
		Token[] tokenArray = new Token[tokens.size()];
	    tokens.toArray(tokenArray);
		for(int i=0; i<tokenArray.length; i++) {
			Token token = tokenArray[i];
			
			rpn.delete(token);
			tokens.remove(token);
			
			removeToken(token);
		}
		
		
		// Delete all arrows
		deleteAllArrows();
		
		System.out.println(rpn.scrollArea.test());
		
		rpn.delete(this);
		
		// Delete self
		rpn.scrollArea.removeNode( this );
		//EditorArea.singleton.deleteNode(this);
	}
	
	public void moveToken(Token token) {
		
		Place previousPlace = token.getPlace();
		if(previousPlace == this)
			return;
		
		previousPlace.removeTokenWithoutLabels(token);
		addToken(token);
		token.setPlace(this);
		
		//prepei na metaferthoun kai ta tokens pou einai syndemena me to token
		Bond[] myBonds = token.getBonds();
		for(int i=0; i<myBonds.length; i++) {
			Bond bond = myBonds[i];
			Token tokenA = bond.getSource();
			Token tokenB = bond.getDestination();
			if(token == tokenA)
				moveToken(tokenB);
			if(token == tokenB)
				moveToken(tokenA);
		}
		
	}
	
	private void removeTokenWithoutLabels(Token token) {

		// Remove from list
		tokens.remove(token);
		
		
		
		// Remove from grid
		grid.getChildren().remove(token.getCircle());
		grid.getChildren().remove(token.getLabel());
		
		// Clear grid, and put tokens back again
		grid.getChildren().clear();
		for(int i=0; i<tokens.size(); i++) {
			grid.add(tokens.get(i).getCircle(), 0, i);
			grid.add(tokens.get(i).getLabel(), 1, i);
			
		}
		// If grid is empty 
		if( tokens.size() == 0) {
			grid.setPadding(new Insets(30, 30, 30, 30));
			grid.setVgap(30); 
			grid.setHgap(30);			
			return;
		}
		
	}
	
	protected void removeToken(Token token) {
		
		removeTokenWithoutLabels(token);
		token.removeAllBonds();
		// Remove all labels that contain token!
		for( Arrow arrow : rpn.arrows ) {
			 
			Set<LabelItem> labelItems = arrow.getSet();
			LabelItem[] labelArray = new LabelItem[labelItems.size()];
			labelItems.toArray( labelArray );
			for( int i=0; i<labelArray.length; i++) {
				LabelItem item = labelArray[i];
				if( item.hasToken(token) )
					labelItems.remove(item);
			}
			arrow.updateLabel();
		}
		
		
		rpn.tokens.remove(token);
		rpn.scrollArea.updateArrowsBonds();
		
	}
	
	
	
	public void addToken(Token token) {
		grid.setPadding(new Insets(2, 2, 2, 2));
		grid.setVgap(2); 
		grid.setHgap(2);
		
		grid.add(token.getCircle(), 0, tokens.size());
		grid.add(token.getLabel(), 1, tokens.size());
		
		tokens.add(token);
		
		rpn.addToken(token);
		
		rpn.scrollArea.updateArrowsBonds();
	}
	
	
	public Token addToken() {
		
		grid.setPadding(new Insets(2, 2, 2, 2));
		grid.setVgap(2); 
		grid.setHgap(2);
		
		Token token = new Token(rpn,this);
		
		grid.add(token.getCircle(), 0, tokens.size());
		grid.add(token.getLabel(), 1, tokens.size());
		
		tokens.add(token);
		
		rpn.addToken(token);
		
		rpn.scrollArea.updateArrowsBonds();
		
		return token;
	}
	
	protected void onDoubleClick() {
		new PopupWindow("Place",this);
		
	}
	
	@Override
	public void onLeftClick() {
		super.onLeftClick();
		
		if(!(rpn.scrollArea instanceof EditorArea ))
			return;
		
		EditorArea editor = (EditorArea)rpn.scrollArea;
		String tool = editor.getSelectedTool();
		
		switch(tool) {
		case "TOKEN": 
			addToken();
			editor.SomethingChanged();
			
			break;
		}
		
	}
	
	
	/*
	@Override
	public void relocate(double x, double y) {
		Point2D local = new Point2D(circle.getLayoutX(),circle.getLayoutY());
		double width = circle.getWidth();
		double height = circle.getHeight();
		
		super.relocate(x+width/2 + local.getX(), y+height/2 + local.getY());
	}*/
	public int getIndex(Token token) {
		return tokens.indexOf(token);
	}
	
	public Point2D getRelativeCoordinates() {
		Point2D local = new Point2D(circle.getLayoutX(),circle.getLayoutY());
		double width = circle.getWidth();
		double height = circle.getHeight();
		System.out.println(width);
		return new Point2D(local.getX()+width/2, local.getY()+height/2);
	}
	
	@Override
	public Point2D getCenter() {
		Point2D global = new Point2D(this.getLayoutX(),this.getLayoutY());
		Point2D local = new Point2D(circle.getLayoutX(),circle.getLayoutY());
		double width = circle.getWidth();
		double height = circle.getHeight();
		
		Point2D start = new Point2D( global.getX() + local.getX(), global.getY() + local.getY() );
		
		Point2D center = new Point2D(start.getX() + width/2, start.getY() + height/2);
		return center;
	}
	
	@Override
	protected void setHightlight(/*boolean active*/) {
		//if(this.isSelected)
			circle.getStyleClass().add("selectedPlace");
	}
	
	@Override
	protected void removeHightlight() {
		circle.getStyleClass().remove("selectedPlace");
	}
	
	private void updateBonds() {
		for(Token token : tokens) {
			for(Bond bond:token.bonds) {
				bond.update();
			}
		}
	}
	

	@Override
	public List<Pair<String,Object>> getDataList(){
		ArrayList<Pair<String,Object>> data = new ArrayList<Pair<String,Object>>();
		
		
		data.add( new Pair<String, Object>("name", this.name ));
		double x = getPosition().getX();
		double y = getPosition().getY();
		data.add( new Pair<String, Object>("x", Double.toString(x) ));
		data.add( new Pair<String, Object>("y", Double.toString(y) ));
		
		ArrayList< Pair<String,String> > tokenList = new ArrayList<Pair<String,String>>();
		for( Token token : tokens ) 
			tokenList.add( new Pair<String,String>("token",token.name) );
		
		data.add( new Pair<String,Object>("tokens",tokenList));
		
		return data;
	}
	
	
	@Override
	protected void onDragOver() {
		updateBonds();
	}
	
	@Override
	public Point2D getIntersectionPoint(Point2D p1, Point2D p2) {
		
		Point2D center = getCenter();
		double r = circle.getWidth()/2;
		List<Point2D> intersectionPoints = Place.getCircleLineIntersectionPoint(p1, p2, center, r);
		
		if(intersectionPoints.isEmpty())
			return null;
		
		if(intersectionPoints.size() == 1)
			return intersectionPoints.get(0);
		
		Point2D A = new Point2D(p1.getX(),p1.getY());
		Point2D B = new Point2D(p2.getX(),p2.getY());
		Point2D AB = B.subtract(A); // B - A
		
		Point2D C = new Point2D(intersectionPoints.get(0).getX(),intersectionPoints.get(0).getY());
		Point2D D = new Point2D(intersectionPoints.get(1).getX(),intersectionPoints.get(1).getY());
		
		Point2D AC = C.subtract(A); // C - A
		Point2D AD = D.subtract(A); // D - A
		
		// Elegxos: An to intersection point 1 (C) einai mesa sto eu8igramo tmima p1 -> p2
		if( AC.dotProduct(AB) < 0 )
			return intersectionPoints.get(1);
		if( AC.magnitude() > AB.magnitude() )
			return intersectionPoints.get(1);
		
		// Elegxos: An to intersection point 2 (C) einai mesa sto eu8igramo tmima p1 -> p2
		if( AD.dotProduct(AB) < 0 )
			return intersectionPoints.get(0);
		if( AD.magnitude() > AB.magnitude() )
			return intersectionPoints.get(0);
			
		// Kai ta dio einai mesa sto eu8igramo tmima.
		// Ara epilegoume to intersection point pou einai pio konta sto p1
		if( AC.magnitude() < AD.magnitude() )
			return intersectionPoints.get(0);
		else
			return intersectionPoints.get(1);
	}
	
	
	public static List<Point2D> getCircleLineIntersectionPoint(Point2D pointA,
			Point2D pointB, Point2D center, double radius) {
        double baX = pointB.getX() - pointA.getX();
        double baY = pointB.getY() - pointA.getY();
        double caX = center.getX() - pointA.getX();
        double caY = center.getY() - pointA.getY();

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;
        

        Point2D p1 = new Point2D(pointA.getX() - baX * abScalingFactor1, pointA.getY()- baY * abScalingFactor1);
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        Point2D p2 = new Point2D(pointA.getX() - baX * abScalingFactor2, pointA.getY()- baY * abScalingFactor2);
        return Arrays.asList(p1, p2);
    }


	public ArrayList<Token> getTokens() {
		return tokens;
	}
	
}
