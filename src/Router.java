import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
	public Table table;
	
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
		
		/*
		table.setRID("192.168.1.104");
		table.readTable("/Storage/192.168.1.101/192.168.1.104.txt");
		//table.getAdittionalInfo("Storage/192.168.1.101/192.168.1.103.info.txt");
		Graph gr = connectionTable.table.recalculateDistances();
		
		System.out.println(gr.nodes.toString());
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
		
		System.out.println("Running router on: " + ip + ":" + port + " as " + RID);
		
		client = new RouterClient(this);
		server = new RouterServer(client, connectionTable, this);
		
		timer = new Timer();
		timer.schedule(new HelloThread(this), 6000, 6000);
	}

	/**
	 * Recalculates distances to other routers
	 */
	public void recalculate() {
		if(DEBUG) System.out.println("[DEBUG] Recalculating distances...");
		
		Graph graph = table.recalculateDistances();
		String line = graph.nodes.toString().substring(1, graph.nodes.toString().length() - 1);
		System.out.println("Analysing: " + line);
		String args[] = line.split(", ");
		
		for(int a = 0; a < args.length; a++) {
			boolean found = false;
			int dist = 0;
			int id = 0;
			
			String gh[] = args[a].split("-");
			for(int b = 0; b < table.RIDs.size(); b++) {
				if(table.RIDs.get(b).equals(gh[0])) {
					System.out.println("Analysin destination: " + gh[0]);
					id = b;
					found = true;
					dist = Integer.parseInt(gh[1]);
					table.hops.set(b, Integer.parseInt(gh[1]));
					
					//Next hop calculation
					String nextRID = null;
					
					int min = 100;
					for(int c = 0; c < table.getNeighbours().size(); c++) {
						System.out.println("Analysing neighb.: " + table.getNeighbours().get(c));
						
						Table tt = new Table();
						tt.setRID(table.getNeighbours().get(c));
						tt.readTable("Storage\\" + RID + "\\" + table.getNeighbours().get(c) + ".txt");
						
						Graph g = tt.recalculateDistances();
						String dfg = g.nodes.toString().substring(1, g.nodes.toString().length() - 1);
						String sdfh[] = dfg.split(", ");
		
						for(int l = 0; l < sdfh.length; l++) {
							if(sdfh[l].split("-")[0].equals(gh[0])) {
								String dd = sdfh[l].split("-")[1];
								
								System.out.println("Found distance " + table.getNeighbours().get(c) + "-" + gh[0] + " " + Integer.parseInt(dd));
								
								if(Integer.parseInt(dd) < min) {
									min = Integer.parseInt(dd);
									nextRID = table.getNeighbours().get(c);
								}
							}
						}
					}
					
					//Neighbours....?
					if(nextRID == null) nextRID = gh[0];
					
					//System.out.println("Selected: " + nextRID + " Distance: " + min);
					
					table.nextHop.set(b, nextRID);
				}			
			}
			
			if(dist == 2147483647) {
				if(found) {
					if(DEBUG) System.out.println(gh[0] + " was removed");
					
					int g = table.RIDs.indexOf(gh[0]);
					table.RIDs.remove(g);
					table.ipList.remove(g);
					table.nextHop.remove(g);
					table.hops.remove(g);
					table.ports.remove(g);
				}
			} else {
				if(found) {
					//System.out.println(gh[0] + " found");
				} else {
					//System.out.println(gh[0] + " not found");
				}	
			}	
		}
		
		//Sending LSU
		for(int a = 0; a < table.RIDs.size(); a++) {
			client.sendTable(table.RIDs.get(a));
		}
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
