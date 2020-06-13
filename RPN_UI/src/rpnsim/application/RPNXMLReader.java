package rpnsim.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.geometry.Point2D;
import rpnsim.application.model.RPN;
import rpnsim.application.ui.ArrowUI;
import rpnsim.application.ui.BondUI;
import rpnsim.application.ui.MovableNode;
import rpnsim.application.ui.PlaceUI;
import rpnsim.application.ui.SelectableNode;
import rpnsim.application.ui.TokenUI;
import rpnsim.application.ui.TransitionUI;

public class RPNXMLReader {


	private static Node parseNode(Element parent, int index, String tag) {
		return parent.getElementsByTagName(tag).item(index);
	}	
	private static NodeList parseNodeList(Element parent, String tag) {
		return parent.getElementsByTagName(tag);
	}
	private static  String parseText(Element parent, int index, String tag) {
		return parseNode(parent,index,tag).getTextContent();
	}
	private static double parseDouble(Element parent, int index, String tag) {
		String str = parseText(parent,index,tag);
		return Double.parseDouble(str);
	}
	
	private static RPN readFile(RPN rpn, File file) throws Exception {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		
        //gia na ginei 2 fores to sxima
        double epipleonY = 0;
        
        
        Map<String,MovableNode> movableNodes = new HashMap<String,MovableNode>();
        Map<String,TokenUI> tokenNodes = new HashMap<String,TokenUI>();
        
        
        // Read and create places
        NodeList placeList = doc.getElementsByTagName("place");
        for(int i=0; i<placeList.getLength(); i++) {
        	Node nNode = placeList.item(i);
        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        		Element eElement = (Element) nNode;
        	
        		
        		String placeName = parseText(eElement,0,"name")/*+"_"+counter*/; 
        		Double x = parseDouble(eElement,0,"x");
        		Double y = parseDouble(eElement,0,"y");
        		/*if(counter!=1)
        			y+=epipleonY;*/
        		Point2D position = new Point2D(x,y);
        		ArrayList<String> tokenNames = new ArrayList<>();
        		
        		
        		if(movableNodes.containsKey(placeName) ) {
        			System.err.println("Error! There is already a place with the name '"+placeName+"'");
        			throw new Exception("Duplicate place name error");
        		}
        		
        		PlaceUI placeUI = new PlaceUI(rpn);
        		placeUI.setPosition( position );
        		placeUI.rename(placeName);
        		
        		movableNodes.put(placeName, placeUI);
        		
        		Node tokens = parseNode(eElement,0,"tokens");
        		if (tokens.getNodeType() == Node.ELEMENT_NODE) {
	        		Element eTokens = (Element) tokens;
	        		NodeList tokenList = parseNodeList(eTokens, "token");
	        		for(int j=0; j<tokenList.getLength();j++) {
	        			String tokenName = parseText(eTokens,j,"token");
	        			
	        			if( tokenNodes.containsKey(tokenName) ) {
	        				System.err.println("Error! There is already a token with the symbol '"+tokenName+"'");
	            			throw new Exception("Duplicate token symbol error");
	        			}
	        			
	        			TokenUI tokenUI = placeUI.addToken();
	        			tokenUI.rename(tokenName);
	        			
	        			tokenNodes.put(tokenName, tokenUI);
	        		}
	        	}
        		
   
        	}	
        	
        }

        
        NodeList transitionList = doc.getElementsByTagName("transition");
        //System.out.println("----------------------------");
        
        
        
        for(int i=0; i<transitionList.getLength(); i++) {
        	Node nNode = transitionList.item(i);
        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        		Element eElement = (Element) nNode;
        		
        		String transitionName = parseText(eElement,0,"name")/*+"_"+counter*/;
        		Double x = parseDouble(eElement,0,"x");
        		Double y = parseDouble(eElement,0,"y");
        				/*if(counter!=1)
    	        			y+=epipleonY;*/
        		Point2D position = new Point2D(x,y);
        		
        		
        		if(movableNodes.containsKey(transitionName) ) {
        			System.err.println("Error! There is already a node with the name '"+transitionName+"'");
        			throw new Exception("Duplicate node name error");
        		}
        		
        		TransitionUI transitionUI = new TransitionUI(rpn);
        		transitionUI.setPosition( position );
        		transitionUI.rename(transitionName);
        		
        		movableNodes.put(transitionName, transitionUI);
        		
        	}	
        }
        

        NodeList arrowList = doc.getElementsByTagName("arrow");
        for(int i=0; i<arrowList.getLength(); i++) {
        	Node nNode = arrowList.item(i);
        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        		Element eElement = (Element) nNode;
        		
        		String sourceName = parseText(eElement,0,"source")/*+"_"+counter*/;
        		String destinationName = parseText(eElement,0,"destination")/*+"_"+counter*/;
        		
        		MovableNode source = movableNodes.get(sourceName);
        		MovableNode destination = movableNodes.get(destinationName);
        		
        		if( source == null ) {
        			System.err.println("Error! No node with the name '"+sourceName+"'");
        			throw new Exception("Non-existent source error");
        		}
        		if( destination == null ) {
        			System.err.println("Error! No node with the name '"+destinationName+"'");
        			throw new Exception("Non-existent destination error");
        		}   
        		if( source instanceof PlaceUI && destination instanceof PlaceUI ) {
        			System.err.println("Error! Cannot connect place ("+sourceName+") with another place ("+destinationName+")");
        			throw new Exception("Place connect place error");
        		}
        		if( source instanceof TransitionUI && destination instanceof TransitionUI ) {
        			System.err.println("Error! Cannot connect transition ("+sourceName+") with another transition ("+destinationName+")");
        			throw new Exception("Transition connect transition error");
        		}
        		
        		ArrowUI arrow = new ArrowUI(source, destination);
        		
        		// No we add the label items
        		Node tokens = parseNode(eElement,0,"tokens");
        		if (tokens.getNodeType() == Node.ELEMENT_NODE) {
	        		Element tokenElement = (Element) tokens;
	        		NodeList tokenList = parseNodeList(tokenElement, "token");
	        		for(int j=0; j< tokenList.getLength();j++) {
	        			String tokenName = parseText(tokenElement,j,"token");
	        			
	        			TokenUI token = tokenNodes.get(tokenName);
	            		if( token == null ) 
	        				continue;
	            		
	        			arrow.myArrow.addToken(token.myToken);
	        		}
        		}
        		
        		Node negTokens = parseNode(eElement,0,"negTokens");
        		if (negTokens.getNodeType() == Node.ELEMENT_NODE) {
	        		Element tokenElement = (Element) negTokens;
	        		NodeList tokenList = parseNodeList(tokenElement, "token");
	        		for(int j=0; j< tokenList.getLength();j++) {
	        			String tokenName = parseText(tokenElement,j,"token")/*+"_"+counter*/;
	        			
	        			TokenUI token = tokenNodes.get(tokenName);
	            		if( token == null ) 
	        				continue;
	            		
	        			arrow.myArrow.addNegativeToken(token.myToken);
	        		}
        		}	
        		
        		Node bonds = parseNode(eElement,0,"bonds");
        		if (bonds.getNodeType() == Node.ELEMENT_NODE) {
	        		NodeList bondList = parseNodeList((Element)bonds, "bond");
	        		for(int j=0; j<bondList.getLength(); j++ ) {
	        			Element bond = (Element)bondList.item(j);
	        			String tokenNameA = parseText(bond,0,"token")/*+"_"+counter*/;
	        			String tokenNameB = parseText(bond,1,"token")/*+"_"+counter*/;
	        			
	        			TokenUI tokenA = tokenNodes.get(tokenNameA);
	            		if( tokenA == null ) 
	        				continue;
	        			TokenUI tokenB = tokenNodes.get(tokenNameB);
	            		if( tokenB == null ) 
	        				continue;
	            		
	        			arrow.myArrow.addBond(tokenA.myToken, tokenB.myToken);
	        		}
        		}
        		
        		Node negBonds = parseNode(eElement,0,"negBonds");
        		if (negBonds.getNodeType() == Node.ELEMENT_NODE) {
	        		NodeList negBondList = parseNodeList((Element)negBonds, "bond");
	        		for(int j=0; j<negBondList.getLength(); j++ ) {
	        			Element bond = (Element)negBondList.item(j);
	        			String tokenNameA = parseText(bond,0,"token")/*+"_"+counter*/;
	        			String tokenNameB = parseText(bond,1,"token")/*+"_"+counter*/;
	        			
	        			TokenUI tokenA = tokenNodes.get(tokenNameA);
	            		if( tokenA == null ) 
	        				continue;
	        			TokenUI tokenB = tokenNodes.get(tokenNameB);
	            		if( tokenB == null ) 
	        				continue;
	            		
	        			arrow.myArrow.addNegativeBond(tokenA.myToken, tokenB.myToken);
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
        	String tokenNameA = parseText(bond,0,"token")/*+"_"+counter*/;
			String tokenNameB = parseText(bond,1,"token")/*+"_"+counter*/;
			
			TokenUI tokenA = tokenNodes.get(tokenNameA);
    		if( tokenA == null ) 
				continue;
			TokenUI tokenB = tokenNodes.get(tokenNameB);
    		if( tokenB == null ) 
				continue;
			
    		BondUI newBond = new BondUI(tokenA, tokenB);
    		
    		
        }
        
        
        return rpn;
	}
	
	public static RPN read(ScrollArea scrollArea, File file) {

		RPN rpn = new RPN(scrollArea);
		
		try {
			readFile(rpn,file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rpn;
	}

}
