import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
			String tr[] = line.split(" ");
			
			for(int a = 0; a < 2; a++) {
				if(tr[0].equals("IP:")) ip = tr[1];
				else if(tr[0].equals("PORT:")) port = Integer.parseInt(tr[1]);
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
		
		//if(ipList.size() >= RIDs.size()) {
			for(int a = 0; a < ipList.size(); a++) {
				System.out.println(ipList.get(a) + ":"  + ports.get(a) + "\t" + RIDs.get(a) + "\t" + hops.get(a) + "\t" + nextHop.get(a));
			}
	/*	} else {
			for(int a = 0; a < RIDs.size(); a++) {
				System.out.println("127.0.0.1" + ":"  + 0 + "\t" + RIDs.get(a) + "\t" + 0 + "\t" + 0);
			}
		}*/
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
