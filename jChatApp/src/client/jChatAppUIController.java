/**
 * Sample Skeleton for 'jChatAppUI.fxml' Controller Class
 */

package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import server.Message;
import server.MessageType;
import javafx.event.*;

public class jChatAppUIController extends AnchorPane implements Observer{
	static final int PORT = 1337;

	public static Socket socket;
	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="chatArea"
	private TextArea chatArea; // Value injected by FXMLLoader

	@FXML // fx:id="userLabel"
	private Label userLabel; // Value injected by FXMLLoader

	@FXML // fx:id="userList"
	private ListView<String> userList = new ListView(ClientReceiver.items); // Value injected by FXMLLoader

	@FXML // fx:id="sendArea"
	private TextArea sendArea; // Value injected by FXMLLoader

	@FXML // fx:id="buSend"
	private Button buSend; // Value injected by FXMLLoader

	@FXML // fx:id="buConnect"
	private Button buConnect; // Value injected by FXMLLoader
	

	private Client client;

	@FXML
	void doNewMessage(ActionEvent event) throws IOException, ClassNotFoundException {
		sendMessage();
		sendArea.requestFocus();
	}
	
	@FXML
	void pressedEnter(KeyEvent event) throws IOException, ClassNotFoundException {		
		if (event.getCode() == KeyCode.ENTER&&!event.isShiftDown()) {
			sendMessage();
			event.consume();
		}
		if(event.getCode()==KeyCode.ENTER&&event.isShiftDown()){
			sendArea.appendText("\n");			
		}
	}
	
	
	private void sendMessage() throws UnknownHostException {
		int systemMessage = 1;
		try{
			//System.out.println("string: "+sendArea.getText().substring(0, 10));//nur zu testzwecken
			if(sendArea.getText().subSequence(0, 10).equals("/username ")){
				Client.user.setUsername(sendArea.getText().substring(10));
				systemMessage = 1;
			}
			if(sendArea.getText().subSequence(0, 9).equals("/connect ")){
				String newIP=sendArea.getText().substring(9);
				//Client.addr=InetAddress.getByName(newIP);
				systemMessage = 1;
				//System.out.println("Connected to: "+Client.addr);
			}
		}
		catch (IndexOutOfBoundsException e) {
			System.out.println(e);
		}		
				
		client.sendText(sendArea.getText(),systemMessage);
		sendArea.clear();
	}
	

	@FXML
	void doConnect(ActionEvent event) throws IOException {
		//ListView userList = new ListView(items);
		ClientReceiver.items.add("tom");
		System.out.println(userList.getItems());
	}
	
    @FXML
    void listLoad(ActionEvent event) {

    }

	@FXML // This method is called by the FXMLLoader when initialization is
			// complete
	void initialize() {
		assert chatArea != null : "fx:id=\"chatArea\" was not injected: check your FXML file 'jChatAppUI.fxml'.";
		assert userLabel != null : "fx:id=\"userLabel\" was not injected: check your FXML file 'jChatAppUI.fxml'.";
		assert userList != null : "fx:id=\"userList\" was not injected: check your FXML file 'jChatAppUI.fxml'.";
		assert sendArea != null : "fx:id=\"sendArea\" was not injected: check your FXML file 'jChatAppUI.fxml'.";
		assert buSend != null : "fx:id=\"buSend\" was not injected: check your FXML file 'jChatAppUI.fxml'.";
		assert buConnect != null : "fx:id=\"buConnect\" was not injected: check your FXML file 'jChatAppUI.fxml'.";
		userList.setItems(ClientReceiver.items);

	}

	public void init(Client client) {
		this.client = client;
		client.addObserver(this); //edit
	}

	@Override
	public void update(Observable client, Object clientArg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				chatArea.appendText((String) clientArg + "\n");
				userList.setItems(ClientReceiver.items);
			}
		});
	}
}