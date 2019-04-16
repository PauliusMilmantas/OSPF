import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class InputHandler extends Thread {

	private Socket socket;
	private BufferedReader reader;
	private AtomicBoolean done;
	private ConnectionTable connectionTable;
	private Router router;
	
	private ArrayList<String> messages;
	
	public InputHandler(Socket socket, Router router) {
		this.socket = socket;
		this.router = router;
		this.connectionTable = router.connectionTable;
		
		messages = new ArrayList<>();
		done = new AtomicBoolean(false);
		
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		start();
	}
	
	public void run() {
		
		while(!done.get()) {
			String line;
			try {
				if(reader.ready()) {
					line = reader.readLine();
					
					if(line != null) {
						if(Router.DEBUG) System.out.println("[IN]" + line);
						
						switch(line.split(" ")[0]) {
							case "Hello":
								ArrayList<Client> clients = connectionTable.getClients();
								
								for(int a = 0; a < clients.size(); a++) {
									Client client = clients.get(a);
									
									if(client.getRID().equals(line.split(" ")[1].split("\t")[0])) {
										client.setTime(new Timestamp(System.currentTimeMillis()));
										
										if(client.getConnectionStatus() != 1) {
											client.setConnectionStatus(1);
											client.setInputHandler(this);
											client.setSocket(socket);
										}
									}
								}
								
								break;
							case "LSU":	//For remove/adding router to table
									if(line.split(" ")[2].equals("0")) {	//Remove router
										connectionTable.removeRouter(line.split(" ")[1]);
									}					
								break;
							case "LSR":
									
								break;
							case "MESSAGE":
								String RID = line.split(" ")[1];
								String msg = line.substring(RID.length() + 9, line.length());

								if(RID.equals(router.RID)) {
									System.out.println(msg);
								} else {
									System.out.println("Message received. Press s to forward");
									
									router.client.messages.add(line);
									
									/*
									String nextHop = router.getTable().getNextHop(RID);
									
									ArrayList<Client> clientsss = connectionTable.getClients();
									
									for(int a = 0; a < clientsss.size(); a++) {
										if(clientsss.get(a).getRID().equals(nextHop)) {
											clientsss.get(a).getOutputHandler().sendMessage("MESSAGE " + RID + " " + msg);
										}
									}
									*/
								}
								break;
							default:
								System.out.println("Unrecognised command");
								break;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public void close() {
		try {
			reader.close();
		} catch(Exception e) {
			//Already closed
		}
	}
}
