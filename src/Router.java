import java.util.Timer;

public class Router {

	public static boolean DEBUG = true;
	
	public RouterClient client;
	public RouterServer server;
	
	public ConnectionTable connectionTable;
	private Table table;
	
	public int port;
	public String RID;
	public String ip;
	
	private Timer timer;
	
	public static void main(String[] args) {
		//Ip address:port, RID
		if(args.length > 0) new Router(args);
		else new Router();
	}

	private Router(String[] args) {
		
		ip = args[0].split(":")[0];
		RID = args[1];
		port = Integer.parseInt(args[0].split(":")[1]);
		
		//=============================================================================
		//port += 1000;
		
		table = new Table(ip, RID, port);
		connectionTable = new ConnectionTable(table, this);
		
		System.out.println("Running router on: " + ip + ":" + port + " as " + RID);
		
		client = new RouterClient(this);
		server = new RouterServer(client, connectionTable, this);
		
		timer = new Timer();
		timer.schedule(new HelloThread(this), 6000, 6000);
	}

	public void close() {
		client.close();

		/*
		try {
			client.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		timer.cancel();
		
		connectionTable.close();
		client.interrupt();
		server.interrupt();		
	}
	
	private Router() {
		String[] args = new String[2];
		/*
		args[0] = "127.0.0.1:92";
		args[1] = "192.168.1.2";
		*/
		
		args[0] = "127.0.0.1:93";
		args[1] = "192.168.1.3";		
		new Router(args);
	}
	
	public void test() {
		System.out.println("Test");
	}
	
	public Table getTable() {
		return table;
	}
}
