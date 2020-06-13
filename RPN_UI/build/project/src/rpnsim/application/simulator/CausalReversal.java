package rpnsim.application.simulator;

import java.util.ArrayList;
import java.util.Set;

import javafx.scene.control.ListView;
import rpnsim.application.rpn.RPN;
import rpnsim.application.rpn.Transition;

public class CausalReversal extends ReverseExecution {

	public CausalReversal(RPN rpn, ListView<String> reverseList) {
		super(rpn, reverseList);
	}
	
	public Set<Transition> getEnabledTransitions(){
		enabledTransitions.clear();
		ArrayList<Transition> transitions = rpn.getTransitions();
		
		for(Transition transition:transitions) {
			boolean is_co_enabled = true;
			if(!transition.getHistoryList().isEmpty()) {
				for(Transition other:transitions) {
					if(transition!=other) {
						if(isCausallyDependent(transition, other)) {
							is_co_enabled = false;
							break;
						}
					}
				}
				if(is_co_enabled) {
					enabledTransitions.add(transition);
					System.out.println(transition.getName()+" is co-enabled");
				}
				else
					System.out.println(transition.getName()+" isn't co-enabled");
					
			}
		}
		
		return enabledTransitions;
	}
	
	public void fireTransition(Transition t) {
		super.fireTransition(t);
		
		int index = t.getHistoryList().size()-1;
		int k = t.getHistoryList().get(index);
		t.getHistoryList().remove(index);
		t.updateHistory();
		for(Transition other:rpn.getTransitions()) {
			if(t != other) {
				ArrayList<Integer> otherHistory = other.getHistoryList();
				if(!(otherHistory.isEmpty())) {
					index = other.getHistoryList().size()-1;
					int otherK = other.getHistoryList().get(index);
					if(otherK > k)
						otherHistory.set(index, otherK-1);
				}
				other.updateHistory();
			}
		}
		
		updateReverseList();
	}

}
