public class RouterClient {

	private CommandThread commandThread;
	private Router router;
	
	public RouterClient(Router router) {
		
		this.router = router;
		
		commandThread = new CommandThread(router);
	}	
}
