import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.Timer;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import alg.*;

public class Router {

	public static boolean DEBUG = true;
	
	public RouterClient client;
	public RouterServer server;
	
	public ConnectionTable connectionTable;
	private Table table;
	
	public int amountOfEndTables = 0;
	
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
		
		
		
		
		
		
		table.setRID("192.168.1.104");
		table.readTable("/Storage/192.168.1.101/192.168.1.104.txt");
		//table.getAdittionalInfo("Storage/192.168.1.101/192.168.1.103.info.txt");
		connectionTable.table.recalculateDistances();
		
		
		
		
		
		
		
		/*
		Node nodeA = new Node("A");
		Node nodeB = new Node("B");
		Node nodeC = new Node("C");
		Node nodeD = new Node("D"); 
		Node nodeE = new Node("E");
		Node nodeF = new Node("F");
		 
		nodeA.addDestination(nodeB, 10);
		nodeA.addDestination(nodeC, 15);
		 
		nodeB.addDestination(nodeD, 12);
		nodeB.addDestination(nodeF, 15);
		 
		nodeC.addDestination(nodeE, 10);
		 
		nodeD.addDestination(nodeE, 2);
		nodeD.addDestination(nodeF, 1);
		 
		nodeF.addDestination(nodeE, 5);
		 
		Graph graph = new Graph();
		 
		graph.addNode(nodeA);
		graph.addNode(nodeB);
		graph.addNode(nodeC);
		graph.addNode(nodeD);
		graph.addNode(nodeE);
		graph.addNode(nodeF);
		 
		graph = Dijkstra.calculateShortestPathFromSource(graph, nodeA);
		
		System.out.println(graph.nodes.toString());	
		*/
		
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
		/*
		System.out.println("Running router on: " + ip + ":" + port + " as " + RID);
		
		client = new RouterClient(this);
		server = new RouterServer(client, connectionTable, this);
		
		timer = new Timer();
		timer.schedule(new HelloThread(this), 6000, 6000);*/
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
