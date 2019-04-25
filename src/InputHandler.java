import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.scene.shape.Path;

public class InputHandler extends Thread {

	private Socket socket;
	private BufferedReader reader;
	private ConnectionTable connectionTable;
	private Router router;
	
	public InputHandler(Socket socket, Router router) {
		this.socket = socket;
		this.router = router;
		this.connectionTable = router.connectionTable;
		
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		start();
	}
	
	public void run() {
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
						case "LSU":
								if(line.split(" ")[1].equals("TABLE")) {
									if(line.split(" ")[2].equals("RID:")) {	//New table									
										File file = new File(System.getProperty("user.dir") + "\\Storage\\" + router.RID + "\\" + line.split(" ")[3] + ".txt");
										file.createNewFile();
										
										PrintWriter writer = new PrintWriter(file);
										writer.print("");
										writer.close();
										
										file = new File(System.getProperty("user.dir") + "\\Storage\\" + router.RID + "\\" + line.split(" ")[3] + ".info.txt");
										file.createNewFile();
										
										writer = new PrintWriter(file);
										writer.print("");
										writer.close();
									} else {
										if(line.split(" ")[3].equals("PORT:") || line.split(" ")[3].equals("IP:")) {	//Handling info file				
											FileWriter fw = new FileWriter(System.getProperty("user.dir") + "\\Storage\\" + router.RID + "\\" + line.split(" ")[2] + ".info.txt", true); //the true will append the new data
										    fw.write(line.split(" ")[3] + " " + line.split(" ")[4] + "\n");
										    fw.close();
										} else {											
											String ip, nextHop, RID;
											int port, hops;
											
											String info[] = line.split(" ");
											
											String tmp[] = info[3].split(":");
											
											ip = tmp[0];
											port = Integer.parseInt(tmp[1]);
											RID = info[4];
											nextHop = info[5];
											
											try {
												hops = Integer.parseInt(info[6]);
											} catch(Exception e) {
												hops = Integer.parseInt(info[6].substring(0, info[6].length()-3));
												
												
											}
											
											FileWriter fw = new FileWriter(System.getProperty("user.dir") + "\\Storage\\" + router.RID + "\\" + line.split(" ")[2] + ".txt", true); //the true will append the new data
										    fw.write(ip + ":" + port + "\t" + RID + "\t" + nextHop + "\t" + hops + "\n");//appends the string to the file
										    fw.close();
										}
									} 
								} else if(line.split(" ")[1].equals("ENDTABLE")) {
									
									PrintWriter writer = new PrintWriter(System.getProperty("user.dir") + "\\Storage\\" + router.RID + "\\info.txt");
									writer.print(line.split(" ")[2] + "\n");
									writer.close();
									
									Table t = new Table();
									t.readTable(System.getProperty("user.dir") + "\\Storage\\" + router.RID + "\\" + line.split(" ")[2] + ".txt");
									t.setRID(line.split(" ")[2]);
									t.getAdittionalInfo(System.getProperty("user.dir") + "\\Storage\\" + router.RID + "\\" + line.split(" ")[2] + ".info.txt");
									
									if(line.split(" ")[3].equals(router.RID)) { //Reached destination
										//t.seer();
									} else {
										router.client.sendTable(line.split(" ")[3], t);
									}
								} else {	//For remove/adding router to table
									if(line.split(" ")[2].equals("0")) {	//Remove router
										connectionTable.removeRouter(line.split(" ")[1]);
									}
								}
							break;
						case "LSR":
							String dest = line.split(" ")[2];
							String source = line.split(" ")[1];
							
							if(dest.equals(router.RID)) {
								router.client.sendTable(source);
							} else {	//Send it away
								ArrayList<Client> clientss = connectionTable.getClients();
								
								String nextHop = connectionTable.table.getNextHop(dest);
									
								for(int b = 0; b < clientss.size(); b++) {
									if(clientss.get(b).getRID().equals(nextHop)) {
										OutputHandler handler = clientss.get(b).getOutputHandler();										
										handler.sendMessage("LSR " + source + " " + dest);
									}
								}
							}
							
							break;
						case "MESSAGE":
							String RID = line.split(" ")[1];
							String msg = line.substring(RID.length() + 9, line.length());

							if(RID.equals(router.RID)) {
								System.out.println(msg);
							} else {
								System.out.println("Message received. Press s to forward");
								
								router.client.messages.add(line);
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
	
	public void close() {
		try {
			reader.close();
		} catch(Exception e) {
			//Already closed
		}
	}
}
