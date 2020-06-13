package rpnsim.application.rpn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import rpnsim.application.editor.EditorArea;

public class Arrow extends EditableNode {

	
	MovableNode source;
	MovableNode destination;
	
    private Line line;
    private Line arrowBody, arrow1, arrow2; 
	private Group group;
    private static final double arrowLength = 20;
    private static final double arrowWidth = 7;
    
    protected TextFlow label;
    
    protected Set<LabelItem> items;
    
    

	Line invisibleLine ;
    //protected ArrayList<Token> tokens;
    //protected ArrayList<Token> negTokens;
    //protected ArrayList<Pair<Token, Token>> bonds;
    //protected ArrayList<Pair<Token, Token>> negBonds;
    protected ArcLabel arcLabel;
    
    public void updateLabel() {
    	arcLabel.createLabel();
    }
    
    public Set<LabelItem> getSet(){
    	return items;
    }
    
    public void addToken(Token token) {
    	//tokens.add(token);
    	items.add( new LabelItem(token) );
    }
    public void addNegativeToken(Token token) {
    	//negTokens.add(token);
    	items.add( new LabelItem(token, true) );
    }
    public void addBond(Token A, Token B) {
    	//bonds.add( new Pair<Token,Token>(A,B) );
    	items.add( new LabelItem(A,B) );
    }
    public void addBond(Pair<Token,Token> bond) {
    	//bonds.add( new Pair<Token,Token>(A,B) );
    	items.add( new LabelItem(bond.getKey(), bond.getValue()) );
    }
    
    public void addNegativeBond(Token A, Token B) {
    	//negBonds.add( new Pair<Token,Token>(A,B) );
    	items.add( new LabelItem(A,B,true) );
    }
    public void addNegativeBond(Pair<Token,Token> bond) {
    	//bonds.add( new Pair<Token,Token>(A,B) );
    	items.add( new LabelItem(bond.getKey(), bond.getValue(), true) );
    }
    
    public ArrayList<Token> getTokens(boolean negative){
    	ArrayList<Token> tokens = new ArrayList<Token>();
    	for( LabelItem item : items ) {
    		if( item.isToken() && (item.isNegative() == negative) )
    			tokens.add( item.getToken() );
    	}  	
    	return tokens;
    }
    public ArrayList<Pair<Token,Token>> getBonds(boolean negative){
    	ArrayList<Pair<Token,Token>> bonds = new ArrayList<Pair<Token,Token>>();
    	for( LabelItem item : items ) {
    		if( item.isBond() && (item.isNegative() == negative) )
    			bonds.add( item.getBond() );
    	}  	
    	return bonds;
    }
    
    
    public Group getGroup() {
    	return group;
    }
    
    public Arrow(RPN rpn) {  
    	super(rpn);
    	
    	items = new HashSet<LabelItem>();
    	
    	//tokens = new ArrayList<>();
        //negTokens = new ArrayList<>();
        //bonds = new ArrayList<>();
        //negBonds = new ArrayList<>();
    	arcLabel = new ArcLabel(this,rpn);
    	//label = new Text("label");
    	
    	line = new Line();
    	arrow1 = new Line();
    	arrow2 = new Line();
    	invisibleLine = new Line();
    	invisibleLine.setStartX(line.getStartX()); invisibleLine.setEndY(line.getEndY());
    	invisibleLine.setEndX(line.getEndX()); invisibleLine.setEndY(line.getEndY());


    	
    	invisibleLine.setStrokeWidth(10.0f);
    	invisibleLine.setStroke(Color.TRANSPARENT);
    	//invisibleLine.setFill( Color.rgb(200, 200, 200, 0.2) );
    	
    	arrowBody = line;
    	
    	line.setMouseTransparent(true);
        arrow1.setMouseTransparent(true);
        arrow2.setMouseTransparent(true);
        
        //getStyleClass().add("arrow");
        //invisibleLine.getStyleClass().add("arrow");
        
        invisibleLine.setMouseTransparent(true);
        
        invisibleLine.setOnMouseClicked(mouseClicked);
        
        group = new Group(line,arrow1,arrow2, invisibleLine );
        label = new TextFlow();
    	group.getChildren().add(label);
    }
    

    public Point2D getStartPoint() {
    	Point2D point = new Point2D( line.getStartX(), line.getStartY() );
    	return point;
    }
    
    public Point2D getEndPoint() {
    	Point2D point = new Point2D( line.getEndX(), line.getEndY() );
    	return point;
    }
    
    public void setStartPoint( double x, double y ) {
    	line.setStartX(x);
    	line.setStartY(y);
    	
    	invisibleLine.setStartX(x);
    	invisibleLine.setStartY(y);
    }
    public void setEndPoint( double x, double y ) {
    	line.setEndX(x);
    	line.setEndY(y);
    	
    	invisibleLine.setEndX(x);
    	invisibleLine.setEndY(y);
    }   
    
    double findAngleBetweenTwoPoints(Point2D p1,Point2D p2) {
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p2.getX();
		double y2 = p2.getY();
		double deltaX = x2 - x1;
		double deltaY = y2 - y1;
		double rad = Math.atan2(deltaY,deltaX);
		double deg = rad * (180/Math.PI);
		return deg;
	}
    
    private void updateArrowHead() {
    	
    	Point2D end = getEndPoint();
    	double ex = end.getX();
        double ey = end.getY();
        
        Point2D start = getStartPoint();
        double sx = start.getX();
        double sy = start.getY();
        
        Point2D midpoint = start.midpoint(end);

        arrow1.setEndX(ex);
        arrow1.setEndY(ey);
        arrow2.setEndX(ex);
        arrow2.setEndY(ey);


        if (ex == sx && ey == sy) {
            // arrow parts of length 0
            arrow1.setStartX(ex);
            arrow1.setStartY(ey);
            arrow2.setStartX(ex);
            arrow2.setStartY(ey);
        } else {
            double factor = arrowLength / Math.hypot(sx-ex, sy-ey);
            double factorO = arrowWidth / Math.hypot(sx-ex, sy-ey);

            // part in direction of main line
            double dx = (sx - ex) * factor;
            double dy = (sy - ey) * factor;

            // part ortogonal to main line
            double ox = (sx - ex) * factorO;
            double oy = (sy - ey) * factorO;

            arrow1.setStartX(ex + dx - oy);
            arrow1.setStartY(ey + dy + ox);
            arrow2.setStartX(ex + dx + oy);
            arrow2.setStartY(ey + dy - ox);
            
            
            
            this.label.setLayoutX(midpoint.getX());
        	this.label.setLayoutY(midpoint.getY());
        	//this.label.setText("Labelx̅");
        	/*String str = "Labelx̅";
        	char[] ch = str.toCharArray();
        	for(int i = 0; i<ch.length;i++) {
        		if(i==ch.length-1)
        		System.out.println((int)ch[i]);
        		else System.out.println(ch[i]);
        	}*/
        }
        
    }
  

    public void update() {
    	
    	Point2D sourcePos = new Point2D(source.getLayoutX(),source.getLayoutY());
    	
    	Point2D start = source.getCenter();
    	
    	Point2D end = getEndPoint();
    	if(destination != null) {
    		end = destination.getCenter();
    	}
    	
    	// Find intersection
    	Point2D actualStart = source.getIntersectionPoint(start, end);
    	if(actualStart == null)
    		actualStart = start;
    	
    	this.setStartPoint(actualStart.getX(), actualStart.getY());
    	
    	Point2D actualEnd = end;
    	if( destination != null ) {
    		actualEnd = destination.getIntersectionPoint(start, end);
    		if(actualEnd == null)
    			actualEnd = end;
    	}
    	
    	this.setEndPoint(actualEnd.getX(), actualEnd.getY());
    	
    	
    	
    	// Update the arrow head
    	updateArrowHead();
    }
    

    public void setSource(MovableNode newSource) {
    	source = newSource;
    }
    public void setDestination(MovableNode newDestination) {
    	destination = newDestination;
    }
    
    public MovableNode getSource() {
    	return source;
    }
    public MovableNode getDestination() {
    	return destination;
    }
    
    
    public void disableMouseTransparent() {
    	this.arrowBody.setMouseTransparent(false);
    	this.arrow1.setMouseTransparent(false);
    	this.arrow2.setMouseTransparent(false);
    	this.invisibleLine.setMouseTransparent(false);
    	setMouseTransparent(false);
    }
    
    protected void onDoubleClick() {
    	arcLabel.showPopup();
		
	}
    
    protected void onLeftClick() {
		System.out.println("Left Mouse Click arrow!");
	}
    
    protected void removeHightlight() {
    	line.getStyleClass().remove("selectedArrow");
        arrow1.getStyleClass().remove("selectedArrow");
        arrow2.getStyleClass().remove("selectedArrow");
	}
	
	protected void setHightlight() {
		line.getStyleClass().add("selectedArrow");
        arrow1.getStyleClass().add("selectedArrow");
        arrow2.getStyleClass().add("selectedArrow");
	}
	

	@Override
	public List<Pair<String,Object>> getDataList(){
		ArrayList<Pair<String,Object>> data = new ArrayList<Pair<String,Object>>();
		
		
		data.add( new Pair<String, Object>("source", this.source.name ));
		data.add( new Pair<String, Object>("destination", this.destination.name ));
		
		ArrayList< Pair<String,Object> > labelList = new ArrayList<Pair<String,Object>>();
		
		
		ArrayList< Pair<String,String> > tokenList = new ArrayList<Pair<String,String>>();
		ArrayList<Token> tokens = getTokens(false);
		for( Token token : tokens )
			tokenList.add( new Pair<String,String>("token",token.name));
		labelList.add( new Pair<String,Object>("tokens",tokenList));
		
		ArrayList< Pair<String,String> > negTokenList = new ArrayList<Pair<String,String>>();
		ArrayList<Token> negTokens = getTokens(true);
		for( Token token : negTokens )
			negTokenList.add( new Pair<String,String>("token",token.name));
		labelList.add( new Pair<String,Object>("negTokens",negTokenList));
		
		
		ArrayList< Pair<String,Object> > bondList = new ArrayList<Pair<String,Object>>();
		ArrayList<Pair<Token,Token>> bonds = getBonds(false);
		for( Pair<Token,Token> bond : bonds ) {
			ArrayList< Pair<String,String> > tempList = new ArrayList<Pair<String,String>>();
			tempList.add( new Pair<String,String>("token",bond.getKey().name));
			tempList.add( new Pair<String,String>("token",bond.getValue().name));	
			
			bondList.add( new Pair<String, Object>("bond", tempList));			
		}
		labelList.add( new Pair<String,Object>("bonds",bondList));
		
		
		ArrayList< Pair<String,Object> > negBondList = new ArrayList<Pair<String,Object>>();
		ArrayList<Pair<Token,Token>> negBonds = getBonds(true);
		for( Pair<Token,Token> bond : negBonds ) {
			ArrayList< Pair<String,String> > tempList = new ArrayList<Pair<String,String>>();
			tempList.add( new Pair<String,String>("token",bond.getKey().name));
			tempList.add( new Pair<String,String>("token",bond.getValue().name));	
			
			negBondList.add( new Pair<String, Object>("bond", tempList));			
		}
		labelList.add( new Pair<String,Object>("negBonds",negBondList));
		
		data.add( new Pair<String,Object>("label",labelList));
		
		return data;
	}
	
	
	@Override
	public void delete() {
		source.arrows.remove(this);
		destination.arrows.remove(this);
		
		// Remove UI from scroll area
		rpn.scrollArea.removeNode( getGroup() );
		
		// Remove from rpn list
		rpn.delete(this);
	}

	/*
	public ArrayList<Token> getTokens() {
		ArrayList<Token> temp = new ArrayList<Token>();
		temp.addAll(tokens);
		return temp;
	}

	public ArrayList<Token> getNegTokens() {
		ArrayList<Token> temp = new ArrayList<Token>();
		temp.addAll(negTokens);
		return negTokens;
	}

	public ArrayList<Pair<Token, Token>> getBonds() {
		ArrayList<Pair<Token, Token>> temp = new ArrayList<Pair<Token,Token>>();
		temp.addAll(bonds);
		return bonds;
	}

	public ArrayList<Pair<Token, Token>> getNegBonds() {
		ArrayList<Pair<Token, Token>> temp = new ArrayList<Pair<Token,Token>>();
		temp.addAll(negBonds);
		return negBonds;
	}
	*/
	
}
