package rpnsim.application.simulator;

import java.util.HashSet;
import java.util.Set;

import rpnsim.application.model.RPN;
import rpnsim.application.model.Transition;


public class Execution {
	
	protected RPN rpn;
	protected Set<Transition> enabledTransitions; 
	
	public Execution(RPN rpn) {
		this.rpn = rpn;
		enabledTransitions = new HashSet<>();
	}
	
	public void setRPN(RPN rpn) {
		this.rpn = rpn;
	}
	

}
