package rpnsim.application.rpn;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.util.Pair;
import rpnsim.application.editor.EditorArea;

public class Bond extends SelectableNode {
	
	private Group group;
	private Arc arc;
	
	private Token A, B;

	private Point2D endPoint;
	
	public Bond(RPN rpn) {
		super(rpn);
		
		endPoint = new Point2D(0,0);

		arc = new Arc();
		//arc.setMouseTransparent(true);
		arc.setStroke(Color.BLACK);
		arc.setStrokeWidth(2);
		//arc.setFill(Color.TRANSPARENT);
		
		
		arc.setType(ArcType.OPEN);
		arc.getStyleClass().add("bond");
		arc.setOnMouseClicked(mouseClicked);
		
		group = new Group(arc);
	}
	
	public Arc getArc() {
		return arc;
	}
	
	public void setSource(Token A) {
		this.A = A;
	}
	public Token getSource() {
		return A;
	}
	
	public void setDestination(Token B) {
		this.B = B;
	}
	public Token getDestination() {
		return B;
	}
	
	
	public void setEndPoint(double x, double y) {
		this.endPoint = new Point2D(x,y);
	}
	
	
	public void update() {
		
		if(A == null)
			return;
		
		Point2D start = A.getCenter();
		//Point2D start = new Point2D( A.getLayoutX() , A.getLayoutY() );
		double indexA = A.getIndex();
		
		Point2D end = new Point2D(start.getX(), endPoint.getY());
		double indexB = 0;
		double distance = Math.abs(end.getY() - start.getY()) / 18.0;
		
		if( B != null) {
			
			indexB = B.getIndex();
			distance = Math.abs(indexB-indexA);
			//end = new Point2D( B.getLayoutX(), B.getLayoutY() );
			end = B.getCenter();
		}
		
		Point2D center = start.midpoint(end); 
		update( center.getX(), center.getY(), distance);
	
		
		System.out.println("Bond "+this+", Pos: "+center+", Distance: "+distance);
		
		/*EditorArea.singleton.dbgCircleA.relocate(start.getX(), start.getY());

		EditorArea.singleton.dbgCircleB.relocate(end.getX(), end.getY());

		EditorArea.singleton.dbgCircleC.relocate(center.getX(), center.getY());*/
	}
	
	@Override
	public String toString() {
		String str = "(";
		if(A!=null)
			str+=A.name;
		str+=" - ";
		if(B!=null)
			str+=B.name;
		str += ")";
		return str;
	}
	
	
	private void update(double centerX, double centerY, double distance) {
		//Setting the properties of the arc 
		double factor = 0.093*distance;
		
		arc.setCenterX(centerX); 
		arc.setCenterY(centerY); 
		arc.setRadiusX(100.0f*factor); 
		arc.setRadiusY(100.0f*factor); 
		arc.setStartAngle(90.0f); 
		arc.setLength(180.0f); 
	}
	
	public Group getGroup() {
    	return group;
    }
	
	protected void onLeftClick() {
		super.onLeftClick();
		System.out.println("SELECTED BOND ON LEFT CLICK");
		
		if(!(rpn.scrollArea instanceof EditorArea)) {
			return;
		}
		
		EditorArea editor = (EditorArea)rpn.scrollArea;
		String tool=editor.getSelectedTool();
		if(tool.equals("SELECT"))
			setHightlight();
	}
	

	@Override
	public List<Pair<String,Object>> getDataList(){
		ArrayList<Pair<String,Object>> data = new ArrayList<Pair<String,Object>>();
		
		ArrayList< Pair<String,String> > tokenList = new ArrayList<Pair<String,String>>(); 
		tokenList.add( new Pair<String,String>("token",A.name ) );
		tokenList.add( new Pair<String,String>("token",B.name ) );
		
		data.add( new Pair<String,Object>("bond",tokenList));
		return data;
	}
	
	
	@Override
	protected void removeHightlight() {
		//arc.getStyleClass().remove("selectedBond");
		arc.setStroke(Color.rgb(88, 112, 203));
	}
	
	@Override
	protected void setHightlight() {
		//arc.getStyleClass().add("selectedBond");
		arc.setStroke(Color.rgb(88, 112, 203));
	}
	
	@Override
	public void delete() {
		A.removeBond(this);
	}
	

}
