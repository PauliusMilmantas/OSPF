import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RouterClient extends Thread {

	public CommandThread commandThread;
	private Router router;
	private ConnectionTable connectionTable;
	//private boolean done = false; //For stopping routerClient while
	private AtomicBoolean done;
	
	public ArrayList<String> messages;
	
	private ServerSocket serverSocket;
	
	public RouterClient(Router router) {
		
		this.router = router;
		this.connectionTable = router.connectionTable;
		
		messages = new ArrayList<>();
		done = new AtomicBoolean(false);
		
		commandThread = new CommandThread(router);
		
		try {
			serverSocket = new ServerSocket(router.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		start();
	}
	
	public void forwardMessage() {
		
	}
	
	public void sendOverNetwork(String DestinationRID, String message) {
		
		if(router.DEBUG) 
			System.out.println("[OUT][" + DestinationRID + "] " + message);
		
		String nextHop = router.getTable().getNextHop(DestinationRID);
		
		ArrayList<Client> clients = connectionTable.getClients();
		
		for(int a = 0; a < clients.size(); a++) {
			if(clients.get(a).getRID().equals(nextHop)) {
				clients.get(a).getOutputHandler().sendMessage("MESSAGE " + DestinationRID + " " + message);
			}
		}
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
