package rpnsim.application.properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import rpnsim.application.AutoCompleteComboBoxListener;
import rpnsim.application.model.Marking;
import rpnsim.application.model.Place;
import rpnsim.application.model.RPN;
import rpnsim.application.model.Token;
import rpnsim.application.model.Transition;
import rpnsim.application.simulator.Execution;
import rpnsim.application.simulator.ForwardExecution;
import rpnsim.application.simulator.OutOfCausalOrder;
import rpnsim.application.simulator.ReverseExecution;
import rpnsim.application.simulator.ReversibilityMode;
import rpnsim.application.ui.BondUI;
import rpnsim.application.ui.PlaceUI;
import rpnsim.application.ui.TokenUI;

public class Reachability extends AnchorPane{
	
	private Stage stage;
	
	 @FXML private Button searchButton;
	 @FXML private ListView<String> listViewTokens;
	 @FXML private ComboBox<String> tokenCombo;
	 @FXML private Button addToken;
	 @FXML private Button removeToken;
	 @FXML private ListView<String> listViewBonds;
	 @FXML private ComboBox<String> comboToken1;
	 @FXML private ComboBox<String> comboToken2;
	 @FXML private Button addBond;
	 @FXML private Button removeBond;
	 @FXML private ComboBox<String> placeCombo;
	 @FXML private Button closeButton, applyButton;
	 @FXML private AnchorPane result;
	 
	 AutoCompleteComboBoxListener<String> autoTokenCombo;
	 AutoCompleteComboBoxListener<String> autoComboToken1;
	 AutoCompleteComboBoxListener<String> autoComboToken2;
	 AutoCompleteComboBoxListener<String> autoPlaceCombo;
	 
	 private RPN rpn;
	 private ArrayList<Edge> path;
	 ReversibilityMode mode;
	
	public Reachability(RPN rpn, ReversibilityMode mode) {
		this.rpn = rpn;
		this.mode = mode;
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/popupForReachability.fxml")
				);
		
		fxmlLoader.setRoot(this); 
		fxmlLoader.setController(this);
		
		try { 
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root);
			stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Check Reachability Property");
			stage.setScene(scene);
			
		} catch (IOException exception) {
		    throw new RuntimeException(exception);
		}
		
		addToken.setOnAction(clicked);
		removeToken.setOnAction(clicked);
		addBond.setOnAction(clicked);
		removeBond.setOnAction(clicked);
		searchButton.setOnAction(clicked);
		closeButton.setOnAction(clicked);
		applyButton.setOnAction(clicked);
	}
	
	public EventHandler<ActionEvent> clickAction = new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(final ActionEvent event) {
	    	tokenCombo.setItems(FXCollections.observableArrayList(initTokenList()));
			comboToken1.setItems(FXCollections.observableArrayList(initTokenList()));
			comboToken2.setItems(FXCollections.observableArrayList(initTokenList()));
			placeCombo.setItems(FXCollections.observableArrayList(initPlaceList()));
			
			autoTokenCombo = new AutoCompleteComboBoxListener<String>(tokenCombo);
			autoComboToken1 = new AutoCompleteComboBoxListener<String>(comboToken1);
			autoComboToken2 = new AutoCompleteComboBoxListener<String>(comboToken2);
			autoPlaceCombo = new AutoCompleteComboBoxListener<String>(placeCombo);
	    	stage.show();
	    }
	};
	
	ArrayList<String> initTokenList(){
 		ArrayList<String> tokenList = new ArrayList<String>();
 		for(Token token:rpn.getTokens())
 			tokenList.add(token.getName());
 		return tokenList;
 	}
	
	ArrayList<String> initPlaceList(){
 		ArrayList<String> placeList = new ArrayList<String>();
 		for(Place place:rpn.getPlaces())
 			placeList.add(place.getName());
 		return placeList;
 	}
	
	void addToken() {
		String selectedToken = tokenCombo.getValue();
		String selectedPlace = placeCombo.getValue();
		
		if(!rpn.hasToken(selectedToken) || !rpn.hasNode(selectedPlace))
			return;
		
		if(selectedToken != null && selectedPlace != null) {
			
			boolean contain = false;
			for(String line:listViewTokens.getItems()) {
				String temp[] = line.split(" ");
				if(selectedToken.equals(temp[0])) {
					contain = true;
					break;
				}
			}
			
			if(!contain)
				listViewTokens.getItems().add(selectedToken+" "+"\u2208"+" M("+selectedPlace+")");
		}
	}
	
	void addBond() {
		String selectedToken1 = comboToken1.getValue();
		String selectedToken2 = comboToken2.getValue();
		
		if(!rpn.hasToken(selectedToken1) || !rpn.hasToken(selectedToken2))
			return;
		
		if(selectedToken1 != null && selectedToken2 != null) {
			
			if(!selectedToken1.equals(selectedToken2)) {
				boolean contain = false;
				for(String line:listViewBonds.getItems()) {
					String temp[] = line.split(" - ");
					if(selectedToken1.equals(temp[0]) && selectedToken2.equals(temp[1])) {
						contain = true;
						break;
					}
					if(selectedToken1.equals(temp[1]) && selectedToken2.equals(temp[0])) {
						contain = true;
						break;
					}
				}
				
				if(!contain)
					listViewBonds.getItems().add(selectedToken1+" - "+selectedToken2);
			}
				
		}
	}
	
	void delete(ListView<String> listView) {
 		String selectedToken = listView.getSelectionModel().getSelectedItem();
 		if(selectedToken != null) {
 			listView.getItems().remove(selectedToken);
 		}
 	}
	
	HashMap<String,String> getTokens(){
		HashMap<String, String> tokensIntoPlace = new HashMap<>();
		for(String line:listViewTokens.getItems()) {
			String temp[] = line.split(" ");
			String token = temp[0];
			String place = temp[2].substring(2,temp[2].length()-1);
			tokensIntoPlace.put(token, place);
		}
		
		return tokensIntoPlace;
	}
	
	HashMap<String, Set<String>> getBonds(){
		HashMap<String, Set<String>> tokenConnections = new HashMap<>();
		for(String line:listViewBonds.getItems()) {
			String temp[] = line.split(" - ");
			if(!tokenConnections.containsKey(temp[0])) {
				Set<String> con = new HashSet<>();
				con.add(temp[1]);
				tokenConnections.put(temp[0], con);
			}
			else {
				Set<String> con = tokenConnections.get(temp[0]);
				con.add(temp[1]);
				tokenConnections.replace(temp[0], con);
			}
		}
		
		return tokenConnections;
	}
	

	
	ArrayList<Edge> search() {
		HashMap<String, String> tokensIntoPlace = getTokens();
		HashMap<String, Set<String>> tokenConnections = getBonds();
		Marking searchMarking = new Marking(rpn,tokensIntoPlace,tokenConnections);
		//System.out.println("Marking: "+searchMarking);
		
		ForwardExecution forward = new ForwardExecution(rpn);
		ReverseExecution reverse = mode.getReverseMethod();
		
		Search rs = new Search(rpn, forward, reverse);
		ArrayList<Edge> path = rs.search(searchMarking);
		result.getChildren().clear();
		if( path != null ) {
			
			StringBuilder sb = new StringBuilder();
			for( Edge edge : path ) {
				sb.append("(");
				sb.append(edge.t);
				sb.append(",");
				if( edge.ex instanceof ForwardExecution )
					sb.append("forward");
				if( edge.ex instanceof ReverseExecution )
					sb.append("reverse");
				sb.append(")");
				sb.append(" -> ");
			}
			sb.append("End");
			
			result.getChildren().add(new Label("Path Found: "+sb.toString()));
			System.out.println(sb.toString());
			
			
		}
		else
			result.getChildren().add(new Label("No Path Found"));
		
		return path;
		
	}
	
	void apply() {
		System.out.println("apply rpn!");
		if(path!=null) {
			Edge lastEdge = path.get(path.size()-1);
			Marking newMarking = lastEdge.destination.marking;
			rpn.setMarking(newMarking);
			rpn.scrollArea.refreshAll();
		}
		
	}
	
	private EventHandler<ActionEvent> clicked = new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(final ActionEvent event) {
	    	Button selectedButton  = (Button) event.getSource();
	    	if(selectedButton.getId().equals("closeButton")) 
	    		stage.close();
	    	else if(selectedButton.getId().equals("addToken")) 
	    		addToken();
	    	else if(selectedButton.getId().equals("addBond")) 
	    		addBond();
	    	else if(selectedButton.getId().equals("removeToken")) 
	    		delete(listViewTokens);
	    	else if(selectedButton.getId().equals("removeBond")) 
	    		delete(listViewBonds);
	    	else if(selectedButton.getId().equals("searchButton"))
	    		path = search();
	    	else if(applyButton.getId().equals("applyButton"))
	    		apply();
	    }
	};

}
