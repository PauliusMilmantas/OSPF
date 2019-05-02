import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Timer;

public class ConnectionTable {

	public ArrayList<Client> clients;
	
	/**
	 * For TimeoutThread
	 */
	public ArrayList<Timer> timers;
	public ArrayList<TimeoutThread> timeoutThreads;
	public Table table;
	
	private Router router;
	
	public ConnectionTable(Table table, Router router) {
		this.table = table;
		this.router = router;
		
		clients = new ArrayList<>();
		timers = new ArrayList<>();
		
		timeoutThreads = new ArrayList<>();
		
		ArrayList<String> nb = table.getNeighbours();
		for(int a = 0; a < nb.size(); a++) {
			Client cl = new Client(nb.get(a), table.getIp(nb.get(a)), table.getPort(nb.get(a)));
			clients.add(cl);
			
			new OutputHandler(router, cl);
			
			Timer t = new Timer();
			timers.add(t);
			TimeoutThread tt = new TimeoutThread(cl, router);
			timeoutThreads.add(tt);
			t.schedule(tt, 3000, 3000);
		}
	}
	
	public void addLink(String RID) {
		ArrayList<String> nb = table.getNeighbours();
		
		for(int a = 0; a < nb.size(); a++) {
			if(nb.get(a).equals(RID)) {
				Client cl = new Client(RID, table.getIp(RID), table.getPort(RID));
				clients.add(cl);
				
				Timer t = new Timer();
				timers.add(t);
				t.schedule(new TimeoutThread(cl, router), 3000, 3000);
			}
		}
	}
	
	public ArrayList<Client> getClients() {
		return clients;
	}
	
	public void status() {
		for(int a = 0; a < clients.size(); a++) {
			System.out.print(clients.get(a).getIp() + ":" + clients.get(a).getPort() + "\t" + clients.get(a).getRID() + "\t");
			
			switch(clients.get(a).getConnectionStatus()) {
				case 0:
					System.out.println("Offline");
					break;
				case 1:
					System.out.println("Online");
					break;
				case 2:
					System.out.println("Down");
					break;
				default:
					System.out.println("Undefined");
					break;
			}
		}
	}
	
	public void removeRouter(String RID) {
		if(table.hasRouter(RID)) {
			table.removeRouter(RID);
			
			//From storage
			File ff = new File(System.getProperty("user.dir") + "\\Storage\\" + router.RID + "\\" + RID + ".txt");
			ff.delete();
			
			//From connectionTable
			for(int a = 0; a < clients.size(); a++) {
				if(clients.get(a).getRID().equals(RID)) {
					clients.get(a).close();
					clients.remove(a);
					System.out.println(RID + " was removed!");
				}
			}
			
			//From table
			boolean ch = true;
			while(!ch) {
				ch = false;
				for(int a = 0; a < table.RIDs.size(); a++) {
					if(table.getNextHop(table.getRIDs().get(a)).equals(RID)) {
						table.RIDs.remove(a);
						table.ipList.remove(a);
						table.nextHop.remove(a);
						table.hops.remove(a);
						table.ports.remove(a);
						ch = true;
					}
				}
			}
			
			//Send LSU messages to others
			for(int a = 0; a < clients.size(); a++) {
				if(clients.get(a).getConnectionStatus() == 1) {
					clients.get(a).getOutputHandler().sendMessage("LSU " + RID + " 0");
				}
			}
			
			router.recalculate();
		}
	}
	
	public void close() {
		for(int a = 0; a < clients.size(); a++) {
			clients.get(a).close();
		}
		
		for(int a = 0; a < timers.size(); a++) {
			timers.get(a).cancel();
		}
	}
}
