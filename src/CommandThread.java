

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
				case "sendt":
					router.client.sendTable(line.split(" ")[1]);
					break;
				case "q":
					finished.set(true);
					System.out.println("Quitting...");
					router.close();
					break;
				case "seer":
					router.getTable().seer();
					break;
				case "status":
					router.connectionTable.status();
					break;
				case "see":
					if(line.equals("see all routers")) {
						router.amountOfEndTables = 0;
						router.client.printAllTables();
					}
					break;
				case "addLink":
					//RID, ip, port, hops
					router.addLink(line.split(" ")[1], line.split(" ")[2], Integer.parseInt(line.split(" ")[3]), 1);
					break;
				case "message":
					String RID = line.split(" ")[1];
					String msg = line.substring(RID.length() + 9, line.length());
					msg = router.RID + ": " + msg;
					
					router.client.sendOverNetwork(RID, msg);
					break;
				case "s":
					if(!router.client.messages.isEmpty()) {
						line = router.client.messages.remove(0);
						
						RID = line.split(" ")[1];
						msg = line.substring(RID.length() + 9, line.length());
						
						String nextHop = router.getTable().getNextHop(RID);
						
						ArrayList<Client> clientsss = router.connectionTable.getClients();
						
						for(int a = 0; a < clientsss.size(); a++) {
							if(clientsss.get(a).getRID().equals(nextHop)) {
								clientsss.get(a).getOutputHandler().sendMessage("MESSAGE " + RID + " " + msg);
							}
						}
					}
					break;
				case "cal":
					router.recalculate();
					break;
				case "d":
					router.DEBUG = !router.DEBUG;
					if(router.DEBUG) System.out.println("Debug mode is ON");
					else System.out.println("Debug mode is OFF");
					break;
				case "help":
					System.out.println("-------========== H E L P ==================-------");
					System.out.println("q - quit");
					System.out.println("seer - See routing table");
					System.out.println("addLink [RID] [ip] [port] - add link");
					System.out.println("status - Show status for all neighbours");
					System.out.println("d - Change DEBUG mode");
					System.out.println("sendt [Destination RID] - send table to destination RID");
					System.out.println("see all routers - print all routing tables");
					System.out.println("message [Destination RID] [text] - send message to RID");
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
