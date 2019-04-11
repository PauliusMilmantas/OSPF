public class Router {

	public static final boolean DEBUG = true;
	
	private RouterClient client;
	private RouterServer server;
	
	private ConnectionTable connectionTable;
	private Table table;
	
	private int port;
	private String RID;
	private String ip;
	
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
		
		System.out.println("Running router on: " + ip + ":" + port + " as " + RID);
		
		client = new RouterClient(this);
		
		
		
		
		
	}

	private Router() {
		String[] args = new String[2];
		args[0] = "127.0.0.1:92";
		args[1] = "192.168.1.2";
		new Router(args);
	}
	
	public void test() {
		System.out.println("Test");
	}
	
	public Table getTable() {
		return table;
	}
}
