import java.net.Socket;
import java.sql.Timestamp;

public class Client {

	private String RID;
	private String ip;
	private int port;
	private InputHandler inputHandler;
	private OutputHandler outputHandler;
	private Socket socket;
	/**
	 * Last received 'Hello' message
	 */
	private Timestamp lastUpdated;
	
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
		
		lastUpdated = new Timestamp(System.currentTimeMillis());
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

	public Socket getSocket() {
		return socket;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void setConnectionStatus(int connectionStatus) {
		this.connectionStatus = connectionStatus;
	}
	
	public OutputHandler getOutputHandler() {
		return outputHandler;
	}
	
	public void setOutputHandler(OutputHandler outputHandler) {
		this.outputHandler = outputHandler;
	}
	
	public InputHandler getInputHandler() {
		return inputHandler;
	}
	
	public void setInputHandler(InputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}
	
	public Timestamp getTime() {
		return lastUpdated;
	}
	
	public void setTime(Timestamp time) {
		this.lastUpdated = time;
	}
	
	public void close() {
		try {
			inputHandler.interrupt();
		} catch (NullPointerException e) {
			//Already closed
		}
	}
}
