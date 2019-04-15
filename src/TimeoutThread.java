import java.util.TimerTask;

public class TimeoutThread extends TimerTask {

	Client client;
	Router router;
	
	public TimeoutThread(Client client, Router router) {
		this.client = client;
		this.router = router;
	}
	
	public void run() {
		if(client.getConnectionStatus() == 1) {
			if((System.currentTimeMillis() - client.getTime().getTime())/1000 > 12) {
				System.out.println(client.getRID() + " went offline.");
				client.setConnectionStatus(2);
			}
		}
	}
}
