package rpnsim.application.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Transition extends Node {


	public Transition(RPN rpn, String name) {
		super(rpn,name);
		
		
	}

	public ArrayList<Integer> getHistoryList(){
		return rpn.marking.history.get(this);
	}
	
	
	@Override
	public boolean setName(String newName) {
		
		String oldName = this.name;
		
		if(!super.setName(newName))
			return false;
		
		// Fix hashmap
		rpn.transitions.remove(oldName);
		rpn.transitions.put(newName, this);
		
		return true;
	}
	
	
	
}
