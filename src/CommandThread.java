

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandThread extends Thread {

	AtomicBoolean finished;
	private Scanner scanner;
	private Router router;
	
	public CommandThread(Router router) {
		finished = new AtomicBoolean(false);
		this.router = router;
		
		start();
	}
	
	public void run() {
		String line;
		scanner = new Scanner(System.in);
		
		while(!finished.get()) {
			line = scanner.nextLine();
			
			switch(line.split(" ")[0]) {
				case "q":
					finished.set(true);
					System.out.println("Quitting...");
					router.close();
					break;
				case "seer":
					router.getTable().seer();
					break;
				case "test":
					router.server.connect();
				break;
				case "status":
					router.connectionTable.status();
				break;
				case "help":
					System.out.println("-------========== H E L P ==================-------");
					System.out.println("q - quit");
					System.out.println("seer - See routing table");
					System.out.println("status - Show status for all neighbours");
					System.out.println("-------=====================================-------");
					break;
				default:
					System.out.println("Unknown command");
					break;
			}
		}
	}
	
	public boolean isFinished() {
		return finished.get();
	} 
}
