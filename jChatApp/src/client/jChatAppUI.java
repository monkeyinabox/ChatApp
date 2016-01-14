package client;
	
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;


public class jChatAppUI extends Application {
	public static Stage stage;
	public  static ObjectOutputStream output;
	public static Socket socket;
	public static Stage primaryStage;
	public static Parent root;
	@Override
	public void start(Stage primaryStage) {
		//Scene scene = new Scene(root);
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		//loader.<jChatAppUIController>getController().init(client);
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("jChappApp");
		logInScene();
	}
	
	public void logInScene(){
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("jConnectUI.fxml"));
			root = (Parent) loader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			//primaryStage.setOnCloseRequest(e->Platform.exit());//not working..?
		}catch (IOException e) {
            e.printStackTrace();
        }
	}
	

	public static void main(String[] args) throws IOException {				
		launch(args);
	}
}