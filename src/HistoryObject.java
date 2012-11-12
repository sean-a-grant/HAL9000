
public class HistoryObject extends IrcBot {
	public String channel;
	public String sender;
	public String login;
	public String hostname;
	public String message;
	
	public HistoryObject(String channel, String sender, String login, String hostname, String message){
		this.channel = channel;
		this.sender = sender;
		this.login = login;
		this.hostname = hostname;
		this.message = message;
	}
}