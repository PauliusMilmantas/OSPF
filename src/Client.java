import java.net.Socket;

public class Client {

	private String RID;
	private String ip;
	private int port;
	private Socket socket;
	//private TimeStamp;////
	
	public Client(String RID, String ip, int port) {
		this.RID = RID;
		this.ip = ip;
		this.port = port;
	}	
}
