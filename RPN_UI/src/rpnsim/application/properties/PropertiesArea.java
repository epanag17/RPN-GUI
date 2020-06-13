package rpnsim.application.properties;

import java.io.File;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import rpnsim.application.RPNXMLReader;
import rpnsim.application.RPNXMLWriter;
import rpnsim.application.ScrollArea;
import rpnsim.application.model.Transition;
import rpnsim.application.simulator.Backtracking;
import rpnsim.application.simulator.CausalReversal;
import rpnsim.application.simulator.Execution;
import rpnsim.application.simulator.ForwardExecution;
import rpnsim.application.simulator.OutOfCausalOrder;
import rpnsim.application.simulator.RPNVerifier;
import rpnsim.application.simulator.ReverseExecution;
import rpnsim.application.simulator.ReversibilityMode;

public class PropertiesArea extends ScrollArea{
	
	
	protected String tempFileName = "TmpPropSave.xml";
	
	protected TabPane tabPane;
	protected Tab propertiesTab;
	
	@FXML
    private AnchorPane properties_content, fx_propertiesScrollArea;

    @FXML
    private VBox fx_vboxProperties;

    @FXML
    private ComboBox<String> reversibilityMode;

    @FXML
    private Button refreshButton, reachabilityButton;


	ReverseExecution reverse;
	ForwardExecution forward;
	ReversibilityMode mode;
    
	public PropertiesArea(TabPane tabPane, Tab propertiesTab) {
		super("/properties_tab.fxml");
		
		this.tabPane = tabPane;
		this.propertiesTab = propertiesTab;
		
		this.fx_scrollArea = fx_propertiesScrollArea;
		this.fx_scrollArea.setOnMouseClicked(mouseClicked);
		
		this.mode = new ReversibilityMode(reversibilityMode);

		reversibilityMode.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			reverse = mode.getReverseMethod();
			if (reverse != null) {
				reversibilityMode.setDisable(true);
			}

		});

		refreshButton.setOnAction(event2 -> {

			// Reopen file
			this.clearAll();
			File file = new File(tempFileName);
			rpn = RPNXMLReader.read(this, file);

			this.updateArrowsBonds();
			reversibilityMode.setDisable(false);

			
			reversibilityMode.getSelectionModel().select("None");
			reversibilityMode.setDisable(false);
			
			rpn.toFile("propertiesRPN.txt");
			
			this.refreshAll();

		});
		
		
		propertiesTab.setOnSelectionChanged(event -> {

			if (propertiesTab.isSelected()) {

				System.out.println("Properties Tab is Selected");
				
				try {
					if (RPNVerifier.verify()) {

						System.out.println("The rpn is well-formed!");

						// Save current RPN (with UI) in a temporary file
						File file = new File(tempFileName);
						RPNXMLWriter.write(RPNVerifier.getRPN(), file);

						// Clear everything in the simulator area
						this.clearAll();
						rpn = RPNXMLReader.read(this, file);

						// Update line positions
						this.updateArrowsBonds();
						
						mode = new ReversibilityMode(reversibilityMode);
						mode.setRPN(rpn);
						//reversibilityMode.getSelectionModel().clearSelection();
						reversibilityMode.setDisable(false);

						Reachability reachability = new Reachability(rpn,mode);
						reachabilityButton.setOnAction(reachability.clickAction);
						
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
	
	EventHandler<MouseEvent> mouseClicked = new EventHandler<MouseEvent>() {
		@Override
		public void handle(final MouseEvent event) {

			System.out.println("Updating!");
			updateArrowsBonds();
			event.consume();
		}
	};
	
	
	

}
