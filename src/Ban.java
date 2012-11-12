import java.util.*;

import org.jibble.pircbot.*;


class Ban {
	private class BanItem {
		public final String user;
		public final boolean tmpban;
		public boolean booked;
		private long banned_timestamp;
		private String hostmask;
		
		public BanItem(String user, Boolean tmpban) {
			this.user = user;
			this.tmpban = tmpban;
			this.booked = false;
		}
		
		public boolean isBooked() {
			return this.booked;
		}
		
		public void doBan(String hostname){
			if(!this.booked){
				this.banned_timestamp = System.currentTimeMillis();
				this.hostmask = "*!*@*" + hostname;
				this.booked = true;
			}
		}
	}
	
	public Map<String, BanItem> banned;
	
	public Ban(){
		this.banned = new HashMap<String, BanItem>();
	}
	
	public void banUser(String user, Boolean tmpban){
		this.banned.put(user, new BanItem(user, tmpban));
		Log.add("Ban request for '" + user + "' (tempban: " + tmpban.toString() + ")");
	}
	
	public Boolean checkForBan(IrcBot bot, String channel, String user, String hostname){
		if(this.banned.containsKey(user)){
			BanItem item = this.banned.get(user);
			if(!item.booked){
				item.doBan(hostname);
				bot.ban(channel, item.hostmask);
				Log.add("Ban request fulfilled for '" + user + "'");
				return true;
			}
		}
		return false;
	}
	
	public void update(IrcBot bot, String channel){
		for(BanItem item : this.banned.values()){
			if(item.booked){
				long then = item.banned_timestamp;
				long now = System.currentTimeMillis();
				long since_then = now - then;
				long since_ban = since_then / 60000;
				if(since_ban >= Long.parseLong(bot.options.get("Tempban minutes"))){
					bot.unBan(channel, item.hostmask);
					this.banned.remove(item.user);
					Log.add("User '" + item.user + "' has been unbanned");
				}
			}
		}
	}
}