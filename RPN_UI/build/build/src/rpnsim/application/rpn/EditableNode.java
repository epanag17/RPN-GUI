package rpnsim.application.rpn;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EditableNode extends SelectableNode {
	
	 protected Label label;
	 protected String name;

	public EditableNode(RPN rpn) {
		super(rpn);
		// TODO Auto-generated constructor stub
	}
	
	public void rename(String newName) {
		this.name = newName;
		label.setText(newName);
		
	}

	public String getName() {
		return name;
	}

}
