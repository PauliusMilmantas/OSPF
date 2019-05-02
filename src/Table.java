import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import alg.*;

public class Table {

	//Local info
	private int port;
	private String RID;
	private String ip;
	
	public ArrayList<String> RIDs;
	public ArrayList<String> ipList;
	public ArrayList<String> nextHop;
	public ArrayList<Integer> hops;
	public ArrayList<Integer> ports;
	private BufferedReader reader;
	
	public Table(String ip, String RID, int port) {
		ipList = new ArrayList<String>();
		nextHop = new ArrayList<String>();
		hops = new ArrayList<Integer>();
		ports = new ArrayList<Integer>();
		RIDs = new ArrayList<>();

		this.port = port;
		this.ip = ip;
		this.RID = RID;
	}
	
	public Table() {
		ipList = new ArrayList<String>();
		nextHop = new ArrayList<String>();
		hops = new ArrayList<Integer>();
		ports = new ArrayList<Integer>();
		RIDs = new ArrayList<>();		
	}
	
	public String getNextHop(String DestinationRID) {
		try {
			if(DestinationRID.equals(RID)) {
				return RID;
			} else {
				return nextHop.get(RIDs.indexOf(DestinationRID));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Reads table text file. RID + '.txt'
	 */
	public void readTable(String path) {
		
		File file = new File(path);
		if(file.exists()) {
			try {
				reader = new BufferedReader(new FileReader(new File(path)));
				
				String line = reader.readLine();
				
				while(line != null) {
					String[] rt = line.split("\t");
					ipList.add(rt[0].split(":")[0]);
					ports.add(Integer.parseInt(rt[0].split(":")[1]));
					RIDs.add(rt[1]);
					nextHop.add(rt[2]);
					hops.add(Integer.parseInt(rt[3]));
					
					line = reader.readLine();
				}
				
				reader.close();
			} catch (FileNotFoundException e) {
				if(Router.DEBUG) System.out.println("Using empty routing tableff");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			
		}
	}
	
	public void addLink(String RID, String ip, int port, int hop) {
		if(!RIDs.contains(RID)) {
			RIDs.add(RID);
			ipList.add(ip);
			ports.add(port);
			hops.add(hop);
			nextHop.add(RID);
		}
	}
	
	public void readTable() {
		readTable("Tables/" + RID);
	}
	
	/**
	 * Reads info file from storage to get IP, PORT
	 * Intended for reading table from storage
	 */
	public void getAdittionalInfo(String path) {
		try {
			reader = new BufferedReader(new FileReader(new File(path)));
			
			String line = reader.readLine();
			
			if(line != null) {
				String tr[] = line.split(" ");
				
				for(int a = 0; a < 2; a++) {
					if(tr[0].equals("IP:")) ip = tr[1];
					else if(tr[0].equals("PORT:")) port = Integer.parseInt(tr[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Prints routing table
	 */
	public void seer() {
		System.out.println("RID: " + RID);
		System.out.println("Router ip address: " + ip);
		System.out.println("Router port: " + port);
		
		for(int a = 0; a < ipList.size(); a++) {
			System.out.println(ipList.get(a) + ":"  + ports.get(a) + "\t" + RIDs.get(a) + "\t" + hops.get(a) + "\t" + nextHop.get(a));
		}
	}
	
	public boolean hasRouter(String RID) {
		if(RIDs.indexOf(RID) != -1) return true;
		else return false;		
	}
	
	public void removeRouter(String RID) {
		while(hasRouter(RID)) {
			int id = RIDs.indexOf(RID);
			RIDs.remove(id);
			ipList.remove(id);
			nextHop.remove(id);
			hops.remove(id);
			ports.remove(id);
		}
	}
	
	/**
	 * Returns a list of neighbours
	 * @return ArrayList<String> - RID list of neighbours
	 */
	public ArrayList<String> getNeighbours() {
		
		ArrayList<String> neighbours = new ArrayList<String>();
		
		for(int a = 0; a < RIDs.size(); a++) {
			if(hops.size() >= RIDs.size()) {
				if(hops.get(a) == 1) {
					neighbours.add(RIDs.get(a));
				}
			}
		}
		
		return neighbours;
	}
	
	/**
	 * Uses Dijkstra to calculate all hops
	 * saves changes to table
	 * null = 2147483647
	 * @return Graph with calculated distances
	 * Graph.Nodes.ToString() - To string
	 */
	public Graph recalculateDistances() {
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "\\Storage\\" + RID + "\\info.txt")));
			ArrayList<String> rids = new ArrayList<>();
			
			Graph graph = new Graph();
			
			HashMap<String, Node> map = new HashMap<String, Node>();
			map.put(RID, new Node(RID));
			
			String line = reader.readLine();
			while(line != null) {
				
				if(!rids.contains(line)) {
					rids.add(line);
					
					if(new File(System.getProperty("user.dir") + "\\Storage\\" + RID + "\\" + line + ".txt").exists())	{
						BufferedReader readerrr = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "\\Storage\\" + RID + "\\" + line + ".txt")));
						
						String ff = readerrr.readLine();
						while(ff != null) {
							
							String gg[] = ff.split("\t");
							if(gg[3].equals("1")) {
								Node A, B;
								
								if(map.containsKey(gg[1])) {
									A = map.get(gg[1]);
								} else {
									A = new Node(gg[1]);
									map.put(gg[1], A);
								}
								
								if(map.containsKey(line)) {
									B = map.get(line);
								} else {
									B = new Node(line);
									map.put(line, B);
								}
								
								B.addDestination(A, 1);
								A.addDestination(B, 1);
							}					
						
							ff = readerrr.readLine();
						}		
						readerrr.close();
					}
					
					for(int a = 0; a < rids.size(); a++) {
						graph.addNode(map.get(rids.get(a)));
					}
					

				}
			
				line = reader.readLine();
			}
			
			graph = Dijkstra.calculateShortestPathFromSource(graph, map.get(RID));
				
			reader.close();
			
			return graph;
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	public int getPort(String RID) {
		return ports.get(RIDs.indexOf(RID));
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getRID() {
		return RID;
	}

	public void setRID(String rID) {
		RID = rID;
	}

	public String getIp(String RID) {
		return ipList.get(RIDs.indexOf(RID));
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public ArrayList<String> getRIDs() {
		return RIDs;
	}

	public void setRIDs(ArrayList<String> rIDs) {
		RIDs = rIDs;
	}

	public ArrayList<String> getIpList() {
		return ipList;
	}

	public void setIpList(ArrayList<String> ipList) {
		this.ipList = ipList;
	}

	public ArrayList<String> getNextHop() {
		return nextHop;
	}

	public void setNextHop(ArrayList<String> nextHop) {
		this.nextHop = nextHop;
	}

	public ArrayList<Integer> getHops() {
		return hops;
	}

	public void setHops(ArrayList<Integer> hops) {
		this.hops = hops;
	}

	public ArrayList<Integer> getPorts() {
		return ports;
	}

	public ArrayList<String> getNextHops() {
		return nextHop;
	}
	
	public void setPorts(ArrayList<Integer> ports) {
		this.ports = ports;
	}
}
