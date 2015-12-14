package server;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

public class MessageHandler implements Runnable {

	public MessageHandler(){}
	
	@Override
	public void run() {
	while (true){
	
	Iterator it = Server.messageQueue.iterator();
		
	    while (it.hasNext()){
			Message nextMessage = Server.messageQueue.remove();
			sendMessage(nextMessage);
			Server.LOG.info("Message send: " + nextMessage.getContent());
			}
		}
	}	
		
/**
 * 
 * @param message
 * Broadcasting all messages from Message Queue to all connected client
 */
	public void sendMessage(Message msg){
	
	Iterator it = Server.clientOutputStreams.iterator();
	
    while (it.hasNext()) 
    {
        try 
        {
			ObjectOutputStream outputStream = (ObjectOutputStream) it.next();
        	outputStream.writeObject(msg);
        	outputStream.flush();

        } 
        catch (Exception ex) {
        	Server.LOG.warning("Send Error: " + ex);
        }
    }
	
	}
}