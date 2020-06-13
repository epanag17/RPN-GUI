package rpnsim.application.ui;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import rpnsim.application.model.RPN;
import rpnsim.application.simulator.RPNVerifier;

public class EditableNode extends SelectableNode {

	protected Label label;
	protected String name;

	public EditableNode(RPN rpn) {
		super(rpn);
		// TODO Auto-generated constructor stub
	}

	public void editErrorPopup(String reason) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Failed to edit node");
		alert.setHeaderText(reason);
		alert.show();
	}

	public void rename(String newName) {
		this.name = newName;
		label.setText(newName);

	}

	public String getName() {
		return name;
	}

}
