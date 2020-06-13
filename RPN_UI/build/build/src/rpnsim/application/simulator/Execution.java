package rpnsim.application.simulator;

import java.util.HashSet;
import java.util.Set;

import rpnsim.application.rpn.RPN;
import rpnsim.application.rpn.Transition;

public class Execution {
	
	protected RPN rpn;
	protected Set<Transition> enabledTransitions; 
	
	public Execution(RPN rpn) {
		this.rpn = rpn;
		enabledTransitions = new HashSet<>();
	}
	

}
