package rpnsim.application.simulator;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import rpnsim.application.model.RPN;

public class ReversibilityMode {
	
	private ComboBox<String> reversibilityMode;
	private RPN rpn;
	
	public ReversibilityMode(ComboBox<String> reversibilityMode) {
		reversibilityMode.setItems(FXCollections.observableArrayList(
				"None",
			    "Backtracking",
			    "Causal reversing", 
			    "Out-of-causal-order reversing"));
		reversibilityMode.getSelectionModel().select("None");
		this.reversibilityMode = reversibilityMode;
		
	}
	
	public void setRPN(RPN rpn) {
		this.rpn = rpn;
	}
	
	public ReverseExecution getReverseMethod() {
		if(reversibilityMode.getValue()!=null) {
			System.out.println("reversibility Mode: "+reversibilityMode.getValue());
			String selectedMethod = reversibilityMode.getValue();
			if(selectedMethod.equals("Backtracking"))
				return new Backtracking(rpn);
			if(selectedMethod.equals("Causal reversing"))
				return new CausalReversal(rpn);
			if(selectedMethod.equals("Out-of-causal-order reversing"))
				return new OutOfCausalOrder(rpn);
		}
		
		
		return null;
	}

}
