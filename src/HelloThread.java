import java.util.TimerTask;

public class HelloThread extends TimerTask {

	private Router router;
	
	public HelloThread(Router router) {
		this.router = router;
	}
	
	public void run() {
		router.server.connect();
	}
}
