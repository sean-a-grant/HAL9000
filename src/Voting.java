import java.util.*;
import org.jibble.pircbot.*;

public class Voting extends PircBot {
	public boolean voting;
	public String title;
	public Map<Integer, String> options;
	public Map<Integer, Integer> votes;
	public List<String> voters;
	public List<String> output = new ArrayList<String>();
	
	public void newVote(String title, List<String> options){
		if(this.voting == true){
			output.add("There is already a vote in progress");
			return;
		}
		this.title = title;
		this.options = new HashMap<Integer, String>();
		this.votes = new HashMap<Integer, Integer>();
		this.voters = new ArrayList<String>();
		
		String rawOptions = "";
		Iterator<String> itr = options.iterator();
		Integer i = 1;
		while(itr.hasNext()){
			String option = itr.next().toString();
			rawOptions = rawOptions.concat(Colors.RED + i.toString() + ". " + Colors.NORMAL + option + " ");
			this.options.put(i, option);
			this.votes.put(i, 0);
			i++;
		}
		output.add(Colors.BOLD + title + ": " + Colors.NORMAL + rawOptions + " | Type '!vote (" + Colors.RED + "number" + Colors.NORMAL + ")' to vote");
		this.voting = true;
	}
	
	public Boolean vote(String voter, int option){
		if(this.voting){
			if(!voters.contains(voter)){
				this.voters.add(voter);
				this.votes.put(option, this.votes.get(option)+1);
				this.output.add("You have voted for: " + this.options.get(option));
				return true;
			} else {
				this.output.add("You cannot vote more than once");
			}
		}
		return false;
	}
	
	public void stopVote(){
		this.voting = false;
	}
	
	public String results(){
		/*List<Integer> highest = new ArrayList<Integer>();
		
		Iterator itr = this.votes.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry pairs = (Map.Entry)itr.next();
			Integer value = Integer.parseInt(pairs.getValue().toString());
			if(value > highest.get(0)){
				highest.clear();
				highest.add(Integer.parseInt((pairs.getKey().toString())));
			} else if(value == highest.get(0)){
				highest.add(Integer.parseInt(pairs.getKey().toString()));
			}
			itr.remove();
		}
		
		String msg = "Results: ";
		itr = highest.iterator();
		while(itr.hasNext()){
			Integer id = Integer.parseInt(itr.next().toString());
			String name = this.options.get(id);
			Integer votes = this.votes.get(id);
			msg = msg + name + " (" + votes + ")";
			if(itr.hasNext()){
				msg = msg + ", ";
			}
		}
		
		return msg;*/
		
		String msg = ">> Results | ";
		Iterator itr = this.votes.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry pairs = (Map.Entry)itr.next();
			Integer id = Integer.parseInt(pairs.getKey().toString());
			String name = this.options.get(id);
			msg = msg + name + ": " + pairs.getValue().toString() + " | ";
		}
		return msg;
	}
	
	public String getOutput(){
		return this.output.get(this.output.size() -1);
	}
}