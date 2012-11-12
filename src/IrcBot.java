import java.util.*;
import org.jibble.pircbot.*;

public class IrcBot extends PircBot {
	private Voting voter;
	public HashMap<String, String> options;
	private List<HistoryObject> history;
	private Ban ban;
	
	public IrcBot(){
		this.setName("_HAL9k");
		
		voter = new Voting();
		options = new HashMap<String, String>();
		history = new ArrayList<HistoryObject>();
		ban = new Ban();
		
		options.put("Only voiced users can vote", "false");
		options.put("Tempban minutes", "1");
		
		this.sendMessage("#moonspace", "Good afternoon. Everything is going extremely well.");
	}
	
	public void onMessage(String channel, String sender,
							String login, String hostname,
							String message){
		// Run ban updates
		ban.checkForBan(this, channel, sender, hostname);
		ban.update(this, channel);
		
		// Process command
		if(!message.contains("!")){
			return;
		}
		String action = "";
		String args = "";
		int actionAt = message.indexOf("!")+1;
		if(message.contains(" ")){
			action = message.substring(actionAt, message.indexOf(" "));
			args = message.substring(message.indexOf(" ")+1);
		} else {
			action = message.substring(actionAt);
		}
		
		switch(action){
		case "kick":
			if(this.isOp(channel, sender)){
				String reason = "";
				String nick = "";
				if(args.contains(" ")){
					reason = args.substring(args.indexOf(" "));
					nick = args.substring(0, args.indexOf(" "));
				} else {
					nick = args;
				}
				Log.add("Kicking '" + nick + "' for '" + reason + "'");
				this.kick(channel, nick, reason);
			} else {
				this.sendNotice(sender, "You are not an OP");
			}
			break;
		case "ban":
			if(!this.isOp(channel, sender)){
				Boolean tmpban = false;
				String user = args;
				if(args.contains(" ")){
					user = args.substring(0, args.indexOf(" "));
					String tmpbanString = args.substring(args.indexOf(" "));
					if(tmpbanString == "true"){
						tmpban = true;
					}
				}
				ban.banUser(user, tmpban);
			} else {
				this.sendNotice(sender, "You are not an OP");
			}
			break;
		case "newVote":
			if(this.isOp(channel, sender)){
				String[] options2 = args.split(", ");
				List<String> options = new ArrayList<String>(options2.length);
				for(String s : options2){
					options.add(s);
				}
				
				voter = new Voting();
				voter.newVote("Vote for the next movie: ", options);
				this.sendMessage(channel, voter.getOutput());
			} else {
				this.sendNotice(sender, "You are not an OP");
			}
			break;
		case "vote":
			if(voter.voting){
				if(this.options.get("Only voiced users can vote").equals("true")){
					if(!this.hasVoice(channel, sender)) break;
				}
				try {
					voter.vote(hostname, Integer.parseInt(args));
				} catch(Exception e){
					this.sendNotice(sender, "You are supposed to vote for the number associated with the movie, not the title. To view the numbers and their movies, type '!voteOptions'");
				}
				this.sendNotice(sender,  voter.getOutput());
			} else {
				this.sendNotice(sender, "There is no vote going on");
			}
			break;
		case "voteOptions":
			if(voter.voting){
				String msg = "Your choices are: ";
				Iterator options_itr = voter.options.entrySet().iterator();
				while(options_itr.hasNext()){
					Map.Entry pairs = (Map.Entry)options_itr.next();
					msg = msg + pairs.getKey().toString() + ". " + pairs.getValue().toString() + ", ";
				}
				this.sendMessage(channel, msg);
			} else {
				this.sendMessage(channel, "There is no poll at this time");
			}
			break;
		case "finishVoting":
			if(this.isOp(channel, sender)){
				voter.stopVote();
				this.sendMessage(channel, voter.results());
			} else {
				this.sendNotice(sender, "You are not an OP");
			}
			break;
		case "adminPanel":
			if(this.isOp(channel, sender)){
				this.sendNotice(sender, "| " + Colors.BOLD + "Admin panel:");
				this.sendNotice(sender, "| Banned users: " + String.valueOf(this.ban.banned.size()));
				this.sendNotice(sender, "| ");
				this.sendNotice(sender, "| " + Colors.BOLD + "Options:");
				Iterator itr = this.options.entrySet().iterator();
				while(itr.hasNext()){
					Map.Entry pairs = (Map.Entry)itr.next();
					this.sendNotice(sender, "| " + pairs.getKey() + ": " + pairs.getValue());
				}
				this.sendNotice(sender, "| ");
				this.sendNotice(sender, "| " + "To change, type '!option (key): (new value)'");
			} else {
				this.sendNotice(sender, "You are not an OP");
			}
			break;
		case "option":
			if(this.isOp(channel, sender)){
				String key = args.substring(0, args.indexOf(": "));
				String value = args.substring(args.indexOf(": "));
				this.options.put(key, value);
				Log.add("Admin panel: '" + key + "' has been set to '" + value + "'");
				this.sendNotice(sender, key + " has been set to " + value);
			} else {
				this.sendNotice(sender, "You are not an OP");
			}
			break;
		case "about":
			this.sendMessage(channel, "My name is HAL9000. I am here to keep order in the chatroom and also ensure that everyone has a good time. To learn about my functions, type '!commands'.");
			break;
		case "commands":
		case "help":
			this.sendNotice(sender, " - Commands - ");
			this.sendNotice(sender, "!vote (number) - Votes for the number you specified.");
			this.sendNotice(sender, "!voteOptions - Lists what the current poll is on");
			this.sendNotice(sender, "!commands/!help - Shows this");
			this.sendNotice(sender, "!about - About me");
			if(this.isOp(channel, sender)){
				this.sendNotice(sender, " - OP-only commands - ");
				this.sendNotice(sender, "!ban (user) (tempban: true/false) - Bans the user. If tempban is true, user will be unbanned after " + this.options.get("Tempban minutes"));
				this.sendNotice(sender, "!kick (user) (reason) - Kicks the user with the reason. Reason may be blank.");
				this.sendNotice(sender, "!newVote (option), (option), etc etc - Starts a new vote. Every option must be seperated with a comma.");
				this.sendNotice(sender, "!finishVoting - Stops the voting and displays the results");
				this.sendNotice(sender, "!adminPanel - Shows the admin panel");
				this.sendNotice(sender, "!option (key): (value) - Sets key to value. All options can be viewed in the admin panel");
			}
			break;
		case "sing":
			this.sendNotice(sender, "Daisy. Daisy");
			this.sendNotice(sender, "Give me your answer, do");
			this.sendNotice(sender, "I'm half... Crazy");
			this.sendNotice(sender, "All for the love of you...");
			break;
		}
		
		history.add(new HistoryObject(channel, sender, login, hostname, message));
	}
	
	public boolean isOp(String channel, String user){
		return true;
		/*User[] users = this.getUsers(channel);
		for(User i : users){
			if(i.getNick().equals(user)){
				if(i.getPrefix().startsWith("@") || i.getPrefix().startsWith("&") || i.getPrefix().startsWith("%")){
					return true;
				}
			}
		}
		return false;*/
	}
	
	public boolean hasVoice(String channel, String user){
		User[] users = this.getUsers(channel);
		for(User i : users){
			if(i.getNick().equals(user)){
				return i.hasVoice();
			}
		}
		return false;
	}
}