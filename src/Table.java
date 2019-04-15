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
	
	private ArrayList<String> RIDs;
	private ArrayList<String> ipList;
	private ArrayList<String> gateways;
	private ArrayList<Integer> hops;
	private ArrayList<Integer> ports;
	private BufferedReader reader;
	
	public Table(String ip, String RID, int port) {
		ipList = new ArrayList<String>();
		gateways = new ArrayList<String>();
		hops = new ArrayList<Integer>();
		ports = new ArrayList<Integer>();
		RIDs = new ArrayList<>();

		this.port = port;
		this.ip = ip;
		this.RID = RID;
		
		readTable();
	}
	
	/**
	 * Reads table text file. RID + '.txt'
	 */
	private void readTable() {
		try {
			reader = new BufferedReader(new FileReader(new File("Tables/" + RID)));
			
			String[] lines = reader.readLine().split(":");
			port = Integer.parseInt(lines[1]);
			ip = lines[0];
			
			String line = reader.readLine();
			
			while(line != null) {
				String[] rt = line.split("\t");
				ipList.add(rt[0].split(":")[0]);
				ports.add(Integer.parseInt(rt[0].split(":")[1]));
				RIDs.add(rt[1]);
				gateways.add(rt[2]);
				hops.add(Integer.parseInt(rt[3]));
				
				line = reader.readLine();	
			}
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: " + RID + " can't find routing table.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			System.out.println(ipList.get(a) + ":"  + ports.get(a) + "\t" + RIDs.get(a) + "\t" + hops.get(a));
		}
	}
	
	public boolean hasRouter(String RID) {
		if(RIDs.indexOf(RID) != -1) return true;
		else return false;		
	}
	
	public void removeRouter(String RID) {
		if(hasRouter(RID)) {
			int id = RIDs.indexOf(RID);
			RIDs.remove(id);
			ipList.remove(id);
			gateways.remove(id);
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
			if(hops.get(a) == 1) {
				neighbours.add(RIDs.get(a));
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
}
