/**
 * Sample Skeleton for 'jConnectUI.fxml' Controller Class
 */

package client;

import java.io.IOException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.*;

public class jConnectUIController {
	public InetAddress addr;
	public String username;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="fieldUsername"
    private TextField fieldUsername; // Value injected by FXMLLoader

    @FXML // fx:id="fieldIP"
    private TextField fieldIP; // Value injected by FXMLLoader

    @FXML // fx:id="buConnect"
    private Button buConnect; // Value injected by FXMLLoader

    @FXML
    void doConnect(ActionEvent event){
    	loadChatWindow(event);
    }
    
    public void loadChatWindow(ActionEvent event ){
    	try{
	        addr = InetAddress.getByName(fieldIP.getText());
	        username = fieldUsername.getText();
	        System.out.print("testaddr:"+addr);
    		Client client = new Client(addr,username);
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("jChatAppUI.fxml"));
    		Parent root = (Parent) loader.load();
    		loader.<jChatAppUIController>getController().init(client);
    		Scene scene = new Scene(root);
    		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    		appStage.setScene(scene);
    		appStage.show();
    	}
    	catch (IOException e) {
                e.printStackTrace();
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert fieldUsername != null : "fx:id=\"fieldUsername\" was not injected: check your FXML file 'jConnectUI.fxml'.";
        assert fieldIP != null : "fx:id=\"fieldIP\" was not injected: check your FXML file 'jConnectUI.fxml'.";
        assert buConnect != null : "fx:id=\"buConnect\" was not injected: check your FXML file 'jConnectUI.fxml'.";

    }
}
