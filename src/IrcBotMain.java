import java.io.*;

public class IrcBotMain {
	static IrcBot bot;
	
	public static void main(String[] args) throws Exception {
		try {
			Log.add("Starting...");
			bot = new IrcBot();
			bot.setVerbose(false);
			Log.add("Connecting...");
			//bot.connect("irc.rizon.net");
			//bot.identify("moonstreamiscool");
			bot.connect("IRC.COLOSOLUTIONS.COM");
			if(true){
				bot.joinChannel("#dreamincode");
				//bot.joinChannel("#hal90002");
			} else {
				bot.joinChannel("#moonspace");
			}
			Log.add("HAL9000 has connected");
		} catch(Exception e){
			System.out.println(" --- Error --- ");
			System.out.println(e.getMessage());
		}
	}
	
	public static void input(){
		try {
			InputStreamReader converter = new InputStreamReader(System.in);
			BufferedReader in = new BufferedReader(converter);
			while(true){
				String cmd = in.readLine();
				handleCmd(cmd);
			}
		} catch (IOException e) {
			System.out.println("Input error: " + e.getMessage());
			System.out.println("Will continue in verbose mode");
			bot.setVerbose(true);
		}
	}
	
	public static void handleCmd(String cmd){
		switch(cmd){
			
		}
	}
}