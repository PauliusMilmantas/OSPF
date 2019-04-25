import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RouterClient extends Thread {

	public CommandThread commandThread;
	private Router router;
	private ConnectionTable connectionTable;
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
	
	/**
	 * Gets routing tables from other routers and prints them
	 */
	public void printAllTables() {
		if(router.DEBUG) 
			System.out.println("[OUT] LSR");
		
		ArrayList<String> RIDs = connectionTable.table.getRIDs();
		ArrayList<Client> clients = connectionTable.getClients();
		
		for(int a = 0; a < RIDs.size(); a++) {
			String nextHop = connectionTable.table.getNextHop(RIDs.get(a));
			
			for(int b = 0; b < clients.size(); b++) {
				if(clients.get(b).getRID().equals(nextHop)) {
					OutputHandler handler = clients.get(b).getOutputHandler();
					handler.sendMessage("LSR " + router.RID + " " + RIDs.get(a));
				}
			}
		}
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
	
	/**
	 * Sends given routing table to destination RID
	 * @param DestinationRID
	 */
	public void sendTable(String DestinationRID, Table table) {
		if(router.DEBUG) 
			System.out.println("[OUT][" + DestinationRID + "] LSU TABLE");
		
		String nextHop = router.getTable().getNextHop(DestinationRID);
		
		ArrayList<Client> clients = connectionTable.getClients();
		
		for(int a = 0; a < clients.size(); a++) {
			if(clients.get(a).getRID().equals(nextHop)) {
				OutputHandler handler = clients.get(a).getOutputHandler();
	
				sleepForSecond();
				handler.sendMessage("LSU TABLE RID: " + table.getRID());
				sleepForSecond();
				sleepForSecond();
				handler.sendMessage("LSU TABLE " +  table.getRID() + " IP: " + table.getIp());
				handler.sendMessage("LSU TABLE " + table.getRID() + " PORT: " + table.getPort());
				sleepForSecond();
				
				ArrayList<String> RIDs = table.getRIDs();
				ArrayList<String> ipList = table.getIpList();
				ArrayList<String> nextHops = table.getNextHops();
				ArrayList<Integer> hops = table.getHops();
				ArrayList<Integer> ports = table.getPorts();
				
				for(int b = 0; b < RIDs.size(); b++) {
					handler.sendMessage("LSU TABLE " + table.getRID() + " " + ipList.get(b) + ":" + ports.get(b) + " " + RIDs.get(b) + " " + nextHops.get(b) + " " + hops.get(b));
				}
				
				sleepForSecond();
				sleepForSecond();
				sleepForSecond();
				
				handler.sendMessage("LSU ENDTABLE " + table.getRID() + " " + DestinationRID);
			}
		}
	}
	
	public void sendTable(String DestinationRID) {
		sendTable(DestinationRID, router.getTable());
	}

	private void sleepForSecond() {
		try {	//To avoid getting ENDTABLE sent too early
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
