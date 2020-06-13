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
import rpnsim.application.model.RPN;
import rpnsim.application.properties.PropertiesArea;
import rpnsim.application.properties.Reachability;
import rpnsim.application.simulator.CausalReversal;
import rpnsim.application.simulator.ForwardExecution;
import rpnsim.application.simulator.RPNVerifier;
import rpnsim.application.simulator.ReverseExecution;
import rpnsim.application.simulator.SimulatorArea;
import rpnsim.application.simulator.SimulatorComponent;
import rpnsim.application.simulator.ReversibilityMode;
import rpnsim.application.ui.SelectableNode;
import rpnsim.application.ui.TransitionUI;

public class RootLayout extends VBox {

	private EditorToolbox editorToolbox;
	private EditorArea editorArea;
	
    @FXML
    private VBox root_pane;

    @FXML
    private MenuItem newMenu,openMenu;
    
    @FXML
    private Menu recentMenu;

    @FXML
    private MenuItem saveMenu, saveAsMenu;

    @FXML
    private MenuItem quitMenu;

    @FXML
    private MenuItem verifyMenu,debugSave;
    
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab fx_editorTab,simulatorTab,propertiesTab;


	// metritis gia to history
	int count = 1;
	ReverseExecution reverse;
	ForwardExecution forward;

	public RootLayout(Stage primaryStage) {

		// Load UI with FXML (Scene Builder)
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui_root.fxml"));

		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();

		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		// System.out.println(editorButtons);
		
		editorArea = new EditorArea();
		RPN rpn = editorArea.getRPN();
		fx_editorTab.setContent( editorArea );
		
		
		
		SimulatorArea simulatorArea = new SimulatorArea(tabPane,simulatorTab);
		simulatorTab.setContent( simulatorArea );
		
		PropertiesArea propertiesArea = new PropertiesArea(tabPane,propertiesTab);
		propertiesTab.setContent( propertiesArea );
		
		
		RPNVerifier.setRPN(rpn);
		verifyMenu.setOnAction(RPNVerifier.clicked);
		

		
		
		
		
		// SelectableNode node = new SelectableNode();
		this.setOnKeyPressed(editorArea.keyClicked);
		FileManager xmlManager = new FileManager(primaryStage,rpn,editorArea,recentMenu); 
		
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

		debugSave.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent we) {
				System.out.println("Saving editor RPN data to file");
				RPN rpn = editorArea.getRPN();
				rpn.toFile("editorRPN.txt");
			}
		});
		


	}

}
