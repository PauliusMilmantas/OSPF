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
}
