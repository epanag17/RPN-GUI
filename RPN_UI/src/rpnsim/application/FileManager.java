package rpnsim.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.w3c.dom.Document;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import rpnsim.application.editor.EditorArea;
import rpnsim.application.model.RPN;
import rpnsim.application.simulator.RPNVerifier;



public class FileManager {
	
	private Stage stage;
	private ScrollArea scrollArea;
	private Document doc;
	
	private RPN rpn;
	private FileManager self;
	
	private File savedFile;
	private Menu recentMenu;
	
	
	public ScrollArea getScrollArea() {
		return scrollArea;
	}
	
	public FileManager(Stage stage, RPN rpn, ScrollArea scrollArea, Menu recentMenu) {
		this.stage = stage;
		this.scrollArea = scrollArea;
		this.self = this;
		this.rpn = rpn;
		this.recentMenu = recentMenu;
		updateRecentMenu(recentMenu);
	}
	
	public void setRPN(RPN rpn) {
		this.rpn = rpn;
	}
	
	private void updateRecentMenu(Menu recentMenu) {
		
		if(recentMenu == null)
			return;
		
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
	    		
	    		scrollArea.clearAll();
	    		
		    	RPN rpn = new RPN(scrollArea);
	            RPNVerifier.setRPN(rpn);
	            
	            scrollArea.setRPN(rpn);
	            
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
        	
        	RPNXMLWriter.write(rpn, file);
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
				RPNXMLWriter.write(rpn, savedFile);
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
            	
            	scrollArea.clearAll();
            	
                RPN rpn = RPNXMLReader.read(scrollArea, file);
                RPNVerifier.setRPN(rpn);
                scrollArea.setRPN(rpn);
                
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
            	
            	scrollArea.clearAll();
            	
            	RPN rpn = RPNXMLReader.read(scrollArea, file);
                RPNVerifier.setRPN(rpn);
                scrollArea.setRPN(rpn);
                
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
		


}
