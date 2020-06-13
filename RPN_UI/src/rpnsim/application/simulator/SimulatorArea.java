package rpnsim.application.simulator;

import java.io.File;
import java.util.Set;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import rpnsim.application.AutoCompleteListViewListener;
import rpnsim.application.FileManager;
import rpnsim.application.RPNXMLReader;
import rpnsim.application.RPNXMLWriter;
import rpnsim.application.ScrollArea;
import rpnsim.application.model.Transition;
import rpnsim.application.properties.Reachability;
import rpnsim.application.ui.SelectableNode;
import rpnsim.application.ui.TransitionUI;

public class SimulatorArea extends ScrollArea {

	protected String tempFileName = "TmpSimSave.xml";



	// metritis gia to history
	int count = 1;
	ReverseExecution reverse;
	ForwardExecution forward;

	@FXML
    private AnchorPane simulator_content, fx_simulatorScrollArea;

    @FXML
    private VBox fx_simulatorProperties;

    @FXML
    private ComboBox<String> reversibilityMode;

    @FXML
    private Button refreshButton, forwardRunButton, reverseRunButton;

    @FXML
    private ListView<String> forwardList,  reverseList;
    @FXML
	private TextField forwardTextField,reverseTextField;
 
	
    public Tab simulatorTab;
	private TabPane tabPane;
	ReversibilityMode mode;
	AutoCompleteListViewListener autoForward, autoReverse;
	
	EventHandler<MouseEvent> mouseClicked = new EventHandler<MouseEvent>() {
		@Override
		public void handle(final MouseEvent event) {

			System.out.println("Updating!");
			updateArrowsBonds();
			event.consume();
		}
	};

	public void updateForwardList() {
		
		autoForward.reset();
		
		//forwardList.getItems().clear();
		System.out.println("Enabled transitions:");
		
		
		long start_time = System.nanoTime();
		
		Set<Transition> enabledTransitions =  forward.getEnabledTransitions();
		
		long end_time = System.nanoTime();
		long time = end_time - start_time;
		System.out.println("Get enabled (forward) transitions: " + time/1000000.0f + " ms");
		
		for (Transition t : enabledTransitions) {
			System.out.println(t.getName() + " ,");
			
			autoForward.add(t.getName());
			//forwardList.getItems().add(t.getName());
		}
		//autoForward.
	}

	public void updateReverseList() {
		if (reverse != null) {
			
			//reverseList.getItems().clear();
			autoReverse.reset();
			
			
			long start_time = System.nanoTime();
			
			Set<Transition> enabledTransitions =  reverse.getEnabledTransitions();
			
			long end_time = System.nanoTime();
			long time = end_time - start_time;
			System.out.println("Get enabled (reverse) transitions: " + time /1000000.0f + " ms");
			
			for (Transition t : enabledTransitions) {
				autoReverse.add(t.getName());
				//reverseList.getItems().add(t.getName());
			}
		}
	}

	public SimulatorArea(TabPane tabPane, Tab simulatorTab) {
		super("/simulator_tab.fxml");
		
		autoForward = new AutoCompleteListViewListener(forwardTextField, forwardList);
		autoReverse = new AutoCompleteListViewListener(reverseTextField, reverseList);
		
		this.fx_scrollArea = fx_simulatorScrollArea;
		this.tabPane = tabPane;
		this.simulatorTab = simulatorTab;
		this.fx_scrollArea.setOnMouseClicked(mouseClicked);
		
		this.mode = new ReversibilityMode(reversibilityMode);

		reversibilityMode.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			reverse = mode.getReverseMethod();
			if (reverse != null) {
				reverse.setRPN(rpn);

				updateReverseList();

				// gia na min allazei i timi tou combobox
				reversibilityMode.setDisable(true);
			}

		});

		refreshButton.setOnAction(event2 -> {

			long refreshTime_start = System.nanoTime();
			
			// Reopen file
			this.clearAll();
			count = 1;
			
			
			long openTime_start = System.nanoTime();
			// Now open it with the file manager
			File file = new File(tempFileName);
			rpn = RPNXMLReader.read(this, file);
			long openTime_end = System.nanoTime();
			long openTime = openTime_end - openTime_start;
			System.out.println("RPN Open time : "+openTime /1000000.0f + " ms");
			
			
			this.updateArrowsBonds();
			reversibilityMode.setDisable(false);

			
			// forward = new ForwardExecution(simulatorRPN, forwardList);
			forward.setRPN(rpn);
			updateForwardList();
			

			if( reverse != null )
				reverse.setRPN(rpn);
			
			updateReverseList();

			reversibilityMode.getSelectionModel().select("None");
			reverseList.getItems().clear();
			reversibilityMode.setDisable(false);
			
			
			count = 1;
			
			
			rpn.toFile("simulatorRPN.txt");

			this.refreshAll();
			

			long refreshTime_end = System.nanoTime();
			long refreshTime = refreshTime_end - refreshTime_start;
			System.out.println("UI refresh time: " + refreshTime /1000000.0f + " ms");
		});

		
		forwardRunButton.setOnAction(event2 -> {
			long forwardRunTime_start = System.nanoTime();
			
			String selectedTransitionName = forwardList.getSelectionModel().getSelectedItem();
			Transition selectedTransition = null;
			
			for (Transition t : rpn.getTransitions()) {
				if (t.getName().equals(selectedTransitionName)) {
					selectedTransition = t;
					break;
				}
			}
			if (selectedTransition != null) {
				
				
				long start_time = System.nanoTime();
				
				forward.fireTransition(selectedTransition, count);
				
				long end_time = System.nanoTime();
				long time = end_time - start_time;
				System.out.println("Fired transition (forward): " + time /1000000.0f + " ms");
				
				
				updateArrowsBonds();
				count++;
				
				//long test_start = System.nanoTime();
				this.refreshAll();
				//long test_end = System.nanoTime();
				//long test_time = test_end - test_start;
				//System.out.println("refresh All delay: " + test_time /1000000.0f + " ms");
				
				rpn.toFile("simulatorRPN.txt");

				
				updateForwardList();
				updateReverseList();
			}
			
			System.out.println("History Count: "+count);

			long forwardRunTime_end = System.nanoTime();
			long forwardRunTime = forwardRunTime_end - forwardRunTime_start;
			System.out.println("UI forward run button time: " + forwardRunTime /1000000.0f + " ms");
			
		});
		
		
		reverseRunButton.setOnAction(event3 -> {

			long reverseRunTime_start = System.nanoTime();
			
			String selectedTransitionName = reverseList.getSelectionModel().getSelectedItem();
			Transition selectedTransition = null;
			for (Transition t : rpn.getTransitions()) {
				if (t.getName().equals(selectedTransitionName)) {
					selectedTransition = t;
					break;
				}
			}
			if (selectedTransition != null) {
				
				
				long start_time = System.nanoTime();
				
				reverse.fireTransition(selectedTransition);
				
				long end_time = System.nanoTime();
				long time = end_time - start_time;
				System.out.println("Fired transition (reverse): " + time /1000000.0f + " ms");
				
				
				count--;
				
				rpn.toFile("simulatorRPN.txt");
				
				updateForwardList();
				updateReverseList();
				this.refreshAll();
			}
			
			System.out.println("History Count: "+count);
			
			long reverseRunTime_end = System.nanoTime();
			long reverseRunTime = reverseRunTime_end - reverseRunTime_start;
			System.out.println("UI reverse run button time: "+reverseRunTime /1000000.0f + " ms");
			
		});
		
		
		simulatorTab.setOnSelectionChanged(event -> {

			
			if (simulatorTab.isSelected()) {

				System.out.println("Simulator Tab is Selected");
				
				try {
					
					long simulatorTabTime_start = System.nanoTime();
					
					long verifyTime_start = System.nanoTime();
					if (RPNVerifier.verify()) {
						long verifyTime_end = System.nanoTime();
						long verifyTime  = verifyTime_end - verifyTime_start;
						System.out.println("RPN Verify time : "+verifyTime /1000000.0f + " ms");
						
						//System.out.println("The rpn is well-formed!");

						// Save current RPN (with UI) in a temporary file
						long saveTime_start = System.nanoTime();
						
						File file = new File(tempFileName);
						RPNXMLWriter.write(RPNVerifier.rpn, file);

						long saveTime_end = System.nanoTime();
						long saveTime = saveTime_end - saveTime_start;
						System.out.println("RPN Save time : "+saveTime /1000000.0f + " ms");
						
						// Clear everything in the simulator area
						this.clearAll();
						count = 1;
						long openTime_start = System.nanoTime();
						// Now open it with the file manager
						rpn = RPNXMLReader.read(this, file);
						long openTime_end = System.nanoTime();
						long openTime = openTime_end - openTime_start;
						System.out.println("RPN Open time : "+openTime /1000000.0f + " ms");
						
						
						// Update line positions
						this.updateArrowsBonds();

						forward = new ForwardExecution(rpn);
						
						mode.setRPN(rpn);

						updateForwardList();
						
						reversibilityMode.getSelectionModel().select("None");
						reverseList.getItems().clear();
						reversibilityMode.setDisable(false);

						count = 1;
						
					
						long simulatorTabTime_end = System.nanoTime();
						long simulatorTabTime = simulatorTabTime_end - simulatorTabTime_start;
						System.out.println("UI simulator tab time: "+simulatorTabTime /1000000.0f + " ms");
					
					}
					
					
					
				}catch (IllegalArgumentException ex) {
					

					System.out.println("The rpn isn't well-formed..");
					// switch to editor tab
					tabPane.getSelectionModel().select(0);
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Parsing Error");
					alert.setContentText(
								"The RPN is not well-formed. "+ ex.getMessage());
					alert.showAndWait();

					
				}
				
				

			}
		});
	}

}
