import java.util.ArrayList;
import java.util.TimerTask;

public class TimeoutThread extends TimerTask {

	public Client client;
	Router router;
	
	public TimeoutThread(Client client, Router router) {
		this.client = client;
		this.router = router;
	}
	
	public void run() {	
		if(client.getConnectionStatus() == 1 || client.getConnectionStatus() == 0) {
			if((System.currentTimeMillis() - client.getTime().getTime())/1000 > 16) {
				System.out.println(client.getRID() + " went offline.");
				client.setConnectionStatus(2);
			}
		} else if(client.getConnectionStatus() == 2) {
			if((System.currentTimeMillis() - client.getTime().getTime())/1000 > 25) {
				
				ArrayList<Client> clients = router.connectionTable.getClients();
				
				for(int a = 0; a < clients.size(); a++) {
					if(clients.get(a).getConnectionStatus() == 1) {
						clients.get(a).getOutputHandler().sendMessage("LSU " + client.getRID() + " 0");
					}
				}
				
				router.connectionTable.removeRouter(client.getRID());
				this.cancel();
			}
		}
	}
}
