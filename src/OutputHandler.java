import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;

public class OutputHandler {

	Client client;
	Router router;
	private BufferedWriter writer;
	
	public OutputHandler(Router router, Client client) {
		this.router = router;
		this.client = client;
		
		client.setOutputHandler(this);
	}
	
	public void sendMessage(String message) {
		try {			
			writer = new BufferedWriter(new OutputStreamWriter(client.getSocket().getOutputStream()));
			writer.write(message);
			writer.close();
		} catch (IOException e) {	//Couldn't reach client
			//if(router.DEBUG) System.out.println("[DEBUG] Couldn't reach " + client.getRID());
			
			Socket ss;
			try {
				ss = new Socket(client.getIp(), client.getPort());
				client.setSocket(ss);
				
				writer = new BufferedWriter(new OutputStreamWriter(client.getSocket().getOutputStream()));
				writer.write(message);
				writer.close();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				//Host went offline
				close();
				try {
					client.getSocket().close();
				} catch (IOException e2) {
					//Already closed!
				}
			}
		}
	}
	
	public void close() {
		
	}
}
