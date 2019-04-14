import java.net.Socket;
import java.util.ArrayList;

public class RouterServer extends Thread {

	private RouterClient client;
	private ConnectionTable connectionTable;
	
	public RouterServer(RouterClient client, ConnectionTable connectionTable) {
		this.client = client;
		this.connectionTable = connectionTable;	
	}
	
	public void run() {
		
	}
	
	/**
	 * Updates sockets for all neighbours
	 */
	public void connect() {
		System.out.println("Updatating...");//=====
		
		ArrayList<Client> list = connectionTable.getClients();
		
		
	}
}
