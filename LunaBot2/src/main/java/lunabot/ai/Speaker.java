package lunabot.ai;

/**
 * @author SirMangler
 *
 * @date 26 Feb 2019
 */
public class Speaker {

	String name;
	String mention;
	long lastMessageTime;
	String lastMessage;
	String secondLastMessage;
	
	public Speaker(String name) {
		this.name=name;
	}
	
}
