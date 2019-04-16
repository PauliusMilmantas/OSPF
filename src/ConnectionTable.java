import java.util.ArrayList;
import java.util.Timer;

public class ConnectionTable {

	private ArrayList<Client> clients;
	
	/**
	 * For TimeoutThread
	 */
	private ArrayList<Timer> timers;
	public Table table;
	
	private Router router;
	
	public ConnectionTable(Table table, Router router) {
		this.table = table;
		this.router = router;
		
		clients = new ArrayList<>();
		timers = new ArrayList<>();
		
		ArrayList<String> nb = table.getNeighbours();
		for(int a = 0; a < nb.size(); a++) {
			Client cl = new Client(nb.get(a), table.getIp(nb.get(a)), table.getPort(nb.get(a)));
			clients.add(cl);
			
			Timer t = new Timer();
			timers.add(t);
			t.schedule(new TimeoutThread(cl, router), 1000, 1000);
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
			
			//From table
			for(int a = 0; a < clients.size(); a++) {
				if(clients.get(a).getRID().equals(RID)) {
					clients.get(a).close();
					clients.remove(a);
					System.out.println(RID + " was removed!");
				}
			}
			
			//Send LSU messages to others
			for(int a = 0; a < clients.size(); a++) {
				if(clients.get(a).getConnectionStatus() == 1) {
					clients.get(a).getOutputHandler().sendMessage("LSU " + RID + " 0");
				}
			}
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
