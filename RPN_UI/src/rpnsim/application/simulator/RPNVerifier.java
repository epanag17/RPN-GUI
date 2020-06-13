package rpnsim.application.simulator;

import java.util.ArrayList;
import java.util.Set;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import rpnsim.application.model.LabelItem;
import rpnsim.application.model.RPN;
import rpnsim.application.model.Token;
import rpnsim.application.model.Transition;


/*
import rpnsim.application.rpn.Arrow;
import rpnsim.application.rpn.LabelItem;
import rpnsim.application.rpn.Place;
import rpnsim.application.rpn.RPN;
import rpnsim.application.rpn.Token;
import rpnsim.application.rpn.Transition;
*/

public class RPNVerifier {
	
	static RPN rpn;
	
	public RPNVerifier(RPN rpn) {
		this.rpn = rpn;
	}
	
	public static void setRPN(RPN rpn) {
		RPNVerifier.rpn = rpn;
	}
	
	public static RPN getRPN() {
		return rpn;
	}
	
	public static EventHandler<ActionEvent> clicked = new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(final ActionEvent event) {
	    	Alert alert = new Alert(Alert.AlertType.ERROR);
	        alert.setTitle("Confirmation message");
	        try {
	        	if(RPNVerifier.verify()) {
		    		alert.setHeaderText("Successful Validation!");
		    		FontAwesomeIconView iconSuccess = new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE);
		    		iconSuccess.setGlyphSize(35);
		    		iconSuccess.setFill(Color.LAWNGREEN);
		    		alert.getDialogPane().setGraphic(iconSuccess);
		            alert.show();
		    		//System.out.println("the rpn is valid!!!");
		    	}
	        }catch (IllegalArgumentException ex) {
	        	alert.setHeaderText("Parsing Error");
	    		System.out.println("The RPN is not well-formed.");
	    		System.out.println(ex.getMessage());
	        	alert.setContentText("The RPN is not well-formed. "+ex.getMessage());
	        	alert.show();
	        }
	    	
	    	event.consume();
	    }
	};
	
	
	public static boolean verify() {
		
		
		Transition[] transitions = rpn.getTransitions();
		RPNUtils u = new RPNUtils( RPNVerifier.rpn );
		
		for( Transition transition : transitions ) {
			Set<LabelItem> pret = u.pre(transition);
			Set<LabelItem> postt = u.post(transition);
			
			
			// Definition 2.1
			{
				Set<Token> tokensLeft = u.getTokensFrom(pret);
				Set<Token> tokensRight = u.getTokensFrom(postt);
				if( !u.compare(tokensLeft, tokensRight) ) {
					throw new IllegalArgumentException("Error in transition "+transition.getName());
					//System.out.println("Definition 2.1 failed at transition "+transition.getName());
					//return false;
				}
			}
			
			// Definition 2.2
			{
				Set<LabelItem> leftBonds = u.getBondsFrom(pret);
				if( !u.subsetOf(leftBonds, postt) ) {
					throw new IllegalArgumentException("Error in transition "+transition.getName());
					//System.out.println("Definition 2.2 failed at transition "+transition.getName());
					//return false;
				}
			}
			
			// Definition 2.3
			{
				ArrayList<Set<LabelItem>> outLabels = u.outLabels(transition);
				for( int i=0; i<outLabels.size(); i++ ) {
					for( int j=i+1; j<outLabels.size(); j++ ) {
						
						Set<LabelItem> setA = outLabels.get(i);
						Set<LabelItem> setB = outLabels.get(j);
						
						Set<LabelItem> intersection = u.intersection(setA, setB);
						
						if(!intersection.isEmpty()) {
							throw new IllegalArgumentException("Error in transition "+transition.getName());
							//System.out.println("Definition 2.3 failed at transition "+transition.getName());
							//return false;
						}
						
					}
				}
			}
		}
		
		
		return true;
	}
	

}
