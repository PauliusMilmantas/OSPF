import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

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
		
		table = new Table(ip, RID, port);
		table.readTable();
		connectionTable = new ConnectionTable(table, this);
		
		//Clearing info file
		try {
			File file = new File(System.getProperty("user.dir") + "\\Storage\\" + RID);
			file.mkdir();
			
			file = new File(System.getProperty("user.dir") + "\\Storage\\" + RID + "\\info.txt");
			file.createNewFile();
			
			PrintWriter writer = new PrintWriter(file);
			writer.print("");
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Running router on: " + ip + ":" + port + " as " + RID);
		
		client = new RouterClient(this);
		server = new RouterServer(client, connectionTable, this);
		
		timer = new Timer();
		timer.schedule(new HelloThread(this), 6000, 6000);
	}

	public void addLink(String RID, String ip, int port, int hop) {
		client.connectionTable.addLink(RID);
		table.addLink(RID, ip, port, hop);
		
		server.connectionTable.addLink(RID);
		
		for(int a = 0; a < 4; a++) {
			sleepForSecond();
		}
		
		client.sendMessage(RID, "Hello " + this.RID + " new " + this.ip + ":" + this.port);
		client.sendTable(RID);
		
		if(DEBUG) System.out.println("[DEBUG] Link added.");
	}
	
	private void sleepForSecond() {
		try {	//To avoid getting ENDTABLE sent too early
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		client.close();

		timer.cancel();
		
		connectionTable.close();
		client.interrupt();
		server.interrupt();		
	}
	
	private Router() {
		String[] args = new String[2];
		
		args[0] = "127.0.0.1:9993";
		args[1] = "192.168.1.99";		
		new Router(args);
	}
	
	public void test() {
		System.out.println("Test");
	}
	
	public Table getTable() {
		return table;
	}
}
