import java.net.Socket;

public class Client {

	private String RID;
	private String ip;
	private int port;
	private Socket socket;
	//private TimeStamp lastUpdate;
	/**
	 * Shows connection status
	 * 0 - offline
	 * 1 - online
	 */
	private int connectionStatus;
	
	public Client(String RID, String ip, int port) {
		this.RID = RID;
		this.ip = ip;
		this.port = port;
		
		connectionStatus = 0;
	}	

	public String getRID() {
		return RID;
	}

	public void setRID(String rID) {
		RID = rID;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getConnectionStatus() {
		return connectionStatus;
	}

	public void setConnectionStatus(int connectionStatus) {
		this.connectionStatus = connectionStatus;
	}
}
