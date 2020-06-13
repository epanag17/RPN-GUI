package rpnsim.application.rpn;

import java.util.ArrayList;


import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import rpnsim.application.ScrollArea;
import rpnsim.application.editor.EditorArea;

public class MovableNode extends EditableNode{

	private EventHandler  mContextDragOver;
    private EventHandler  mContextDragDropped;
	private Point2D mDragOffset = new Point2D(0.0, 0.0);
	
	private final MovableNode self;
	
	protected DataFormat dataformat;
	protected Point2D position;
	
	public MovableNode(RPN rpn,DataFormat dataformat) {
		super(rpn);
		// TODO Auto-generated constructor stub
		self = this;

		this.dataformat = dataformat;
		//buildNodeDragHandlers();
	}

	protected ArrayList<Arrow> arrows = new ArrayList<Arrow>();
	
	
	public Point2D getCenter() {
		double x = getLayoutX() + (getBoundsInLocal().getWidth() / 2);
		double y = getLayoutY() + (getBoundsInLocal().getHeight() / 2);
		Point2D center = new Point2D(x,y);
		return center;
	}
	
	public Point2D getIntersectionPoint(Point2D p3, Point2D p4) {
		return null;
	}
	
	protected void initArrow( EditorArea editor ) {
		Arrow arrow = new Arrow(rpn);
		
		arrow.setSource(this);
		arrows.add(arrow);
		
		Point2D center = this.getCenter();
		arrow.setEndPoint(center.getX() , center.getY());
		arrow.update();
		
		editor.addNode(arrow.getGroup());
		editor.setCurrentArrow(arrow);
	}
	
	public ArrayList<Arrow> getArrows() {
		return arrows;
	}

	@Override
	protected void onLeftClick() {
		System.out.println("Left Clicked a Movable Node");
		
		
		if( !(rpn.scrollArea instanceof EditorArea) ) {
			return;
		}
		
		EditorArea editor = (EditorArea)rpn.scrollArea;
		String tool = editor.getSelectedTool();
		
		switch(tool) {
		
		case "SELECT":
			buildNodeDragHandlers();
			break;
		
		case "ARROW": 
			
			Arrow currentArrow = editor.getCurrentArrow();
			if( currentArrow == null ) {
				initArrow(editor);
				
			}
			else {
				
				MovableNode source = currentArrow.source;
				
				if( source == this ) {
					System.out.println("Cannot create arrow with self");
					return;
				}
				
				if( source instanceof Place && this instanceof Place ) {
					System.out.println("Cannot link place with place");
					return;
				}
				if( source instanceof Transition && this instanceof Transition ) {
					System.out.println("Cannot link transition with transition");
					return;
				}
				
				currentArrow.setDestination(this);
				currentArrow.update();
				
				rpn.arrows.add(currentArrow);
				arrows.add(currentArrow);
				editor.setCurrentArrow(null);
				
				currentArrow.disableMouseTransparent();
			
				editor.SomethingChanged();
				
			}
			
			break;
		}
		
	}
	
	protected void deleteAllArrows() {
		
		Arrow[] arrowArray = new Arrow[arrows.size()];
		arrows.toArray(arrowArray);
		for(int i=0; i<arrowArray.length; i++) {
			Arrow arrow = arrowArray[i];
			arrow.delete();
		}
	}
	
	
	protected void onDragOver() {
		
	}
	
public void buildNodeDragHandlers() {
		
	    self.setOnDragDetected ( new EventHandler <MouseEvent> () {

	        @Override
	        public void handle(MouseEvent event) {
	                
	            getParent().setOnDragOver(null);
	            getParent().setOnDragDropped(null);

	            getParent().setOnDragOver (mContextDragOver);
	            getParent().setOnDragDropped (mContextDragDropped);

	        //begin drag ops
	            mDragOffset = new Point2D(event.getX(), event.getY());
	                    
	            relocateToPoint (new Point2D(event.getSceneX(), event.getSceneY()));
	            //relocateToPoint (mDragOffset);
	                    
	        ClipboardContent content = new ClipboardContent();
	            DragContainer container = new DragContainer();
	                    
	           // container.addData ("type", mType);
	            content.put(dataformat, container);
	                    
	        startDragAndDrop (TransferMode.ANY).setContent(content);                  
	                    
	        
	        	
	            event.consume();                    
	        }
	                
	    });  
	    
	    mContextDragOver = new EventHandler <DragEvent> () {

	        //dragover to handle node dragging in the right pane view
	        @Override
	        public void handle(DragEvent event) {       

	            event.acceptTransferModes(TransferMode.ANY);                
	            relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));
	            //relocateToPoint(new Point2D( event.getX(), event.getY()));

	            for(Arrow arrow : arrows) {
	            	arrow.update();
	            }
	            
	            onDragOver();
	            
	            event.consume();
	        }
	    };
	    
	    
	  //dragdrop for node dragging
	    mContextDragDropped = new EventHandler <DragEvent> () {
	            
	        @Override
	        public void handle(DragEvent event) {

	        getParent().setOnDragOver(null);
	        getParent().setOnDragDropped(null);
	                        
	        event.setDropCompleted(true);
	        
	        self.setOnDragDetected(null);
	             
	        
	        for(Arrow arrow : arrows) {
            	arrow.update();
            }
	        
	        event.consume();
	        
	        ScrollArea scrollArea = rpn.scrollArea;
	        if(scrollArea instanceof EditorArea) {
	        	scrollArea.SomethingChanged();
	        }
	        
	        }
	    };
	    
//	}
	    
	    
	    
	}
	
	public void relocateToPoint (Point2D p) {

		//relocates the object to a point that has been converted to
		//scene coordinates
		Point2D localCoords = getParent().sceneToLocal(p);
		
		Point2D newPos = new Point2D(
				(localCoords.getX() - (getBoundsInLocal().getWidth() /2)),
				(localCoords.getY() - (getBoundsInLocal().getHeight() /2)) );
		
		setPosition(newPos);
	}

	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D newPos) {
		this.position = newPos;
		relocate ( (int) newPos.getX(), (int) newPos.getY() );
	}
	
	
}
