package rpnsim.application.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.controlsfx.control.ToggleSwitch;
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
import rpnsim.application.model.LabelItem;
import rpnsim.application.model.RPN;
import rpnsim.application.model.Token;
import rpnsim.application.simulator.RPNUtils;

public class ArcLabelUI extends AnchorPane {
	
	@FXML private Button okButton;
	
	// Tokens
    @FXML private ListView<String> listViewTokens;
    @FXML private ComboBox<String> tokenCombo;
	AutoCompleteComboBoxListener<String> autoTokenCombo;
	AutoCompleteComboBoxListener<String> autoComboToken1;
	AutoCompleteComboBoxListener<String> autoComboToken2;
    
    
    @FXML private ToggleSwitch isNegativeToken;
    @FXML private Button addToken;
    @FXML private Button removeToken;
    
    // Bonds
    @FXML private ListView<String> listViewBonds;
    @FXML private ComboBox<String> comboToken1;
    @FXML private ComboBox<String> comboToken2;
    @FXML private ToggleSwitch isNegativeBond;
    @FXML private Button addBond;
    @FXML private Button removeBond;
	 	
	 	private Stage stage;
	 	private RPN rpn;
	 	
	 	private ArrowUI arc;
	 	
	 	public ArcLabelUI(RPN rpn) {
	 		this.rpn = rpn;
	 		FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("/popupForLabelArc.fxml")
					);
			
			fxmlLoader.setRoot(this); 
			fxmlLoader.setController(this);
			try { 
				Parent root = fxmlLoader.load();
				Scene scene = new Scene(root);
				scene.getStylesheets().add("/popupForLabelArcStyling.css");
				stage = new Stage();
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.setTitle("Edit Label");
				stage.setScene(scene);
	        
			} catch (IOException exception) {
			    throw new RuntimeException(exception);
			}
			
			

	 	}
	 	
	 	public void showPopup( ArrowUI arc ) {
	 		
	 		this.arc = arc;
	 		
	 		listViewTokens.getItems().clear();
	 		listViewBonds.getItems().clear();
	 		
	 		
	 		
	 		tokenCombo.setItems(FXCollections.observableArrayList(initTokenList()));
			comboToken1.setItems(FXCollections.observableArrayList(initTokenList()));
			comboToken2.setItems(FXCollections.observableArrayList(initTokenList()));
			
			autoTokenCombo = new AutoCompleteComboBoxListener<String>(tokenCombo);
			autoComboToken1 = new AutoCompleteComboBoxListener<String>(comboToken1);
			autoComboToken2 = new AutoCompleteComboBoxListener<String>(comboToken2);
			
			for( LabelItem item : arc.getSet() ) {
				
				if( item.isToken() )
					addToken( item.getToken().name, item.isNegative() );
				else {
					Pair<Token,Token> bond = item.getBond();
					addBond( bond.getKey().name, bond.getValue().name, item.isNegative() );
				}
			}
			
	 		stage.show();
			
	 	}
	 	
	 	
	 	ArrayList<String> initTokenList(){
	 		ArrayList<String> tokenList = new ArrayList<String>();
	 		for(Token token:rpn.getTokens())
	 			tokenList.add(token.name);
	 		return tokenList;
	 	}
	 	
	 	void addToken(String tokenName, boolean isNegative) {
	 		String selectedToken = tokenName;
	 		
	 		if( ! rpn.hasToken(tokenName) )
	 			return;
	 		
	 		List<String> list_items = listViewTokens.getItems();
	 		if(selectedToken!=null) {
	 			if(!list_items.contains(selectedToken) && !list_items.contains("¬"+selectedToken)) {
	 				if(isNegative) {
	 					listViewTokens.getItems().add("¬"+selectedToken);
	 				}	
	 				else {
	 					listViewTokens.getItems().add(selectedToken);
	 				}			
	 			}			
	 		}	
	 	}
	 	
	 	void addToken() {
	 		addToken(tokenCombo.getValue(), isNegativeToken.isSelected() );	
	 	}
	 	
	 	void delete(ListView<String> listView) {
	 		String selectedToken = listView.getSelectionModel().getSelectedItem();
	 		if(selectedToken != null) {
	 			listView.getItems().remove(selectedToken);
	 		}
	 	}
	 	
	 	void addBond( String tokenA, String tokenB, boolean isNegative) {
	 		if( ! rpn.hasToken(tokenA) || ! rpn.hasToken(tokenB) )
	 			return;
	 		
	 		String selectedToken1 = tokenA;
	 		String selectedToken2 = tokenB;
	 		
	 		
	 		List<String> list_items = listViewBonds.getItems();
	 		if(selectedToken1!=null && selectedToken2!=null && !selectedToken1.equals(selectedToken2)) {
	 			for(String item:list_items) {
	 				if(item.indexOf(selectedToken1)!=-1 && item.indexOf(selectedToken2)!=-1)
	 					return;
	 				
	 			}
	 			if(!isNegative) {
	 				listViewBonds.getItems().add(selectedToken1+" - "+selectedToken2);
	 			}
	 			else {
	 				listViewBonds.getItems().add("¬ ("+selectedToken1+" - "+selectedToken2+")");
	 			}
	 			
	 		}
	 		
	 	}
	 	
	 	void addBond() {
	 		String selectedToken1 = comboToken1.getValue();
	 		String selectedToken2 = comboToken2.getValue();
	 		addBond(selectedToken1,selectedToken2,isNegativeBond.isSelected());
	 	}
	 	
	 	void initArcLists() {
	 		
	 		// Krata antigrafo gia sygrisi meta
	 		Set<LabelItem> prevItems = new HashSet<LabelItem>();
	 		prevItems.addAll(arc.getSet());
	 		
	 		arc.getSet().clear();
	 		
	 		
	 		List<String> list_items = listViewTokens.getItems();
	 		for(String tokenName:list_items) {
	 			boolean isNegativeToken = false;
	 			if(tokenName.charAt(0)=='¬') {
	 				isNegativeToken = true;
	 				tokenName = tokenName.substring(1);
	 			}
	 				
	 			for(Token token:rpn.getTokens()) {
	 				if(token.name.equals(tokenName)) {
	 					if(isNegativeToken)
	 						arc.myArrow.addNegativeToken(token);
	 						//arc.negTokens.add(token);
	 					else
	 						arc.myArrow.addToken(token);
	 						//arc.tokens.add(token);
	 					break;
	 				}
	 			}
	 		}
	 		
	 		list_items = listViewBonds.getItems();
	 		for(String bond:list_items) {
	 			boolean isNegativeBond = false;
	 			String tokens[] = bond.split(" - ");
	 			String token1 = tokens[0];
	 			if(tokens[0].charAt(0) =='¬') {
	 				isNegativeBond = true;
	 				token1 = tokens[0].substring(3);
	 				
	 			}
	 			String token2 = tokens[1];
	 			if(isNegativeBond)
	 				token2 = tokens[1].substring(0, tokens[1].length()-1);
	 			Token key=null,value=null;
	 			for(Token token:rpn.getTokens()) {
	 				if(token.name.equals(token1))
	 					key=token;
	 				if(token.name.equals(token2))
	 					value=token;
	 			}
	 			Pair<Token, Token> pair = new Pair<>(key, value);
	 			if(isNegativeBond)
	 				arc.myArrow.addNegativeBond(pair);
	 				//arc.negBonds.add(pair);
	 			else
	 				arc.myArrow.addBond(pair);
	 				//arc.bonds.add(pair);
	 			
	 		}
	 		
	 		// Kane elegxo. An exei ala3ei ta label items
	 		RPNUtils utils = new RPNUtils(arc.rpn);
	 		boolean areSame = utils.compareLabelItems(prevItems,arc.getSet());
	 		if(!areSame) {
	 			System.out.println("Label item on arc has changed");
	 			arc.rpn.scrollArea.SomethingChanged();
	 		}
	 		
	 	}
	 	
	 	void addTokenLabel( Token token, boolean isNegative ) {
	 		Label l = new Label(token.name);
 			if(isNegative)
 				l.getStyleClass().add("overline");
 			arc.label.getChildren().add(l);
 			
 			Label comma = new Label(",");
	 		arc.label.getChildren().add(comma);
	 	}
	 	
	 	void addBondLabel( Pair<Token,Token> bond, boolean isNegative ) {
 			Label l = new Label(bond.getKey().name+" - "+bond.getValue().name);
 			if(isNegative)
 				l.getStyleClass().add("overline");
 			arc.label.getChildren().add(l);
 			
 			Label comma = new Label(",");
	 		arc.label.getChildren().add(comma);
 			
	 	}
	 	
	 	
	 	void createLabel() {
	 		arc.label.getChildren().clear();
	 		
	 		Set<LabelItem> labelItemSet = arc.getSet();
	 		for(LabelItem labelItem : labelItemSet ) {
	 			if( labelItem.isToken() )
	 				addTokenLabel( labelItem.getToken(), labelItem.isNegative());
	 			else
	 				addBondLabel( labelItem.getBond(), labelItem.isNegative());
	 		}
	 		
	 		int length = arc.label.getChildren().size();
	 		if( length>0 )
	 			arc.label.getChildren().remove( length-1 );
	 	}
	 	
	 	
	 	@FXML
		private void initialize() {
	 		addToken.setOnAction(clicked);
	 		removeToken.setOnAction(clicked);
	 		addBond.setOnAction(clicked);
	 		removeBond.setOnAction(clicked);
	 		okButton.setOnAction(clicked);
	 	}
	 	
	 	public EventHandler<ActionEvent> clicked = new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(final ActionEvent event) {
		    	Button selectedButton  = (Button) event.getSource();
		    	if(selectedButton.getId().equals("addToken"))
		    		addToken();
		    	else if(selectedButton.getId().equals("removeToken"))
		    		delete(listViewTokens);
		    	else if(selectedButton.getId().equals("removeBond"))
		    		delete(listViewBonds);
		    	else if(selectedButton.getId().equals("addBond"))
		    		addBond();
		    	else if(selectedButton.getId().equals("okButton")) {
		    		initArcLists();
		    		createLabel();
		    		stage.close();
		    	}
		    		
		    }

		};
	
}
