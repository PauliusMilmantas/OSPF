import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class RouterClient extends Thread {

	public CommandThread commandThread;
	private Router router;
	private ConnectionTable connectionTable;
	//private boolean done = false; //For stopping routerClient while
	private AtomicBoolean done;
	
	private ServerSocket serverSocket;
	
	public RouterClient(Router router) {
		
		this.router = router;
		this.connectionTable = router.connectionTable;
		
		done = new AtomicBoolean(false);
		
		commandThread = new CommandThread(router);
		
		try {
			serverSocket = new ServerSocket(router.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		start();
	}
	
	public void run() {
		while(commandThread.isFinished() == false && !done.get()) {
			try {
				Socket clientSocket = serverSocket.accept();
				
				new InputHandler(clientSocket, router);
				
			} catch(Exception e) {	//No one tries to join
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) { //Closed during sleep
					//e1.printStackTrace();
				}
			}
		}
	}
	
	public void close() {
		
		try {
			done.set(true);
			commandThread.interrupt();
			//commandThread.join();
		//} catch (InterruptedException e) {
		//	e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//System.out.println("test");
		
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
