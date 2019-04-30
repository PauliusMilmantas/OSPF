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
		//if(router.DEBUG) System.out.println("[OUT] " + message);
		
		if(client == null) System.out.println("ERROR");
		
		try {			
			writer = new BufferedWriter(new OutputStreamWriter(client.getSocket().getOutputStream()));
			writer.write(message);
			writer.flush();
			writer.close();
		} catch (IOException e) {	//Couldn't reach client		
			Socket ss;
			try {
				ss = new Socket(client.getIp(), client.getPort());
				client.setSocket(ss);
				
				writer = new BufferedWriter(new OutputStreamWriter(client.getSocket().getOutputStream()));
				writer.write(message);
				writer.flush();
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
