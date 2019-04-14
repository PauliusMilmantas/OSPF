import java.net.Socket;

public class RouterServer extends Thread {

	private RouterClient client;
	private ConnectionTable connectionTable;
	
	public RouterServer(RouterClient client, ConnectionTable connectionTable) {
		this.client = client;
		this.connectionTable = connectionTable;	
	}
	
	public void run() {
		
	}
}
