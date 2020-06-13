package rpnsim.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import rpnsim.application.editor.EditorArea;
import rpnsim.application.rpn.Arrow;
import rpnsim.application.rpn.Bond;
import rpnsim.application.rpn.Place;
import rpnsim.application.rpn.RPN;
import rpnsim.application.rpn.Token;
import rpnsim.application.rpn.Transition;
import rpnsim.application.simulator.RPNVerifier;


public class XMLManager {
	
	private Stage stage;
	private RPN rpn;
	private ScrollArea scrollArea;
	private Document doc;
	
	private XMLManager self;
	
	private File savedFile;
	private Menu recentMenu;
	
	
	public ScrollArea getScrollArea() {
		return scrollArea;
	}
	
	public XMLManager(Stage stage, RPN rpn, ScrollArea scrollArea, Menu recentMenu) {
		this.stage = stage;
		this.rpn = rpn;
		this.scrollArea = scrollArea;
		this.self = this;
		this.recentMenu = recentMenu;
		updateRecentMenu(recentMenu);
	}
	
	private void updateRecentMenu(Menu recentMenu) {
		 //recent files
        try {
        File recentFile = new File(System.getProperty("user.dir")+"/recentFiles");
        if(!recentFile.exists()) 
        	recentFile.createNewFile();
        BufferedReader br = new BufferedReader(new FileReader(recentFile)); 
        String st; 
        ArrayList<String> recentFiles = new ArrayList<>();
			while ((st = br.readLine()) != null) {
				recentFiles.add(st);
			}
			
			recentMenu.getItems().clear();
			for(String fileName:recentFiles) {
	        	MenuItem item = new MenuItem(fileName);
	        	item.setOnAction( openRecent );
	        	recentMenu.getItems().add(item);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} 
        
	}
	
	public void setRPN(RPN rpn) {
		this.rpn = rpn;
	}
	
	public boolean saveConfirmation(String title, String header) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText("Please choose one of the following.");

		ButtonType buttonTypeYes = new ButtonType("Yes");
		ButtonType buttonTypeNo = new ButtonType("No");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeYes){
		    save();
		} else if (result.get() == buttonTypeNo) {
		   	// Tipota
		} else {
			return false;
		}
		
		return true;
	}
	
	public void updateRecentFiles(File recentFile) {
		String path = recentFile.getAbsolutePath();
		ArrayList<String> recentFiles = new ArrayList<>();
		try {
	        File file = new File(System.getProperty("user.dir")+"/recentFiles");
	        if(!file.exists()) 
	        	file.createNewFile();
	        BufferedReader br = new BufferedReader(new FileReader(file)); 
	        String st; 
			while ((st = br.readLine()) != null) {
				recentFiles.add(st);
			}
			
			if(recentFiles.contains(path)) {
				recentFiles.remove(path);
				recentFiles.add(0, path);
			}
			else {
				recentFiles.add(0, path);
			}
			
			System.out.println("recent files: "+recentFiles);
			
			FileWriter writer = new FileWriter(System.getProperty("user.dir")+"/recentFiles"); 
			for(String str: recentFiles) {
			  writer.write(str + System.lineSeparator());
			}
			writer.close();
			updateRecentMenu(recentMenu);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	
	public EventHandler<ActionEvent> newAction = new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(final ActionEvent event) {
	    	
	    	if(scrollArea instanceof EditorArea) {	        

	    		if( scrollArea.changed ) {
	    			System.out.println("Do you want to save changes?");
	    			if(!saveConfirmation("Creating New File", "Do you wish to save changes of your RPN before creating a new file?"))
	    				return;
	    			
	    		}
	    		
		    	RPN rpn = new RPN(scrollArea);
	            RPNVerifier.setRPN(rpn);
	            scrollArea.loadRPN(rpn);
	            self.rpn = rpn;
	            
	            scrollArea.NewFile();
	            scrollArea.SavedChanges();
	            
	            savedFile = null;
	            
	            RPNApp.Rename("Untitled");
	    	}
	    	
	    	event.consume();
	    }
	};
		
	public void saveAs() {
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("eXtensible Markup Language File", "*.xml")
            );
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            create(file);
            updateRecentFiles(file);
             
	        if( scrollArea instanceof EditorArea) {
	        	System.out.println("Saved Changes!");
	        	RPNApp.Rename(file.getName()); 
	        	scrollArea.SavedChanges();
	        	savedFile = file;
	        }   
        }

	}
	
	public void save() {
		
		if(savedFile == null)
			saveAs();
		else {
			
			if (scrollArea instanceof EditorArea) {
				create(savedFile);
				updateRecentFiles(savedFile);
				System.out.println("Saved File!");
				RPNApp.Rename(savedFile.getName());
				scrollArea.SavedChanges();
			}

		}
	}
	
	

	public EventHandler<ActionEvent> saveAsAction = new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(final ActionEvent event) {
	    	saveAs();
	    	event.consume();
	    }
	};
	
	
	
	public EventHandler<ActionEvent> saveAction = new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(final ActionEvent event) {
	    	save();
	    	event.consume();
	    }
	};
	

	public EventHandler<ActionEvent> openRecent = new EventHandler<ActionEvent>() {
	
		@Override
		public void handle(ActionEvent event) {
			
			MenuItem source = (MenuItem)event.getSource();
			String filename = source.getText();
			
	    	if( scrollArea.changed ) {
	    		if(!saveConfirmation("Opening file","Do you wish to save changes of your RPN before opening another file?"))
	    			return;
	    	}
	    	
	    	
	    	File file = new File(filename);
            if (file != null) {
                RPN rpn = openFile(file);
                RPNVerifier.setRPN(rpn);
                scrollArea.loadRPN(rpn);
                self.rpn = rpn;
                updateRecentFiles(file);
                
                        
		        if( scrollArea instanceof EditorArea) {
		        	System.out.println("Saved Changes!");
		        	RPNApp.Rename(file.getName()); 
		        	scrollArea.SavedChanges();
		        	savedFile = file;
		        } 
                
            }
            
	    	event.consume();
	    	
			
		}
		
		
		
	};
	
	public EventHandler<ActionEvent> openAction = new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(final ActionEvent event) {
	    	
	    	if( scrollArea.changed ) {
	    		if(!saveConfirmation("Opening file","Do you wish to save changes of your RPN before opening another file?"))
	    			return;
	    	}
	    	
	    	FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Open File");
	        File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                RPN rpn = openFile(file);
                RPNVerifier.setRPN(rpn);
                scrollArea.loadRPN(rpn);
                self.rpn = rpn;
                updateRecentFiles(file);
                
                        
		        if( scrollArea instanceof EditorArea) {
		        	System.out.println("Saved Changes!");
		        	RPNApp.Rename(file.getName()); 
		        	scrollArea.SavedChanges();
		        	savedFile = file;
		        } 
                
            }
            
	    	event.consume();
	    }
	};
	
	private Element appendChild(Element parent, String childName) {
		Element child = doc.createElement(childName);
		parent.appendChild(child);
		return child;
	}
	
	private void appendChildWithText(Element parent, String childName,String childText) {
		Element child = doc.createElement(childName);
		child.appendChild(doc.createTextNode(childText));
		parent.appendChild(child);
	}
	
	
	private void appendList(Element parent, List< ? > list) {
		for( Object item : list ) {
			
			if( item instanceof Pair<?,?> ) {
				Object key = ((Pair)item).getKey();
				Object value = ((Pair) item).getValue();
				
				if( value instanceof List<?> ) {
					Element childElement = appendChild(parent, key.toString());
					appendList(childElement, (List<?>) value );
				}
				else {
					appendChildWithText(parent, key.toString(), value.toString());
				}
			}
			
		}
	}
	

	
	public void create(File file) {
		ArrayList<Place> places = rpn.getPlaces();
		Bond[] bonds = rpn.getBonds();
		ArrayList<Transition> transitions = rpn.getTransitions();
		ArrayList<Arrow> arrows = rpn.getArrows();
		
		 try {
	         DocumentBuilderFactory dbFactory =
	         DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         this.doc = dBuilder.newDocument();
	         
	         // root element
	         Element rootElement =  doc.createElement("rpn");
	         doc.appendChild(rootElement);
	         
	         Element placesElement = appendChild(rootElement, "places");
	         for(Place place: places) {
	        	 Element placeElement = appendChild(placesElement, "place");
	        	 appendList(placeElement, place.getDataList());
	         }
	           
	         Element transitionsElement = appendChild(rootElement, "transitions");
	         for(Transition transition: transitions) {
	        	 Element transitionElement = appendChild(transitionsElement, "transition");
	        	 appendList(transitionElement, transition.getDataList());
	         }
	         
	         Element arrowsElement = appendChild(rootElement, "arrows");
	         for(Arrow arrow: arrows) {
	        	 Element arrowElement = appendChild(arrowsElement, "arrow");
	        	 appendList(arrowElement,arrow.getDataList());
	         }
	         
	         Element bondsElement = appendChild(rootElement, "totalBonds");
	         for(Bond bond: bonds) {
	        	 //Element bondElement = appendChild(bondsElement, "bond");
	        	 appendList(bondsElement, bond.getDataList());
	         }
	         

	         // write the content into xml file
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         DOMSource source = new DOMSource(doc);
	         StreamResult result = new StreamResult(file);
	         transformer.transform(source, result);
	         
	         // Output to console for testing
	         StreamResult consoleResult = new StreamResult(System.out);
	         transformer.transform(source, consoleResult);
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		 
	}
	
	private Node parseNode(Element parent, int index, String tag) {
		return parent.getElementsByTagName(tag).item(index);
	}	
	private NodeList parseNodeList(Element parent, String tag) {
		return parent.getElementsByTagName(tag);
	}
	private String parseText(Element parent, int index, String tag) {
		return parseNode(parent,index,tag).getTextContent();
	}
	private double parseDouble(Element parent, int index, String tag) {
		String str = parseText(parent,index,tag);
		return Double.parseDouble(str);
	}

	
	public RPN openFile(File file) {
		RPN rpn = new RPN(scrollArea);
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
	        doc.getDocumentElement().normalize();
	        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	        NodeList placeList = doc.getElementsByTagName("place");
	        
	        for(int i=0; i<placeList.getLength(); i++) {
	        	Node nNode = placeList.item(i);
	        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	        		Element eElement = (Element) nNode;
	        	
	        		
	        		String placeName = parseText(eElement,0,"name"); 
	        		Double x = parseDouble(eElement,0,"x"); 
	        		Double y = parseDouble(eElement,0,"y");
	        		Point2D position = new Point2D(x,y);
	        		ArrayList<String> tokenNames = new ArrayList<>();
	        		
	        		Place place = rpn.addPlace();
	        		place.setPosition( position );
	        		place.rename(placeName);
	        		
	        		Node tokens = parseNode(eElement,0,"tokens");
	        		if (tokens.getNodeType() == Node.ELEMENT_NODE) {
    	        		Element eTokens = (Element) tokens;
    	        		NodeList tokenList = parseNodeList(eTokens, "token");
    	        		for(int j=0; j<tokenList.getLength();j++) {
    	        			String tokenName = parseText(eTokens,j,"token");
    	        			Token token = place.addToken();
    	        			token.rename(tokenName);
    	        		}
    	        	}
	        		
	        		//rpn.addPlaceNode(placeName, position, tokenNames);
	        		
	        	}	
	        }

	        NodeList transitionList = doc.getElementsByTagName("transition");
	        System.out.println("----------------------------");
	        
	        for(int i=0; i<transitionList.getLength(); i++) {
	        	Node nNode = transitionList.item(i);
	        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	        		Element eElement = (Element) nNode;
	        		
	        		String transitionName = parseText(eElement,0,"name");
	        		Double x = parseDouble(eElement,0,"x");
	        		Double y = parseDouble(eElement,0,"y");
	        		Point2D position = new Point2D(x,y);
	        		
	        		Transition transition = rpn.addTransition();
	        		transition.setPosition( position );
	        		transition.rename(transitionName);
	        	}	
	        }

	        NodeList arrowList = doc.getElementsByTagName("arrow");
	        for(int i=0; i<arrowList.getLength(); i++) {
	        	Node nNode = arrowList.item(i);
	        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	        		Element eElement = (Element) nNode;
	        		
	        		String source = parseText(eElement,0,"source");
	        		String destination = parseText(eElement,0,"destination");
	        		Arrow arrow = rpn.addArrow(source, destination);
	        		//editorArea.addNode(arrow.getGroup());
	        		
	        		Node tokens = parseNode(eElement,0,"tokens");
	        		if (tokens.getNodeType() == Node.ELEMENT_NODE) {
    	        		Element tokenElement = (Element) tokens;
    	        		NodeList tokenList = parseNodeList(tokenElement, "token");
    	        		for(int j=0; j< tokenList.getLength();j++) {
    	        			String tokenName = parseText(tokenElement,j,"token");
    	        			Token token = rpn.findToken(tokenName);
    	        			if(token == null)
    	        				continue;
    	        			arrow.addToken(token);
    	        		}
	        		}
	        		
	        		Node negTokens = parseNode(eElement,0,"negTokens");
	        		if (negTokens.getNodeType() == Node.ELEMENT_NODE) {
    	        		Element tokenElement = (Element) negTokens;
    	        		NodeList tokenList = parseNodeList(tokenElement, "token");
    	        		for(int j=0; j< tokenList.getLength();j++) {
    	        			String tokenName = parseText(tokenElement,j,"token");
    	        			Token token = rpn.findToken(tokenName);
    	        			if(token == null)
    	        				continue;
    	        			arrow.addNegativeToken(token);
    	        		}
	        		}	
	        		
	        		Node bonds = parseNode(eElement,0,"bonds");
	        		if (bonds.getNodeType() == Node.ELEMENT_NODE) {
		        		NodeList bondList = parseNodeList((Element)bonds, "bond");
		        		for(int j=0; j<bondList.getLength(); j++ ) {
		        			Element bond = (Element)bondList.item(j);
		        			String tokenNameA = parseText(bond,0,"token");
		        			String tokenNameB = parseText(bond,1,"token");
		        			Token tokenA = rpn.findToken(tokenNameA);
		        			Token tokenB = rpn.findToken(tokenNameB);	
		        			arrow.addBond(tokenA, tokenB);
		        		}
	        		}
	        		
	        		Node negBonds = parseNode(eElement,0,"negBonds");
	        		if (negBonds.getNodeType() == Node.ELEMENT_NODE) {
		        		NodeList negBondList = parseNodeList((Element)negBonds, "bond");
		        		for(int j=0; j<negBondList.getLength(); j++ ) {
		        			Element bond = (Element)negBondList.item(j);
		        			String tokenNameA = parseText(bond,0,"token");
		        			String tokenNameB = parseText(bond,1,"token");
		        			Token tokenA = rpn.findToken(tokenNameA);
		        			Token tokenB = rpn.findToken(tokenNameB);	
		        			arrow.addNegativeBond(tokenA, tokenB);
		        		}
	        		}
	        		
	        		arrow.updateLabel();
	        		arrow.update();
	        		arrow.disableMouseTransparent();
	        	}	
	        	
	        }
	        
	        Element bonds = (Element)doc.getElementsByTagName("totalBonds").item(0);
	        NodeList bondList = parseNodeList(bonds,"bond");
	        for(int i=0; i<bondList.getLength();i++) {
	        	Element bond = (Element) bondList.item(i);
	        	String tokenNameA = parseText(bond,0,"token");
    			String tokenNameB = parseText(bond,1,"token");
    			Token tokenA = rpn.findToken(tokenNameA);
    			Token tokenB = rpn.findToken(tokenNameB);	
    			
        		Bond newBond = rpn.addBond(tokenA, tokenB);
        		//editorArea.addNode(newBond.getGroup());
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return rpn;
	}

}
