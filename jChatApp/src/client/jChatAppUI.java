package client;
	
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import server.Message;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;


public class jChatAppUI extends Application {
	
	
	public  static ObjectOutputStream output;
	public static Socket socket;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Client client = new Client();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("jChatAppUI.fxml"));
			Parent root = (Parent) loader.load();
			loader.<jChatAppUIController>getController().init(client);
			Scene scene = new Scene(root);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("JavaFX Chat Client");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) throws IOException {				
		launch(args);
	}
	
}
