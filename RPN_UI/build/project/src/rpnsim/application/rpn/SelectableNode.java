package rpnsim.application.rpn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import rpnsim.application.editor.EditorArea;

public class SelectableNode extends AnchorPane {

	public static SelectableNode currentSelected;
	protected SelectableNode self;

	protected RPN rpn;

	public SelectableNode(RPN rpn) {
		super();

		this.rpn = rpn;

		// onMousePressed();
		this.setOnMouseClicked(mouseClicked);
		self = this;
	}

	public RPN getRPN() {
		return rpn;
	}
	
	protected void removeHightlight() {
		getStyleClass().remove("selectedNode");
	}

	protected void setHightlight() {
		getStyleClass().add("selectedNode");
	}

	public void removeUI() {

	}

	public void delete() {
		System.out.println("Delete Node (at SelectableNode)");

		// Remove from RPN list
		// rpn.delete(this);

		// Remove as UI component

	}

	protected void onLeftClick() {
		System.out.println("Left Mouse Click");
	}

	protected void onRightClick() {
		System.out.println("Right Mouse Click");
	}

	protected void onDoubleClick() {
		System.out.println("Double Mouse Click");

	}

	/*
	 * public SelectableNode onMousePressed() { this.setOnMousePressed(new
	 * EventHandler<MouseEvent>() {
	 * 
	 * @Override public void handle(final MouseEvent event) {
	 * 
	 * 
	 * 
	 * if(currentSelected!=null) { currentSelected.removeHightlight();
	 * currentSelected.isSelected = false; } else
	 * System.out.println("not current selected"); self.setHightlight();
	 * currentSelected = self;
	 * 
	 * isSelected= true; //System.out.println(deleteKeyPressed);
	 * 
	 * event.consume(); } });
	 * 
	 * 
	 * 
	 * return currentSelected;
	 * 
	 * }
	 */

	public List<Pair<String, Object>> getDataList() {
		ArrayList<Pair<String, Object>> data = new ArrayList<Pair<String, Object>>();
		return data;
	}

	protected EventHandler<MouseEvent> mouseClicked = new EventHandler<MouseEvent>() {
		@Override
		public void handle(final MouseEvent event) {

			/*
			 * if( event.getButton() == MouseButton.SECONDARY ) onRightClick();
			 */
			if (event.getClickCount() == 2)
				onDoubleClick();
			
			System.out.println(event.getButton());
			if (event.getButton() == MouseButton.PRIMARY) {

				onLeftClick();

				if ((rpn.scrollArea instanceof EditorArea)) {

					EditorArea editor = (EditorArea) rpn.scrollArea;
					String tool = editor.getSelectedTool();

					System.out.println(tool);
					if (tool.equals("SELECT")) {
						if (currentSelected != null)
							currentSelected.removeHightlight();
						self.setHightlight();
						currentSelected = self;
					}
				}

			}

			event.consume();
		}
	};

}
