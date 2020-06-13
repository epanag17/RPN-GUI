package rpnsim.application.simulator;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import rpnsim.application.rpn.RPN;

public class SimulatorProperties {
	
	private ComboBox<String> reversibilityMode;
	private RPN rpn;
	
	public SimulatorProperties(ComboBox<String> reversibilityMode, RPN rpn) {
		reversibilityMode.setItems(FXCollections.observableArrayList(
			    new String("Backtracking"),
			    new String("Causal reversing"), 
			    new String("Out-of-causal-order reversing")));
		this.reversibilityMode = reversibilityMode;
		this.rpn = rpn;
		
	}
	
	public ReverseExecution getReverseMethod(ListView<String> reverseList) {
		if(reversibilityMode.getValue()!=null) {
			System.out.println("reversibility Mode: "+reversibilityMode.getValue());
			String selectedMethod = reversibilityMode.getValue();
			if(selectedMethod.equals("Backtracking"))
				return new Backtracking(rpn,reverseList);
			if(selectedMethod.equals("Causal reversing"))
				return new CausalReversal(rpn,reverseList);
			if(selectedMethod.equals("Out-of-causal-order reversing"))
				return new OutOfCausalOrder(rpn,reverseList);
		}
		
		
		return null;
	}

}
