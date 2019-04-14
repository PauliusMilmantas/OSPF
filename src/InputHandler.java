import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class InputHandler extends Thread {

	private Socket socket;
	private BufferedReader reader;
	private AtomicBoolean done;
	private ConnectionTable connectionTable;
	
	public InputHandler(Socket socket, ConnectionTable connectionTable) {
		this.socket = socket;
		this.connectionTable = connectionTable;
		
		done = new AtomicBoolean(false);
		
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		start();
	}
	
	public void run() {
		
		while(!done.get()) {
			String line;
			try {
				if(reader.ready()) {
					line = reader.readLine();
					
					if(line != null) {
						if(Router.DEBUG) System.out.println("[IN]" + line);
						
						switch(line.split(" ")[0]) {
							case "Hello":
								ArrayList<Client> clients = connectionTable.getClients();
								
								/*for(int a = 0; a < clients.size(); a++) {
									if(clients.get(a).getRID().equals(line.split(" ")[1])) {
										if(clients.get(a).getConnectionStatus() != 1) {
											clients.get(a).setConnectionStatus(1);
										}
									}
								}*/
								
								break;
							default:
								System.out.println("Unrecognised command");
								break;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public void close() {
		try {
			reader.close();
		} catch(Exception e) {
			//Already closed
		}
	}
}
