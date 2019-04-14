import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

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
		System.out.println("Updatating...");//=====
		
		ArrayList<Client> list = connectionTable.getClients();
		
		for(int a = 0; a < list.size(); a++) {
			if(list.get(a).getConnectionStatus() != 1) {
				try {
					Socket ss = new Socket(list.get(a).getIp(), list.get(a).getPort());
					
					
					//==================== TO OUTPUTHANDLER
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ss.getOutputStream()));
					writer.write("Hello " + router.RID + "\t");
					writer.close();
					//=====================
					
					list.get(a).setConnectionStatus(1);
					list.get(a).setSocket(ss);
				} catch (IOException e) { //Couldn't connect
					//e.printStackTrace();
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
