package rpnsim.application.simulator;

import java.util.ArrayList;
import java.util.Set;

import javafx.scene.control.ListView;
import javafx.util.Pair;
import rpnsim.application.rpn.Arrow;
import rpnsim.application.rpn.LabelItem;
import rpnsim.application.rpn.Place;
import rpnsim.application.rpn.RPN;
import rpnsim.application.rpn.Transition;
import rpnsim.application.rpn.Token;
import rpnsim.application.rpn.Bond;

public class Backtracking extends ReverseExecution {

	public Backtracking(RPN rpn, ListView<String> reverseList) {
		super(rpn,reverseList);
	}
	
	/**
	 * Epistrefei to transition (an yparxei) to opoio mporoei na pyrodoti8ei me backtracking
	 */
	public Set<Transition> getEnabledTransitions(){
		enabledTransitions.clear();
		Transition t = null;
		
		// blepw poio transition ektelestike teleytaio kai to topothetw stin lista me ta
		// enabledTransitions. To teleytaio transition pou ektelestike exei tin e3is idiotita:
		// o teleytaios arithmos pou exei stin history list tou einai o megalyteros akeraios arithmos
		// pou yparxei apo ola ta transitions history lists.
		int max_value = 0;
		ArrayList<Transition> transitions = rpn.getTransitions();
		for(Transition transition: transitions) {
			ArrayList<Integer> transitionHistory = transition.getHistoryList();
			if(!transitionHistory.isEmpty()) {
				int last_item = transitionHistory.get(transitionHistory.size()-1);
				if(last_item > max_value) {
					max_value = last_item;
					t = transition;
				}
			}
			
		}
		
		if(t!=null)
			enabledTransitions.add(t);
		
		return enabledTransitions;
	}
	
	/**
	 * Pyrwdwtei to transition me backtracking
	 */
	public void fireTransition(Transition t) {;
		super.fireTransition(t);
		
		int index = t.getHistoryList().size()-1;
		t.getHistoryList().remove(index);
		t.updateHistory();
		updateReverseList();
		
	}

}
