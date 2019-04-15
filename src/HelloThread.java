import java.util.TimerTask;

public class HelloThread extends TimerTask {

	private OutputHandler outputHandler;
	private Client client;
	private Router router;
	
	public HelloThread(OutputHandler outputHandler, Client client, Router router) {
		this.outputHandler = outputHandler;
		this.client = client;
		this.router = router;
	}
	
	public void run() {
		router.server.connect();
	}
}
