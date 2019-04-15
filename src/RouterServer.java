import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;

public class RouterServer extends Thread {

	private RouterClient client;
	private ConnectionTable connectionTable;
	private Router router;
	
	public RouterServer(RouterClient client, ConnectionTable connectionTable, Router router) {
		this.client = client;
		this.connectionTable = connectionTable;
		this.router = router;
	}
	
	public void run() {
		
	}
	
	/**
	 * Updates sockets for all neighbours
	 */
	public void connect() {
		ArrayList<Client> list = connectionTable.getClients();
		
		for(int a = 0; a < list.size(); a++) {
			if(list.get(a).getConnectionStatus() != 1) {
				try {
					Socket ss = new Socket(list.get(a).getIp(), list.get(a).getPort());
					list.get(a).setSocket(ss);
					
					new OutputHandler(router, list.get(a));
					list.get(a).getOutputHandler().sendMessage("Hello " + router.RID);

					list.get(a).setSocket(ss);
				} catch (IOException e) { //Couldn't connect
					//e.printStackTrace();
				}
			} else {
				if(list.get(a).getOutputHandler() == null) {
					Socket ss;
					
					try {
						ss = new Socket(list.get(a).getIp(), list.get(a).getPort());
						
						list.get(a).setSocket(ss);
						
						new OutputHandler(router, list.get(a));
						list.get(a).getOutputHandler().sendMessage("Hello " + router.RID);	
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}				
				} else {
					list.get(a).getOutputHandler().sendMessage("Hello " + router.RID);
				}
			}
		}
	}
	
	public void close() {
		ArrayList<Client> clients = connectionTable.getClients();
		for(int a = 0; a < clients.size(); a++) {
			try {
				clients.get(a).getSocket().close();
			} catch (IOException e) { //Already closed
				e.printStackTrace();
			}
		}
	}
}
