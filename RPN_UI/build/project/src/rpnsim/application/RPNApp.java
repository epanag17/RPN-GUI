package rpnsim.application;


import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class RPNApp extends Application {
	
	
	public static Stage stage;
	
	protected static String appName = "RPN Editor & Simulator";
	protected static String fileName = "Untitled";
	
	public static void AddAsterisk() {
		stage.setTitle(appName + " - * " + fileName);
	}
	
	public static void RemoveAsterisk() {
		stage.setTitle(appName + " - " + fileName);
	}
	
	public static void Rename( String newFileName ) {
		fileName = newFileName;
		stage.setTitle(appName + " - " + fileName);
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		RPNApp.stage = primaryStage;
		primaryStage.setTitle(appName + " - " + fileName);
		
		BorderPane root = new BorderPane();
		
		try {
			
			Scene scene = new Scene(root,1000,700);
			scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
			/*stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) {
		              System.out.println("Stage is closing");
		          }
		      });*/
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		root.setCenter(new RootLayout(primaryStage));
		
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
