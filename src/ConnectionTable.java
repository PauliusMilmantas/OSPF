import java.util.ArrayList;

public class ConnectionTable {

	private ArrayList<Client> clients;
	private Table table;
	
	public ConnectionTable(Table table) {
		this.table = table;
		
		clients = new ArrayList<>();
		
		ArrayList<String> nb = table.getNeighbours();
		for(int a = 0; a < nb.size(); a++) {
			clients.add(new Client(nb.get(a), table.getIp(nb.get(a)), table.getPort(nb.get(a))));
		}
	}
	
	public ArrayList<Client> getClients() {
		return clients;
	}
	
	public void status() {
		for(int a = 0; a < clients.size(); a++) {
			System.out.print(clients.get(a).getIp() + clients.get(a).getPort() + "\t" + clients.get(a).getRID() + "\t");
			
			switch(clients.get(a).getConnectionStatus()) {
				case 0:
					System.out.println("Offiline");
					break;
				case 1:
					System.out.println("Online");
					break;
				default:
					System.out.println("Undefined");
					break;
			}
		}
	}
}
