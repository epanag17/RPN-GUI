package rpnsim.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rpnsim.application.editor.EditorArea;
import rpnsim.application.editor.EditorToolbox;
import rpnsim.application.editor.EditorView;
import rpnsim.application.rpn.RPN;
import rpnsim.application.rpn.SelectableNode;
import rpnsim.application.rpn.Transition;
import rpnsim.application.simulator.CausalReversal;
import rpnsim.application.simulator.ForwardExecution;
import rpnsim.application.simulator.RPNVerifier;
import rpnsim.application.simulator.ReverseExecution;
import rpnsim.application.simulator.SimulatorArea;
import rpnsim.application.simulator.SimulatorProperties;

public class RootLayout extends VBox {

	private EditorView editorView;
	private EditorToolbox editorToolbox;
	private EditorArea editorRPNViewer;

	@FXML
	private Tab fx_editorTab;
	@FXML
	private VBox fx_editorButtons;
	@FXML
	private AnchorPane fx_editorScrollArea, fx_simulatorScrollArea;
	@FXML
	private MenuItem saveMenu, openMenu, verifyMenu;
	@FXML
	private MenuItem newMenu, saveAsMenu, quitMenu;
	@FXML
	private Menu recentMenu;

	@FXML
	private TabPane tabPane;
	@FXML
	private Tab simulatorTab;
	@FXML
	private ComboBox<String> reversibilityMode;
	@FXML
	private ListView<String> forwardList, reverseList;
	@FXML
	private Button forwardRunButton, reverseRunButton;

	// metritis gia to history
	int count = 1;
	ReverseExecution reverse;

	public RootLayout(Stage primaryStage) {

		// Load UI with FXML (Scene Builder)
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui.fxml"));

		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();

		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		editorView = new EditorView(fx_editorTab);
		// System.out.println(editorButtons);
		editorToolbox = new EditorToolbox(fx_editorButtons);
		editorRPNViewer = new EditorArea(fx_editorScrollArea, editorToolbox);
		RPN rpn = editorRPNViewer.getRPN();

		// SelectableNode node = new SelectableNode();
		this.setOnKeyPressed(editorRPNViewer.keyClicked);
		XMLManager xmlManager = new XMLManager(primaryStage, rpn, editorRPNViewer, recentMenu);

		saveMenu.setOnAction(xmlManager.saveAction);
		saveAsMenu.setOnAction(xmlManager.saveAsAction);

		openMenu.setOnAction(xmlManager.openAction);
		newMenu.setOnAction(xmlManager.newAction);

		KeyCombination shortcutSave = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
		KeyCombination shortcutNew = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
		KeyCombination shortcutOpen = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);

		this.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
			if (shortcutSave.match(event))
				saveMenu.fire();
			if (shortcutNew.match(event))
				newMenu.fire();
			if (shortcutOpen.match(event))
				openMenu.fire();

		});

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				System.out.println("Stage is closing");

				if (xmlManager.getScrollArea().changed)
					if (!xmlManager.saveConfirmation("Closing Application",
							"Do you wish to save changes of your RPN before closing?"))
						we.consume();

			}
		});

		quitMenu.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent we) {
				System.out.println("Stage is closing");

				if (xmlManager.getScrollArea().changed)
					if (!xmlManager.saveConfirmation("Closing Application",
							"Do you wish to save changes of your RPN before closing?"))
						return;
				
				primaryStage.close();

			}
		});

		// verify
		// RPNVerifier rpnVerifier = new RPNVerifier(rpn);
		// verifyMenu.setOnAction(rpnVerifier.clicked);
		RPNVerifier.setRPN(rpn);
		verifyMenu.setOnAction(RPNVerifier.clicked);

		SimulatorArea simulatorArea = new SimulatorArea(fx_simulatorScrollArea);

		simulatorTab.setOnSelectionChanged(event -> {
			if (simulatorTab.isSelected()) {
				System.out.println("Simulator Tab is Selected");
				if (/* rpnVerifier.verify() */ RPNVerifier.verify()) {
					System.out.println("The rpn is well-formed!");
					File file = new File("test.xml");
					xmlManager.create(file);
					RPN simulatorRPN = xmlManager.openFile(file);
					simulatorArea.loadRPN(simulatorRPN);
					simulatorArea.updateArrowsBonds();

					ForwardExecution forward = new ForwardExecution(simulatorRPN, forwardList);
					SimulatorProperties simulatorProperties = new SimulatorProperties(reversibilityMode, simulatorRPN);

					reversibilityMode.getSelectionModel().clearSelection();
					reverseList.getItems().clear();

					reversibilityMode.getSelectionModel().selectedItemProperty()
							.addListener((options, oldValue, newValue) -> {
								reverse = simulatorProperties.getReverseMethod(reverseList);
							});

					count = 1;
					forwardRunButton.setOnAction(event2 -> {
						String selectedTransitionName = forwardList.getSelectionModel().getSelectedItem();
						Transition selectedTransition = null;
						for (Transition t : simulatorRPN.getTransitions()) {
							if (t.getName().equals(selectedTransitionName)) {
								selectedTransition = t;
								break;
							}
						}
						if (selectedTransition != null) {
							forward.fireTransition(selectedTransition, count);
							simulatorArea.updateArrowsBonds();
							count++;
							if (reverse != null)
								reverse.updateReverseList();
						}
					});

					reverseRunButton.setOnAction(event3 -> {

						/*
						 * CausalReversal test = new CausalReversal(simulatorRPN, reverseList); for(
						 * Transition t1 : simulatorRPN.getTransitions() ) { for( Transition t2 :
						 * simulatorRPN.getTransitions() ) { if(t1==t2) continue;
						 * 
						 * boolean causalDependence = test.isCausallyDependent(t1, t2);
						 * if(causalDependence)
						 * System.out.println(t1.getName()+" causally dependent of "+t2.getName()); } }
						 */

						String selectedTransitionName = reverseList.getSelectionModel().getSelectedItem();
						Transition selectedTransition = null;
						for (Transition t : simulatorRPN.getTransitions()) {
							if (t.getName().equals(selectedTransitionName)) {
								selectedTransition = t;
								break;
							}
						}
						if (selectedTransition != null) {
							reverse.fireTransition(selectedTransition);
							count--;
							forward.updateForwardList();
						}

						/*
						 * if(reverse!=null) { System.out.println("Reverse transition: ");
						 * Set<Transition> enabledTransitions = reverse.getEnabledTransitions();
						 * 
						 * }
						 */

					});
				} else {
					System.out.println("The rpn isn't well-formed..");
					// switch to editor tab
					tabPane.getSelectionModel().select(0);
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Failed Verification");
					alert.setContentText(
							"Please, check the rpn before proceed into simulation tab. The rpn should be well-formed");
					alert.showAndWait();
				}
			}
		});

	}

}
